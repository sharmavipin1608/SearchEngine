
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
    private HashMap<String,List<PositionalPostingsStructure>> pIndex;
    
    public PositionalInvertedIndex(){
        pIndex = new HashMap<String,List<PositionalPostingsStructure>>();
    }
    
    public void addTerm(String term, int documentId, int position){
        List<Integer> positionList = new ArrayList<Integer>();
        HashMap<Integer,List<Integer>> docIdMap = new HashMap<Integer,List<Integer>>();
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
        if(pIndex.containsKey(term)){
            List<PositionalPostingsStructure> termPostingsList = 
                    pIndex.get(term);
            if(termPostingsList.get(termPostingsList.size()-1).getDocumentId() 
                    == documentId){
                termPostingsList.get(termPostingsList.size()-1)
                        .addPosition(position);
            }
            else{
                PositionalPostingsStructure termPostings = 
                    new PositionalPostingsStructure(documentId,position);
                termPostingsList.add(termPostings);
            }
        }
        else{
            List<PositionalPostingsStructure> termPostingsList = 
                    new ArrayList<PositionalPostingsStructure>();
            PositionalPostingsStructure termPostings = 
                    new PositionalPostingsStructure(documentId,position);
            termPostingsList.add(termPostings);
            pIndex.put(term, termPostingsList);
        }
    }
    
    public List<PositionalPostingsStructure> getPostings(String term){
        return pIndex.get(term);
    }
    
    public void printResults(){
        int longestTerm = 0;
        String[] termsArray = pIndex.keySet().toArray(
                new String[pIndex.keySet().size()]);
        Arrays.sort(termsArray);
        for(String term : termsArray)
            longestTerm = Math.max(longestTerm, term.length());
            
        for(String term : termsArray){
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
            for(PositionalPostingsStructure posStructure : pIndex.get(term)){
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
}
