package scrabble.game;

import javafx.scene.shape.Polygon;
import scrabble.GameController;

/**
 * <h1>scrabble.game.WordGraph</h1>
 *
 * <p>
 * This class represents a Cross Word structure, every validated word will be stored here.
 * </p>
 *
 * @author Eldar Kasmamytov
 */
public class WordGraph {

  GameController controller;

  private LetterTile start;
  private int count;
  private final int maxWidth;
  private final int maxHeight;

  private Polygon shape;

  /**
   * Initiates the FRONT-end part (A Polygon that will be shown as a border of a cross word)
   *
   * @param points vertices of the polygon
   */
  private void initShape(double... points) {
    shape = new Polygon(points);
    shape.getStyleClass().add("line");
  }

  /**
   * Constructor
   *
   * @param start      Starting LetterTile
   * @param controller GameController
   */
  public WordGraph(LetterTile start, GameController controller) {
    this.start = start;
    count = 0;
    maxWidth = 15;
    maxHeight = 15;

    this.controller = controller;

    initShape();
  }

  /**
   * Constructor
   *
   * @param start      Starting LetterTile
   * @param maxWidth   max width
   * @param maxHeight  max height
   * @param controller GameController
   */
  public WordGraph(LetterTile start, int maxWidth, int maxHeight, GameController controller) {
    this.start = start;
    count = 0;
    this.maxWidth = maxWidth;
    this.maxHeight = maxHeight;

    this.controller = controller;

    initShape();
  }

}