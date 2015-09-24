
import java.util.ArrayList;
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
public class PositionalPostingsStructure {
    private int docId;
    private List<Integer> positionList;
    
    public PositionalPostingsStructure(int documentId, int position){
        docId = documentId;
        positionList = new ArrayList<Integer>();
        positionList.add(position);
    }
    
    public int getDocumentId(){
        return docId;
    }
    
    public String getDocumentName(){
        return IndexBuilderFactory.getInstance().getFilesNames(docId);
    }
    
    public List<Integer> getPositionList(){
        return positionList;
    }
    
    public void addDocumentId(int documentId){
        docId = documentId;
    }
    
    public void addPosition(int position){
        positionList.add(position);
    }
    
    public void printData(int longestTerm){
        SearchEngineUtilities.printSpaces(longestTerm);
        System.out.print(" : " + IndexBuilderFactory.getInstance().getFilesNames(docId) + " -> ");
        for(int i = 0; i < positionList.size(); i++){
            System.out.print(positionList.get(i) + ", ");
        }
    }
}
