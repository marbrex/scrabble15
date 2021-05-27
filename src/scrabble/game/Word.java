package scrabble.game;

import java.util.LinkedList;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import scrabble.GameController;
import scrabble.model.Dictionary;

/**
 * <h1>scrabble.game.Word</h1>
 *
 * <p>Word class that is needed to detect, store and verify words.</p>
 *
 * @author Eldar Kasmamytov
 */
public class Word {

  GameController controller;

  private LinkedList<LetterTile> w;
  private int wordLength;
  private int points;
  private boolean isValid;
  boolean isHorizontal;
  boolean isVertical;
  public boolean frozen;
  public boolean newlyPlaced;

  private int multiplierValue;
  private boolean multiplier;

  public AnchorPane container;
  Label pointsLabel;

  /**
   * Initiates the FRONT-end part (AnchorPane that contains a Label of points)
   */
  private void initShape() {
    System.out.println("\n-----@Word initShape() START-----");
    System.out.println("the word is: ");
    display();
    System.out.print("\n");

    if (!controller.grid.words.isEmpty()) {

      System.out.println("wordsGrid NOT empty");

      for (int k = 0; k < controller.grid.words.size(); k++) {

        if ((this.isVertical && controller.grid.words.get(k).isVertical) || (this.isHorizontal
            && controller.grid.words.get(k).isHorizontal)) {

          if (this.getCommonLetter(controller.grid.words.get(k)) != null) {

            if (this.containsWord(controller.grid.words.get(k))) {

              System.out.println("the word contains " + k + "th word in wordsGrid");
              controller.gridWrapper.getChildren().remove(controller.grid.words.get(k).container);
            }
          }
        }
      }
    }

    System.out.println("createNewBox");

    for (LetterTile l : w) {
      System.out.println("next letter: " + l.getLetter());
    }
    LetterTile firstLetter = w.get(0);
    LetterTile lastLetter = w.getLast();

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

    Bounds boundsFirst = firstLetter.slot.container.getBoundsInParent();
    System.out.println("Bounds First: " + boundsFirst);
    Bounds boundsLast = lastLetter.slot.container.getBoundsInParent();
    System.out.println("Bounds Last: " + boundsLast);

    // Adjusting the coordinates (in Pixels) of the highlighted word box
    container.setTranslateX(boundsFirst.getMinX());
    container.setTranslateY(boundsFirst.getMinY());

    // Adjusting the size (Width and Height) of the highlighted word box
    double sizeX = boundsLast.getMaxX() - boundsFirst.getMinX();
    double sizeY = boundsLast.getMaxY() - boundsFirst.getMinY();
    container.setPrefSize(sizeX, sizeY);
    container.setMinSize(sizeX, sizeY);
    container.setMaxSize(sizeX, sizeY);

    // Bind the position of the box to the first letter's slot
    firstLetter.slot.container.boundsInParentProperty().addListener((obs, oldValue, newValue) -> {
      container.setTranslateX(newValue.getMinX());
      container.setTranslateY(newValue.getMinY());
    });

    System.out.println("width: " + container.getWidth());
    System.out.println("height: " + container.getHeight());

    // Adding Points Label to Highlighted Box
    container.getChildren().add(pointsLabel);
    // Adjusting The Points Label position in the Highlighted Box
    AnchorPane.setRightAnchor(pointsLabel, -15.0);
    AnchorPane.setTopAnchor(pointsLabel, -15.0);

    container.setMouseTransparent(true);
    container.setViewOrder(--controller.minViewOrder);

    // Adding the Highlighted Box to the GridPane's wrapper
    controller.gridWrapper.getChildren().add(container);

    controller.grid.words.add(this);
    System.out.print("grid.words: ");
    controller.grid.words.forEach(word -> {
      System.out.print(word.getWordAsString() + " ");
    });
    System.out.print("\n");
    System.out.println("-----@Word initShape() END-----");
  }

