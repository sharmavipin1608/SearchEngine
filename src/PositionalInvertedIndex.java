
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
    private HashMap<String,HashMap<Integer,List<Integer>>> pIndex;
    
    public PositionalInvertedIndex(){
        pIndex = new HashMap<String,HashMap<Integer,List<Integer>>>();
    }
    
    public void addTerm(String term, int documentId, int position){
        List<Integer> positionList = new ArrayList<Integer>();
        HashMap<Integer,List<Integer>> docIdMap = new HashMap<Integer,List<Integer>>();
        if(pIndex.containsKey(term)){
            docIdMap = pIndex.get(term);
            if(docIdMap.containsKey(documentId)){
                positionList = docIdMap.get(documentId);
                positionList.add(position);
            }
            else{
                positionList.add(position);
                docIdMap.put(documentId,positionList);
            }
        }
        else{
            positionList.add(position);
            docIdMap.put(documentId,positionList);
            pIndex.put(term, docIdMap);
        }
    }
    
    public HashMap<Integer,List<Integer>> getPostings(String term){
        HashMap<Integer,List<Integer>> docIdMap = pIndex.get(term);
        return docIdMap;
    }
    
    public void printResults(List<String> fileNames){
        int longestTerm = 0;
        String[] termsArray = pIndex.keySet().toArray(
                new String[pIndex.keySet().size()]);
        Arrays.sort(termsArray);
        for(String term : termsArray)
            longestTerm = Math.max(longestTerm, term.length());
            
        for(String term : termsArray){
            System.out.print("\n" + term);
            printSpaces(longestTerm - term.length());
            //System.out.print(":");
            
            HashMap<Integer,List<Integer>> docIdMap = pIndex.get(term);
            Integer[] docIdArray = docIdMap.keySet().toArray(new Integer[docIdMap.keySet().size()]);
            Arrays.sort(docIdArray);
            
            for(Integer docId : docIdArray){
                List<Integer> posIndexes = docIdMap.get(docId);
                System.out.print(" : "+fileNames.get(docId) + " -> ");
                for(Integer posIndex : posIndexes){
                    System.out.print(posIndex + " , ");
                }
                System.out.println();
                printSpaces(longestTerm);
                //System.out.print(":");
            }
            
            
//            for(Integer docIndex : pIndex.get(term).keySet())
//                System.out.print("  "+fileNames.get(docIndex));
        }
    }
    
    // prints a bunch of spaces
    private static void printSpaces(int spaces) {
        for (int i = 0; i < spaces; i++) {
            System.out.print(" ");
        }
    }
}
