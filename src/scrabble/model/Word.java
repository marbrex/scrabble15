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
  private int points;
  private boolean isValid;
  private boolean isHorizontal;
  private boolean isVertical;

  /**
   * Default Constructor.
   */
  public Word () {
    w = new LinkedList<Cell>();
    points = 0;
    wordLength = 0;
    isValid = false;
    isHorizontal = false;
    isVertical = false;
  }

  /**
   * Most important Constructor.
   * Fills in all data members of the Word class.
   * It checks whether the word has no empty gaps, i.e. the word is FULL.
   * If thw word is full, then it checks if the word is VALID.
   *
   * @param start Starting point of the Word (First Letter)
   * @param end Ending point of the Word (Last Letter)
   * @param data Back-End Data Grid
   */
  public Word (Cell start, Cell end, Grid data) {
    w = new LinkedList<Cell>();
    points = 0;
    wordLength = 0;
    isValid = false;

    int startX = data.getCellX(start);
    int startY = data.getCellY(start);
    System.out.println("\n@Word - Start Cell (" + startX + ", " + startY + ")");

    int endX = data.getCellX(end);
    int endY = data.getCellY(end);
    System.out.println("@Word - End Cell (" + endX + ", " + endY + ")");

    if (startY == endY) {
      // we know that's a Horizontal word

      if (startX < endX) {
        // we know that the length is at least 2 Letters
        System.out.println("\n@Word - HORIZONTAL - word (>= 2 letters)");

        isHorizontal = true;

        boolean fullWord = true;
        for (int i = startX; i <= endX; i++) {
          System.out.println("\n@Word - HORIZONTAL - in for: (" + i + ", " + startY + ")");

          if (data.getCell(i, startY).isFree()) {
            // NOT A WORD - Empty Gaps between 2 Cells
            System.out.println("\n@Word - HORIZONTAL - Letter (" + i + ", " + startY + ") is empty");
            fullWord = false;
            break;
          }
          else {
            System.out.println("\n@Word - HORIZONTAL - Letter: (" + i + ", " + startY + ") is filled in");
            w.add(data.getCell(i, startY));
            System.out.println("@Word - HORIZONTAL - Added: (" + i + ", " + startY + ")");
            points += data.getCell(i, startY).getPts();
            wordLength++;
            System.out.println("@Word - HORIZONTAL - Letter (" + i + ", " + startY + ") = " + w.getLast().getLtr());
            System.out.println("@Word - HORIZONTAL - Points: " + points);
            System.out.println("@Word - HORIZONTAL - Length: " + wordLength);
          }
        }

        if (fullWord) {
          // Every Letter is Filled In
          // => We can proceed to WORD VALIDATION
          System.out.println("\n@Word - HORIZONTAL - Full word");
          System.out.println("@Word - HORIZONTAL - The Word is: " + getWordAsString());

          // The Dictionary has to be set only once, otherwise it does not work
          // The Dictionary is already set in "initialize()" function of ScrabbleController
          isValid = Dictionary.matches(this);
          System.out.println("@Word - HORIZONTAL - is valid: " + isValid);
        }
      }
    }

    if (startX == endX) {
      // we know that's a Vertical word

      if (startY < endY) {
        // we know that the length is at least 2 Letters
        System.out.println("\n@Word - VERTICAL - word (>= 2 letters)");

        isVertical = true;

        boolean fullWord = true;
        for (int j = startY; j <= endY; j++) {
          System.out.println("\n@Word - VERTICAL - in for: (" + startX + ", " + j + ")");

          if (data.getCell(startX, j).isFree()) {
            // NOT A WORD - Empty Gaps between 2 Cells
            System.out.println("\n@Word - VERTICAL - in for: (" + startX + ", " + j + ") is empty");
            fullWord = false;
            break;
          }
          else {
            w.add(data.getCell(startX, j));
            System.out.println("@Word - VERTICAL - Added: (" + startX + ", " + j + ")");
            points += data.getCell(startX, j).getPts();
            wordLength++;
            System.out.println("@Word - VERTICAL - Letter (" + startX + ", " + j + ") = " + w.getLast().getLtr());
            System.out.println("@Word - VERTICAL - Points: " + points);
            System.out.println("@Word - VERTICAL - Length: " + wordLength);
          }
        }

        if (fullWord) {
          // Every Letter is Filled In
          // => We can proceed to WORD VALIDATION
          System.out.println("\n@Word - VERTICAL - Full word");
          System.out.println("\n@Word - VERTICAL - The Word is: " + getWordAsString());

          // The Dictionary has to be set only once, otherwise it does not work
          // The Dictionary is already set in "initialize()" function of ScrabbleController
          isValid = Dictionary.matches(this);
          System.out.println("@Word - VERTICAL - is valid: " + isValid);
        }
      }
    }
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
    String str = new String("");
    for (int i = 0; i < wordLength; i++) {
      str += w.get(i).getLtr();
    }
    return str;
  }

  /**
   * Getter of Points.
   * @return Total points of the Word.
   */
  public int getPoints() {
    return points;
  }

  /**
   * Getter of Length.
   * @return Total Length of the Word (Number of Letters)
   */
  public int getWordLength() {
    return wordLength;
  }

  /**
   * Returns True if the Word is valid, otherwise False.
   * @return isValid
   */
  public boolean isValid() {
    return isValid;
  }

  /**
   * Returns True if the Word is horizontal, otherwise False.
   * @return isHorizontal
   */
  public boolean isHorizontal() {
    return isHorizontal;
  }

  /**
   * Returns True if the Word is vertical, otherwise False.
   * @return isVertical
   */
  public boolean isVertical() {
    return isVertical;
  }
}