  /**
   * Default Constructor.
   *
   * @param controller GameController
   */
  public Word(GameController controller) {
    w = new LinkedList<>();
    points = 0;
    wordLength = 0;
    isValid = false;
    isHorizontal = false;
    isVertical = false;
    frozen = false;
    newlyPlaced = true;

    multiplier = false;
    multiplierValue = 1;

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
    System.out.println("\n-----@Word Constructor START-----");
    w = new LinkedList<>();
    points = 0;
    wordLength = 0;
    isValid = false;
    frozen = false;
    newlyPlaced = true;

    multiplier = false;
    multiplierValue = 1;

    this.controller = controller;

    int startX = controller.grid.getCellColumn(start);
    int startY = controller.grid.getCellRow(start);
    System.out.println("Start Cell (" + startX + ", " + startY + ")");

    int endX = controller.grid.getCellColumn(end);
    int endY = controller.grid.getCellRow(end);
    System.out.println("End Cell (" + endX + ", " + endY + ")");

    if (startY == endY) {
      // we know that's a Horizontal word

      if (startX < endX) {
        // we know that the length is at least 2 Letters
        System.out.println("HORIZONTAL - word (>= 2 letters)");

        isHorizontal = true;

        boolean fullWord = true;
        for (int i = startX; i <= endX; i++) {
          System.out.println("HORIZONTAL - in for: (" + i + ", " + startY + ")");

          if (controller.grid.getSlot(i, startY).isFree()) {
            // NOT A WORD - Empty Gaps between 2 Cells
            System.out
                .println("HORIZONTAL - Letter (" + i + ", " + startY + ") is empty");
            fullWord = false;
            break;
          } else {
            System.out
                .println("HORIZONTAL - Letter: (" + i + ", " + startY + ") is filled in");
            w.add(controller.grid.getSlotContent(i, startY));
            System.out.println("HORIZONTAL - Added: (" + i + ", " + startY + ")");

            Multiplier mult = controller.grid.getSlot(i, startY).getMultiplier();
            if (mult.getScope().equals("LETTER")) {
              if (!controller.grid.getSlotContent(i, startY).isBlank) {
                if (!controller.grid.getSlot(i, startY).isFrozen) {
                  points += controller.grid.getSlotContent(i, startY).getPoints() * mult.getValue();
                } else {
                  points += controller.grid.getSlotContent(i, startY).getPoints();
                }
              }
            } else if (mult.getScope().equals("WORD")) {
              if (!controller.grid.getSlot(i, startY).isFrozen) {
                multiplier = true;
                multiplierValue *= mult.getValue();
              }
              if (!controller.grid.getSlotContent(i, startY).isBlank) {
                points += controller.grid.getSlotContent(i, startY).getPoints();
              }
            } else {
              if (!controller.grid.getSlotContent(i, startY).isBlank) {
                points += controller.grid.getSlotContent(i, startY).getPoints();
              }
            }

            wordLength++;
            System.out.println(
                "HORIZONTAL - Letter (" + i + ", " + startY + ") = " + w.getLast()
                    .getLetter());
            System.out.println("HORIZONTAL - Points: " + points);
            System.out.println("HORIZONTAL - Length: " + wordLength);
          }
        }

        if (fullWord) {
          // Every Letter is Filled In
          // => We can proceed to WORD VALIDATION
          System.out.println("HORIZONTAL - Full word");
          System.out.println("HORIZONTAL - The Word is: " + getWordAsString());

          if (multiplier) {
            points *= multiplierValue;
          }

          // The Dictionary has to be set only once, otherwise it does not work
          // The Dictionary is already set in "initialize()" function of ScrabbleController
          isValid = Dictionary.matches(this);
          System.out.println("HORIZONTAL - is valid: " + isValid);
        }
      }
    }

    if (startX == endX) {
      // we know that's a Vertical word

      if (startY < endY) {
        // we know that the length is at least 2 Letters
        System.out.println("VERTICAL - word (>= 2 letters)");

        isVertical = true;

        boolean fullWord = true;
        for (int j = startY; j <= endY; j++) {
          System.out.println("VERTICAL - in for: (" + startX + ", " + j + ")");

          if (controller.grid.getSlot(startX, j).isFree()) {
            // NOT A WORD - Empty Gaps between 2 Cells
            System.out.println("VERTICAL - in for: (" + startX + ", " + j + ") is empty");
            fullWord = false;
            break;
          } else {
            w.add(controller.grid.getSlotContent(startX, j));
            System.out.println("VERTICAL - Added: (" + startX + ", " + j + ")");

            Multiplier mult = controller.grid.getSlot(startX, j).getMultiplier();
            if (mult.getScope().equals("LETTER")) {
              if (!controller.grid.getSlotContent(startX, j).isBlank) {
                if (!controller.grid.getSlot(startX, j).isFrozen) {
                  points += controller.grid.getSlotContent(startX, j).getPoints() * mult.getValue();
                } else {
                  points += controller.grid.getSlotContent(startX, j).getPoints();
                }
              }
            } else if (mult.getScope().equals("WORD")) {
              if (!controller.grid.getSlot(startX, j).isFrozen) {
                multiplier = true;
                multiplierValue *= mult.getValue();
              }
              if (!controller.grid.getSlotContent(startX, j).isBlank) {
                points += controller.grid.getSlotContent(startX, j).getPoints();
              }
            } else {
              if (!controller.grid.getSlotContent(startX, j).isBlank) {
                points += controller.grid.getSlotContent(startX, j).getPoints();
              }
            }

            wordLength++;
            System.out.println(
                "VERTICAL - Letter (" + startX + ", " + j + ") = " + w.getLast()
                    .getLetter());
            System.out.println("VERTICAL - Points: " + points);
            System.out.println("VERTICAL - Length: " + wordLength);
          }
        }

        if (fullWord) {
          // Every Letter is Filled In
          // => We can proceed to WORD VALIDATION
          System.out.println("VERTICAL - Full word");
          System.out.println("VERTICAL - The Word is: " + getWordAsString());

          if (multiplier) {
            points *= multiplierValue;
          }

          // The Dictionary has to be set only once, otherwise it does not work
          // The Dictionary is already set in "initialize()" function of ScrabbleController
          isValid = Dictionary.matches(this);
          System.out.println("VERTICAL - is valid: " + isValid);
        }
      }
    }

    initShape();

    System.out.println("-----@Word Constructor END-----");
  }

