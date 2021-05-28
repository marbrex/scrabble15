package scrabble.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import scrabble.ScrabbleApp;

/**
 * scrabble.game.Map class represents a grid of Multipliers (DW,DL,TW,TL,NO).
 *
 * @author ekasmamy
 */
public class Map {

  private ArrayList<Multiplier> cells;
  private final String path;
  private int size;

  /**
   * Default Constructor.
   *
   * @author ekasmamy
   */
  Map() {
    cells = new ArrayList<Multiplier>();
    path = "/maps/default-map.txt";
    size = 0;
    loadMap();
  }

  /**
   * Constructor that loads a Map from the specified text file. This file should follow some
   * formatting rules (see "maps/default-map.txt" for an example).
   *
   * @param path Path to the file in resources.
   * @author ekasmamy
   */
  Map(String path) {
    cells = new ArrayList<Multiplier>();
    this.path = path;
    size = 0;
    loadMap();
  }

  /**
   * Loads the default map. This file should follow some formatting rules (see
   * "maps/default-map.txt" for an example).
   *
   * @author ekasmamy
   */
  private void loadMap() {
    try {
      Scanner scanner;
      Path p = Paths.get(path);
      if (Files.exists(p)) {
        InputStream in = new FileInputStream(new File(path));
        scanner = new Scanner(new InputStreamReader(in));
      } else {
        InputStream in = getClass().getResourceAsStream(path);
        scanner = new Scanner(new InputStreamReader(in));
      }
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();

        Scanner lineScanner = new Scanner(line);
        while (lineScanner.hasNext()) {
          String multiplier = lineScanner.next();

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

      }
      scanner.close();
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }
  }

  /**
   * Get the Multiplier at global index "index".
   *
   * @param index Global Index.
   * @return Multiplier.
   * @author ekasmamy
   */
  public Multiplier getMultiplier(int index) {
    return cells.get(index);
  }

  /**
   * Get the size of the Map, number of cells (height=width).
   *
   * @return Number of cells on each side (height=width).
   * @author ekasmamy
   */
  public int getSize() {
    return size;
  }

}
