/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package similarity.cs.semantics;


public class Pairs implements Comparable<Pairs> {
    String word;
    double score;
    public Pairs(String word, double score){
        this.word = word;
        this.score = score;
    }
    
    @Override
    public int compareTo(Pairs otherWord){
        if(this.score < otherWord.score) return 1;
        if(this.score > otherWord.score) return -1;
        return 0;
    }
}