  /**
   * Checks whether thw Word contains the specified tile
   *
   * @param tile LetterTile
   * @return TRUE if contains, FALSE otherwise
   */
  public boolean contains(LetterTile tile) {
    System.out.println("\n-----@Word " + getWordAsString() + " contains(" + tile.getLetter() + ") START-----");
    for (int i = 0; i < wordLength; i++) {
      System.out.println("Comparing: ");
      System.out.println(w.get(i) + " - " + w.get(i).getLetter());
      System.out.println(tile + " - " + tile.getLetter());
      if (w.get(i).equals(tile)) {
        System.out.println("return true ");
        System.out.println("-----@Word contains(LetterTile) END-----");
        return true;
      }
    }
    System.out.println("return false ");
    System.out.println("-----@Word " + getWordAsString() + " contains(" + tile.getLetter() + ") END-----");
    return false;
  }

  /**
   * Get a LetterTile at index "i"
   *
   * @param i Index in the Word
   * @return LetterTile at position "i"
   */
  public LetterTile getLetter(int i) {
    return w.get(i);
  }

  /**
   * Checks whether the Word is part of another Word.
   *
   * @param word Word (length should be >= of this Word)
   * @return TRUE if is part, FALSE otherwise
   */
  public boolean isPartOf(Word word) {
    boolean result = false;
    System.out.println("\n@isPartOf()");
    if (this.wordLength <= word.getWordLength()) {
      int matchLetters = 0;
      int ixFirstLtr = 0;
      System.out.println("@isPartOf() - ixFirstLtr = " + ixFirstLtr);
      System.out.println("@isPartOf() - size of outer = " + word.getWordLength());
      while (word.getLetter(ixFirstLtr) != this.getLetter(0)) {
        if (ixFirstLtr < word.getWordLength()) {
          ixFirstLtr++;
        }
        System.out.println("@isPartOf() - ixFirstLtr = " + ixFirstLtr);
      }
      System.out.println("\n@isPartOf() - starting index: " + ixFirstLtr);
      for (int i = 0; i < this.getWordLength(); i++) {
        System.out
            .println("\n@isPartOf() - Outer word's next letter = " + word.getLetter(i).getLetter());
        System.out.println(
            "@isPartOf() - Inner word's next letter = " + getLetter(i % this.wordLength)
                .getLetter());
        if (word.getLetter(ixFirstLtr).getLetter() == this.getLetter(i).getLetter()) {
          matchLetters++;
          System.out.println("@isPartOf() - +1 match letters (" + matchLetters + ")");
          if (matchLetters == this.wordLength) {
            System.out.println("@isPartOf() - True!");
            result = true;
            break;
          }
        } else {
          if (matchLetters > 0) {
            matchLetters--;
          }
          result = false;
        }
        ixFirstLtr++;
      }
    }
    return result;
  }

  /**
   * Checks whether the Word contains another Word.
   *
   * @param word Word (length should be <= of this Word)
   * @return TRUE if contains, FALSE otherwise
   */
  public boolean containsWord(Word word) {
    boolean result = false;
    System.out.println("\n@containsWord()");
    if (this.wordLength >= word.getWordLength()) {
      System.out.println("\n@containsWord() - words' lengths are ok");
      result = word.isPartOf(this);
    }
    return result;
  }

  /**
   * Returns a common LetterTile of two Words.
   *
   * @param word Another Word
   * @return Common LetterTile
   */
  public LetterTile getCommonLetter(Word word) {
    System.out.println("@getCommonLetter");
//    if (!containsWord(word) || !isPartOf(word)) {
    for (int i = 0; i < wordLength; i++) {
      for (int j = 0; j < word.wordLength; j++) {
        if (w.get(i) == word.w.get(j)) {
          return w.get(i);
        }
      }
    }
//    }
    return null;
  }

  public void display() {
    for (LetterTile l : w) {
      System.out.print(l.getLetter());
    }
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
    String str = "";
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

  /**
   * Get First LetterTile
   *
   * @return First LetterTile
   */
  public LetterTile getFirst() {
    return w.getFirst();
  }

  /**
   * Get Last LetterTile
   *
   * @return Last LetterTile
   */
  public LetterTile getLast() {
    return w.getLast();
  }

  public void setMouseTransparent(boolean value) {
    container.setMouseTransparent(value);
  }

  public void removeSubWords() {
    if (!controller.grid.words.isEmpty()) {

      System.out.println("wordsGrid NOT empty");

      for (int k = 0; k < controller.grid.words.size(); k++) {

        if ((this.isVertical && controller.grid.words.get(k).isVertical) || (this.isHorizontal
            && controller.grid.words.get(k).isHorizontal)) {

          if (this.getCommonLetter(controller.grid.words.get(k)) != null) {

            if (this.containsWord(controller.grid.words.get(k))) {

              System.out.println("the word contains " + k + "th word in wordsGrid");
              controller.gridWrapper.getChildren().remove(controller.grid.words.get(k).container);
              controller.grid.words.remove(k);
            }
          }
        }
      }
    }
  }
}
