
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vipinsharma
 */
public class IndexBuilderFactory {
    private static PositionalInvertedIndex posIndex = new PositionalInvertedIndex();
    private static IndexBuilderFactory indexBuilderFactory = null;
    private List<String> fileNames = new ArrayList<String>();
    
    private IndexBuilderFactory( Path currentWorkingPath ){
        
        // This is our standard "walk through all .txt files" code.
        try{
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
                        indexFile(file.toFile(), mDocumentID);
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
        }
        catch(IOException ex){
            System.out.println("Exception : " + ex.getMessage());
        }
    }
    
    public static IndexBuilderFactory getInstance(){
        return indexBuilderFactory;
    }
    
    public String getFilesNames(int docId){
        return fileNames.get(docId);
    }
    
    public static void createIndex(Path currentWorkingPath){
        indexBuilderFactory = new IndexBuilderFactory(currentWorkingPath);
    }
    
    public PositionalInvertedIndex getPositionalIndex(){
        return posIndex;
    }
    
    public HashSet<Integer> fileNamesHashSet(){
        HashSet<Integer> fileSet = new HashSet<>();
        for(int i = 0; i < fileNames.size(); i++){
            fileSet.add(i);
        }
        return fileSet;
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
    private static void indexFile(File file, int docID) {
        // Construct a SimpleTokenStream for the given File.
        // Read each token from the stream and add it to the index.
        try {
            SimpleTokenStream tokenStream = new SimpleTokenStream(file);
            int count = 1;
            while (tokenStream.hasNextToken()) {
                String term = tokenStream.nextToken();
                //hyphenation
                if(term.contains("-")){
                    String[] multipleTerms = term.split("-");
                    for(String termPart : multipleTerms){
                        if(termPart != ""){
//                            termPart = PorterStemmer.processToken(termPart);
//                            index.addTerm(termPart, docID);
//                            posIndex.addTerm(termPart, docID, count);
                            stemAndAddToIndex(termPart, docID, count);
                        }
                    }
                    term = term.replaceAll("-", "");
//                    term = PorterStemmer.processToken(term);
//                    index.addTerm(term, docID);
//                    posIndex.addTerm(term, docID, count);
                    stemAndAddToIndex(term, docID, count);
                }
                else{
//                    term = PorterStemmer.processToken(term);
//                    index.addTerm(term, docID);
//                    posIndex.addTerm(term, docID, count);
                    stemAndAddToIndex(term, docID, count);
                }
                count++;
            }
        } catch (Exception ex) {
            System.out.println("Exception in opening the file" + 
                    ex.getMessage() + ex.getLocalizedMessage());
        }
    }
    
    private static void stemAndAddToIndex(String term, int docId, int position){
        term = PorterStemmer.processToken(term);
        //index.addTerm(term, docId);
        posIndex.addTerm(term, docId, position);
    }
    
    
    
    public List<PositionalPostingsStructure> searchPhrase(String phraseQuery){
        List<PositionalPostingsStructure> list1 = new ArrayList<PositionalPostingsStructure>();
        List<PositionalPostingsStructure> list2 = new ArrayList<PositionalPostingsStructure>();
        List<PositionalPostingsStructure> resultList = new ArrayList<PositionalPostingsStructure>();
        String terms[] = phraseQuery.split(" ");
        for(int i = 0; i < (terms.length - 1); i++){
            if( i == 0 ){
                list1 = searchTerm(terms[i]);
            }
            else{
                list1 = resultList;
            }
            list2 = searchTerm(terms[i+1]);
            if(list1 == null || list2 == null){
                resultList = null;
                break;
            }
                
            resultList = SearchEngineUtilities.positionalSearch(list1, list2);
        }
        
        if(resultList != null){
            System.out.println("printing results");
            for(PositionalPostingsStructure posStruct : resultList){
                System.out.println(posStruct.getDocumentName());
//                posStruct.printData(0);
            }
        }
        
        
        return resultList;
    }
    
    public List<PositionalPostingsStructure> searchTerm(String term){
        term = term.replaceAll("^\\W+|\\W+$","");
            //System.out.println(input);
            //trial 
//            Pattern p = Pattern.compile(".*\\\"(.*)\\\".*");
//            Matcher m = p.matcher(input);
//            System.out.println("count " + m.groupCount());
//            while (m.find()) {
//              System.out.println(m.group(1));
//            }
//            String[] str = input.split("\"");
//            System.out.print(str.length);
            
//            Pattern p = Pattern.compile("([\'])(\\1)");
//            if(p.matcher(input).find())
//                System.out.println("found");
            //end trial
        term = PorterStemmer.processToken(term);
            
            
            
            //searching the term in NaiveInvertedIndex
//            List<Integer> postingsList = index.getPostings(input);
//            if(postingsList != null){
//                for(Integer docIndex : postingsList)
//                    System.out.print("  "+fileNames.get(docIndex));
//            }
//            else               
//                System.out.println("This term is not present "
//                        + "in any of the documents");
            
            //Searching the term in PositionalInvertedIndex
        List<PositionalPostingsStructure> termPostingsList = posIndex.getPostings(term);
//            if(termPostingsList != null){
////                Integer[] docIdArray = postingsMap.keySet().toArray(new Integer[postingsMap.keySet().size()]);
////                Arrays.sort(docIdArray);
//            
////                for(Integer docId : docIdArray){
////                    List<Integer> posIndexes = postingsMap.get(docId);
////                    System.out.print("\n" + fileNames.get(docId) + " -> ");
////                    for(Integer positionIndex : posIndexes){
////                        System.out.print(positionIndex + " , ");
////                    }
////                }
//                for(PositionalPostingsStructure posStructure : termPostingsList){
//                    posStructure.printData(0);
//                    System.out.print("\n");
//                }
//            }
//            else               
//                System.out.println("This term is not present "
//                        + "in any of the documents");
        
        return termPostingsList;
    }
    
    public HashSet<Integer> queryProcessing(String userQuery){
        HashSet<Integer> resultSet = new HashSet<>();
        
        Pattern orProcessing = Pattern.compile("(.*)\\+(.*)");   
        
        if(orProcessing.matcher(userQuery).find()){
            String[] orTerms = userQuery.split("\\+");
            int count = 0;
            for(String queryterm : orTerms){
                HashSet<Integer> tempResultSet = parseQueryPart(queryterm);
                if( tempResultSet != null )
                    resultSet.addAll(parseQueryPart(queryterm));
            }
        }
        else{
            HashSet<Integer> tempResultSet = parseQueryPart(userQuery);
            if( tempResultSet != null )
                resultSet.addAll(parseQueryPart(userQuery));
        }
        
        return resultSet;
    }
    
    public HashSet<Integer> parseQueryPart(String userQuery){
        userQuery = userQuery.trim();
        String[] termParts = userQuery.split(" ");
        List<String> searchTermParts = new ArrayList<>();
        Pattern startingQuotes = Pattern.compile("^\"");
        Pattern endingQuotes = Pattern.compile("\"$");
        
        for(int i = 0; i < termParts.length ; i++){
            if(startingQuotes.matcher(termParts[i]).find()){
                System.out.println("Start of double quotes encountered");
                String phraseQuery = termParts[i];
                while(!endingQuotes.matcher(termParts[i]).find()){
                    phraseQuery = phraseQuery + " " + termParts[i+1];
                    i++;
                }
                
                //phraseQuery = SearchEngineUtilities.hyphenation(phraseQuery);
                searchTermParts.add(phraseQuery);
                i++;
                
                if(i == termParts.length)
                    break;
            }
            searchTermParts.add(termParts[i]);
        }
        
//        for(String termPart : searchTermParts){
//            System.out.println(termPart);
//        }
//        
        int index = 0;
        
        HashSet<Integer> resultSet = new HashSet<>();
        
        if(searchTermParts.size() > 1){
            while(index < searchTermParts.size() - 1){
                if(index == 0){
                    resultSet = searchVocab(searchTermParts.get(index));
                }

                HashSet<Integer> docIdSet1 = searchVocab(searchTermParts.get(index + 1));
                
                if(resultSet != null && docIdSet1 != null){
                    resultSet.retainAll(docIdSet1);
                    index++;
                }
                else
                    return null;
            }
        }
        else{
            resultSet = searchVocab(searchTermParts.get(0));
        }
        
//        System.out.println("Query result documents : ");
//        for(Integer docId : resultSet){
//            System.out.println(fileNames.get(docId));
//        }
        
        return resultSet;
    }
    
    public HashSet<Integer> searchVocab(String term){
        if(term.split(" ").length > 1){
            return SearchEngineUtilities.convertListToSet(searchPhrase(term));
        }
        else{
            boolean notQuery = false;
            Pattern notTerm = Pattern.compile("^-(.*)");
            if(notTerm.matcher(term).find()){
                System.out.println("negative term");
                notQuery = true;
            }
            
            HashSet<Integer> docIdSet = SearchEngineUtilities.convertListToSet(searchTerm(term));
            HashSet<Integer> fileNameSet = fileNamesHashSet();
            if(notQuery){
                if(docIdSet != null)
                    fileNameSet.removeAll(docIdSet);
                return fileNameSet;
            }
            return docIdSet;
        }
    }
}
