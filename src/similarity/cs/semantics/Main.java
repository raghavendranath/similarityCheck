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
import java.util.*;
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
            //br = new BufferedReader(new FileReader("C:\\Users\\Samanvoy\\Documents\\NetBeansProjects\\similarityCheck\\stopwords.txt"));
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
           
        Main obj = new Main();
        Scanner sc = new Scanner(new File(filename)).useDelimiter("(\\.|\\?|\\!)");
        List<List<String>> allWords = new ArrayList<List<String>>();
        
        //ArrayList<String[]> words = new ArrayList<>();
        while(sc.hasNext()){
            ArrayList<String> wordsInASentence = obj.cleanUpAndStem(sc.next().trim());
            allWords.add(wordsInASentence);
        }
              
        for(int i=0;i<allWords.size();i++){
            List wordsInASentence = allWords.get(i);
            for(int j=0;j<wordsInASentence.size();j++){
                System.out.print(wordsInASentence.get(j)+",");
            }
            System.out.println();
        }
        
        TreeMap<String, List<WordFrequency>> allVectors = obj.makeVectors(allWords);
        obj.printVectors(allVectors);
        
        
        String queryWord = "cat";
        PorterStemmer ptr = new PorterStemmer();
        queryWord = ptr.stem(queryWord);
        
        List<WordFrequency> queryVector = obj.makeQueryVector(queryWord, allWords);
        if(queryVector == null)
            System.out.println("Cannot compute top J similarity to Q");
        else{
            System.out.print(queryWord+": [");
            for(WordFrequency word: queryVector){
                System.out.print(word.word+"="+word.frequency+" ");
            }
            System.out.print("]");
            System.out.println();
            
        }
        
        
        
        if (cmd.hasOption("h")) {
            HelpFormatter helpf = new HelpFormatter();
            helpf.printHelp("Main", options, true);
            System.exit(0);
        }
        
        //System.out.println(filename);     
    }
    
    
    public ArrayList<String> cleanUpAndStem(String sent){
        String arr[] = sent.toLowerCase().replaceAll("\n"," ").replaceAll(","," ").replaceAll("\""," ").split(" ");
        //String unique[]= Arrays.stream(arr).distinct().toArray(String[]::new);
        ArrayList<String> result = new ArrayList<>();
        PorterStemmer ptr = new PorterStemmer();
        for(String str: arr){
            if(!stopWords.contains(str) && !str.isEmpty()){
                String temp = ptr.stem(str);
                //System.out.println("Actual String:"+str+"   Stemmed string:"+temp+" StopWords:"+stopWords.contains(str));
                result.add(temp);
            }
        }
        //return result.toArray(new String[0]);
          return result;
    }
    
    public TreeMap<String, List<WordFrequency>> makeVectors(List<List<String>> words){
        //make a hashset of unique words
        HashSet<String> uniqueWords = new HashSet<String>();
        //int count = 0;
        for(int i=0;i<words.size();i++){
            List wordsInASentence = words.get(i);
            for(int j=0;j<wordsInASentence.size();j++){
                String word = (String) wordsInASentence.get(j);
                //count++;
                uniqueWords.add(word);
            }
        }
        //System.out.println("count: "+count);
        
        TreeMap<String, List<WordFrequency>> allVectors = new TreeMap<String, List<WordFrequency>>();
        Iterator iterator = uniqueWords.iterator();
        while (iterator.hasNext()){
            String currentWord = (String) iterator.next();
            //System.out.println("Current word: "+currentWord+" ----------------");
            TreeMap<String, Integer> treeMap = new TreeMap<String, Integer>();
            //System.out.println("word: "+currentWord + " ");  
            for(int i=0;i<words.size();i++){
                List wordsInASentence = words.get(i);
                if(wordsInASentence.contains(currentWord)){
                    for(int j=0;j<wordsInASentence.size();j++){
                        String word = (String) wordsInASentence.get(j);
                        if(word.equals(currentWord)){
                            if(treeMap.containsKey(word))
                                continue;
                            else
                                treeMap.put(word, 0);
                            continue;
                        }
                        if(treeMap.containsKey(word)){
                            treeMap.put(word, treeMap.get(word)+1);
                        }
                        else{
                            treeMap.put(word, 1);
                        }
                    }
                }
                else{
                     for(int j=0;j<wordsInASentence.size();j++){
                        String word = (String) wordsInASentence.get(j);
                        if(treeMap.containsKey(word))
                            continue;
                        else
                            treeMap.put(word, 0);
                        
                     }
                    
                }
            }
            
            WordFrequency wordFrequency;
            List<WordFrequency> vector = new ArrayList<WordFrequency>();
            Iterator it = treeMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                //System.out.println(pair.getKey() + " = " + pair.getValue());
                wordFrequency = new WordFrequency( (String) pair.getKey(), (Integer) pair.getValue());
                vector.add(wordFrequency);
                it.remove(); // avoids a ConcurrentModificationException
            }
            //Collections.sort(vector);
            allVectors.put(currentWord, vector);
        }
        return allVectors;
    }
    
    public void printVectors(TreeMap<String, List<WordFrequency>> allVectors){
        Iterator it = allVectors.entrySet().iterator();
        String word;
        List<WordFrequency> wordsWithFrequencies;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next(); 
            word = (String) pair.getKey();
            wordsWithFrequencies = (List<WordFrequency>) pair.getValue();
            System.out.print(word+": [ ");
            for(WordFrequency wordWithFrequency: wordsWithFrequencies){
                System.out.print(wordWithFrequency.word+"="+wordWithFrequency.frequency+" ");
            }
            System.out.println("]");
        }
    }
    
     public List<WordFrequency> makeQueryVector(String query, List<List<String>> words){
         TreeMap<String, Integer> treeMap = new TreeMap<String, Integer>();
         //for couting if the word is present in a sentence. If none contains you return null;
         int presentCount = 0;
          for(int i=0;i<words.size();i++){
                List wordsInASentence = words.get(i);
                if(wordsInASentence.contains(query)){
                    for(int j=0;j<wordsInASentence.size();j++){
                        String word = (String) wordsInASentence.get(j);
                        if(word.equals(query)){
                            if(treeMap.containsKey(word))
                                continue;
                            else
                                treeMap.put(word, 0);
                            continue;
                        }
                        if(treeMap.containsKey(word)){
                            treeMap.put(word, treeMap.get(word)+1);
                        }
                        else{
                            treeMap.put(word, 1);
                        }
                    }
                }
                else{
                    presentCount++;
                     for(int j=0;j<wordsInASentence.size();j++){
                        String word = (String) wordsInASentence.get(j);
                        if(treeMap.containsKey(word))
                            continue;
                        else
                            treeMap.put(word, 0);
                        
                     }
                    
                }
          }
          if(presentCount == words.size())
              return null;
          WordFrequency wordFrequency;
          List<WordFrequency> vector = new ArrayList<WordFrequency>();
          Iterator it = treeMap.entrySet().iterator();
          while (it.hasNext()) {
              Map.Entry pair = (Map.Entry)it.next();
              //System.out.println(pair.getKey() + " = " + pair.getValue());
              wordFrequency = new WordFrequency( (String) pair.getKey(), (Integer) pair.getValue());
              vector.add(wordFrequency);
              it.remove(); // avoids a ConcurrentModificationException
          }
          //Collections.sort(vector);
          return vector;
          
     }
    
}
    