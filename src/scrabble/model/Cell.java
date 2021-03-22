package scrabble.model;

public class Cell {

    private char ltr;
    private int pts;
    private boolean isFree;

    public Cell () {
        isFree = true;
        pts = 0;
        ltr = (char) 0;
    }

    public Cell (char ltr, int pts) {
        isFree = false;
        this.pts = pts;
        this.ltr = ltr;
    }

    public void setCell (char letter, int points) {
        isFree = false;
        ltr = letter;
        pts = points;
    }

    public char getLtr() {
        return ltr;
    }

    public int getPts() {
        return pts;
    }
}
