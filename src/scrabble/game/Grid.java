package scrabble.game;

import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import scrabble.GameController;
import scrabble.model.Letter;

/**
 * scrabble.game.Grid Class is used to stock the internal state of the letters grid.
 *
 * @author ekasmamy
 */
public class Grid {

  final GameController controller;

  private Slot[] slots;
  int size;
  private final int globalSize;

  private Map map;

  double padding;
  double padSize;
  private double paneSize;
  public double cellSize;

  public ArrayList<Word> words;

  GridPane container;

  /**
   * Initiates GridPane's settings.
   */
  private void initGrid() {

    // setting the padding of the entire grid
    // and margins for each cell
    container.setPadding(new Insets(padding));
    container.setHgap(padSize);
    container.setVgap(padSize);
  }

  /**
   * Initiates slots (StackPanes) of the GridPane.
   */
  public void initCells() {
    for (int column = 0; column < size; column++) {
      for (int row = 0; row < size; row++) {

        Slot slot = new Slot(map.getMultiplier(getGlobalIndex(column, row)), controller);
        addSlot(slot, column, row);
      }
    }
  }

  /**
   * Constructor that creates a square grid.
   *
   * @param grid       GridPane from the GameController.
   * @param size       GridPane's width and height.
   * @param controller GameController.
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
   * @param grid       GridPane from the GameController.
   * @param mapPath    Path to a map ".txt" file in resources folder.
   * @param size       GridPane's width and height.
   * @param controller GameController.
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
   * @param column Column of the cell.
   * @param row    Row of the cell.
   * @return Global Index.
   */
  public int getGlobalIndex(int column, int row) {
    return column + size * row;
  }

  /**
   * Get a Slot's content (LetterTile) using global index.
   *
   * @param globalIndex global index of the cell.
   * @return LetterTile.
   * @see #getGlobalIndex(int, int)
   */
  public LetterTile getSlotContent(int globalIndex) {
    return slots[globalIndex].content;
  }

  /**
   * Get a Slot using global index.
   *
   * @param globalIndex global index of the cell.
   * @return Slot.
   */
  public Slot getSlot(int globalIndex) {
    return slots[globalIndex];
  }

  /**
   * Get a Slot's content (LetterTile) using column/row indexes.
   *
   * @param column Column.
   * @param row    Row.
   * @return LetterTile.
   */
  public LetterTile getSlotContent(int column, int row) {
    return slots[getGlobalIndex(column, row)].content;
  }

  /**
   * Get a Slot using column/row indexes.
   *
   * @param column Column.
   * @param row    Row.
   * @return Slot.
   */
  public Slot getSlot(int column, int row) {
    return slots[getGlobalIndex(column, row)];
  }

  /**
   * Sets the specified Slot's content.
   *
   * @param column Column of the cell to be set.
   * @param row    Row of the cell to be set.
   * @param letter Letter.
   * @param points Points.
   */
  public void setSlotContent(int column, int row, char letter, int points) {
    slots[getGlobalIndex(column, row)].content.setLetter(letter);
    slots[getGlobalIndex(column, row)].content.setPoints(points);
  }

