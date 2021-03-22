package scrabble.model;

public class Grid {

    private Cell g[];
    private int size;
    private int globalSize;

    public Grid (int size) {
        this.size = size;
        globalSize = size*size;
        g = new Cell[globalSize];
    }

    public int getGlobalIndex (int x, int y) {
        return x + size*y;
    }

    public Cell getCell (int globalIndex) {
        return g[globalIndex];
    }

    public Cell getCell (int x, int y) {
        return g[getGlobalIndex(x, y)];
    }

    public void setCell (int x, int y, char letter, int points) {
        g[getGlobalIndex(x, y)].setCell(letter, points);
    }
}
