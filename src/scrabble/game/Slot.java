package scrabble.game;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import scrabble.GameController;

/**
 * <h1>scrabble.game.Slot</h1>
 *
 * <p>
 * This class represents a Slot, in which a LetterTile can be placed. Used in the Grid and LetterBar
 * classes.
 * </p>
 *
 * @author Eldar Kasmamytov
 */
public class Slot {

  GameController controller;

  private double size;
  private boolean isFree;

  private Multiplier multiplier;

  public LetterTile content;

  StackPane container;

  /**
   * Initiates the FRONT-end part (StackPane)
   */
  private void initShape() {

    InnerShadow is = new InnerShadow();
    is.setColor(Color.DARKGRAY);
    is.setOffsetX(-5);
    is.setOffsetY(5);
    is.setHeight(20);
    is.setWidth(20);

    container = new StackPane();

    container.getStyleClass().add("slot");
    if (multiplier != null && multiplier != Multiplier.NO) {
      switch (multiplier) {
        case DL:
          container.getStyleClass().add("slot-dl");
          break;
        case TL:
          container.getStyleClass().add("slot-tl");
          break;
        case DW:
          container.getStyleClass().add("slot-dw");
          break;
        case TW:
          container.getStyleClass().add("slot-tw");
          break;
      }
      container.getChildren().add(new Label(multiplier.getAsString()));
    }

    container.setPrefSize(size, size);
    container.setMinSize(size, size);
    container.setMaxSize(size, size);
    container.setEffect(is);

    // Binding size of the Slot to size of the GridPane
    controller.grid.container.heightProperty().addListener((observable, oldValue, newValue) -> {
      double temp = controller.grid.padSize * (controller.grid.size + 1);
      container.setMinHeight((newValue.doubleValue() - temp) / controller.grid.size);
      container.setPrefHeight((newValue.doubleValue() - temp) / controller.grid.size);
      container.setMaxHeight((newValue.doubleValue() - temp) / controller.grid.size);
    });

    controller.grid.container.widthProperty().addListener((observable, oldValue, newValue) -> {
      double temp = controller.grid.padSize * (controller.grid.size + 1);
      container.setMinWidth((newValue.doubleValue() - temp) / controller.grid.size);
      container.setPrefWidth((newValue.doubleValue() - temp) / controller.grid.size);
      container.setMaxWidth((newValue.doubleValue() - temp) / controller.grid.size);
    });

    container.setOnDragOver(event -> {
      // data is dragged over the target
      // System.out.println(this + " - onDragOver");

      /* accept it only if it is not dragged from the same node
       * and if it has a string data */
      if (event.getGestureSource() != container &&
          event.getDragboard().hasString()) {
        // allow for both copying and moving, whatever user chooses
        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
      }

      event.consume();
    });

    container.setOnDragEntered(event -> {
      // the drag-and-drop gesture entered the target
      // System.out.println(this + " - onDragEntered");

      // show to the user that it is an actual gesture target
      if (event.getGestureSource() != container &&
          event.getDragboard().hasString()) {
        container.getStyleClass().removeAll("slot-dl", "slot-tl", "slot-dw", "slot-tw");
        container.getStyleClass().add("slot-on-drag-entered");
      }

      event.consume();
    });

    container.setOnDragExited(event -> {
      // System.out.println(this + " - onDragExited");

      // mouse moved away, remove the graphical cues
      container.getStyleClass().removeAll("slot-on-drag-entered");
      if (multiplier != null && multiplier != Multiplier.NO) {
        String style = "slot-" + multiplier.getAsString().toLowerCase(Locale.ROOT);
        container.getStyleClass().add(style);
      }

      event.consume();
    });

    container.setOnDragDropped(event -> {
      // data dropped
      // System.out.println(this + " - onDragDropped");

      // if there is a string data on drag board, read it and use it
      Dragboard db = event.getDragboard();
      boolean success = false;
      if (db.hasString()) {

        // Getting the letter tile's data transferred by D&D
        Map<String, String> params = new HashMap<>();
        String paramsString = db.getString();
        String[] pairs = paramsString.split("&");
        for (String p : pairs) {
          String[] pair = p.split("=");
          params.put(pair[0], pair[1]);
        }

        char letter = params.get("letter").charAt(0);
        int points = Integer.parseInt(params.get("points"));
        boolean isBlank = Boolean.parseBoolean(params.get("isBlank"));

        if (isBlank) {
          // blank letterTile

          // Setting visible a popup window
          controller.popupBlankBlock.setVisible(true);
          controller.popupBlankBlock.setViewOrder(-2);

          LetterBag bag = LetterBag.getInstance();
          for (int i = 0; i < bag.getAlphabetSize(); i++) {
            char l = bag.getLetterInAlphabet(i);
            int v = bag.getValueInAlphabet(i);

            LetterTile ltrTile = new LetterTile(l, v, size, controller);
            ltrTile.setPointsVisible(false);
            ltrTile.isBlank = true;
            ltrTile.container.setOnDragDetected(null);
            ltrTile.container.setOnDragDone(null);
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
              this.setContent(tile);

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
          LetterTile tile = new LetterTile(letter, points, controller.grid.cellSize, controller);

          tile.setVisible(true);
          tile.isBlank = false;

          // Adding the Letter to the Slot
          this.setContent(tile);

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

        success = true;
      }

      /* let the source know whether the string was successfully
       * transferred and used */
      event.setDropCompleted(success);

      event.consume();
    });

    container.setViewOrder(2);
  }

  /**
   * Constructor
   *
   * @param controller GameController
   */
  public Slot(GameController controller) {
    size = 30;
    isFree = true;

    this.controller = controller;

    initShape();
  }

  /**
   * Constructor
   *
   * @param multiplier Set the multiplier on creation
   * @param controller GameController
   */
  public Slot(Multiplier multiplier, GameController controller) {
    size = 30;
    isFree = true;

    this.controller = controller;
    this.multiplier = multiplier;

    initShape();
  }

  /**
   * Constructor
   *
   * @param tile       Set the Tile on creation
   * @param controller GameController
   */
  public Slot(LetterTile tile, GameController controller) {
    size = 30;
    isFree = true;

    this.controller = controller;

    initShape();

    setContent(tile);
  }

  /**
   * Constructor
   *
   * @param tile       LetterTile
   * @param multiplier Multiplier
   * @param controller GameController
   */
  public Slot(LetterTile tile, Multiplier multiplier, GameController controller) {
    size = 30;
    isFree = true;

    this.controller = controller;
    this.multiplier = multiplier;

    initShape();

    setContent(tile);
  }

  /**
   * Constructor
   *
   * @param size       Size (number of cells, width=height)
   * @param controller GameController
   */
  public Slot(double size, GameController controller) {
    this.size = size;
    isFree = true;

    this.controller = controller;

    initShape();
  }

  /**
   * Constructor
   *
   * @param size       Size (number of cells, width=height)
   * @param tile       LetterTile
   * @param controller GameController
   */
  public Slot(double size, LetterTile tile, GameController controller) {
    this.size = size;
    isFree = true;

    this.controller = controller;

    initShape();

    setContent(tile);
  }

  /**
   * Checks whether the slot has content.
   *
   * @return TRUE if has content, FALSE otherwise
   */
  public boolean isFree() {
    return content == null;
  }

  /**
   * Removes the content of the Slot
   */
  public void removeContent() {
    if (!container.getChildren().isEmpty()) {
      container.getChildren().clear();
    }
    if (content != null) {
      content.slot = null;
    }
    content = null;
    isFree = true;
    if (multiplier != null && multiplier != Multiplier.NO) {
      container.getChildren().add(new Label(multiplier.getAsString()));
    }
  }

  /**
   * Sets the content of the Slot (re-write)
   *
   * @param tile LetterTile
   * @see scrabble.game.LetterTile
   */
  public void setContent(LetterTile tile) {
    if (tile != null) {
      removeContent();
      container.getChildren().add(tile.container);
      content = tile;
      tile.slot = this;
      isFree = false;
    }
  }

  /**
   * Sets the Multiplier
   *
   * @param multiplier Multiplier
   * @see scrabble.game.Multiplier
   */
  public void setMultiplier(Multiplier multiplier) {
    this.multiplier = multiplier;
  }

  /**
   * Get the Multiplier
   *
   * @return Multiplier
   * @see scrabble.game.Multiplier
   */
  public Multiplier getMultiplier() {
    return multiplier;
  }
}
