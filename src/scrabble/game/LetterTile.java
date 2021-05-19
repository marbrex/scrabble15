package scrabble.game;

import java.io.Serializable;
import java.util.ArrayList;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import scrabble.GameController;

/**
 * <h1>scrabble.game.LetterTile</h1>
 *
 * <p>This class represents Tiles with letters and points.</p>
 *
 * @author Eldar Kasmamytov
 */
public class LetterTile implements Serializable {

  final GameController controller;

  private double cellSize;

  AnchorPane container;
  private Label letter;
  private Label points;
  boolean isBlank;
  public boolean isFrozen;

  Slot slot;

  // Neighbours
  private LetterTile top;
  private LetterTile right;
  private LetterTile bottom;
  private LetterTile left;

  private void moveTileTo(Slot pointedSlot) {
    // Creating a new LetterTile
    if (isBlank) {
      // blank letterTile

      // Setting visible a popup window
      controller.popupBlankBlock.setVisible(true);
      controller.popupBlankBlock.setViewOrder(--controller.minViewOrder);

      LetterBag bag = LetterBag.getInstance();
      for (int j = 0; j < bag.getAlphabetSize(); j++) {
        char l = bag.getLetterInAlphabet(j);
        int v = bag.getValueInAlphabet(j);

        LetterTile ltrTile = new LetterTile(l, v, cellSize, controller);
        ltrTile.setPointsVisible(false);
        ltrTile.isBlank = true;
        ltrTile.container.setOnDragDetected(null);
        ltrTile.container.setOnDragDone(null);
        ltrTile.container.setOnMouseDragged(null);
        ltrTile.container.setOnMouseReleased(null);
        ltrTile.container.setOnMouseClicked(clickEvent -> {
          System.out.println(ltrTile + " - @onMouseClicked");

          // Creating a new Letter letterTile
          LetterTile tile = new LetterTile(l, v, controller.grid.cellSize, controller);

          System.out.println(ltrTile + " - @onMouseClicked - created a tile");

          tile.setLetterVisible(true);
          tile.setPointsVisible(false);
          tile.isBlank = true;

          System.out.println(ltrTile + " - @onMouseClicked - set the tile");

          controller.popupBlankBlock.setVisible(false);
          controller.okBtn.setDisable(false);

          System.out.println(ltrTile + " - @onMouseClicked - removed popup and turned on OK btn");

          // Adding the Letter to the Slot
          pointedSlot.setContent(tile);

          System.out.println(ltrTile + " - @onMouseClicked - Added the Letter to the Slot");

          // Checking if there are any Neighbours
          // Getting First and Last letter, both for Horizontal and Vertical
          LetterTile mostTop = controller.grid.getMostTopOf(tile);
          LetterTile mostRight = controller.grid.getMostRightOf(tile);
          LetterTile mostBottom = controller.grid.getMostBottomOf(tile);
          LetterTile mostLeft = controller.grid.getMostLeftOf(tile);

          System.out.println(ltrTile + " - @onMouseClicked - got the neighbours");

          System.out.println(ltrTile + " - @onMouseClicked - mostTop=" + mostTop);
          System.out.println(ltrTile + " - @onMouseClicked - mostRight=" + mostRight);
          System.out.println(ltrTile + " - @onMouseClicked - mostBottom=" + mostBottom);
          System.out.println(ltrTile + " - @onMouseClicked - mostLeft=" + mostLeft);
          System.out.println(ltrTile + " - @onMouseClicked - tile=" + tile);

          if (mostLeft != tile || mostRight != tile) {
            // There are HORIZONTAL neighbours
            System.out.println(ltrTile + " - @onMouseClicked - creating a horizontal word");
            new Word(mostLeft, mostRight, controller);
          }

          if (mostTop != tile || mostBottom != tile) {
            // There are VERTICAL neighbours
            System.out.println(ltrTile + " - @onMouseClicked - creating a vertical word");
            new Word(mostTop, mostBottom, controller);
          }

          controller.popupBlankMessage.getChildren().clear();
          System.out.println(ltrTile + " - @onMouseClicked - finish");
        });

        controller.popupBlankMessage.getChildren().add(ltrTile.container);
      }

      controller.okBtn.setDisable(true);
    } else {
      // regular letterTile

      // Creating a new Letter letterTile
      LetterTile tile = new LetterTile(getLetter(), getPoints(), cellSize, controller);

      tile.setVisible(true);
      tile.isBlank = false;

      // Adding the Letter to the Slot
      pointedSlot.setContent(tile);

      // Checking if there are any Neighbours
      // Getting First and Last letter, both for Horizontal and Vertical
      LetterTile mostTop = controller.grid.getMostTopOf(tile);
      LetterTile mostRight = controller.grid.getMostRightOf(tile);
      LetterTile mostBottom = controller.grid.getMostBottomOf(tile);
      LetterTile mostLeft = controller.grid.getMostLeftOf(tile);

      if (mostLeft != tile || mostRight != tile) {
        // There are HORIZONTAL neighbours

        // Creating a Word.
        // Fills in all data members of the Word class.
        // It checks whether the word has no empty gaps, i.e. the word is FULL.
        // If thw word is full, then it checks if the word is VALID.
        new Word(mostLeft, mostRight, controller);
      }

      if (mostTop != tile || mostBottom != tile) {
        // There are VERTICAL neighbours
        new Word(mostTop, mostBottom, controller);
      }
    }

    // Removing the moved LetterTile from its old Slot
    Slot slot = this.slot;
    if (this.slot != null) {
      System.out.println(this + " - onDragDone - Removing the LetterTile");
      this.slot.removeContent();
    }

    ArrayList<Word> wordsToRemove = new ArrayList<>();
    // Searching Words that contain this Letter Tile
    System.out.println("Words that contain just moved letter \"" + this.getLetter() + "\": ");
    for (Word word : controller.grid.words) {
      if (word.contains(this)) {
        word.display();
        System.out.print("\n");
        wordsToRemove.add(word);

        // Removing the highlighting box of the Word that was containing the moved LetterTile
        controller.gridWrapper.getChildren().remove(word.container);
      }
      word.container.setViewOrder(controller.minViewOrder+1);
    }
    for (Word word : wordsToRemove) {
      // Removing the Word that was containing the moved LetterTile (BACK-END)
      controller.grid.words.remove(word);

      // Shrinking the word if the removed LetterTile is first or last letter of the word
      if (this == word.getFirst() && word.getWordLength() > 2) {

        // Finding out whether the shrank word is entirely frozen
        // in that case, a new Word will not be created
        boolean frozenWord = true;
        for (int j = 1; j < word.getWordLength(); j++) {
          if (!word.getLetter(j).isFrozen) {
            frozenWord = false;
            break;
          }
        }

        if (!frozenWord) {
          Word shrank = new Word(word.getLetter(1), word.getLast(), controller);
          shrank.container.setViewOrder(controller.minViewOrder+2);
        }
      }

      if (this == word.getLast() && word.getWordLength() > 2) {

        boolean frozenWord = true;
        for (int j = 0; j < word.getWordLength() - 1; j++) {
          if (!word.getLetter(j).isFrozen) {
            frozenWord = false;
            break;
          }
        }

        if (!frozenWord) {
          int ltrIdx = word.getWordLength() - 2;
          Word shrank = new Word(word.getFirst(), word.getLetter(ltrIdx), controller);
          shrank.container.setViewOrder(controller.minViewOrder+2);
        }
      }
    }

    if (!controller.letterBar.isFull()) {
      controller.okBtn.setText("OK");
    } else {
      controller.okBtn.setText("PASS");
    }

    controller.grid.display();
  }

