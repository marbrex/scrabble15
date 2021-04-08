package scrabble.game;

import javafx.scene.shape.Polygon;
import scrabble.GameController;

public class WordGraph {

  GameController controller;

  private LetterTile start;
  private int count;
  private final int maxWidth;
  private final int maxHeight;

  private Polygon shape;

  private void initShape(double... points) {
    shape = new Polygon(points);
    shape.getStyleClass().add("line");
  }

  public WordGraph(LetterTile start, GameController controller) {
    this.start = start;
    count = 0;
    maxWidth = 15;
    maxHeight = 15;

    this.controller = controller;

    initShape();
  }

  public WordGraph(LetterTile start, int maxWidth, int maxHeight, GameController controller) {
    this.start = start;
    count = 0;
    this.maxWidth = maxWidth;
    this.maxHeight = maxHeight;

    this.controller = controller;

    initShape();
  }

  public void visitAll() {
    LetterTile current = start;


  }

}
