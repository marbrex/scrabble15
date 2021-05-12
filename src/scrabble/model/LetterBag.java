package scrabble.model;

import java.util.LinkedList;
import java.util.Random;

public class LetterBag {

  private LinkedList<Cell> bag; //Scrabble bag with 100 stones
  private static LetterBag bagClass = null;

  //private Constructor. Client or Server doesnt need more than one instance
  private LetterBag() {
    initializeScrabbleLetters();
  }

  //Singleton Constructor
  public static LetterBag getLetterBag() {
		if (bagClass == null) {
			bagClass = new LetterBag();
		}
    return bagClass;
  }


  //Exchange letters form players
  public Cell[] changeLetters(HumanPlayer player, Cell[] letters) {
    if (bag.size() >= 7) {
      Random random = new Random(); //random generator for drawing letters from bag
      Cell[] newLetters = new Cell[letters.length]; //Drawing same amount of given Letters
      for (int i = 0; i < letters.length; i++) {
        newLetters[i] = bag.get(random.nextInt(bag.size() - 1));//drawing a random Cell from bag
      }
      for (int i = 0; i < letters.length; i++) { //adding the changed letters to the bag
//				letters[i].setOwner(null);
        bag.add(letters[i]);
      }
      return newLetters; //array of new Letters
    } else {
      return null; //min amount of bag letters Exception needs to be integrated
    }
  }

  //filling the bag with the english scrabble Letters
  private void initializeScrabbleLetters() {
    this.bag = new LinkedList<Cell>();
    bag.add(new Cell('*', 0)); //representing the blank tiles
    bag.add(new Cell('*', 0));
    bag.add(new Cell('E', 1));//12xE
    bag.add(new Cell('E', 1));
    bag.add(new Cell('E', 1));
    bag.add(new Cell('E', 1));
    bag.add(new Cell('E', 1));
    bag.add(new Cell('E', 1));
    bag.add(new Cell('E', 1));
    bag.add(new Cell('E', 1));
    bag.add(new Cell('E', 1));
    bag.add(new Cell('E', 1));
    bag.add(new Cell('E', 1));
    bag.add(new Cell('E', 1));
    bag.add(new Cell('A', 1));//9xA
    bag.add(new Cell('A', 1));
    bag.add(new Cell('A', 1));
    bag.add(new Cell('A', 1));
    bag.add(new Cell('A', 1));
    bag.add(new Cell('A', 1));
    bag.add(new Cell('A', 1));
    bag.add(new Cell('A', 1));
    bag.add(new Cell('A', 1));
    bag.add(new Cell('I', 1));//9xI
    bag.add(new Cell('I', 1));
    bag.add(new Cell('I', 1));
    bag.add(new Cell('I', 1));
    bag.add(new Cell('I', 1));
    bag.add(new Cell('I', 1));
    bag.add(new Cell('I', 1));
    bag.add(new Cell('I', 1));
    bag.add(new Cell('I', 1));
    bag.add(new Cell('O', 1));//8xO
    bag.add(new Cell('O', 1));
    bag.add(new Cell('O', 1));
    bag.add(new Cell('O', 1));
    bag.add(new Cell('O', 1));
    bag.add(new Cell('O', 1));
    bag.add(new Cell('O', 1));
    bag.add(new Cell('O', 1));
    bag.add(new Cell('N', 1));//6xN
    bag.add(new Cell('N', 1));
    bag.add(new Cell('N', 1));
    bag.add(new Cell('N', 1));
    bag.add(new Cell('N', 1));
    bag.add(new Cell('N', 1));
    bag.add(new Cell('R', 1));//6xR
    bag.add(new Cell('R', 1));
    bag.add(new Cell('R', 1));
    bag.add(new Cell('R', 1));
    bag.add(new Cell('R', 1));
    bag.add(new Cell('R', 1));
    bag.add(new Cell('T', 1));//6xT
    bag.add(new Cell('T', 1));
    bag.add(new Cell('T', 1));
    bag.add(new Cell('T', 1));
    bag.add(new Cell('T', 1));
    bag.add(new Cell('T', 1));
    bag.add(new Cell('L', 1));//4xL
    bag.add(new Cell('L', 1));
    bag.add(new Cell('L', 1));
    bag.add(new Cell('L', 1));
    bag.add(new Cell('S', 1)); //4xS
    bag.add(new Cell('S', 1));
    bag.add(new Cell('S', 1));
    bag.add(new Cell('S', 1));
    bag.add(new Cell('U', 1));//4xU
    bag.add(new Cell('U', 1));
    bag.add(new Cell('U', 1));
    bag.add(new Cell('U', 1));
    bag.add(new Cell('D', 2));//4xD
    bag.add(new Cell('D', 2));
    bag.add(new Cell('D', 2));
    bag.add(new Cell('D', 2));
    bag.add(new Cell('G', 2));//3xG
    bag.add(new Cell('G', 2));
    bag.add(new Cell('G', 2));
    bag.add(new Cell('B', 3));//2xB
    bag.add(new Cell('B', 3));
    bag.add(new Cell('C', 3));//2xC
    bag.add(new Cell('C', 3));
    bag.add(new Cell('M', 3));//2xM
    bag.add(new Cell('M', 3));
    bag.add(new Cell('P', 3));//2xP
    bag.add(new Cell('P', 3));
    bag.add(new Cell('F', 4));//2xF
    bag.add(new Cell('F', 4));
    bag.add(new Cell('H', 4));//2xH
    bag.add(new Cell('H', 4));
    bag.add(new Cell('V', 4));//2xV
    bag.add(new Cell('V', 4));
    bag.add(new Cell('W', 4));//2xW
    bag.add(new Cell('W', 4));
    bag.add(new Cell('Y', 4));//2xY
    bag.add(new Cell('Y', 4));
    bag.add(new Cell('K', 5));//1xK
    bag.add(new Cell('J', 8));//1xJ
    bag.add(new Cell('X', 8));//1xX
    bag.add(new Cell('Q', 10));//1xQ
    bag.add(new Cell('Z', 10));//1xZ
  }
}
