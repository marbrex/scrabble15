package scrabble.game;

import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import scrabble.ScrabbleController;

class LetterTile {

  final ScrabbleController controller;

  private double cellSize;

  AnchorPane container;
  private Label letter;
  private Label points;

  Slot slot;

  private LetterTile top;
  private LetterTile right;
  private LetterTile bottom;
  private LetterTile left;

  private void initShape(char ltr, int pts) {

    DropShadow ds = new DropShadow();
    ds.setHeight(20);
    ds.setWidth(20);
    ds.setOffsetY(-3);
    ds.setOffsetX(3);
    ds.setColor(Color.GRAY);

    container = new AnchorPane();
    container.getStyleClass().add("letter-btn");
    container.setPrefSize(cellSize, cellSize);
    container.setMinSize(cellSize, cellSize);
    container.setMaxSize(cellSize, cellSize);
    container.setEffect(ds);

    letter = new Label(String.valueOf(ltr));
    letter.getStyleClass().add("letter-label");
    letter.setLabelFor(container);
    letter.setPrefSize(cellSize, cellSize);

    points = new Label(String.valueOf(pts));
    points.getStyleClass().add("points-label");
    points.setLabelFor(container);

    container.setOnMousePressed(event -> {
      System.out
          .println("Event.getScene: (" + event.getSceneX() + ", " + event.getSceneY() + ")");
      System.out.println("Event.get: (" + event.getX() + ", " + event.getY() + ")\n");
    });

    container.setOnMouseDragged(mouseEvent -> {
      container.toFront();
      container.setCursor(Cursor.CLOSED_HAND);
      container.setStyle("-fx-opacity: 0.6");

      container.setLayoutX(mouseEvent.getX() + container.getLayoutX() - container.getWidth() / 2);
      container.setLayoutY(mouseEvent.getY() + container.getLayoutY() - container.getHeight() / 2);

      mouseEvent.consume();
    });

    container.setOnMouseReleased(mouseEvent -> {
      container.setCursor(Cursor.DEFAULT);
      container.setStyle("-fx-opacity: 1");

      mouseEvent.consume();
    });

    container.setOnMouseEntered(event -> container.setStyle("-fx-opacity: 0.8"));
    container.setOnMouseExited(event -> container.setStyle("-fx-opacity: 1"));

    container.setOnDragDetected(event -> {
      // drag was detected, start drag-and-drop gesture
      // System.out.println(this + " - onDragDetected");

      // allow any transfer mode
      Dragboard db = container.startDragAndDrop(TransferMode.ANY);

      // put a string on drag board
      ClipboardContent content = new ClipboardContent();
      content.putString(letter.getText() + points.getText());
      db.setContent(content);

      event.consume();
    });

    container.setOnDragDone(event -> {
      // the drag-and-drop gesture ended
      // System.out.println(this + " - onDragDone");

      // if the data was successfully moved, clear it
      if (event.getTransferMode() == TransferMode.MOVE) {

        Slot slot = this.slot;
        if (slot != null) {
          System.out.println("Removing the LetterTile");
          slot.removeContent();
        }

        controller.grid.display();

      }

      event.consume();
    });

    container.getChildren().add(letter);
    container.getChildren().add(points);

    AnchorPane.setLeftAnchor(letter, 0.0);
    AnchorPane.setTopAnchor(letter, 0.0);

    AnchorPane.setRightAnchor(points, container.getWidth() * 0.05);
    AnchorPane.setBottomAnchor(points, container.getWidth() * 0.05);

    container.widthProperty().addListener((observable, oldValue, newValue) -> {
      letter.setPrefSize(newValue.doubleValue(), newValue.doubleValue());

      AnchorPane.setRightAnchor(points, newValue.doubleValue() * 0.05);
      AnchorPane.setBottomAnchor(points, newValue.doubleValue() * 0.05);
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

  public LetterTile(ScrabbleController controller) {
    cellSize = 50;

    this.controller = controller;

    initShape((char) 0, 0);
  }

  public LetterTile(char letter, int points, double cellSize, ScrabbleController controller) {
    this.cellSize = cellSize;

    this.controller = controller;

    initShape(letter, points);
  }

  public void setTop(LetterTile top) {
    this.top = top;
  }

  public void setRight(LetterTile right) {
    this.right = right;
  }

  public void setBottom(LetterTile bottom) {
    this.bottom = bottom;
  }

  public void setLeft(LetterTile left) {
    this.left = left;
  }

  public LetterTile getTop() {
    return top;
  }

  public LetterTile getRight() {
    return right;
  }

  public LetterTile getBottom() {
    return bottom;
  }

  public LetterTile getLeft() {
    return left;
  }

  public LetterTile getMostTop() {
    LetterTile current = this;
    while (current.top != null) {
      current = current.top;
    }
    return current;
  }

  public LetterTile getMostRight() {
    LetterTile current = this;
    while (current.right != null) {
      current = current.right;
    }
    return current;
  }

  public LetterTile getMostBottom() {
    LetterTile current = this;
    while (current.bottom != null) {
      current = current.bottom;
    }
    return current;
  }

  public LetterTile getMostLeft() {
    LetterTile current = this;
    while (current.left != null) {
      current = current.left;
    }
    return current;
  }

  public void addTop(LetterTile top) {
    getMostTop().top = top;
  }

  public void addRight(LetterTile right) {
    getMostRight().right = right;
  }

  public void addBottom(LetterTile bottom) {
    getMostBottom().bottom = bottom;
  }

  public void addLeft(LetterTile left) {
    getMostLeft().left = left;
  }

  public void setLetter(char letter) {
    this.letter.setText(String.valueOf(letter));
  }

  public void setPoints(int points) {
    this.points.setText(String.valueOf(points));
  }

  public char getLetter() {
    return letter.getText().charAt(0);
  }

  public int getPoints() {
    return Integer.parseInt(points.getText());
  }
}
