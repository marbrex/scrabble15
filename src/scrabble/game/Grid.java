package scrabble.game;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import scrabble.GameController;

/**
 * <h1>scrabble.game.Grid</h1>
 *
 * <p>Class is used to stock the internal state of the letters grid.</p>
 *
 * @author Eldar Kasmamytov
 */
public class Grid {

  final GameController controller;

  private Slot[] slots;
  int size;
  private int globalSize;

  private Map map;

  double padding;
  double padSize;
  private double paneSize;
  double cellSize;

  ArrayList<Word> words;

  GridPane container;

  /**
   * Initiates GridPane's settings
   */
  private void initGrid() {

    // setting the padding of the entire grid
    // and margins for each cell
    container.setPadding(new Insets(padding));
    container.setHgap(padSize);
    container.setVgap(padSize);
  }

  /**
   * Initiates slots (StackPanes) of the GridPane
   */
  public void initCells() {
    for (int row = 0; row < size; row++) {
      for (int column = 0; column < size; column++) {

        Slot slot = new Slot(map.getMultiplier(getGlobalIndex(row, column)), controller);
        addSlot(slot, row, column);
      }
    }
  }

  /**
   * Constructor that creates a square grid.
   *
   * @param grid       GridPane from the GameController
   * @param size       GridPane's width and height
   * @param controller GameController
   */
  public Grid(GridPane grid, int size, GameController controller) {
    this.size = size;
    globalSize = size * size;
    slots = new Slot[globalSize];

    padding = 10;
    paneSize = 825;
    padSize = 3;
    cellSize = paneSize / size - padSize;

    this.controller = controller;
    this.container = grid;

    map = new Map();

    words = new ArrayList<>();

    initGrid();
  }

  /**
   * Constructor that creates a square grid.
   *
   * @param grid       GridPane from the GameController
   * @param mapPath    Path to a map ".txt" file in resources folder
   * @param size       GridPane's width and height
   * @param controller GameController
   */
  public Grid(GridPane grid, String mapPath, int size, GameController controller) {
    this.size = size;
    globalSize = size * size;
    slots = new Slot[globalSize];

    paneSize = 825;
    padSize = 3;
    cellSize = paneSize / size - padSize;

    padding = 10;
    this.controller = controller;
    this.container = grid;

    map = new Map(mapPath);

    words = new ArrayList<>();

    initGrid();
  }

  /**
   * Returns the global index (index in 1D array). of the specified cell.
   *
   * @param row    Row of the cell
   * @param column Column of the cell
   * @return Global Index
   */
  public int getGlobalIndex(int row, int column) {
    return row + size * column;
  }

  /**
   * Get a Slot's content (LetterTile) using global index.
   *
   * @param globalIndex global index of the cell
   * @return LetterTile
   * @see #getGlobalIndex(int, int)
   */
  public LetterTile getSlotContent(int globalIndex) {
    return slots[globalIndex].content;
  }

  /**
   * Get a Slot using global index.
   *
   * @param globalIndex global index of the cell
   * @return Slot
   */
  public Slot getSlot(int globalIndex) {
    return slots[globalIndex];
  }

  /**
   * Get a Slot's content (LetterTile) using row/column indexes.
   *
   * @param row    Row
   * @param column Column
   * @return LetterTile
   */
  public LetterTile getSlotContent(int row, int column) {
    return slots[getGlobalIndex(row, column)].content;
  }

  /**
   * Get a Slot using row/column indexes.
   *
   * @param row    Row
   * @param column Column
   * @return Slot
   */
  public Slot getSlot(int row, int column) {
    return slots[getGlobalIndex(row, column)];
  }

  /**
   * Sets the specified Slot's content.
   *
   * @param row    Row of the cell to be set
   * @param column Column of the cell to be set
   * @param letter Letter
   * @param points Points
   */
  public void setSlotContent(int row, int column, char letter, int points) {
    slots[getGlobalIndex(row, column)].content.setLetter(letter);
    slots[getGlobalIndex(row, column)].content.setPoints(points);
  }

  /**
   * Sets the specified Slot's content with an LetterTile object.
   *
   * @param row    Row of the cell to be set
   * @param column Column of the cell to be set
   * @param tile   LetterTile
   */
  public void setSlotContent(int row, int column, LetterTile tile) {
    slots[getGlobalIndex(row, column)].setContent(tile);
  }