  /**
   * Initiates the FRONT-end part (AnchorPane with 2 Labels inside)
   *
   * @param ltr Letter to put inside the tile
   * @param pts Points
   */
  private void initShape(char ltr, int pts) {

    /*
    DropShadow ds = new DropShadow();
    ds.setHeight(20);
    ds.setWidth(20);
    ds.setOffsetY(-3);
    ds.setOffsetX(3);
    ds.setColor(Color.GRAY);
    */

    container = new AnchorPane();
    container.getStyleClass().add("letter-btn");
    container.setPrefSize(cellSize, cellSize);
    container.setMinSize(cellSize, cellSize);
    container.setMaxSize(cellSize, cellSize);
    // container.setEffect(ds);

    letter = new Label(String.valueOf(ltr));
    if (ltr == '\0') {
      letter.setVisible(false);
      this.isBlank = true;
    }
    letter.getStyleClass().add("letter-label");
    letter.setLabelFor(container);
    letter.setPrefSize(cellSize, cellSize);

    points = new Label(String.valueOf(pts));
    if (pts == 0) {
      points.setVisible(false);
      this.isBlank = true;
    }
    points.getStyleClass().add("points-label");
    points.setLabelFor(container);

    container.setOnMousePressed(event -> {
      System.out
          .println("Event.getScene: (" + event.getSceneX() + ", " + event.getSceneY() + ")");
      System.out.println("Event.get: (" + event.getX() + ", " + event.getY() + ")\n");
    });

    container.setOnMouseDragged(mouseEvent -> {
      controller.letterDrag = true;

      container.setManaged(false);

      slot.container.setViewOrder(--controller.minViewOrder);
      Node parent = slot.container.getParent();
      while (!parent.equals(controller.root)) {
        parent.setViewOrder(--controller.minViewOrder);
        parent = parent.getParent();
      }

      container.setCursor(Cursor.CLOSED_HAND);
      container.setStyle("-fx-opacity: 0.7");

      container.setLayoutX(mouseEvent.getX() + container.getLayoutX() - container.getWidth() / 2);
      container.setLayoutY(mouseEvent.getY() + container.getLayoutY() - container.getHeight() / 2);

      mouseEvent.consume();
    });

    container.setOnMouseReleased(mouseEvent -> {
      controller.letterDrag = false;

      container.setCursor(Cursor.DEFAULT);
      container.setStyle("-fx-opacity: 1");

      Bounds wrapperBounds = controller.gridWrapper.getBoundsInParent();
      double wrapperMinX = wrapperBounds.getMinX();
      double wrapperMinY = wrapperBounds.getMinY();

      double mouseX = mouseEvent.getSceneX();
      double mouseY = mouseEvent.getSceneY();

      boolean found = false;

      for (int i = 0; i < controller.grid.getGlobalSize(); i++) {
        Slot pointedSlot = controller.grid.getSlot(i);

        Bounds cellBounds = pointedSlot.container.getBoundsInParent();
        double cellMinX = wrapperMinX + cellBounds.getMinX();
        double cellMaxX = wrapperMinX + cellBounds.getMaxX();
        double cellMinY = wrapperMinY + cellBounds.getMinY();
        double cellMaxY = wrapperMinY + cellBounds.getMaxY();

        if (mouseX >= cellMinX && mouseX <= cellMaxX && mouseY >= cellMinY && mouseY <= cellMaxY) {

          if (pointedSlot.isFree()) {
            found = true;

            moveTileTo(pointedSlot);

            break;
          }
        }
      }

      Bounds panelBounds = controller.lettersBlock.getParent().getBoundsInParent();
      double panelMinX = panelBounds.getMinX();
      double panelMinY = panelBounds.getMinY();

      Bounds ltrBlockBounds = controller.lettersBlock.getBoundsInParent();
      double ltrBlockMinX = ltrBlockBounds.getMinX();
      double ltrBlockMinY = ltrBlockBounds.getMinY();

      if (!found) {

        for (int i = 0; i < controller.letterBar.getSize(); i++) {
          Slot pointedSlot = controller.letterBar.getSlot(i);

          Bounds cellBounds = pointedSlot.container.getBoundsInParent();
          double cellMinX = ltrBlockMinX + panelMinX + cellBounds.getMinX();
          double cellMaxX = ltrBlockMinX + panelMinX + cellBounds.getMaxX();
          double cellMinY = ltrBlockMinY + panelMinY + cellBounds.getMinY();
          double cellMaxY = ltrBlockMinY + panelMinY + cellBounds.getMaxY();

          if (mouseX >= cellMinX && mouseX <= cellMaxX && mouseY >= cellMinY
              && mouseY <= cellMaxY) {

            if (pointedSlot.isFree()) {
              found = true;

              moveTileTo(pointedSlot);

              break;
            }
          }
        }
      }

      if (!found) {
        this.container.setManaged(true);
      }

      mouseEvent.consume();
    });

    container.setOnMouseEntered(event -> container.setStyle("-fx-opacity: 0.8"));
    container.setOnMouseExited(event -> container.setStyle("-fx-opacity: 1"));

    container.getChildren().add(letter);
    container.getChildren().add(points);

    AnchorPane.setLeftAnchor(letter, 0.0);
    AnchorPane.setTopAnchor(letter, 0.0);

    AnchorPane.setRightAnchor(points, container.getWidth() * 0.03);
    AnchorPane.setBottomAnchor(points, container.getWidth() * 0.03);

    container.widthProperty().addListener((observable, oldValue, newValue) -> {
      letter.setPrefSize(newValue.doubleValue(), newValue.doubleValue());

      AnchorPane.setRightAnchor(points, newValue.doubleValue() * 0.03);
      AnchorPane.setBottomAnchor(points, newValue.doubleValue() * 0.03);
    });

    // Changing size of the Letter according to size of the Grid
    double gridHeight = controller.grid.container.heightProperty().getValue();
    double tileSize = (gridHeight - (controller.grid.padSize * (controller.grid.size + 1)))
        / controller.grid.size;
    container.setMaxSize(tileSize, tileSize);
    container.setPrefSize(tileSize, tileSize);
    container.setMinSize(tileSize, tileSize);

    // Binding height of the Letter to height of the Grid
    controller.grid.container.heightProperty().addListener((observable, oldValue, newValue) -> {
      double tileNewSize =
          (newValue.doubleValue() - (controller.grid.padSize * (controller.grid.size + 1)))
              / controller.grid.size;
      container.setMaxHeight(tileNewSize);
      container.setPrefHeight(tileNewSize);
      container.setMinHeight(tileNewSize);
    });

    // Binding width of the Letter to width of the Grid
    controller.grid.container.widthProperty().addListener((observable, oldValue, newValue) -> {
      double tileNewSize =
          (newValue.doubleValue() - (controller.grid.padSize * (controller.grid.size + 1)))
              / controller.grid.size;
      container.setMaxWidth(tileNewSize);
      container.setPrefWidth(tileNewSize);
      container.setMinWidth(tileNewSize);
    });
  }

