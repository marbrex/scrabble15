package scrabble.model;

import java.util.LinkedList;

/**
 * Word class that is needed to detect, store and verify words.
 *
 * @author Eldar Kasmamytov
 */
public class Word {

  private LinkedList<Cell> w;
  private int wordLength;
  public boolean isCorrect;

  /**
   * Default Constructor.
   */
  public Word () {
    w = new LinkedList<Cell>();
    wordLength = 0;
    isCorrect = false;
  }

  /**
   * Specifies the cell as an ending point of a word.
   * @param cell Cell in Grid, containing a letter
   */
  public void addCellEnd (Cell cell) {
    if (!cell.isFree()) {
      w.addLast(cell);
    }
  }

  /**
   * Specifies the cell as a starting point of a word.
   * @param cell Cell in Grid, containing a letter
   */
  public void addCellStart (Cell cell) {
    if (!cell.isFree()) {
      w.addFirst(cell);
    }
  }

  /**
   * Removes specified Cell from the word.
   * @param cell Cell in Grid, containing a letter
   */
  public void removeCell (Cell cell) {
    w.remove(cell);
  }

  /**
   * Returns the whole word as a String.
   * @return Word as "String"
   */
  public String getWordAsString () {
    return w.toString();
  }

}
