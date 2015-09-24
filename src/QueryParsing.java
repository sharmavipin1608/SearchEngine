
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
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
public class QueryParsing {
    
    List<PositionalPostingsStructure> resultantPostings = 
            new ArrayList<PositionalPostingsStructure>();
    
    private static final Pattern sQuotes = Pattern.compile("\"(.*)\"");
    //private static final Pattern eQuotes = Pattern.compile("\"$");
    
    public static void main(String[] args){
        
        
        final Path currentWorkingPath = Paths.get("").toAbsolutePath();
        
        IndexBuilderFactory.createIndex(currentWorkingPath);
        IndexBuilderFactory.getInstance().getPositionalIndex().printResults();
        
        while(true){
            System.out.print("Search phrase : ");
            Scanner input = new Scanner(System.in);
            String userInput = input.nextLine();
            userInput = userInput.toLowerCase();

            if(userInput.equalsIgnoreCase("quit"))
                break;
            HashSet<Integer> resultSet = IndexBuilderFactory.getInstance().queryProcessing(userInput);

            System.out.println("Query result documents : ");
            if(resultSet != null && resultSet.size() > 0){
                for(Integer docId : resultSet){
                    System.out.println(IndexBuilderFactory.getInstance().getFilesNames(docId));
                }
            }
            else
                System.out.println("No matches found");
        }
//        Matcher phasedQueryMatch = sQuotes.matcher(userInput);
//        
//        if(phasedQueryMatch.find())// &&
//        {
//            String str = phasedQueryMatch.group(1);
//            
//            System.out.println("in here" + str);
//            IndexBuilderFactory.getInstance().searchQuery(str);
//        }
                //eQuotes.matcher(userInput).find())
//            System.out.println("match found" + sQuotes.matcher(userInput).group(1));
//        String[] queryParts = userInput.split(" ");
//        if(queryParts.length > 1){
//            
//        }
    }
    
    
}
