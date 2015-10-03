package com.java.searchengine.datastructure;

import com.java.searchengine.main.IndexBuilderFactory;
import com.java.searchengine.util.SearchEngineUtilities;
import java.util.ArrayList;
import java.util.List;

/**
 * This class defines the data structure being used to save document id along
 * with the positional information of the terms
 */
public class PositionalPostingsStructure {
    //stores the document id of the term
    private int docId;
    
    //stores the positions term is present in a particular document id
    private List<Integer> positionList;

    /**
     * Constructor for the class being used to initialize the attributes of 
     * the class and set the value of the document id
     * 
     * @param documentId
     * @param position
     */
    public PositionalPostingsStructure(int documentId, int position) {
        docId = documentId;
        positionList = new ArrayList<>();
        positionList.add(position);
    }

    /**
     * Get the document id of the term
     * 
     * @return document id
     */
    public int getDocumentId() {
        return docId;
    }

    /**
     * Get the document name of the term
     * 
     * @return document name 
     */
    public String getDocumentName() {
        return IndexBuilderFactory.getInstance().getFilesNames(docId);
    }

    /**
     * Get the position list of a particular term in the current document
     * 
     * @return list of positions
     */
    public List<Integer> getPositionList() {
        return positionList;
    }

    /**
     *
     * @param position
     */
    public void addPosition(int position) {
        positionList.add(position);
    }

    /**
     * Prints document name along with the positional information of the term
     * 
     * @param longestTerm - longest term in the index
     */
    public void printData(int longestTerm) {
        SearchEngineUtilities.printSpaces(longestTerm);
        System.out.print(" : " + IndexBuilderFactory.getInstance().getFilesNames(docId) + " -> ");
        for (int i = 0; i < positionList.size(); i++) {
            System.out.print(positionList.get(i) + ", ");
        }
    }
}