  /**
   * Constructor, that takes a GameController in parameters
   *
   * @param controller GameController
   */
  public LetterTile(GameController controller) {
    cellSize = 50;

    this.controller = controller;

    initShape((char) 0, 0);
  }

  /**
   * Constructor
   *
   * @param letter     Letter to put inside the tile
   * @param points     Points
   * @param cellSize   Tile's size (in pixels)
   * @param controller GameController
   */
  public LetterTile(char letter, int points, double cellSize, GameController controller) {
    this.cellSize = cellSize;

    this.controller = controller;

    initShape(letter, points);
  }

  public void setVisible(boolean val) {
    setLetterVisible(val);
    setPointsVisible(val);
  }

  public void setPointsVisible(boolean val) {
    points.setVisible(val);
  }

  public void setLetterVisible(boolean val) {
    letter.setVisible(val);
  }

  /**
   * Sets the top neighbour
   *
   * @param top Top neighbour (LetterTile)
   */
  public void setTop(LetterTile top) {
    this.top = top;
  }

  /**
   * Sets the right neighbour
   *
   * @param right right neighbour (LetterTile)
   */
  public void setRight(LetterTile right) {
    this.right = right;
  }

  /**
   * Sets the bottom neighbour
   *
   * @param bottom bottom neighbour (LetterTile)
   */
  public void setBottom(LetterTile bottom) {
    this.bottom = bottom;
  }

