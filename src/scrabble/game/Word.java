package scrabble.game;

import java.util.LinkedList;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import scrabble.GameController;
import scrabble.model.Dictionary;

/**
 * Word class that is needed to detect, store and verify words.
 *
 * @author Eldar Kasmamytov
 */
public class Word {

  GameController controller;

  private LinkedList<LetterTile> w;
  private int wordLength;
  private int points;
  private boolean isValid;
  private boolean isHorizontal;
  private boolean isVertical;

  AnchorPane container;
  Label pointsLabel;

  private void initShape() {
    System.out.println("@ initShape()");

    boolean createNewBox = true;
    if (!controller.wordsInGrid.isEmpty()) {

      System.out.println("@ initShape() - wordsGrid NOT empty");

      for (int k = 0; k < controller.wordsInGrid.size(); k++) {
        if (this.containsWord(controller.wordsInGrid.get(k))) {
          System.out.println("@ initShape() - the word contains " + k + "th word in wordsGrid");
          controller.grid.container.getChildren().remove(controller.wordsInGrid.get(k).container);
          createNewBox = true;
        }
      }
    }

    if (createNewBox) {

      System.out.println("@ initShape() - createNewBox");
      LetterTile firstLetter = w.get(0);

      if (isHorizontal) {
        // Creating a highlighting effect for the word
        System.out.println("HORIZONTAL - Creating a highlighting effect for the word");

        container = new AnchorPane();
        if (this.isValid()) {
          container.getStyleClass().add("valid-word");
        } else {
          container.getStyleClass().add("invalid-word");
        }

        // Creating a label with word's points
        pointsLabel = new Label(String.valueOf(this.getPoints()));
        if (this.isValid()) {
          pointsLabel.getStyleClass().add("word-points-label");
        } else {
          pointsLabel.getStyleClass().add("word-points-label-invalid");
        }
        pointsLabel.setPrefSize(30, 30);

        // Adjusting the size of the highlighted word box
        double sizeX = this.getWordLength() * firstLetter.container.getMaxWidth()
            + (this.getWordLength() - 1) * controller.grid.padSize;
        double sizeY = firstLetter.container.getMaxWidth();
        container.setPrefSize(sizeX, sizeY);
        container.setMinSize(sizeX, sizeY);
        container.setMaxSize(sizeX, sizeY);

        firstLetter.container.widthProperty().addListener((observable, oldValue, newValue) -> {
          double newWidth = this.getWordLength() * newValue.doubleValue()
              + (this.getWordLength() - 1) * controller.grid.padSize;
          container.setMinWidth(newWidth);
          container.setPrefWidth(newWidth);
          container.setMaxWidth(newWidth);
        });

        firstLetter.container.heightProperty().addListener((observable, oldValue, newValue) -> {
          container.setMinHeight(newValue.doubleValue());
          container.setPrefHeight(newValue.doubleValue());
          container.setMaxHeight(newValue.doubleValue());
        });

        System.out.println("\nfirstLetter width: " + firstLetter.container.getMaxWidth());
        System.out.println("firstLetter height: " + firstLetter.container.getMaxHeight());

        System.out.println("\nwidth: " + container.getWidth());
        System.out.println("height: " + container.getHeight());

        // Adjusting position of the Highlighted Box
        container
            .setTranslateX(container.getMaxWidth() / 2 - firstLetter.container.getMaxWidth() / 2);

        // Adding Points Label to Highlighted Box
        container.getChildren().add(pointsLabel);
        // Adjusting The Points Label position in the Highlighted Box
        AnchorPane.setRightAnchor(pointsLabel, -15.0);
        AnchorPane.setTopAnchor(pointsLabel, -15.0);

        container.setMouseTransparent(true);

        // Adding the Highlighted Box to the Grid Pane
        Slot slot = controller.grid.getSlotThatContains(firstLetter);
        slot.container.getChildren().add(container);

        controller.wordsInGrid.add(this);
      }

      if (isVertical) {
        // Creating a highlighting effect for the word
        container = new AnchorPane();
        if (this.isValid()) {
          container.getStyleClass().add("valid-word");
        } else {
          container.getStyleClass().add("invalid-word");
        }

        // Creating a label with word's points
        pointsLabel = new Label(String.valueOf(this.getPoints()));
        if (this.isValid()) {
          pointsLabel.getStyleClass().add("word-points-label");
        } else {
          pointsLabel.getStyleClass().add("word-points-label-invalid");
        }
        pointsLabel.setPrefSize(30, 30);

        // Adjusting the size of the highlighted word box
        double sizeX = firstLetter.container.getMaxWidth();
        double sizeY = this.getWordLength() * firstLetter.container.getMaxWidth()
            + (this.getWordLength() - 1) * controller.grid.padSize;
        container.setPrefSize(sizeX, sizeY);
        container.setMinSize(sizeX, sizeY);
        container.setMaxSize(sizeX, sizeY);

        firstLetter.container.widthProperty().addListener((observable, oldValue, newValue) -> {
          container.setMinWidth(newValue.doubleValue());
          container.setPrefWidth(newValue.doubleValue());
          container.setMaxWidth(newValue.doubleValue());
        });

        firstLetter.container.heightProperty().addListener((observable, oldValue, newValue) -> {
          double newHeight = this.getWordLength() * newValue.doubleValue()
              + (this.getWordLength() - 1) * controller.grid.padSize;
          container.setMinHeight(newHeight);
          container.setPrefHeight(newHeight);
          container.setMaxHeight(newHeight);
        });

        System.out.println("\nwidth: " + container.getWidth());
        System.out.println("height: " + container.getHeight());

        // Adjusting position of the Highlighted Box
        container
            .setTranslateY(container.getMaxHeight() / 2 - firstLetter.container.getMaxWidth() / 2);

        // Adding Points Label to Highlighted Box
        container.getChildren().add(pointsLabel);
        // Adjusting The Points Label position in the Highlighted Box
        AnchorPane.setRightAnchor(pointsLabel, -15.0);
        AnchorPane.setBottomAnchor(pointsLabel, -15.0);

        container.setMouseTransparent(true);

        // Adding the Highlighted Box to the Grid Pane
        Slot slot = controller.grid.getSlotThatContains(firstLetter);
        slot.container.getChildren().add(container);

        controller.wordsInGrid.add(this);
      }
    }
  }

