/**
 * scrabble.model.Grid class is used to stock
 * the internal state of the letters grid.
 *
 * @author      Eldar Kasmamytov
 */

package scrabble.model;

public class Grid {

  private Cell g[];
  private int size;
  private int globalSize;

  /**
   * Constructor that creates a square grid.
   * @param size Width and Height of the grid
   */
  public Grid(int size) {
    this.size = size;
    globalSize = size * size;
    g = new Cell[globalSize];
  }

  /**
   * Returns the global index (index in 1D array).
   * of the specified cell.
   * @param x Column of the cell
   * @param y Row of the cell
   * @return Global Index
   */
  public int getGlobalIndex(int x, int y) {
    return x + size * y;
  }

  /**
   * Getter for the cell which uses global index.
   * @param globalIndex global index of the cell
   * @see #getGlobalIndex(int, int)
   * @return Cell
   */
  public Cell getCell(int globalIndex) {
    return g[globalIndex];
  }

  /**
   * Getter for the cell which uses X/Y indexes.
   * @param x Column
   * @param y Row
   * @return Cell
   */
  public Cell getCell(int x, int y) {
    return g[getGlobalIndex(x, y)];
  }

  /**
   * Sets the specified cell.
   * @param x Column of the cell to be set
   * @param y Row of the cell to be set
   * @param letter Letter
   * @param points Points
   */
  public void setCell(int x, int y, char letter, int points) {
    g[getGlobalIndex(x, y)].setCell(letter, points);
  }
}