  /**
   * Sets the left neighbour
   *
   * @param left left neighbour (LetterTile)
   */
  public void setLeft(LetterTile left) {
    this.left = left;
  }

  /**
   * Get the top neighbour
   *
   * @return Top neighbour (LetterTile)
   */
  public LetterTile getTop() {
    return top;
  }

  /**
   * Get the right neighbour
   *
   * @return right neighbour (LetterTile)
   */
  public LetterTile getRight() {
    return right;
  }

  /**
   * Get the bottom neighbour
   *
   * @return bottom neighbour (LetterTile)
   */
  public LetterTile getBottom() {
    return bottom;
  }

  /**
   * Get the left neighbour
   *
   * @return left neighbour (LetterTile)
   */
  public LetterTile getLeft() {
    return left;
  }

  /**
   * Get the most top existing neighbour
   *
   * @return The most top neighbour (LetterTile)
   */
  public LetterTile getMostTop() {
    LetterTile current = this;
    while (current.top != null) {
      current = current.top;
    }
    return current;
  }

  /**
   * Get the most right existing neighbour
   *
   * @return The most right neighbour (LetterTile)
   */
  public LetterTile getMostRight() {
    LetterTile current = this;
    while (current.right != null) {
      current = current.right;
    }
    return current;
  }

  /**
   * Get the most bottom existing neighbour
   *
   * @return The most bottom neighbour (LetterTile)
   */
  public LetterTile getMostBottom() {
    LetterTile current = this;
    while (current.bottom != null) {
      current = current.bottom;
    }
    return current;
  }

