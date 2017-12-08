/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package similarity.cs.semantics;

import java.util.List;

/**
 *
 * @author Samanvoy
 */
public class Vector {
    String keyWord;
    List<WordFrequency> listOfWordFrequencies;
    public Vector(String keyWord, List<WordFrequency> listOfWordFrequencies){
        this.keyWord = keyWord;
        this.listOfWordFrequencies = listOfWordFrequencies;
    }
}
