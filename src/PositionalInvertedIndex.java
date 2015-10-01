
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author vipinsharma
 */
public class PositionalInvertedIndex {

    private HashMap<String, List<PositionalPostingsStructure>> pIndex;

    //Variables for calculation of Index Statistics
    private HashSet<String> typeSet;
    private int numOfTerms = 0;
    private int numOfTypes = 0;
    private HashMap<String, Integer> averagePostings;
    private double[] documentProportion;
    private long totalMemoryRequirement = 0;

    public PositionalInvertedIndex() {
        pIndex = new HashMap<String, List<PositionalPostingsStructure>>();
        typeSet = new HashSet<>();
        averagePostings = new HashMap<>();
        documentProportion = new double[10];
    }

    public void addType(String term) {
        typeSet.add(term);
    }

    public void addTerm(String term, int documentId, int position) {
        List<Integer> positionList = new ArrayList<>();
        HashMap<Integer, List<Integer>> docIdMap = new HashMap<Integer, List<Integer>>();
//        if(pIndex.containsKey(term)){
//            docIdMap = pIndex.get(term);
//            if(docIdMap.containsKey(documentId)){
//                positionList = docIdMap.get(documentId);
//                positionList.add(position);
//            }
//            else{
//                positionList.add(position);
//                docIdMap.put(documentId,positionList);
//            }
//        }
//        else{
//            positionList.add(position);
//            docIdMap.put(documentId,positionList);
//            pIndex.put(term, docIdMap);
//        }
        if (pIndex.containsKey(term)) {
            List<PositionalPostingsStructure> termPostingsList
                    = pIndex.get(term);
            if (termPostingsList.get(termPostingsList.size() - 1).getDocumentId()
                    == documentId) {
                termPostingsList.get(termPostingsList.size() - 1)
                        .addPosition(position);
            } else {
                PositionalPostingsStructure termPostings
                        = new PositionalPostingsStructure(documentId, position);
                termPostingsList.add(termPostings);
            }
        } else {
            List<PositionalPostingsStructure> termPostingsList
                    = new ArrayList<PositionalPostingsStructure>();
            PositionalPostingsStructure termPostings
                    = new PositionalPostingsStructure(documentId, position);
            termPostingsList.add(termPostings);
            pIndex.put(term, termPostingsList);
        }
    }

    public List<PositionalPostingsStructure> getPostings(String term) {
        return pIndex.get(term);
    }

    public void printResults() {
        int longestTerm = 0;
        String[] termsArray = pIndex.keySet().toArray(
                new String[pIndex.keySet().size()]);
        Arrays.sort(termsArray);
        for (String term : termsArray) {
            longestTerm = Math.max(longestTerm, term.length());
        }

        for (String term : termsArray) {
            System.out.print("\n" + term);
            printSpaces(longestTerm - term.length());
            int longestTermTemp = 0;
            //System.out.print(":");

//            HashMap<Integer,List<Integer>> docIdMap = pIndex.get(term);
//            Integer[] docIdArray = docIdMap.keySet().toArray(new Integer[docIdMap.keySet().size()]);
//            Arrays.sort(docIdArray);
//            
//            for(Integer docId : docIdArray){
//                List<Integer> posIndexes = docIdMap.get(docId);
//                System.out.print(" : "+fileNames.get(docId) + " -> ");
//                for(Integer posIndex : posIndexes){
//                    System.out.print(posIndex + " , ");
//                }
//                System.out.println();
//                printSpaces(longestTerm);
//                //System.out.print(":");
//            }
//            for(Integer docIndex : pIndex.get(term).keySet())
//                System.out.print("  "+fileNames.get(docIndex));
            for (PositionalPostingsStructure posStructure : pIndex.get(term)) {
                posStructure.printData(longestTermTemp);
                System.out.print("\n");
                longestTermTemp = longestTerm;
            }
        }
    }

    // prints a bunch of spaces
    private static void printSpaces(int spaces) {
        for (int i = 0; i < spaces; i++) {
            System.out.print(" ");
        }
    }

    public void calculateMetrics() {
        numOfTerms = pIndex.keySet().size();
        numOfTypes = typeSet.size();

        //calculating average num of documents in a posting list
        int numOfDocuments;
        int positionListSize;

        long termMemory = 0;
        long postingListMemory = 0;
        long postingsMemory = 0;

        IndexBuilderFactory indexBuilderFactory = IndexBuilderFactory.getInstance();
        int numOfDocsIndexed = indexBuilderFactory.fileNamesHashSet().size();

        HashMap<String, Integer> totalPostings = new HashMap<>();

        for (String term : pIndex.keySet()) {
            numOfDocuments = pIndex.get(term).size();
            positionListSize = 0;

            for (PositionalPostingsStructure posStructure : pIndex.get(term)) {
                positionListSize += posStructure.getPositionList().size();
            }

            averagePostings.put(term, positionListSize / numOfDocuments);
            totalPostings.put(term, positionListSize);

            termMemory += (40 + 2 * (term.length()));
            postingListMemory += (24 + 8 * numOfDocuments);
            postingsMemory += ((48 * numOfDocuments) + (4 * positionListSize));
        }

        totalMemoryRequirement = (24 + 36 * numOfTerms) + termMemory + postingListMemory + postingsMemory;

        //converting averagePostings to treemap sorted on the average number of 
        //documents in the posting list
        Comparator<String> valueComparator = new Comparator<String>() {
            public int compare(String k1, String k2) {
                int compare = totalPostings.get(k2).compareTo(totalPostings.get(k1));
                if (compare == 0) {
                    return 1;
                } else {
                    return compare;
                }
            }
        };

        Map<String, Integer> averagePostingsTree = new TreeMap<String, Integer>(valueComparator);

        averagePostingsTree.putAll(totalPostings);

        //calculating the proportion of documents that contain 10 most frequent terms
        int count = 0;

        for (Entry<String, Integer> e : averagePostingsTree.entrySet()) {
            if (count == 10) {
                break;
            }
            System.out.println(e.getKey() + ": " + e.getValue() + " : " + indexBuilderFactory.searchTerm(e.getKey()).size()
                    + " : " + numOfDocsIndexed);
            documentProportion[count] = (double) (indexBuilderFactory.searchTerm(e.getKey()).size()) / numOfDocsIndexed;
            count++;
        }

        for (double proportion : documentProportion) {
            System.out.println("Proportion : " + proportion);
        }

        System.out.println("totalMemoryRequirement : " + ((double) totalMemoryRequirement / (1024 * 1024)) + " MB");
    }

    public void printMetrics() {
        System.out.println("Term count : " + pIndex.keySet().size() + " : Type count : " + typeSet.size());
    }
}
