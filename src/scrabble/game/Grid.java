package scrabble.game;

import com.jfoenix.controls.JFXButton;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import scrabble.GameController;
import scrabble.model.Letter;

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

  /**
   * Verifies whether placed words are valid. Generates a pop-up window in case of an invalid
   * input.
   *
   * @return True if input is valid, False otherwise
   */
  public boolean verifyWordsValidity() {
    boolean res = false;

    ArrayList<Slot> validStartingSlots = new ArrayList<>();

    if (controller.roundCounter == 0) {
      validStartingSlots.add(getSlot(size / 2, size / 2));
    }

    int validWords = 0;
    int wordsUsingStartSlot = 0;
    boolean startingSlotUsed = false;

    int nbHorizontal = 0;
    int nbVertical = 0;

    for (Word word : words) {
      // verifying if all words present in the grid are valid
      // (by counting the number of valid words and comparing it to number of all words)
      if (word.isValid()) {
        validWords++;
      }

      if (word.isHorizontal()) {
        nbHorizontal++;
      }
      if (word.isVertical()) {
        nbVertical++;
      }

      for (Slot slot : validStartingSlots) {
        // we want to find out whether at least one starting slot is used by a word
        if (!slot.isFree() && word.contains(slot.content)) {
          startingSlotUsed = true;
          wordsUsingStartSlot++;
          break;
        }
      }
    }

    boolean noSingleTiles = true;
    for (Slot slot : slots) {
      if (!slot.isFree()) {
        if (getNeighbourCell(slot.content, "top") == null &&
            getNeighbourCell(slot.content, "right") == null &&
            getNeighbourCell(slot.content, "bottom") == null &&
            getNeighbourCell(slot.content, "left") == null) {
          noSingleTiles = false;
          break;
        }
      }
    }

    String errorMessage = "";
    if (noSingleTiles) {
      // there is no single placed letter tiles
      System.out.println("ALL WORDS HAVE LENGTH >= 2 !");

      if (validWords == words.size()) {
        // All words present in the grid are valid
        System.out.println("ALL WORDS ARE VALID !");

        if (wordsUsingStartSlot == words.size()) {
          // at least one of the previous letters is used by ALL new words
          System.out.println("ALL WORDS ARE USING AT LEAST 1 OF PREVIOUS LETTERS !");

          if (nbHorizontal == words.size() || nbVertical == words.size()) {
            // All words have the same direction
            System.out.println("ALL WORDS HAVE THE SAME DIRECTION !");

          } else {
            errorMessage = "ALL WORDS SHOULD BE IN THE SAME DIRECTION !";
            System.err.println(errorMessage);
          }

        } else {

//        if (startingSlotUsed) {
//          // at least one of the previous letters is used by a word
//          System.out.println("AT LEAST 1 OF PREVIOUS LETTERS IS USED !");
//        }
//        else {
//          System.err.println("USE AT LEAST 1 OF PREVIOUS LETTERS TO MAKE A WORD !");
//        }

          errorMessage = "ALL WORDS SHOULD USE AT LEAST 1 OF PREVIOUS LETTERS !";
          System.err.println(errorMessage);
        }

      } else {
        errorMessage = "ALL NEW WORDS SHOULD BE VALID !";
        System.err.println(errorMessage);
      }

    } else {
      errorMessage = "ALL WORDS SHOULD HAVE LENGTH >= 2 !";
      System.err.println(errorMessage);
    }

    if (!errorMessage.equals("")) {
      // there is an error

      BorderPane popup = new BorderPane();
      popup.getStyleClass().add("popup-error-block");
      popup.setOnMouseClicked(event -> {
        controller.gridWrapper.getChildren().remove(popup);
        controller.okBtn.setDisable(false);
      });

      Label errorLabel = new Label(errorMessage);
      errorLabel.setAlignment(Pos.CENTER);
      errorLabel.getStyleClass().add("popup-error-message");
      errorLabel.setOnMouseClicked(event -> {
        controller.gridWrapper.getChildren().remove(popup);
        controller.okBtn.setDisable(false);
      });

      popup.setCenter(errorLabel);

      controller.gridWrapper.getChildren().add(popup);
      controller.okBtn.setDisable(true);

    } else {
      // everything is OK
      System.out.println("ROUND OVER\nWORD-S ARE VALIDATED\nPROCEEDING TO THE NEXT PLAYER...");

      res = true;
    }

    return res;
  }


}
