
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;

/**
 * A very simple search engine. Uses an inverted index over a folder of TXT
 * files.
 */
public class SimpleEngine {

    public static void main(String[] args) throws IOException {
        final Path currentWorkingPath = Paths.get("").toAbsolutePath();

        // the inverted index
        final NaiveInvertedIndex index = new NaiveInvertedIndex();

        // the list of file names that were processed
        final List<String> fileNames = new ArrayList<String>();
        
        //positional inverted index
        final PositionalInvertedIndex posIndex = new PositionalInvertedIndex();

        // This is our standard "walk through all .txt files" code.
        Files.walkFileTree(currentWorkingPath, new SimpleFileVisitor<Path>() {
            int mDocumentID = 0;

            public FileVisitResult preVisitDirectory(Path dir,
                    BasicFileAttributes attrs) {
                // make sure we only process the current working directory
                if (currentWorkingPath.equals(dir)) {
                    return FileVisitResult.CONTINUE;
                }
                return FileVisitResult.SKIP_SUBTREE;
            }

            public FileVisitResult visitFile(Path file,
                    BasicFileAttributes attrs) {
                // only process .txt files
                if (file.toString().endsWith(".txt")) {
               // we have found a .txt file; add its name to the fileName list,
                    // then index the file and increase the document ID counter.
                    System.out.println("Indexing file " + file.getFileName());

                    fileNames.add(file.getFileName().toString());
                    indexFile(file.toFile(), index, posIndex, mDocumentID);
                    mDocumentID++;
                }
                return FileVisitResult.CONTINUE;
            }

            // don't throw exceptions if files are locked/other errors occur
            public FileVisitResult visitFileFailed(Path file,
                    IOException e) {

                return FileVisitResult.CONTINUE;
            }

        });

        printResults(index, fileNames);
        
        posIndex.printResults(fileNames);
        
        searchTerm(index, posIndex, fileNames);  
    }
    
    public static void searchTerm(NaiveInvertedIndex index, 
            PositionalInvertedIndex posIndex, List<String> fileNames){
        while (true) {
            System.out.print("\n\nEnter a term to search for : ");
            Scanner userInput = new Scanner(System.in);
            String input = PorterStemmer.processToken(userInput.nextLine());
            
            if(input.equalsIgnoreCase("quit"))
                break;
            
            //searching the term in NaiveInvertedIndex
            List<Integer> postingsList = index.getPostings(input);
            if(postingsList != null){
                for(Integer docIndex : postingsList)
                    System.out.print("  "+fileNames.get(docIndex));
            }
            else               
                System.out.println("This term is not present "
                        + "in any of the documents");
            
            //Searching the term in PositionalInvertedIndex
            HashMap<Integer,List<Integer>> postingsMap = posIndex.getPostings(input);
            if(postingsMap != null){
                Integer[] docIdArray = postingsMap.keySet().toArray(new Integer[postingsMap.keySet().size()]);
                Arrays.sort(docIdArray);
            
                for(Integer docId : docIdArray){
                    List<Integer> posIndexes = postingsMap.get(docId);
                    System.out.print("\n" + fileNames.get(docId) + " -> ");
                    for(Integer positionIndex : posIndexes){
                        System.out.print(positionIndex + " , ");
                    }
                } 
            }
        }
        System.out.println("Bye...");
    }
    
    /**
     * Indexes a file by reading a series of tokens from the file, treating each
     * token as a term, and then adding the given document's ID to the inverted
     * index for the term.
     *
     * @param file a File object for the document to index.
     * @param index the current state of the index for the files that have
     * already been processed.
     * @param docID the integer ID of the current document, needed when indexing
     * each term from the document.
     */
    private static void indexFile(File file, NaiveInvertedIndex index,
            PositionalInvertedIndex posIndex, int docID) {
      // TO-DO: finish this method for indexing a particular file.
        // Construct a SimpleTokenStream for the given File.
        // Read each token from the stream and add it to the index.
        try {
            SimpleTokenStream tokenStream = new SimpleTokenStream(file);
            int count = 1;
            while (tokenStream.hasNextToken()) {
                String term = PorterStemmer.processToken(tokenStream.nextToken());
                index.addTerm(term, docID);
                posIndex.addTerm(term, docID, count);
                count++;
            }
        } catch (Exception ex) {
            System.out.println("Exception in opening the file" + 
                    ex.getMessage() + ex.getLocalizedMessage());
        }
    }

    private static void printResults(NaiveInvertedIndex index,
            List<String> fileNames) {
      // TO-DO: print the inverted index.
        // Retrieve the dictionary from the index. (It will already be sorted.)
        // For each term in the dictionary, retrieve the postings list for the
        // term. Use the postings list to print the list of document names that
        // contain the term. (The document ID in a postings list corresponds to 
        // an index in the fileNames list.)
      // Print the postings list so they are all left-aligned starting at the
        // same column, one space after the longest of the term lengths. Example:
        // 
        // as:      document0 document3 document4 document5
        // engines: document1
        // search:  document2 document4  
        
        int longestTerm = 0;
        for(String term : index.getDictionary())
            longestTerm = Math.max(longestTerm, term.length());
            
        for(String term : index.getDictionary()){
            System.out.print("\n" + term);
            printSpaces(longestTerm - term.length());
            System.out.print(":");
            
            for(Integer docIndex : index.getPostings(term))
                System.out.print("  "+fileNames.get(docIndex));
        }
    }
    
    // prints a bunch of spaces
    private static void printSpaces(int spaces) {
        for (int i = 0; i < spaces; i++) {
            System.out.print(" ");
        }
    }
}
