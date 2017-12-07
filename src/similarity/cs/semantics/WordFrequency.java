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
    int frequency;
    public WordFrequency(String word, int frequency){
        this.word = word;
        this.frequency = frequency;
    }
    
    @Override
    public int compareTo(WordFrequency otherWord){
        return otherWord.frequency-this.frequency;
    }
}
