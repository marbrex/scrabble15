package scrabble.model;

/**
 * scrabble.model.Cell class represents a cell of the letters grid.
 *
 * @author Eldar Kasmamytov
 */
public class Cell {

  /**
   * Letter that is stocked by this cell.
   */
  private char ltr;

  /**
   * Points that a player will get by using this letter in his/her word.
   */
  private int pts;

  /**
   * Indicates whether a cell is free.
   */
  private boolean isFree;

  /**
   * Default constructor.
   */
  public Cell() {
    isFree = true;
    pts = 0;
    ltr = (char) 0;
  }

  /**
   * Constructor that takes 2 arguments.
   *
   * @param ltr Letter to put into the cell
   * @param pts Points of the cell
   */
  public Cell(char ltr, int pts) {
    isFree = false;
    this.pts = pts;
    this.ltr = ltr;
  }

  /**
   * Sets the letter and points of the cell.
   *
   * @param letter Letter to put into the cell
   * @param points Points of the cell
   */
  public void setCell(char letter, int points) {
    isFree = false;
    ltr = letter;
    pts = points;
  }

  /**
   * Get the letter.
   *
   * @return The letter stocked by the cell
   */
  public char getLtr() {
    return ltr;
  }

  /**
   * Get the points.
   *
   * @return Points of the cell
   */
  public int getPts() {
    return pts;
  }

  /**
   * Frees the cell
   */
  public void freeCell() {
    isFree = true;
    pts = 0;
    ltr = (char) 0;
  }

  /**
   * Returns:
   * <ul>
   *   <li>True - if the cell is free (a letter can be placed here)</li>
   *   <li>False - if the cell already has a letter</li>
   * </ul>
   *
   * @return Boolean
   */
  public boolean isFree() {
    return this.isFree;
  }
}
