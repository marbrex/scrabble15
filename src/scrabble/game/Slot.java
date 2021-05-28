package scrabble.game;

import javafx.scene.control.Label;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import scrabble.GameController;

/**
 * scrabble.game.Slot class represents a Slot, in which a LetterTile can be placed. Used in the Grid
 * and LetterBar classes.
 *
 * @author ekasmamy
 */
public class Slot {

  GameController controller;

  private double size;
  private boolean isFree;
  public boolean isFrozen;

  private Multiplier multiplier;

  public LetterTile content;

  StackPane container;

  /**
   * Initiates the FRONT-end part (StackPane).
   *
   * @author ekasmamy
   */
  private void initShape() {

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
    setEffect();

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

    container.setViewOrder(2);
  }

  /**
   * Constructor.
   *
   * @param controller GameController.
   * @author ekasmamy
   */
  public Slot(GameController controller) {
    size = 30;
    isFree = true;

    this.controller = controller;

    initShape();
  }

  /**
   * Constructor.
   *
   * @param multiplier Set the multiplier on creation.
   * @param controller GameController.
   * @author ekasmamy
   */
  public Slot(Multiplier multiplier, GameController controller) {
    size = 30;
    isFree = true;

    this.controller = controller;
    this.multiplier = multiplier;

    initShape();
  }

  /**
   * Constructor.
   *
   * @param tile       Set the Tile on creation.
   * @param controller GameController.
   * @author ekasmamy
   */
  public Slot(LetterTile tile, GameController controller) {
    size = 30;
    isFree = true;

    this.controller = controller;

    initShape();

    setContent(tile);
  }

  /**
   * Constructor.
   *
   * @param tile       LetterTile.
   * @param multiplier Multiplier.
   * @param controller GameController.
   * @author ekasmamy
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
   * Constructor.
   *
   * @param size       Size (number of cells, width=height).
   * @param controller GameController.
   * @author ekasmamy
   */
  public Slot(double size, GameController controller) {
    this.size = size;
    isFree = true;

    this.controller = controller;

    initShape();
  }

  /**
   * Constructor.
   *
   * @param size       Size (number of cells, width=height).
   * @param tile       LetterTile.
   * @param controller GameController.
   * @author ekasmamy
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
   * @return TRUE if has content, FALSE otherwise.
   * @author ekasmamy
   */
  public boolean isFree() {
    return content == null;
  }

  /**
   * Removes the content of the Slot.
   *
   * @author ekasmamy
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
    setEffect();
    if (multiplier != null && multiplier != Multiplier.NO) {
      container.getChildren().add(new Label(multiplier.getAsString()));
    }
  }

  /**
   * Sets the content of the Slot (re-write).
   *
   * @param tile LetterTile.
   * @author ekasmamy
   * @see scrabble.game.LetterTile
   */
  public void setContent(LetterTile tile) {
    if (tile != null) {
      removeContent();
      container.getChildren().add(tile.container);
      content = tile;
      tile.slot = this;
      isFree = false;
      removeEffect();
    }
  }

  /**
   * Sets the Multiplier.
   *
   * @param multiplier Multiplier.
   * @author ekasmamy
   * @see scrabble.game.Multiplier
   */
  public void setMultiplier(Multiplier multiplier) {
    this.multiplier = multiplier;
  }

  /**
   * Get the Multiplier.
   *
   * @return Multiplier.
   * @author ekasmamy
   * @see scrabble.game.Multiplier
   */
  public Multiplier getMultiplier() {
    return multiplier;
  }

  /**
   * Sets an inner shadow effect.
   *
   * @author ekasmamy
   */
  public void setEffect() {
    InnerShadow is = new InnerShadow();
    is.setColor(Color.DARKGRAY);
    is.setOffsetX(-5);
    is.setOffsetY(5);
    is.setHeight(20);
    is.setWidth(20);

    container.setEffect(is);
  }

  /**
   * Removes every effect applied.
   *
   * @author ekasmamy
   */
  public void removeEffect() {
    container.setEffect(null);
  }
}
