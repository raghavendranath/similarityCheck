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
        options.addOption("s", false, "option for sentences");
        options.addOption("v", false, "option for vectors");
        options.addOption("t", "tValues", true, "word,integer to process");
        options.addOption("k", "kMeans", true, "integer,integer,[integer] to process");
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
           
        Main obj = new Main();
        Scanner sc = new Scanner(new File(filename)).useDelimiter("(\\.|\\?|\\!)");
        List<List<String>> allWords = new ArrayList<List<String>>();
        
        //ArrayList<String[]> words = new ArrayList<>();
        while(sc.hasNext()){
            String temp = sc.next();
            //System.out.println(temp);
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
        //obj.testTaskFive();
        
        TreeMap<String, List<WordFrequency>> allVectors = obj.makeVectors(allWords);
        
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
            if(!allVectors.containsKey(queryWord))
                System.out.println("Cannot compute top-J similarity to "+queryWord);
           else{
                String measureToUse = "";
                if(cmd.hasOption("m")){
                    arguments = cmd.getOptionValue("m").trim().split(",");
                    measureToUse = arguments[0];
                }
                List<Pairs> results =  obj.computeScores(queryWord, allVectors, measureToUse);
                obj.printScores(results,num);
            }
        } 

        //task 6
        if(cmd.hasOption("k")){
           String arguments[] = cmd.getOptionValue("k").trim().split(",");
           if(arguments.length<2){
                System.err.println("Must provide atleast 2 argumets with the option k");
           }
           else{
                int k = Integer.parseInt(arguments[0]);
                int iter = Integer.parseInt(arguments[1]);
                int j = -1;
                if(arguments.length==3)
                    j = Integer.parseInt(arguments[2]);
                obj.generateKMeans(allVectors, k, iter, j);               
           }
        }
        
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
            allVectors.put(currentWord, vector);
        }
        return allVectors;
    }
    
    public void printVectors(TreeMap<String, List<WordFrequency>> allVectors) {
        Iterator it = allVectors.entrySet().iterator();
        String word;
        List<WordFrequency> wordsWithFrequencies;
        PrintWriter writer = null;
        //int count = 0;
        try{
            //writer = new PrintWriter("C:\\Users\\Samanvoy\\Documents\\NetBeansProjects\\similarityCheck\\output.txt");
            writer = new PrintWriter("output2.txt");
            //writer = new PrintWriter("C:\\Users\\ragha\\OneDrive\\Documents\\NetBeansProjects\\Similarity\\output.txt");
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next(); 
                word = (String) pair.getKey();
                wordsWithFrequencies = (List<WordFrequency>) pair.getValue();
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
     public List<Pairs> computeScores(String queryWord, TreeMap<String, List<WordFrequency>> allVectors, String measureToUse){
        //Iterator it = allVectors.entrySet().iterator();
        List<WordFrequency> queryWF = allVectors.get(queryWord);
        Vector[] vectorList = convertTreeMapToVectors(allVectors);
        return computeScores(queryWF, Arrays.asList(vectorList), measureToUse, queryWord, true);
        /*String word;
        List<Pairs> wordsWithScores = new ArrayList<>();
        List<WordFrequency> otherWF;
        Pairs temp ;
        double result;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next(); 
            word = (String) pair.getKey();
            if(word.equals(queryWord))
                continue;
            otherWF = (List<WordFrequency>) pair.getValue();
            switch(measureToUse){
                case "euc":
                    result = euclideanDistance(queryWF, otherWF);
                    break;
                case "eucnorm":
                    result = euclideanDistanceNorm(queryWF, otherWF);
                    break;
                default:
                    result = cosineSimilarity(queryWF, otherWF);
            }
            temp = new Pairs(word, result);
            temp.word = word;
            temp.score = result;
            wordsWithScores.add(temp);
        }
        Collections.sort(wordsWithScores);
        return wordsWithScores;*/
     }
     
     public List<Pairs> computeScores(List<WordFrequency> queryWF, List<Vector> allVectors , String measureToUse, String queryWord, boolean shouldUseWord){
        List<Pairs> wordsWithScores = new ArrayList<>();
        List<WordFrequency> otherWF;
        String word;
        Pairs temp ;
        double result;
        for(Vector vector : allVectors){
            word = vector.keyWord;
            if(shouldUseWord && word.equals(queryWord))
                continue;
            otherWF = vector.listOfWordFrequencies;
            switch(measureToUse){
                case "euc":
                    result = euclideanDistance(queryWF, otherWF);
                    break;
                case "eucnorm":
                    result = euclideanDistanceNorm(queryWF, otherWF);
                    break;
                default:
                    result = cosineSimilarity(queryWF, otherWF);
            }
            temp = new Pairs(word, result);
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
     public void generateKMeans(TreeMap<String, List<WordFrequency>> allVectors, int k, int iter, int j){
         //generate random k means
         Vector[] allVectorsArray = convertTreeMapToVectors(allVectors);
         Vector[] means = new Vector[k];
         Random rand = new Random();
         for(int i=0;i<k;i++){
             means[i] = allVectorsArray[rand.nextInt(allVectors.size())];
         }
         
         List<List<Vector>> clusters = null;
         int closestMeanIndex = 0;
         double minDistance = Integer.MAX_VALUE;
         double euclideanDistance;
         List<Vector> cluster;
         for(int i=0;i<iter;i++){
             //create empty clusters
             clusters = new ArrayList<List<Vector>>(k);
             for(int b=0;b<k;b++){
                clusters.add(new ArrayList<Vector>());
             }
             //put each point in the corresponding cluster
             double[] avgDistances = new double[k];
             for(Vector v : allVectorsArray){
                 closestMeanIndex = 0;
                 minDistance = Integer.MAX_VALUE;
                 for(int b=0;b<k;b++){
                     euclideanDistance = euclideanDistance(v.listOfWordFrequencies, means[b].listOfWordFrequencies)*-1;
                     if(euclideanDistance < minDistance){
                         closestMeanIndex = b;
                         minDistance = euclideanDistance;
                     }
                 }
                 avgDistances[closestMeanIndex] += minDistance;
                 clusters.get(closestMeanIndex).add(v);
             }
             
             for(int b=0;b<avgDistances.length;b++){
                 avgDistances[b] /= clusters.get(b).size();
             }
             
             //calculate new means
             for(int b=0;b<k;b++){
                 cluster = clusters.get(b);
                 if(cluster.size()==0)
                     continue;
                 Vector newMean = cluster.get(0);
                 int sizeOfCluster = newMean.listOfWordFrequencies.size();
                 Vector currentVector;
                 List<WordFrequency> curWordFrequencies, newMeanWordFrequencies;
                 newMeanWordFrequencies = newMean.listOfWordFrequencies;
                 for(int a=1;a<cluster.size();a++){
                    currentVector = cluster.get(1);
                    curWordFrequencies = currentVector.listOfWordFrequencies;
                    for(int c=0;c<curWordFrequencies.size();c++){
                        newMeanWordFrequencies.get(c).frequency += curWordFrequencies.get(c).frequency;
                    }
                 }
                 
                 for(int a=0;a<newMeanWordFrequencies.size();a++){
                    newMeanWordFrequencies.get(a).frequency /= sizeOfCluster;
                 }
                 means[b].listOfWordFrequencies = newMeanWordFrequencies;
             }
         }
         if(j!=-1){
             doStep7(means, clusters, j);
         }
         else{
             printClusters(clusters);
         }
     }
     
     public void doStep7(Vector[] means, List<List<Vector>> clusters, int j){
         List<Vector> cluster;
         List<List<Pairs>> allPairs = new ArrayList<List<Pairs>>();
         for(int i=0;i<clusters.size();i++){
             cluster = clusters.get(i);
             allPairs.add(computeScores(means[i].listOfWordFrequencies, cluster, "cos", "", false));
         }
         List<Pairs> pairList;
         for(int i=0;i<allPairs.size();i++){
             pairList = allPairs.get(i);
             System.out.println("Cluster: "+i);
             for(int b=0;b<j;b++){
                 System.out.print(pairList.get(b).word+", ");
             }
             System.out.println();
         }
     }
     
     public Vector[] convertTreeMapToVectors(TreeMap<String, List<WordFrequency>> allVectors){
         Vector[] allVectorsArray = new Vector[allVectors.size()];
         int i=0;
         Iterator it = allVectors.entrySet().iterator();
         Map.Entry vector;
         String key;
         List<WordFrequency> vectorValue;
         while(it.hasNext()){
             vector = (Map.Entry)it.next();
             key = (String) vector.getKey();
             vectorValue = (List<WordFrequency>) vector.getValue();
             allVectorsArray[i++] = new Vector(key, vectorValue);
         }
         return allVectorsArray;
     }
     
     public void printClusters(List<List<Vector>> clusters){
         int index = 0;
         for(List<Vector> cluster: clusters) {
             System.out.println("Cluster "+index++);
             for(Vector v : cluster){
                 System.out.print(v.keyWord+", ");
             }
             System.out.println();
         }
     }
}
    