  /**
   * Sets the specified Slot's content with an LetterTile object.
   *
   * @param column Column of the cell to be set.
   * @param row    Row of the cell to be set.
   * @param tile   LetterTile.
   */
  public void setSlotContent(int column, int row, LetterTile tile) {
    slots[getGlobalIndex(column, row)].setContent(tile);
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
   * @param slot Slot.
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
   * @param tile LetterTile.
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
   * Returns an Y coordinate of a specified cell.
   *
   * @param tile Tile that is in Grid.
   * @return Y coordinate (Row).
   */
  public int getCellRow(LetterTile tile) {
    int glInd;
    for (int column = 0; column < size; column++) {
      for (int row = 0; row < size; row++) {
        glInd = getGlobalIndex(column, row);
        if (tile == slots[glInd].content) {
          return row;
        }
      }
    }
    return -1;
  }

  /**
   * Returns an X coordinate of a specified cell.
   *
   * @param tile Tile that is in Grid.
   * @return X coordinate (Column).
   */
  public int getCellColumn(LetterTile tile) {
    int glInd;
    for (int column = 0; column < size; column++) {
      for (int row = 0; row < size; row++) {
        glInd = getGlobalIndex(column, row);
        if (tile == slots[glInd].content) {
          return column;
        }
      }
    }
    return -1;
  }

  /**
   * Get the size of the square GridPane (height=width).
   *
   * @return Number of Slots on each side (height=width).
   */
  public int getSize() {
    return size;
  }

  /**
   * Get the Global Size of the Grid.
   *
   * @return Total number of Slots.
   */
  public int getGlobalSize() {
    return globalSize;
  }

  /**
   * Get a Slot that contains the specified LetterTile.
   *
   * @param tile Slot's content, LetterTile.
   * @return Slot.
   */
  public Slot getSlotThatContains(LetterTile tile) {
    for (int i = 0; i < globalSize; i++) {
      if (slots[i].content == tile) {
        return slots[i];
      }
    }
    return null;
  }

  /**
   * Returns an array of currently present tiles.
   *
   * @return ArrayList of currently present LetterTiles.
   */
  public ArrayList<LetterTile> getTilesInGrid() {
    ArrayList<LetterTile> list = new ArrayList<>();
    for (int i = 0; i < globalSize; i++) {
      if (slots[i].content != null) {
        list.add(slots[i].content);
      }
    }
    return list;
  }

  /**
   * Getter for a specified neighbour of the tile.
   *
   * @param tile      LetterTile.
   * @param neighbour Neighbour LetterTile.
   * @return Specified neighbour of the tile.
   */
  public LetterTile getNeighbourCell(LetterTile tile, String neighbour) {
    int glInd;
    for (int column = 0; column < size; column++) {
      for (int row = 0; row < size; row++) {
        glInd = getGlobalIndex(column, row);
        if (tile == slots[glInd].content) {
          switch (neighbour) {
            case "top":
              if (row != 0) {
                return getSlotContent(column, row - 1);
              } else {
                return null;
              }
            case "right":
              if (column != size) {
                return getSlotContent(column + 1, row);
              } else {
                return null;
              }
            case "bottom":
              if (row != size) {
                return getSlotContent(column, row + 1);
              } else {
                return null;
              }
            case "left":
              if (column != 0) {
                return getSlotContent(column - 1, row);
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
   * Get the most top existing neighbour.
   *
   * @return The most top neighbour (LetterTile).
   */
  public LetterTile getMostTopOf(LetterTile tile) {
    LetterTile current = tile;
    int col = getCellColumn(tile);
    int row = getCellRow(tile);
    System.out.println("@getMostTopOf() - Current: " + current.getLetter());
    if (0 <= col && col < size && 0 < row && row < size) {
      int i = 1;
      while (row - i >= 0 && slots[getGlobalIndex(col, row - i)].content != null) {
        current = slots[getGlobalIndex(col, row - i)].content;
        System.out.println("@getMostTopOf() - Top: " + current.getLetter());
        i++;
      }
    }
    return current;
  }

  /**
   * Get the most right existing neighbour.
   *
   * @return The most right neighbour (LetterTile).
   */
  public LetterTile getMostRightOf(LetterTile tile) {
    LetterTile current = tile;
    int col = getCellColumn(tile);
    int row = getCellRow(tile);
    System.out.println("@getMostRightOf() - Current: " + current.getLetter());
    if (0 <= col && col < size - 1 && 0 <= row && row < size) {
      int i = 1;
      while (col + i <= size - 1 && slots[getGlobalIndex(col + i, row)].content != null) {
        current = slots[getGlobalIndex(col + i, row)].content;
        System.out.println("@getMostRightOf() - Right: " + current.getLetter());
        i++;
      }
    }
    return current;
  }

  /**
   * Get the most bottom existing neighbour.
   *
   * @return The most bottom neighbour (LetterTile).
   */
  public LetterTile getMostBottomOf(LetterTile tile) {
    LetterTile current = tile;
    int col = getCellColumn(tile);
    int row = getCellRow(tile);
    System.out.println("@getMostBottomOf() - Current: " + current.getLetter());
    if (0 <= col && col < size && 0 <= row && row < size - 1) {
      int i = 1;
      while (row + i <= size - 1 && slots[getGlobalIndex(col, row + i)].content != null) {
        current = slots[getGlobalIndex(col, row + i)].content;
        System.out.println("@getMostBottomOf() - Bottom: " + current.getLetter());
        i++;
      }
    }
    return current;
  }

  /**
   * Get the most left existing neighbour.
   *
   * @return The most left neighbour (LetterTile).
   */
  public LetterTile getMostLeftOf(LetterTile tile) {
    LetterTile current = tile;
    int col = getCellColumn(tile);
    int row = getCellRow(tile);
    System.out.println("@getMostLeftOf() - Current: " + current.getLetter());
    if (0 < col && col < size && 0 <= row && row < size) {
      int i = 1;
      while (col - i >= 0 && slots[getGlobalIndex(col - i, row)].content != null) {
        current = slots[getGlobalIndex(col - i, row)].content;
        System.out.println("@getMostLeftOf() - Left: " + current.getLetter());
        i++;
      }
    }
    return current;
  }

  /**
   * Removes the specified Slot's content (based on the Slot object).
   *
   * @param slot Slot, the content of which should be removed.
   */
  public void removeSlotContent(Slot slot) {
    for (int i = 0; i < globalSize; i++) {
      if (slots[i] == slot) {
        slots[i].removeContent();
      }
    }
  }

  /**
   * Removes the specified Slot's content (based on Column/Row indexes).
   *
   * @param column Column (X index).
   * @param row    Row (Y index).
   */
  public void removeSlotContent(int column, int row) {
    slots[getGlobalIndex(column, row)].removeContent();
  }

  /**
   * Adds a Slot to the specified cell.
   *
   * @param slot   Slot.
   * @param column Column (X).
   * @param row    Row (Y).
   */
  public void addSlot(Slot slot, int column, int row) {
    int index = getGlobalIndex(column, row);

    container.add(slot.container, column, row);
    slots[index] = slot;
  }

  public void removeSubWords() {
    if (!words.isEmpty()) {

      for (int k = 0; k < words.size() - 1; k++) {
        Word word1 = words.get(k);

        for (int l = k + 1; l < words.size(); l++) {
          Word word2 = words.get(l);

          if ((word1.isVertical && word2.isVertical) ||
              (word1.isHorizontal && word2.isHorizontal)) {

            if (word1.getCommonLetter(word2) != null) {

              if (word1.containsWord(word2)) {

                System.out.println("the word \"" + word1 + "\" contains \"" + word2 + "\"");
                controller.gridWrapper.getChildren().remove(word2.container);
                words.remove(word2);
              }
              else if (word2.containsWord(word1)) {

                System.out.println("the word \"" + word2 + "\" contains \"" + word1 + "\"");
                controller.gridWrapper.getChildren().remove(word1.container);
                words.remove(word1);
              }
            }
          }

        }

      }
    }
  }

  /**
   * Verifies whether placed words are valid. Generates a pop-up window in case of an invalid
   * input.
   *
   * @return True if input is valid, False otherwise.
   */
  public boolean verifyWordsValidity() {
    System.out.println("\n-----@Grid verifyWordsValidity() START-----");
    boolean res = false;

    ArrayList<Slot> validStartingSlots = new ArrayList<>();

    ArrayList<LetterTile> presentTiles = new ArrayList<>();
    for (LetterTile letterTile : getTilesInGrid()) {
      if (letterTile.isFrozen) {
        presentTiles.add(letterTile);
      }
    }

    if (presentTiles.isEmpty()) {
      System.out.println("Valid Starting Slot is CENTER");

      // Adding the center slot as Starting Point
      validStartingSlots.add(getSlot(size / 2, size / 2));

    } else {
      System.out.println("Valid Starting Slots are SLOTS ALREADY PRESENT IN THE GRID");

      // Searching all frozen words and adding them into Starting Slots Array

      System.out.println("Words:");
      for (Word word : words) {
        System.out.println(word.getWordAsString());
        System.out.println("is frozen: " + word.frozen);
        if (word.frozen) {
          System.out.println("tiles:");
          for (int j = 0; j < word.getWordLength(); j++) {
            System.out.print(word.getLetter(j).getLetter() + " ");
            validStartingSlots.add(word.getLetter(j).slot);
          }
          System.out.print("\n");
        }
      }

      removeSubWords();
    }

    int validWords = 0;
    int wordsUsingStartSlot = 0;

    for (Word word : words) {
      System.out.println("current word: " + word.getWordAsString());
      System.out.println("is frozen: " + word.frozen);
      System.out.println("is newly placed: " + word.newlyPlaced);

      // verifying if all words present in the grid are valid
      // (by counting the number of valid words and comparing it to number of all words)
      if (word.isValid()) {
        validWords++;
      }

      System.out.println("validStartingSlots: ");
      for (Slot slot : validStartingSlots) {
        System.out.println("Current valid starting slot: " + slot + " " + slot.content.getLetter());
        // we want to find out whether at least one starting slot is used by a word
        if (word.contains(slot.content) && !slot.isFree()) {
          System.out.print(word.getWordAsString() + " uses that slot");
          System.out.print("check next newly placed words...");

          wordsUsingStartSlot++;
          break;
        } else {
          System.out.print(word.getWordAsString() + " doesn't use that slot");
          System.out.print("check if " + word.getWordAsString() + " uses another start slot...");
        }
      }
    }

    // ======== START checking if every tile is in the same direction ========
    ArrayList<LetterTile> justPlacedTiles = new ArrayList<>();
    for (LetterTile letterTile : getTilesInGrid()) {
      if (!letterTile.isFrozen) {
        justPlacedTiles.add(letterTile);
      }
    }

    boolean sameDirection = false;
    if (!justPlacedTiles.isEmpty()) {
      // there are newly placed tiles

      LetterTile letterTile1 = justPlacedTiles.get(0);
      int col1 = getCellColumn(letterTile1);
      int row1 = getCellRow(letterTile1);

      int sameRowCount = 0;
      int sameColCount = 0;

      // it's enough to compare 1 tile with every other tile
      for (int j = 1; j < justPlacedTiles.size(); j++) {
        LetterTile letterTile2 = justPlacedTiles.get(j);
        int col2 = getCellColumn(letterTile2);
        int row2 = getCellRow(letterTile2);

        if (row1 == row2 && col1 != col2) {
          // 2 tiles are horizontally positioned on the same row

          sameRowCount++;
        } else if (col1 == col2 && row1 != row2) {
          // 2 tiles are vertically positioned in the same column

          sameColCount++;
        } else {
          // 2 tiles aren't in the same direction

          sameDirection = false;
          break;
        }
      }

      // comparing
      // only 1 of these 2 expressions in IF should be TRUE (they cannot be both true)
      if (sameRowCount == justPlacedTiles.size() - 1
          || sameColCount == justPlacedTiles.size() - 1) {
        sameDirection = true;
      }

    }
    else {
      // there are no newly placed tiles

      sameDirection = true;
    }
    // ======== END checking if every tile is in the same direction ========

    boolean noSingleTiles = true;
    // Checking if there are Single LetterTiles in the Grid
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

    System.out.println("words using starting slots: " + wordsUsingStartSlot);
    System.out.println("grid.words.size(): " + words.size());

    String errorMessage = "";
    if (wordsUsingStartSlot == words.size()) {
      // at least one of the previous letters is used by ALL new words
      System.out.println("ALL WORDS ARE USING AT LEAST 1 OF PREVIOUS LETTERS !");

      if (noSingleTiles) {
        // there is no single placed letter tiles
        System.out.println("ALL WORDS HAVE LENGTH >= 2 !");

        if (validWords == words.size()) {
          // All words present in the grid are valid
          System.out.println("ALL WORDS ARE VALID !");

          if (sameDirection) {
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

          errorMessage = "ALL NEW WORDS SHOULD BE VALID !";
          System.err.println(errorMessage);
        }

      } else {
        errorMessage = "ALL WORDS SHOULD HAVE LENGTH >= 2 !";
        System.err.println(errorMessage);
      }

    } else {
      errorMessage = "ALL WORDS SHOULD USE AT LEAST 1 OF PREVIOUS LETTERS !";
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
      popup.setViewOrder(--controller.minViewOrder);

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
      System.out.println("WORD-S ARE VALIDATED");

      res = true;

      freezeWords();
    }

    System.out.println("return " + res);
    System.out.println("-----@Grid verifyWordsValidity() END-----");
    return res;
  }

  /**
   * Freezes all currently present words.
   */
  private void freezeWords() {
    System.out.println("\n-----@Grid freezeWords() START-----");

    for (Word word : words) {
      // words present in the grid

      for (int l = 0; l < word.getWordLength(); l++) {
        // letter tiles of the current word

        word.getLetter(l).container.getStyleClass().clear();
        word.getLetter(l).container.getStyleClass().add("letter-btn");
        word.getLetter(l).container.getStyleClass().add("letter-btn-frozen");

//        if (word.isHorizontal()) {
//
//          if (l == 0) {
//            word.getLetter(l).container.getStyleClass().add("letter-btn-hor-min");
//          } else if (l < word.getWordLength() - 1) {
//            word.getLetter(l).container.getStyleClass().add("letter-btn-hor-mid");
//          } else if (l == word.getWordLength() - 1) {
//            word.getLetter(l).container.getStyleClass().add("letter-btn-hor-max");
//          }
//
////          if (l < word.getWordLength() - 1) {
////            StackPane gap = new StackPane();
////            gap.setViewOrder(--controller.minViewOrder);
////            gap.setPrefSize(padSize + 1, word.getLetter(l).container.getHeight());
////            gap.setMinSize(padSize + 1, word.getLetter(l).container.getHeight());
////            gap.setMaxSize(padSize + 1, word.getLetter(l).container.getHeight());
////            gap.getStyleClass().add("gap-hor");
////
////            controller.gridWrapper.getChildren().add(gap);
////
////            gap.setTranslateX(
////                word.getLetter(l).slot.container.getBoundsInParent().getMaxX() - 1);
////            gap.setTranslateY(
////                word.getLetter(l).slot.container.getBoundsInParent().getMinY());
////
////            word.getLetter(l).slot.container.boundsInParentProperty()
////                .addListener((obs, oldValue, newValue) -> {
////                  gap.setTranslateX(newValue.getMaxX() - 1);
////                  gap.setTranslateY(newValue.getMinY());
////                });
////
////            gap.prefHeightProperty().bind(word.getLetter(l).container.heightProperty());
////            gap.minHeightProperty().bind(word.getLetter(l).container.heightProperty());
////            gap.maxHeightProperty().bind(word.getLetter(l).container.heightProperty());
////          }
//
//        } else if (word.isVertical()) {
//
//          if (l == 0) {
//            word.getLetter(l).container.getStyleClass().add("letter-btn-ver-min");
//          } else if (l < word.getWordLength() - 1) {
//            word.getLetter(l).container.getStyleClass().add("letter-btn-ver-mid");
//          } else if (l == word.getWordLength() - 1) {
//            word.getLetter(l).container.getStyleClass().add("letter-btn-ver-max");
//          }
//
////          if (l < word.getWordLength() - 1) {
////            StackPane gap = new StackPane();
////            gap.setPrefSize(word.getLetter(l).container.getWidth(), padSize + 1);
////            gap.setMinSize(word.getLetter(l).container.getWidth(), padSize + 1);
////            gap.setMaxSize(word.getLetter(l).container.getWidth(), padSize + 1);
////            gap.getStyleClass().add("gap-ver");
////
////            controller.gridWrapper.getChildren().add(gap);
////
////            gap.setTranslateX(
////                word.getLetter(l).slot.container.getBoundsInParent().getMinX() + 1);
////            gap.setTranslateY(
////                word.getLetter(l).slot.container.getBoundsInParent().getMaxY() - 1);
////
////            word.getLetter(l).slot.container.boundsInParentProperty()
////                .addListener((obs, oldValue, newValue) -> {
////                  gap.setTranslateX(newValue.getMinX() + 1);
////                  gap.setTranslateY(newValue.getMaxY() - 1);
////                });
////
////            gap.prefWidthProperty().bind(word.getLetter(l).container.widthProperty());
////            gap.minWidthProperty().bind(word.getLetter(l).container.widthProperty());
////            gap.maxWidthProperty().bind(word.getLetter(l).container.widthProperty());
////          }
//
//        }

        word.getLetter(l).slot.container.setEffect(null);
        word.getLetter(l).slot.container.setMouseTransparent(true);
        word.getLetter(l).slot.isFrozen = true;
        word.getLetter(l).container.setMouseTransparent(true);
        word.getLetter(l).isFrozen = true;
        word.getLetter(l).isNewlyPlaced = true;
      }

      word.frozen = true;
//      controller.gridWrapper.getChildren().remove(words.get(w).container);
      word.container.getStyleClass().clear();
      word.container.getChildren().clear();
    }

    System.out.println("-----@Grid freezeWords() END-----");
  }


}