  /**
   * Default Constructor.
   */
  public Word(GameController controller) {
    w = new LinkedList<LetterTile>();
    points = 0;
    wordLength = 0;
    isValid = false;
    isHorizontal = false;
    isVertical = false;

    this.controller = controller;

    initShape();
  }

  /**
   * Most important Constructor. Fills in all data members of the Word class. It checks whether the
   * word has no empty gaps, i.e. the word is FULL. If thw word is full, then it checks if the word
   * is VALID.
   *
   * @param start      Starting point of the Word (First Letter)
   * @param end        Ending point of the Word (Last Letter)
   * @param controller Controller
   */
  public Word(LetterTile start, LetterTile end, GameController controller) {
    w = new LinkedList<LetterTile>();
    points = 0;
    wordLength = 0;
    isValid = false;

    this.controller = controller;

    int startX = controller.grid.getCellRow(start);
    int startY = controller.grid.getCellColumn(start);
    System.out.println("\n@Word - Start Cell (" + startX + ", " + startY + ")");

    int endX = controller.grid.getCellRow(end);
    int endY = controller.grid.getCellColumn(end);
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

          if (controller.grid.getSlot(i, startY).isFree()) {
            // NOT A WORD - Empty Gaps between 2 Cells
            System.out
                .println("\n@Word - HORIZONTAL - Letter (" + i + ", " + startY + ") is empty");
            fullWord = false;
            break;
          } else {
            System.out
                .println("\n@Word - HORIZONTAL - Letter: (" + i + ", " + startY + ") is filled in");
            w.add(controller.grid.getSlotContent(i, startY));
            System.out.println("@Word - HORIZONTAL - Added: (" + i + ", " + startY + ")");
            points += controller.grid.getSlotContent(i, startY).getPoints();
            wordLength++;
            System.out.println(
                "@Word - HORIZONTAL - Letter (" + i + ", " + startY + ") = " + w.getLast()
                    .getLetter());
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

          if (controller.grid.getSlot(startX, j).isFree()) {
            // NOT A WORD - Empty Gaps between 2 Cells
            System.out.println("\n@Word - VERTICAL - in for: (" + startX + ", " + j + ") is empty");
            fullWord = false;
            break;
          } else {
            w.add(controller.grid.getSlotContent(startX, j));
            System.out.println("@Word - VERTICAL - Added: (" + startX + ", " + j + ")");
            points += controller.grid.getSlotContent(startX, j).getPoints();
            wordLength++;
            System.out.println(
                "@Word - VERTICAL - Letter (" + startX + ", " + j + ") = " + w.getLast()
                    .getLetter());
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

    initShape();
  }

  public boolean contains(LetterTile tile) {
    for (int i = 0; i < wordLength; i++) {
      if (w.get(i) == tile) {
        return true;
      }
    }
    return false;
  }

  public LetterTile getLetter(int i) {
    return w.get(i);
  }

  public boolean isPartOf(Word word) {
    boolean result = false;
    if (this.wordLength <= word.getWordLength()) {
      int matchLetters = 0;
      for (int i = 0; i < word.getWordLength(); i++) {
        if (word.getLetter(i) == this.getLetter(i % this.wordLength)) {
          matchLetters++;
          if (matchLetters == this.wordLength) {
            result = true;
            break;
          }
        } else {
          if (matchLetters > 0) {
            matchLetters--;
          }
          result = false;
        }
      }
    }
    return result;
  }

  public boolean containsWord(Word word) {
    boolean result = false;
    if (this.wordLength >= word.getWordLength()) {
      result = word.isPartOf(this);
    }
    return result;
  }

  public LetterTile getCommonLetter(Word word) {
    if (!containsWord(word) || !isPartOf(word)) {
      for (int i = 0; i < wordLength; i++) {
        for (int j = 0; j < word.wordLength; i++) {
          if (w.get(i) == word.w.get(j)) {
            return w.get(i);
          }
        }
      }
    }
    return null;
  }

  /**
   * Specifies the cell as an ending point of a word.
   *
   * @param tile Cell in Grid, containing a letter
   */
  public void addCellEnd(LetterTile tile) {
    w.addLast(tile);
  }

  /**
   * Specifies the cell as a starting point of a word.
   *
   * @param tile tile in Grid, containing a letter
   */
  public void addCellStart(LetterTile tile) {
    w.addFirst(tile);
  }

  /**
   * Removes specified Cell from the word.
   *
   * @param tile tile in Grid, containing a letter
   */
  public void removeCell(LetterTile tile) {
    w.remove(tile);
  }

  /**
   * Returns the whole word as a String.
   *
   * @return Word as "String"
   */
  public String getWordAsString() {
    String str = new String("");
    for (int i = 0; i < wordLength; i++) {
      str += w.get(i).getLetter();
    }
    return str;
  }

  /**
   * Getter of Points.
   *
   * @return Total points of the Word.
   */
  public int getPoints() {
    return points;
  }

  /**
   * Getter of Length.
   *
   * @return Total Length of the Word (Number of Letters)
   */
  public int getWordLength() {
    return wordLength;
  }

  /**
   * Returns True if the Word is valid, otherwise False.
   *
   * @return isValid
   */
  public boolean isValid() {
    return isValid;
  }

  /**
   * Returns True if the Word is horizontal, otherwise False.
   *
   * @return isHorizontal
   */
  public boolean isHorizontal() {
    return isHorizontal;
  }

  /**
   * Returns True if the Word is vertical, otherwise False.
   *
   * @return isVertical
   */
  public boolean isVertical() {
    return isVertical;
  }

  public LetterTile getFirst() {
    return w.getFirst();
  }

  public LetterTile getLast() {
    return w.getLast();
  }
}
