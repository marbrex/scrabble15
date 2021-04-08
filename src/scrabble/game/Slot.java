package scrabble.game;

import javafx.scene.effect.InnerShadow;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import scrabble.ScrabbleController;

public class Slot {

  ScrabbleController controller;

  private double size;
  private boolean isFree;

  public LetterTile content;

  StackPane container;

  private void initShape() {

    InnerShadow is = new InnerShadow();
    is.setColor(Color.DARKGRAY);
    is.setOffsetX(-5);
    is.setOffsetY(5);
    is.setHeight(20);
    is.setWidth(20);

    container = new StackPane();
    container.getStyleClass().add("slot");
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
        container.getStyleClass().add("slot-on-drag-entered");
      }

      event.consume();
    });

    container.setOnDragExited(event -> {
      // System.out.println(this + " - onDragExited");

      // mouse moved away, remove the graphical cues
      container.getStyleClass().removeAll("slot-on-drag-entered");

      event.consume();
    });

    container.setOnDragDropped(event -> {
      // data dropped
      // System.out.println(this + " - onDragDropped");

      // if there is a string data on drag board, read it and use it
      Dragboard db = event.getDragboard();
      boolean success = false;
      if (db.hasString()) {

        // Getting the coordinates of the placeholder rectangle
        int x = GridPane.getColumnIndex(container);
        int y = GridPane.getRowIndex(container);
        int index = y + controller.grid.size * x;

        // Getting the letter tile's data transferred by D&D
        char letter = db.getString().charAt(0);
        int points = Character.getNumericValue(db.getString().charAt(1));

        // Creating a new Letter Tile
        LetterTile tile = new LetterTile(letter, points, size, controller);

        // Adding the Letter to the Slot
        this.setContent(tile);

        // Checking if there are any Neighbours
        boolean top = false;
        boolean right = false;
        boolean bottom = false;
        boolean left = false;
        int topX = x;
        int topY = y - 1;
        int rightX = x + 1;
        int rightY = y;
        int bottomX = x;
        int bottomY = y + 1;
        int leftX = x - 1;
        int leftY = y;
        while (topY >= 0) {
          if (!controller.grid.getSlot(topX, topY).isFree()) {
            top = true;
            topY--;
          } else {
            topY++;
            break;
          }
        }
        while (rightX < controller.grid.size) {
          if (!controller.grid.getSlot(rightX, rightY).isFree()) {
            right = true;
            rightX++;
          } else {
            rightX--;
            break;
          }
        }
        while (bottomY < controller.grid.size) {
          if (!controller.grid.getSlot(bottomX, bottomY).isFree()) {
            bottom = true;
            bottomY++;
          } else {
            bottomY--;
            break;
          }
        }
        while (leftX >= 0) {
          if (!controller.grid.getSlot(leftX, leftY).isFree()) {
            left = true;
            leftX--;
          } else {
            leftX++;
            break;
          }
        }

        if (left || right) {
          // There are HORIZONTAL neighbours

          // Getting First and Last letter (Y is the same for all letters on this row)
          // We don't know yet if there are empty gaps between these 2 letters.
          int minX = Math.min(leftX, rightX);
          int maxX = Math.max(leftX, rightX);
          System.out.println("\nHORIZONTAL - Min Cell (" + minX + ", " + leftY + ")");
          System.out.println("\nHORIZONTAL - Max Cell (" + maxX + ", " + leftY + ")");

          // Creating a Word.
          // Fills in all data members of the Word class.
          // It checks whether the word has no empty gaps, i.e. the word is FULL.
          // If thw word is full, then it checks if the word is VALID.
          Word word = new Word(controller.grid.getSlot(minX, leftY).content,
              controller.grid.getSlot(maxX, leftY).content, controller);
        }

        if (top || bottom) {
          // There are VERTICAL neighbours

          int minY = Math.min(topY, bottomY);
          int maxY = Math.max(topY, bottomY);
          System.out.println("\nVERTICAL - Min Cell (" + topX + ", " + minY + ")");
          System.out.println("\nVERTICAL - Max Cell (" + topX + ", " + maxY + ")");

          Word word = new Word(controller.grid.getSlot(topX, minY).content,
              controller.grid.getSlot(topX, maxY).content, controller);
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

  public Slot(ScrabbleController controller) {
    size = 30;
    isFree = true;

    this.controller = controller;

    initShape();
  }

  public Slot(LetterTile tile, ScrabbleController controller) {
    size = 30;
    isFree = true;

    this.controller = controller;

    initShape();

    setContent(tile);
  }

  public Slot(double size, ScrabbleController controller) {
    this.size = size;
    isFree = true;

    this.controller = controller;

    initShape();
  }

  public Slot(double size, LetterTile tile, ScrabbleController controller) {
    this.size = size;
    isFree = true;

    this.controller = controller;

    initShape();

    setContent(tile);
  }

  public boolean isFree() {
    return content == null;
  }

  public void removeContent() {
    if (!container.getChildren().isEmpty()) {
      container.getChildren().clear();
    }
    if (content != null) {
      content.slot = null;
    }
    content = null;
    isFree = true;
  }

  public void setContent(LetterTile tile) {
    removeContent();
    container.getChildren().add(tile.container);
    content = tile;
    tile.slot = this;
    isFree = false;
  }
}