  /**
   * Displays the current internal state of the grid in the console.
   */
  public void display() {
    System.out.println("\nInternal State: ");
    for (int i = 0; i < globalSize; i++) {
      if (slots[i].isFree()) {
        System.out.print("[   ]");
      } else {
        System.out.print(
            "[" + slots[i].content.getLetter()
                + "|" + slots[i].content.getPoints() + "]");
      }

      if ((i + 1) % size == 0) {
        System.out.print("\n");
      } else {
        System.out.print(" ");
      }
    }
    System.out.print("\n");
  }

  /**
   * Returns a Global Index of the specified Slot.
   *
   * @param slot Slot
   * @return Global Index if the Cell is in the Grid, -1 otherwise.
   */
  public int getCellIndex(Slot slot) {
    for (int i = 0; i < globalSize; i++) {
      if (slot == slots[i]) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Returns a Global Index of the specified LetterTile.
   *
   * @param tile LetterTile
   * @return Global Index if the Cell is in the Grid, -1 otherwise.
   */
  public int getCellIndex(LetterTile tile) {
    for (int i = 0; i < globalSize; i++) {
      if (tile == slots[i].content) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Returns an X coordinate of a specified cell
   *
   * @param tile Tile that is in Grid
   * @return X coordinate (Column)
   */
  public int getCellRow(LetterTile tile) {
    int glInd;
    for (int row = 0; row < size; row++) {
      for (int column = 0; column < size; column++) {
        glInd = getGlobalIndex(row, column);
        if (tile == slots[glInd].content) {
          return row;
        }
      }
    }
    return -1;
  }

  /**
   * Returns an Y coordinate of a specififed cell
   *
   * @param tile Tile that is in Grid
   * @return Y coordinate (Row)
   */
  public int getCellColumn(LetterTile tile) {
    int glInd;
    for (int row = 0; row < size; row++) {
      for (int column = 0; column < size; column++) {
        glInd = getGlobalIndex(row, column);
        if (tile == slots[glInd].content) {
          return column;
        }
      }
    }
    return -1;
  }

  /**
   * Get the size of the square GridPane (height=width)
   *
   * @return Number of Slots on each side (height=width)
   */
  public int getSize() {
    return size;
  }

  /**
   * Get the Global Size of the Grid
   *
   * @return Total number of Slots
   */
  public int getGlobalSize() {
    return globalSize;
  }

  /**
   * Get a Slot that contains the specified LetterTile
   *
   * @param tile Slot's content, LetterTile
   * @return Slot
   */
  public Slot getSlotThatContains(LetterTile tile) {
    for (int i = 0; i < globalSize; i++) {
      if (slots[i].content == tile) {
        return slots[i];
      }
    }
    return null;
  }

  public ArrayList<LetterTile> getTilesInGrid() {
    ArrayList<LetterTile> list = new ArrayList<>();
    for (int i = 0; i < globalSize; i++) {
      if (slots[i].content != null) {
        list.add(slots[i].content);
      }
    }
    return list;
  }

  public LetterTile getNeighbourCell(LetterTile tile, String neighbour) {
    int glInd;
    for (int row = 0; row < size; row++) {
      for (int column = 0; column < size; column++) {
        glInd = getGlobalIndex(row, column);
        if (tile == slots[glInd].content) {
          switch (neighbour) {
            case "top":
              if (column != 0) {
                return getSlotContent(row, column - 1);
              } else {
                return null;
              }
            case "right":
              if (row != size) {
                return getSlotContent(row + 1, column);
              } else {
                return null;
              }
            case "bottom":
              if (column != size) {
                return getSlotContent(row, column + 1);
              } else {
                return null;
              }
            case "left":
              if (row != 0) {
                return getSlotContent(row - 1, column);
              } else {
                return null;
              }
            default:
              return null;
          }
        }
      }
    }
    return null;
  }

  /**
   * Removes the specified Slot's content (based on the Slot object)
   *
   * @param slot Slot, the content of which should be removed
   */
  public void removeSlotContent(Slot slot) {
    for (int i = 0; i < globalSize; i++) {
      if (slots[i] == slot) {
        slots[i].removeContent();
      }
    }
  }

  /**
   * Removes the specified Slot's content (based on Row/Column indexes)
   *
   * @param row    Row (Y index)
   * @param column Column (X index)
   */
  public void removeSlotContent(int row, int column) {
    slots[getGlobalIndex(row, column)].removeContent();
  }

  /**
   * Adds a Slot to the specified cell
   *
   * @param slot   Slot
   * @param row    Row (Y)
   * @param column Column (X)
   */
  public void addSlot(Slot slot, int row, int column) {
    int index = getGlobalIndex(row, column);

    container.add(slot.container, row, column);
    slots[getGlobalIndex(row, column)] = slot;
  }


}
