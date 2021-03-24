package scrabble.model;

import java.util.LinkedList;
import java.util.Random;

public class LetterBag {
	private LinkedList<Letter> bag; //Scrabble bag with 100 stones
	private static LetterBag bagClass = null;
	
	//private Constructor. Client or Server doesnt need more than one instance
	private LetterBag() {
		initializeScrabbleLetters();
	}
	
	//Singleton Constructor
	public LetterBag getLetterBag() {
		if(bagClass == null)
			bagClass = new LetterBag();
		return bagClass;
	}
	
	
	//Exchange letters form players 
	public Letter[] changeLetters(HumanPlayer player, Letter[] letters) {
		if(bag.size() >= 7) {
			Random random = new Random(); //random generator for drawing letters from bag
			Letter[] newLetters = new Letter[letters.length]; //Drawing same amount of given Letters
			for(int i = 0; i < letters.length; i++) {
				newLetters[i] = bag.get(random.nextInt(bag.size()-1));//drawing a random Letter from bag
			}
			for(int i = 0; i < letters.length; i++) { //adding the changed letters to the bag
				letters[i].setOwner(null);
				bag.add(letters[i]);
			}
			return newLetters; //array of new Letters
		}
		else {
			return null; //min amount of bag letters Exception needs to be integrated
		}
	}
	
	//filling the bag with the english scrabble Letters
	private void initializeScrabbleLetters() {
		this.bag = new LinkedList<Letter>();
		bag.add(new Letter(0, '*')); //representing the blank tiles
		bag.add(new Letter(0, '*'));
		bag.add(new Letter(1, 'E'));//12xE
		bag.add(new Letter(1, 'E'));
		bag.add(new Letter(1, 'E'));
		bag.add(new Letter(1, 'E'));
		bag.add(new Letter(1, 'E'));
		bag.add(new Letter(1, 'E'));
		bag.add(new Letter(1, 'E'));
		bag.add(new Letter(1, 'E'));
		bag.add(new Letter(1, 'E'));
		bag.add(new Letter(1, 'E'));
		bag.add(new Letter(1, 'E'));
		bag.add(new Letter(1, 'E'));
		bag.add(new Letter(1, 'A'));//9xA
		bag.add(new Letter(1, 'A'));
		bag.add(new Letter(1, 'A'));
		bag.add(new Letter(1, 'A'));
		bag.add(new Letter(1, 'A'));
		bag.add(new Letter(1, 'A'));
		bag.add(new Letter(1, 'A'));
		bag.add(new Letter(1, 'A'));
		bag.add(new Letter(1, 'A'));
		bag.add(new Letter(1, 'I'));//9xI
		bag.add(new Letter(1, 'I'));
		bag.add(new Letter(1, 'I'));
		bag.add(new Letter(1, 'I'));
		bag.add(new Letter(1, 'I'));
		bag.add(new Letter(1, 'I'));
		bag.add(new Letter(1, 'I'));
		bag.add(new Letter(1, 'I'));
		bag.add(new Letter(1, 'I'));
		bag.add(new Letter(1, 'O'));//8xO
		bag.add(new Letter(1, 'O'));
		bag.add(new Letter(1, 'O'));
		bag.add(new Letter(1, 'O'));
		bag.add(new Letter(1, 'O'));
		bag.add(new Letter(1, 'O'));
		bag.add(new Letter(1, 'O'));
		bag.add(new Letter(1, 'O'));
		bag.add(new Letter(1, 'N'));//6xN
		bag.add(new Letter(1, 'N'));
		bag.add(new Letter(1, 'N'));
		bag.add(new Letter(1, 'N'));
		bag.add(new Letter(1, 'N'));
		bag.add(new Letter(1, 'N'));
		bag.add(new Letter(1, 'R'));//6xR
		bag.add(new Letter(1, 'R')); 
		bag.add(new Letter(1, 'R')); 
		bag.add(new Letter(1, 'R')); 
		bag.add(new Letter(1, 'R')); 
		bag.add(new Letter(1, 'R'));
		bag.add(new Letter(1, 'T'));//6xT
		bag.add(new Letter(1, 'T'));
		bag.add(new Letter(1, 'T'));
		bag.add(new Letter(1, 'T'));
		bag.add(new Letter(1, 'T'));
		bag.add(new Letter(1, 'T'));
		bag.add(new Letter(1, 'L'));//4xL
		bag.add(new Letter(1, 'L'));
		bag.add(new Letter(1, 'L'));
		bag.add(new Letter(1, 'L'));
		bag.add(new Letter(1, 'S')); //4xS
		bag.add(new Letter(1, 'S'));
		bag.add(new Letter(1, 'S'));
		bag.add(new Letter(1, 'S'));
		bag.add(new Letter(1, 'U'));//4xU
		bag.add(new Letter(1, 'U'));
		bag.add(new Letter(1, 'U'));
		bag.add(new Letter(1, 'U'));
		bag.add(new Letter(2, 'D'));//4xD
		bag.add(new Letter(2, 'D'));
		bag.add(new Letter(2, 'D'));
		bag.add(new Letter(2, 'D'));
		bag.add(new Letter(2, 'G'));//3xG
		bag.add(new Letter(2, 'G'));
		bag.add(new Letter(2, 'G'));
		bag.add(new Letter(3, 'B'));//2xB
		bag.add(new Letter(3, 'B'));
		bag.add(new Letter(3, 'C'));//2xC
		bag.add(new Letter(3, 'C'));
		bag.add(new Letter(3, 'M'));//2xM
		bag.add(new Letter(3, 'M'));
		bag.add(new Letter(3, 'P'));//2xP
		bag.add(new Letter(3, 'P'));
		bag.add(new Letter(4, 'F'));//2xF
		bag.add(new Letter(4, 'F'));
		bag.add(new Letter(4, 'H'));//2xH
		bag.add(new Letter(4, 'H'));
		bag.add(new Letter(4, 'V'));//2xV
		bag.add(new Letter(4, 'V'));
		bag.add(new Letter(4, 'W'));//2xW
		bag.add(new Letter(4, 'W'));
		bag.add(new Letter(4, 'Y'));//2xY
		bag.add(new Letter(4, 'Y'));
		bag.add(new Letter(5, 'K'));//1xK
		bag.add(new Letter(8, 'J'));//1xJ
		bag.add(new Letter(8, 'X'));//1xX
		bag.add(new Letter(10, 'Q'));//1xQ
		bag.add(new Letter(10, 'Z'));//1xZ
	}
}
