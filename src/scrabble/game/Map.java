package scrabble.game;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import scrabble.ScrabbleApp;

/**
 * <h1>scrabble.game.Map</h1>
 *
 * <p>This class represents a grid of Multipliers (DW,DL,TW,TL,NO)</p>
 *
 * @author Eldar Kasmamytov
 */
public class Map {

  private ArrayList<Multiplier> cells;
  private final String path;
  private int size;

  /**
   * Default Constructor.
   */
  Map() {
    cells = new ArrayList<Multiplier>();
    path = "maps/default-map.txt";
    size = 0;
    loadMap();
  }

  /**
   * Constructor that loads a Map from the specified text file. This file should follow some
   * formatting rules (see "maps/default-map.txt" for an example)
   *
   * @param path Path to the file in resources
   */
  Map(String path) {
    cells = new ArrayList<Multiplier>();
    this.path = path;
    size = 0;
    loadMap();
  }

  /**
   * Loads the default map. This file should follow some formatting rules (see
   * "maps/default-map.txt" for an example)
   */
  private void loadMap() {
    try {
//     File file = new File(ScrabbleApp.class.getResource(path).getPath());
      InputStream in = getClass().getResourceAsStream("/scrabble/" + path);
      Scanner scanner = new Scanner(new InputStreamReader(in));
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

  /**
   * Get the Multiplier at global index "index"
   *
   * @param index Global Index
   * @return Multiplier
   */
  public Multiplier getMultiplier(int index) {
    return cells.get(index);
  }

  /**
   * Get the size of the Map, number of cells (height=width)
   *
   * @return Number of cells on each side (height=width)
   */
  public int getSize() {
    return size;
  }

}
