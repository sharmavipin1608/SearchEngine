
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vipinsharma
 */
public class SearchEngineUtilities {

    public static void printSpaces(int spaces) {
        for (int i = 0; i < spaces; i++) {
            System.out.print(" ");
        }
    }
    
//    public static String hyphenation(String input) {
//        input = input.replaceAll("-", "hyphen");
//        input = input.replaceAll("\\W", "");
//        input = input.replaceAll("hyphen", "-");
//        input = input.toLowerCase();
//        return input;
//    }
    
    public static List<PositionalPostingsStructure> positionalSearch(
        List<PositionalPostingsStructure> list1, List<PositionalPostingsStructure> list2){
        int pointer1 = 0, pointer2 = 0;
        //int docId1 = -1, docId2 = -1;
        List<PositionalPostingsStructure> resultList = 
                new ArrayList<>();
        
        while(true){
            int docId1 = list1.get(pointer1).getDocumentId();
            int docId2 = list2.get(pointer2).getDocumentId();
            
            if(docId1 == docId2){
                boolean flag = compareList(list1.get(pointer1).getPositionList(),
                        list2.get(pointer2).getPositionList());
                if(flag)
                    resultList.add(list2.get(pointer2));
                pointer1++;
                pointer2++;
            }
            else if(docId1 > docId2){
                pointer2++;
            }
            else if(docId1 < docId2){
                pointer1++;
            }
            if(pointer1 >= list1.size() || pointer2 >= list2.size())    
                break;
        }
        return resultList;
    }
    
    public static boolean compareList(List<Integer> list1, List<Integer> list2){
        int pointer1 = 0, pointer2 = 0;
        
        while(true){
            int position1 = list1.get(pointer1);
            int position2 = list2.get(pointer2);
            if(position2 == position1+1){
                return true;
            }
            else if(position2 > position1+1){
                pointer1++;
            }
            else if(position2 < position1){
                pointer2++;
            }
            
            if(pointer1 == list1.size() || pointer2 == list2.size())    
                break;
        }
        return false;
    }
    
    public static HashSet<Integer> convertListToSet(List<PositionalPostingsStructure> list){
        if(list != null){
            HashSet<Integer> documentSet = new HashSet<Integer>();
            for(PositionalPostingsStructure positionalPosting : list){
                documentSet.add(positionalPosting.getDocumentId());
            }
            return documentSet;
        }
        else
            return null;
    }
}
