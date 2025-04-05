package com.dyma.game;

import java.util.ArrayList;
import java.util.List;
/*
 * Class test
 */
public class GuessGame {
    private final List<Character> secretWord = new ArrayList<>();
    private int lifePoints;
    private final List<Character> guessWord = new ArrayList<>();

    public GuessGame (String wordToGuess, int lifePoints ){
        for( char c :  wordToGuess.toCharArray()){
            secretWord.add(c);
        }
        this.lifePoints = lifePoints;
        for(char c : wordToGuess.toCharArray()){
            guessWord.add('_');
        }
    }
    @Override
    public String toString() {
        return "GuessGame{" +
                ", lifePoints=" + lifePoints +
                ", guessWord=" + guessWord +
                '}';
    }


    public void guessLetter(char letter) {
        if(secretWord.contains(letter) && !guessWord.contains(letter)){
            for (int i = 0; i <secretWord.size(); i++){
               if(secretWord.get(i) == letter){
                   guessWord.set(i,letter);
               }
            }
        }else {
            lifePoints--;
        }
    }

    public boolean isWin() {
        return !guessWord.contains('_');
    }

    public boolean isLoss() {

        return lifePoints ==0;
    }
    public void reset(){
        lifePoints = 10;
    }
}
