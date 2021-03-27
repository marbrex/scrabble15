package scrabble.model;

import java.util.LinkedList;

public class Word {

  private LinkedList<Cell> w;
  private int wordLength;
  public boolean isCorrect;

  public Word () {
    w = new LinkedList<Cell>();
    wordLength = 0;
    isCorrect = false;
  }

  public void addCellEnd (Cell cell) {
    if (!cell.isFree()) {
      w.addLast(cell);
    }
  }

  public void addCellStart (Cell cell) {
    if (!cell.isFree()) {
      w.addFirst(cell);
    }
  }

  public void removeCell (Cell cell) {
    w.remove(cell);
  }

}