  /**
   * Get the most left existing neighbour
   *
   * @return The most left neighbour (LetterTile)
   */
  public LetterTile getMostLeft() {
    LetterTile current = this;
    System.out.println("@getMostLeft() - Current: " + current.getLetter());
    while (current.left != null) {
      current = current.left;
      System.out.println("@getMostLeft() - Left: " + current.getLetter());
    }
    return current;
  }

  /**
   * Adds a Tile at the top of the most top existing neighbour
   *
   * @param top Tile to be added (LetterTile)
   */
  public void addTop(LetterTile top) {
    getMostTop().top = top;
  }

  /**
   * Adds a Tile at the right of the most right existing neighbour
   *
   * @param right Tile to be added (LetterTile)
   */
  public void addRight(LetterTile right) {
    getMostRight().right = right;
  }

  /**
   * Adds a Tile at the bottom of the most bottom existing neighbour
   *
   * @param bottom Tile to be added (LetterTile)
   */
  public void addBottom(LetterTile bottom) {
    getMostBottom().bottom = bottom;
  }

  /**
   * Adds a Tile at the left of the most left existing neighbour
   *
   * @param left Tile to be added (LetterTile)
   */
  public void addLeft(LetterTile left) {
    getMostLeft().left = left;
  }

  /**
   * Set Tile's letter
   *
   * @param letter Letter to set
   */
  public void setLetter(char letter) {
    this.letter.setText(String.valueOf(letter));
  }

  /**
   * Set Tile's points
   *
   * @param points Letter to set
   */
  public void setPoints(int points) {
    this.points.setText(String.valueOf(points));
  }

  /**
   * Get Tile's letter
   *
   * @return Letter
   */
  public char getLetter() {
    return letter.getText().charAt(0);
  }

  /**
   * Get Tile's points
   *
   * @return Points
   */
  public int getPoints() {
    return Integer.parseInt(points.getText());
  }

  public void setDisable(boolean value) {
    container.setDisable(value);
  }

  public void setMouseTransparent(boolean value) {
    container.setMouseTransparent(value);
  }
}
