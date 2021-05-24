package scrabble.game;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.io.Serializable;
import java.util.Random;

/**
 * <h1>scrabble.game.LetterBag</h1>
 *
 * <p>This is Singleton class that can be instantiated only once.</p>
 * <p>To create the single instance use: LetterBag.getInstance()</p>
 *
 * @author ekasmamy
 */
public class LetterBag implements Serializable {

  /**
   * <h1>scrabble.game.LetterBag.Tile</h1>
   *
   * <p>This class shouldn't be used to create actual Letter Tiles.</p>
   * <p>To create an actual letter tile use scrabble.game.LetterTile class instead</p>
   *
   * @see scrabble.game.LetterTile
   */
  public static class Tile implements Serializable {

    public char letter;
    public int value;
    public boolean isVowel;
    public boolean isBlank;

    public Tile() {
      this.letter = (char) 0;
      this.value = 0;
    }

    public Tile(char letter, int value) {
      this.letter = letter;
      this.value = value;
    }
  }

  private static LetterBag instance = null;
  private final Multiset<Tile> bag;
  private int originalSize;

  private final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + '\0';
  private final int[] count = {9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2,
      1, 2, 1, 2};
  private final int[] values = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4,
      4, 8, 4, 10, 0};

  /**
   * Default Constructor. This constructor should not be accessed directly. Use getInstance() method
   * instead.
   */
  private LetterBag() {
    bag = HashMultiset.create();

    fillBag();
  }

  /**
   * This will create an instance if there is no one, or just return already created instance.
   *
   * @return The single instance of LetterBag
   */
  public static LetterBag getInstance() {
    if (instance == null) {
      instance = new LetterBag();
    }

    return instance;
  }

  /**
   * Internal method that is used to fill the LetterBag with Tiles.
   */
  private void fillBag() {
    originalSize = 0;
    if (bag.isEmpty()) {
      char[] alphabetArray = alphabet.toCharArray();

      for (int i = 0; i < alphabetArray.length; i++) {
        Tile t = new Tile(alphabetArray[i], values[i]);

        String vowels = "AEIOUY";
        for (char vowel : vowels.toCharArray()) {
          if (t.letter == vowel) {
            t.isVowel = true;
            break;
          } else {
            t.isVowel = false;
          }
        }
        t.isBlank = false;

        bag.add(t);
        bag.setCount(t, count[i]);

        originalSize += count[i];
      }
    }
  }

  /**
   * Returns the original quantity letter tiles in the bag.
   *
   * @return Original quantity of letter tiles in the bag.
   */
  public int getOriginalAmount() {
    return originalSize;
  }

  /**
   * Returns the remaining quantity of the specified letter tile.
   *
   * @param letter letter (char)
   * @return Remaining quantity of the specified letter tile.
   */
  public int getAmountOf(char letter) {
    for (Tile tile : bag.elementSet()) {
      if (tile.letter == Character.toUpperCase(letter)) {
        return bag.count(tile);
      }
    }
    return 0;
  }

  /**
   * Returns the original quantity of the specified letter tile.
   *
   * @param letter letter (char)
   * @return Original quantity of the specified letter tile.
   */
  public int getOriginalAmountOf(char letter) {
    char[] charArray = alphabet.toCharArray();
    for (int i = 0; i < charArray.length; i++) {
      char c = charArray[i];
      if (c == letter) {
        return count[i];
      }
    }
    return 0;
  }

  /**
   * Internal method that is used to remove a tile from bag. Should not be accessed or used
   * directly.
   *
   * @param tile Letter tile to remove from the bag.
   */
  private void removeTile(Tile tile) {
    for (Tile t : bag.elementSet()) {
      if (t.letter == Character.toUpperCase(tile.letter)) {
        bag.remove(t, 1);
        break;
      }
    }
  }

  /**
   * Returns the current value of the specified letter tile.
   *
   * @param letter letter tile (char)
   * @return Current value of the specified letter tile.
   */
  public int getValueOf(char letter) {
    for (Tile tile : bag.elementSet()) {
      if (tile.letter == Character.toUpperCase(letter)) {
        return tile.value;
      }
    }
    return -1;
  }

  /**
   * Returns a Multiset containing remaining vowels in the bag.
   *
   * @return Multiset containing remaining vowels in the bag.
   */
  public Multiset<Tile> getRemainingVowels() {
    Multiset<Tile> set = HashMultiset.create();
    bag.elementSet().forEach(tile -> {
      if (tile.isVowel) {
        set.add(tile);
      }
    });
    return set;
  }

  /**
   * Returns a Multiset containing remaining consonants in the bag.
   *
   * @return Multiset containing remaining consonants in the bag.
   */
  public Multiset<Tile> getRemainingConsonants() {
    Multiset<Tile> set = HashMultiset.create();
    bag.elementSet().forEach(tile -> {
      if (!tile.isVowel) {
        set.add(tile);
      }
    });
    return set;
  }

  /**
   * Returns a Multiset containing remaining blank tiles in the bag.
   *
   * @return Multiset containing remaining blank tiles in the bag.
   */
  public Multiset<Tile> getRemainingBlanks() {
    Multiset<Tile> set = HashMultiset.create();
    bag.elementSet().forEach(tile -> {
      if (tile.isBlank) {
        set.add(tile);
      }
    });
    return set;
  }

  /**
   * Picks a letter tile from the bag. And laos removes the picked tile from the bag.
   *
   * @return Tile
   */
  public Tile grabRandomTile() {
    Tile res = new Tile();

    Random r = new Random();
    Multiset<Tile> subset = HashMultiset.create();
    while (subset.isEmpty()) {
      if (bag.isEmpty()) {
        return null;
      }

      float random = r.nextFloat();

      if (random <= 0.5) {
        bag.elementSet().forEach(tile -> {
          if (tile.isBlank) {
            subset.add(tile);
          }
        });
      } else if (random <= 0.45) {
        bag.elementSet().forEach(tile -> {
          if (tile.isVowel) {
            subset.add(tile);
          }
        });
      } else {
        bag.elementSet().forEach(tile -> {
          if (!tile.isVowel) {
            subset.add(tile);
          }
        });
      }
    }

    int letterIndex = Math.abs(r.nextInt()) % subset.size();
    int i = 0;
    for (Tile tile : subset.elementSet()) {
      if (i == letterIndex) {
        res = tile;
        break;
      }
      i++;
    }

    removeTile(res);

    return res;
  }

  /**
   * Returns a Multiset containing "count" random tiles from the bag. These tiles are removed from
   * the bag.
   *
   * @param count number of tiles top grab
   * @return Multiset containing "count" random tiles from the bag.
   */
  public Multiset<Tile> grabRandomTiles(int count) {
    Multiset<Tile> set = HashMultiset.create();
    for (int i = 0; i < count; i++) {
      set.add(grabRandomTile());
    }
    return set;
  }

  /**
   * Returns a copy Multiset containing all remaining tiles. Changes made on the returned Multiset
   * will not affect the original bag.
   *
   * @return Copy Multiset containing all remaining tiles.
   */
  public Multiset<Tile> getRemainingTiles() {
    Multiset<Tile> set = HashMultiset.create();
    for (Tile tile : bag) {
      if (!tile.isBlank) {
        set.add(tile);
      }
    }
    return set;
  }

  /**
   * Get current size of the bag.
   *
   * @return Current size of the bag.
   */
  public int getAmount() {
    return bag.size();
  }

  /**
   * Returns initial alphabet's size.
   *
   * @return Initial alphabet's size.
   */
  public int getAlphabetSize() {
    return alphabet.length();
  }

  /**
   * Returns n'th letter in the initial alphabet.
   *
   * @param index n'th letter
   * @return n'th letter in the initial alphabet.
   */
  public char getLetterInAlphabet(int index) {
    return Character.toUpperCase(alphabet.charAt(index));
  }

  /**
   * Returns n'th letter's value in the initial alphabet.
   *
   * @param index n'th letter's value
   * @return n'th letter's value in the initial alphabet.
   */
  public int getValueInAlphabet(int index) {
    return values[index];
  }

  public static void main(String[] args) {
    LetterBag ltrBag = LetterBag.getInstance();

    for (int i = 0; i < 7; i++) {
      Tile t = ltrBag.grabRandomTile();
      if (t.isBlank) {
        System.out.print("[BLANK] ");
      } else {
        System.out.print("[" + t.letter + ", " + t.value + "] ");
      }
    }

    System.out.print("\n");
    System.out.println("Size is: " + ltrBag.getAmount());

    ltrBag.grabRandomTiles(7).forEach(tile -> {
      if (tile.isBlank) {
        System.out.print("[BLANK] ");
      } else {
        System.out.print("[" + tile.letter + ", " + tile.value + "] ");
      }
    });

    System.out.print("\n");
    System.out.println("Size is: " + ltrBag.getAmount());

    ltrBag.grabRandomTiles(3).forEach(tile -> {
      if (tile.isBlank) {
        System.out.print("[BLANK] ");
      } else {
        System.out.print("[" + tile.letter + ", " + tile.value + "] ");
      }
    });

    System.out.print("\n");
    System.out.println("Size is: " + ltrBag.getAmount());

    ltrBag.bag.clear();

    ltrBag.grabRandomTiles(7).forEach(tile -> {
      if (tile == null) {
        System.out.print("[NULL] ");
      } else if (tile.isBlank) {
        System.out.print("[BLANK] ");
      } else {
        System.out.print("[" + tile.letter + ", " + tile.value + "] ");
      }
    });

  }

}
