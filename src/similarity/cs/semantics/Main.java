/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package similarity.cs.semantics;

/**
 *
 * @author ragha
 */
import org.apache.commons.cli.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import opennlp.tools.stemmer.*;

public class Main {
    //Stop words
    static ArrayList<String> stopWords = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException{
        //System.out.println("Hi Sam");
        Options options = new Options();
        options.addRequiredOption("f", "file", true, "input file to process");
        options.addOption("h", false, "print this help message");

        CommandLineParser parser = new DefaultParser();

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            new HelpFormatter().printHelp("Main", options, true);
            System.exit(1);
        }

        String filename = cmd.getOptionValue("f");
	if (!new File(filename).exists()) {
            System.err.println("file does not exist "+filename);
            System.exit(1);
	}
        //Populating stop words in ArrayList
        BufferedReader br = null;
        try{
            br = new BufferedReader(new FileReader("C:\\Users\\ragha\\OneDrive\\Documents\\NetBeansProjects\\Similarity\\stopwords.txt"));
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                stopWords.add( sCurrentLine.trim());
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(br!=null)
                    br.close();
                }
            catch(IOException io){
                io.printStackTrace();
            }
        }
                
        Scanner sc = new Scanner(new File(filename)).useDelimiter("(\\.|\\?|\\!)");
        ArrayList<String[]> words = new ArrayList<>();
        while(sc.hasNext()){
            String[] temp = cleanUpAndStem(sc.next().trim());
            words.add(temp);
        }
              
        for(String[] word: words){
            for(String str: word){
                System.out.print(str+",");
            }
            System.out.println();
        }
                
        if (cmd.hasOption("h")) {
            HelpFormatter helpf = new HelpFormatter();
            helpf.printHelp("Main", options, true);
            System.exit(0);
        }
        
        System.out.println(filename);
        
        
        
    }
    
    public static String[] cleanUpAndStem(String sent){
        String arr[] = sent.toLowerCase().replaceAll("\n"," ").replaceAll(","," ").split(" ");
        String unique[]= Arrays.stream(arr).distinct().toArray(String[]::new);
        ArrayList<String> result = new ArrayList<>();
        PorterStemmer ptr = new PorterStemmer();
        for(String str: unique){
            if(!stopWords.contains(str) && !str.isEmpty()){
                String temp = ptr.stem(str);
                result.add(temp);
            }
        }
        return result.toArray(new String[0]);
    }
    
}
  