/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package similarity.cs.semantics;
import java.util.*;
/**
 *
 * @author Samanvoy
 */
class WordFrequency implements Comparable<WordFrequency> {
    String word;
    double frequency;
    public WordFrequency(String word, double frequency){
        this.word = word;
        this.frequency = frequency;
    }
    
    @Override
    public int compareTo(WordFrequency otherWord){
        if(this.frequency < otherWord.frequency) return 1;
        if(this.frequency > otherWord.frequency) return -1;
        return 0;
    }
}
