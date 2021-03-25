package scrabble.model;

import java.util.ArrayList;

public class Word {
	private String word;
	private ArrayList<LetterPosition> wordPosition;
	
	
	public Word(ArrayList<LetterPosition> position) {
		this.wordPosition = position;
	}
	
	public void setWord(String word) {
		this.word = word;
	}

	public String getWord() {
		return word;
	}

	public ArrayList<LetterPosition> getWordPosition() {
		return wordPosition;
	}
	
}
