package scrabble.game;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import scrabble.GameController;

/**
 * scrabble.game.Grid class is used to stock the internal state of the letters grid.
 *
 * @author Eldar Kasmamytov
 */
public class Grid {

  final GameController controller;

  private Slot[] slots;
  int size;
  private int globalSize;

  double padSize;
  private double paneSize;
  double cellSize;

  GridPane container;

  private void initGrid() {

    // setting the padding of the entire grid
    // and margins for each cell
    container.setPadding(new Insets(padSize));
    container.setHgap(padSize);
    container.setVgap(padSize);

    // Binding GridPane's Height to be always equal to its Width
    container.widthProperty().addListener((observable, oldValue, newValue) -> {
      container.setPrefHeight(newValue.doubleValue());
    });

    // Binding GridPane's Width to be always equal to its Height
    controller.gridWrapper.heightProperty().addListener((observable, oldValue, newValue) -> {
      container.setPrefWidth(newValue.doubleValue());
    });
  }

  public void initCells() {
    for (int row = 0; row < size; row++) {
      for (int column = 0; column < size; column++) {

        Slot slot = new Slot(controller);
        addSlot(slot, row, column);
      }
    }
  }

  /**
   * Constructor that creates a square grid.
   *
   * @param size Width and Height of the grid
   */
  public Grid(GridPane grid, int size, GameController controller) {
    this.size = size;
    globalSize = size * size;
    slots = new Slot[globalSize];

    paneSize = 825;
    padSize = 3;
    cellSize = paneSize / size - padSize;

    this.controller = controller;
    this.container = grid;

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
   * Getter for the cell which uses global index.
   *
   * @param globalIndex global index of the cell
   * @return Cell
   * @see #getGlobalIndex(int, int)
   */
  public LetterTile getSlotContent(int globalIndex) {
    return slots[globalIndex].content;
  }

  public Slot getSlot(int globalIndex) {
    return slots[globalIndex];
  }

  /**
   * Getter for the cell which uses X/Y indexes.
   *
   * @param row    Row
   * @param column Column
   * @return Cell
   */
  public LetterTile getSlotContent(int row, int column) {
    return slots[getGlobalIndex(row, column)].content;
  }

  public Slot getSlot(int row, int column) {
    return slots[getGlobalIndex(row, column)];
  }

  /**
   * Sets the specified cell.
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

  public void setSlotContent(int row, int column, LetterTile tile) {
    slots[getGlobalIndex(row, column)].setContent(tile);
  }

  /**
   * Displays the current internal state of the grid.
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
   * Returns a Global Index of the specified Cell.
   *
   * @param slot slot
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

  public int getCellIndex(LetterTile tile) {
    for (int i = 0; i < globalSize; i++) {
      if (tile == slots[i].content) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Returns an X coordinate of a specififed cell
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

  public int getSize() {
    return size;
  }

  public int getGlobalSize() {
    return globalSize;
  }

  public Slot getSlotThatContains(LetterTile tile) {
    for (int i = 0; i < globalSize; i++) {
      if (slots[i].content == tile) {
        return slots[i];
      }
    }
    return null;
  }

  public void removeSlotContent(Slot slot) {
    for (int i = 0; i < globalSize; i++) {
      if (slots[i] == slot) {
        slots[i].removeContent();
      }
    }
  }

  public void removeSlotContent(int row, int column) {
    slots[getGlobalIndex(row, column)].removeContent();
  }

  public void addSlot(Slot slot, int row, int column) {
    int index = getGlobalIndex(row, column);

    container.add(slot.container, row, column);
    slots[getGlobalIndex(row, column)] = slot;
  }


}
