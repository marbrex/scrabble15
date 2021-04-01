package scrabble.model;

/**
 * scrabble.model.Grid class is used to stock the internal state of the letters grid.
 *
 * @author Eldar Kasmamytov
 */
public class Grid {

  private Cell[] g;
  private int size;
  private int globalSize;

  /**
   * Constructor that creates a square grid.
   *
   * @param size Width and Height of the grid
   */
  public Grid(int size) {
    this.size = size;
    globalSize = size * size;
    g = new Cell[globalSize];
    for (int i = 0; i < globalSize; i++) {
      g[i] = new Cell();
    }
  }

  /**
   * Returns the global index (index in 1D array). of the specified cell.
   *
   * @param x Column of the cell
   * @param y Row of the cell
   * @return Global Index
   */
  public int getGlobalIndex(int x, int y) {
    return x + size * y;
  }

  /**
   * Getter for the cell which uses global index.
   *
   * @param globalIndex global index of the cell
   * @return Cell
   * @see #getGlobalIndex(int, int)
   */
  public Cell getCell(int globalIndex) {
    return g[globalIndex];
  }

  /**
   * Getter for the cell which uses X/Y indexes.
   *
   * @param x Column
   * @param y Row
   * @return Cell
   */
  public Cell getCell(int x, int y) {
    return g[getGlobalIndex(x, y)];
  }

  /**
   * Sets the specified cell.
   *
   * @param x      Column of the cell to be set
   * @param y      Row of the cell to be set
   * @param letter Letter
   * @param points Points
   */
  public void setCell(int x, int y, char letter, int points) {
    g[getGlobalIndex(x, y)].setCell(letter, points);
  }

  /**
   * Displays the current internal state of the grid.
   */
  public void display() {
    System.out.println("\nInternal State: ");
    for (int i = 0; i < globalSize; i++) {
      System.out.print(
          "[" + (g[i].getLtr() == (char) 0 ? " " : g[i].getLtr()) + "|" + g[i].getPts() + "]");
      if ((i + 1) % size == 0) {
        System.out.print("\n");
      } else {
        System.out.print(" ");
      }
    }
    System.out.print("\n");
  }

  /**
   * Returns a Global Index of the specified Cell.
   * @param cell Cell
   * @return Global Index if the Cell is in the Grid, -1 otherwise.
   */
  public int getCellIndex (Cell cell) {
    for (int i = 0; i < globalSize; i++) {
      if (cell == g[i]) return i;
    }
    return -1;
  }

  /**
   * Returns an X coordinate of a specififed cell
   * @param cell Cell that is in Grid
   * @return X coordinate (Column)
   */
  public int getCellX (Cell cell) {
    int glInd;
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        glInd = getGlobalIndex(i, j);
        if (cell == g[glInd]) {
          return i;
        }
      }
    }
    return -1;
  }

  /**
   * Returns an Y coordinate of a specififed cell
   * @param cell Cell that is in Grid
   * @return Y coordinate (Row)
   */
  public int getCellY (Cell cell) {
    int glInd;
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        glInd = getGlobalIndex(i, j);
        if (cell == g[glInd]) {
          return j;
        }
      }
    }
    return -1;
  }
}
