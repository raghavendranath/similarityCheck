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
/*
public class Another {
    //Stop words
    static ArrayList<String> stopWords = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException{
        //System.out.println("Hi Sam");
        Options options = new Options();
        options.addRequiredOption("f", "file", true, "input file to process");
        options.addOption("h", false, "print this help message");
        options.addOption("s", false, "option for sentences");
        options.addOption("v", false, "option for vectors");
        options.addOption("t", "tValues", true, "word,integer to process");
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
            br = new BufferedReader(new FileReader("stopwords.txt"));
            //br = new BufferedReader(new FileReader("C:\\Users\\ragha\\OneDrive\\Documents\\NetBeansProjects\\Similarity\\stopwords.txt"));
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
           
        Another obj = new Another();
        Scanner sc = new Scanner(new File(filename)).useDelimiter("(\\.|\\?|\\!)");
        List<List<String>> allWords = new ArrayList<List<String>>();
        
        //ArrayList<String[]> words = new ArrayList<>();
        while(sc.hasNext()){
            String temp = sc.next();
            ArrayList<String> wordsInASentence = obj.cleanUpAndStem(temp.trim());
            allWords.add(wordsInASentence);
        }
        
        //For printing sentences
        if(cmd.hasOption("s")){
            for(int i=0;i<allWords.size();i++){
                List wordsInASentence = allWords.get(i);
                System.out.print("[");
                try{ Thread.sleep(1000); 
                }catch (Exception e){
                    System.err.println(e);
                }
                for(int j=0;j<wordsInASentence.size();j++){
                    System.out.print(wordsInASentence.get(j)+",");
                }
                System.out.print("]");
                System.out.println();
             }
        }
        
        //Step 5 testing - remove below comment to test
        obj.testTaskFive();
        
        //1TreeMap<String, List<WordFrequency>> allVectors = obj.makeVectors(allWords);
        List<Vector> allVectors = obj.makeVectors(allWords);
        
        //To print the descriptors
        if(cmd.hasOption("v")){
            obj.printVectors(allVectors);
        }
        
       if(cmd.hasOption("t")){
           String arguments[] = cmd.getOptionValue("t").trim().split(",");
           String queryWord = arguments[0];
           int num = Integer.parseInt(arguments[1]);
           PorterStemmer ptr = new PorterStemmer();
           queryWord = ptr.stem(queryWord);
           boolean containsWord = false;
           int index = -1;
           for(int i=0;i<allVectors.size();i++){
               if(allVectors.get(i).keyWord.equals(queryWord)){
                   containsWord = true;
                   index = i;
                   break;
               }
           }
            if(!containsWord)
                System.out.println("Cannot compute top-J similarity to "+queryWord);
           else{
                String measureToUse = "";
                if(cmd.hasOption("m")){
                    arguments = cmd.getOptionValue("m").trim().split(",");
                    measureToUse = arguments[0];
                }
                List<Pairs> results =  obj.computeScores(queryWord, allVectors, index, measureToUse);
                obj.printScores(results,num);
            }
       } 

       //task 6
       int k = 10; int iter = 10;
       obj.generateKMeans(allVectors, k, iter);
       
        if (cmd.hasOption("h")) {
            HelpFormatter helpf = new HelpFormatter();
            helpf.printHelp("Main", options, true);
            System.exit(0);
        }
        
        //System.out.println(filename);     
    }
    
    
    public ArrayList<String> cleanUpAndStem(String sent){
        //String arr[] = sent.toLowerCase().replaceAll("\n"," ").replaceAll(","," ").replaceAll("\\“"," ").replaceAll("\\”"," ").replaceAll("\\:"," ").replaceAll("\\*"," ").replaceAll("\\-"," ").replaceAll("\\["," ").replaceAll("\\]", " ").replaceAll("\\("," ").replaceAll("\\)", " ").split(" ");
        String arr[] = sent.toLowerCase().replaceAll("\n"," ").replaceAll("[^a-zA-Z0-9’'‘ÆÐƎƏƐƔĲŊŒẞÞǷȜæðǝəɛɣĳŋœĸſßþƿȝĄƁÇĐƊĘĦĮƘŁØƠŞȘŢȚŦŲƯY̨Ƴąɓçđɗęħįƙłøơşșţțŧųưy̨ƴÁÀÂÄǍĂĀÃÅǺĄÆǼǢƁĆĊĈČÇĎḌĐƊÐÉÈĖÊËĚĔĒĘẸƎƏƐĠĜǦĞĢƔáàâäǎăāãåǻąæǽǣɓćċĉčçďḍđɗðéèėêëěĕēęẹǝəɛġĝǧğģɣĤḤĦIÍÌİÎÏǏĬĪĨĮỊĲĴĶƘĹĻŁĽĿʼNŃN̈ŇÑŅŊÓÒÔÖǑŎŌÕŐỌØǾƠŒĥḥħıíìiîïǐĭīĩįịĳĵķƙĸĺļłľŀŉńn̈ňñņŋóòôöǒŏōõőọøǿơœŔŘŖŚŜŠŞȘṢẞŤŢṬŦÞÚÙÛÜǓŬŪŨŰŮŲỤƯẂẀŴẄǷÝỲŶŸȲỸƳŹŻŽẒŕřŗſśŝšşșṣßťţṭŧþúùûüǔŭūũűůųụưẃẁŵẅƿýỳŷÿȳỹƴźżžẓ]"," " ).split(" ");

        //String unique[]= Arrays.stream(arr).distinct().toArray(String[]::new);
        ArrayList<String> result = new ArrayList<>();
        PorterStemmer ptr = new PorterStemmer();
        for(String str: arr){
            str = str.trim();
            //to remove including numbers in the vector
            if(str.matches(".*\\d+.*"))
                continue;
            if(!stopWords.contains(str) && !str.isEmpty()){
                String temp = ptr.stem(str);
                //System.out.println("Actual String:"+str+"   Stemmed string:"+temp+" StopWords:"+stopWords.contains(str));
                result.add(temp);
            }
        }
        //return result.toArray(new String[0]);
          return result;
    }
    
    public List<Vector> makeVectors(List<List<String>> words){
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
        
        //1TreeMap<String, List<WordFrequency>> allVectors = new TreeMap<String, List<WordFrequency>>();
        List<Vector> allVectors = new ArrayList<>();
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
                            if(!treeMap.containsKey(word))
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
                        if(!treeMap.containsKey(word))
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
            allVectors.add(new Vector(currentWord, vector));
        }
        return allVectors;
    }
    
    public void printVectors(List<Vector> allVectors) {
        //1Iterator it = allVectors.entrySet().iterator();
        String word;
        List<WordFrequency> wordsWithFrequencies;
        PrintWriter writer = null;
        //int count = 0;
        try{
            //writer = new PrintWriter("C:\\Users\\Samanvoy\\Documents\\NetBeansProjects\\similarityCheck\\output.txt");
            writer = new PrintWriter("output2.txt");
            //writer = new PrintWriter("C:\\Users\\ragha\\OneDrive\\Documents\\NetBeansProjects\\Similarity\\output.txt");
            for(int i=0;i<allVectors.size();i++) {
                //1Map.Entry pair = (Map.Entry)it.next();
                Vector v = allVectors.get(i);
                word = v.keyWord;
                wordsWithFrequencies = v.listOfWordFrequencies;
                System.out.print(word+": [ ");
                //writer.append(word+": [ ");
                for(WordFrequency wordWithFrequency: wordsWithFrequencies){
                    System.out.print(wordWithFrequency.word+"="+wordWithFrequency.frequency+" ");
                    //writer.append(wordWithFrequency.word+"="+wordWithFrequency.frequency+" ");
                }
                System.out.println("]");
                //writer.append("]");
                //writer.append("\n");
                //if(count++==30) break;
            }
        }
        catch(Exception e){
            System.err.println(e.getMessage());
        }
        finally{
            if(writer!=null) writer.close();            
        }
    }
    
        
     //For computing the scores
     public List<Pairs> computeScores(String queryWord, List<Vector> allVectors, int index, String measureToUse){
        //1Iterator it = allVectors.entrySet().iterator();
        List<WordFrequency> queryVectorValue = allVectors.get(index).listOfWordFrequencies;
        String word;
        List<Pairs> wordsWithScores = new ArrayList<>();
        List<WordFrequency> otherVectorValue;
        Pairs temp ;
        double result;
        //1while (it.hasNext()) {
        for(int i=0;i<allVectors.size();i++) {
            //1Map.Entry pair = (Map.Entry)it.next(); 
            Vector v = allVectors.get(i);
            word = v.keyWord;
            if(word.equals(queryWord))
                continue;
            otherVectorValue = v.listOfWordFrequencies;
            switch(measureToUse){
                case "euc":
                    result = euclideanDistance(queryVectorValue, otherVectorValue);
                    break;
                case "eucnorm":
                    result = euclideanDistanceNorm(queryVectorValue, otherVectorValue);
                    break;
                default:
                    result = cosineSimilarity(queryVectorValue, otherVectorValue);
            }
            temp = new Pairs(word, result);
            temp.word = word;
            temp.score = result;
            wordsWithScores.add(temp);
        }
        Collections.sort(wordsWithScores);
        return wordsWithScores;
     }
     
     //Cosine similarity
     public double cosineSimilarity(List<WordFrequency> u, List<WordFrequency> v){
         //U and V same size
         double sumUV = 0;
         double sumUsquare = 0;
         double sumVSquare = 0;
         WordFrequency uTemp = null;
         WordFrequency vTemp = null;
         for(int i=0; i< u.size();i++){
             uTemp = u.get(i);
             vTemp = v.get(i);
             sumUV = sumUV + uTemp.frequency*vTemp.frequency;
             sumUsquare = sumUsquare+Math.pow(uTemp.frequency,2);
             sumVSquare = sumVSquare+Math.pow(vTemp.frequency,2);
         }
         double result = (sumUV)/Math.sqrt(sumUsquare*sumVSquare);
         return result;
     }
     
     //Step 5
     //Euclidean Distance
     public double euclideanDistance(List<WordFrequency> u, List<WordFrequency> v){
         double[] one = convertWFListsToArrays(u);
         double[] two = convertWFListsToArrays(v);
         return euclideanDistance(one, two);
     }
     
     public double[] convertWFListsToArrays(List<WordFrequency> input){
         double[] result = new double[input.size()];
         for(int i=0; i< input.size();i++){
             result[i] = input.get(i).frequency;
         }
         return result;
     }
     
     public double euclideanDistance(double[] u, double[] v){
         double result = 0;
         double sumOfSquares = 0;
         for(int i=0; i< u.length;i++){
             //System.out.println("x: "+u[i]+" - y: "+v[i]);
             sumOfSquares += (u[i]-v[i])*(u[i]-v[i]);
             //System.out.println("sumOfSquares: "+sumOfSquares);
         }
         result = -Math.sqrt(sumOfSquares);
         return result;

     }
     
     public double euclideanDistanceNorm(List<WordFrequency> u, List<WordFrequency> v){
         double[] one = normalize(u); 
         double[] two = normalize(v);
         return euclideanDistance(one, two);
     }
     
     public double[] normalize(List<WordFrequency> input){
         double sumOfSquares = 0;
         for(int i=0; i< input.size();i++){
             WordFrequency current = input.get(i);
             sumOfSquares += current.frequency*current.frequency;
         }
         double denominator = Math.sqrt(sumOfSquares);
         double[] result = new double[input.size()];
         for(int i=0; i< input.size();i++){
             WordFrequency current = input.get(i);
             result[i] = (double)current.frequency/denominator;
             //System.out.println("result["+i+"]: "+result[i]);
         }
         return result;
     }
     
     public void printScores(List<Pairs> pairs, int n){
         Pairs temp;
         System.out.println();
         System.out.print("[");
         for(int i=0; i<n && i< pairs.size(); i++){
             temp = pairs.get(i);
             System.out.print("Pair{"+temp.word+","+temp.score+"}, ");
         }
         System.out.print("]");
     }
    
     public void testTaskFive(){
        List<WordFrequency> u = new ArrayList<WordFrequency>();
        List<WordFrequency> v = new ArrayList<WordFrequency>();
        u.add(new WordFrequency("a", 1)); u.add(new WordFrequency("b", 4)); u.add(new WordFrequency("c", 1)); u.add(new WordFrequency("d", 0));
        u.add(new WordFrequency("e", 0)); u.add(new WordFrequency("f", 0));
        v.add(new WordFrequency("a", 3)); v.add(new WordFrequency("b", 0)); v.add(new WordFrequency("c", 0)); v.add(new WordFrequency("d", 1));
        v.add(new WordFrequency("e", 1)); v.add(new WordFrequency("f", 2));
        System.out.println("Euclidean distance: "+euclideanDistance(u, v));
        
        System.out.println("Euclidean distance with normalized vectors: "+euclideanDistanceNorm(u, v));
     }
     
     
     //task 6
     public void generateKMeans(List<Vector> allVectors, int k, int iter){
         //generate random k means
         Vector[] means = new Vector[k];
         //Random r = new Random();
         //r.nextInt(allVectors);
     }
}
    */