package scrabble.model;

public class Letter {
	private HumanPlayer owner; //should be replaced with player class
	private int letterValue;
	private char letter;
	
	
	public Letter(int letterValue, char letter) {
		this.letter = letter;
		this.letterValue = letterValue;
	}


	public int getLetterValue() {
		return letterValue;
	}


	public char getLetter() {
		return letter;
	}


	public void setOwner(HumanPlayer owner) {
		this.owner = owner;
	}


	public HumanPlayer getOwner() {
		return owner;
	}
	
}
