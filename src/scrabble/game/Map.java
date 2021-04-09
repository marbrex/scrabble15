package scrabble.game;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import scrabble.ScrabbleApp;

public class Map {

  private ArrayList<Multiplier> cells;
  private final String path;
  private int size;

  Map() {
    cells = new ArrayList<Multiplier>();
    path = "maps/default-map.txt";
    size = 0;
    loadMap();
  }

  Map(String path) {
    cells = new ArrayList<Multiplier>();
    this.path = path;
    size = 0;
    loadMap();
  }

  private void loadMap() {
    try {
      File file = new File(ScrabbleApp.class.getResource(path).getPath());

      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();

        Scanner lineScanner = new Scanner(line);
        while (lineScanner.hasNext()) {
          String multiplier = lineScanner.next();
          // System.out.print(multiplier + " ");

          switch (multiplier) {
            case "DL":
              cells.add(Multiplier.DL);
              break;
            case "DW":
              cells.add(Multiplier.DW);
              break;
            case "TL":
              cells.add(Multiplier.TL);
              break;
            case "TW":
              cells.add(Multiplier.TW);
              break;
            default:
              cells.add(Multiplier.NO);
              break;
          }
        }
        lineScanner.close();
        size++;
        // System.out.print("\n");
      }
      scanner.close();
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }
  }

  public Multiplier getMultiplier(int index) {
    return cells.get(index);
  }

  public int getSize() {
    return size;
  }

  public static void main(String[] args) {
    Map m = new Map();
    System.out.println("Size: " + m.getSize());
  }

}
