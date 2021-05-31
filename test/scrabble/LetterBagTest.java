package scrabble;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.collect.Multiset;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import scrabble.game.LetterBag;
import scrabble.game.LetterBag.Tile;
import scrabble.game.LetterTile;

/**
 * This is a JUnit test class for the scrabble.game.LetterBag class.
 *
 * @author ekasmamy
 */
@DisplayName("JUnit 5 - Letter Bag Test")
public class LetterBagTest {

  static LetterBag bag = LetterBag.getInstance();

  @Test
  @DisplayName("grabTileTest")
  void grabTileTest() {
    System.out.println("\n┌─@grabTileTest START");

    System.out.println("│ Grabbing 1 random tile...");
    Tile t = bag.grabRandomTile();
    System.out.println("│ Grabbed Tile = [" + t.letter + ", " + t.value + "]");

    assertNotEquals(null, t, "[error]");

    System.out.println("│ Checking the bag's size...");
    int originalSize = bag.getOriginalAmount();
    int newSize = bag.getAmount();
    System.out.println("│ Remaining Tiles = " + newSize);
    System.out.println("│ Original Size = " + originalSize);

    assertTrue(newSize < originalSize, "[error]");

    System.out.println("│ Checking the tile's count in the bag...");
    int remainAmount = bag.getAmountOf(t.letter);
    int originalAmount = bag.getOriginalAmountOf(t.letter);
    System.out.println("│ Remaining amount of Tiles with this letter = " + remainAmount);
    System.out.println("│ Original amount of Tiles with this letter = " + originalAmount);

    assertTrue(remainAmount < originalAmount, "[error]");

    System.out.println("└─@grabTileTest END");
  }

  @RepeatedTest(5)
  @DisplayName("grabTilesTest")
  void grabTilesTest() {
    System.out.println("\n┌─@grabTilesTest START");

    Multiset<Tile> set;
    System.out.println("│ Grabbing 4 random tiles...");
    set = bag.grabRandomTiles(4);

    assertTrue(!set.isEmpty(), "[set is empty]");

    System.out.println("│ Checking the bag's size...");
    int originalSize = bag.getOriginalAmount();
    int newSize = bag.getAmount();
    System.out.println("│ Remaining Tiles = " + newSize);
    System.out.println("│ Original Size = " + originalSize);

    assertTrue(newSize < originalSize, "[error]");

    System.out.println("│ Grabbed Tiles:");
    set.forEach(t -> {
      System.out.println("│");
      System.out.println("│ Grabbed Tile = [" + t.letter + ", " + t.value + "]");

      assertNotEquals(null, t, "[error]");

      System.out.println("│ Checking the tile's count in the bag...");
      int remainAmount = bag.getAmountOf(t.letter);
      int originalAmount = bag.getOriginalAmountOf(t.letter);
      System.out.println("│ Remaining amount of Tiles with this letter = " + remainAmount);
      System.out.println("│ Original amount of Tiles with this letter = " + originalAmount);

      assertTrue(remainAmount < originalAmount, "[error]");
    });

    System.out.println("└─@grabTilesTest END");
  }

  @Test
  @DisplayName("exchangeTilesTest")
  void exchangeTilesTest() {
    System.out.println("\n┌─@exchangeTilesTest START");

    System.out.println("│ Grabbing random tiles...");
    Multiset<Tile> set = bag.grabRandomTiles(7);

    assertTrue(!set.isEmpty(), "[grabbed set is empty]");

    System.out.println("│ Checking the bag's size...");
    int originalSize = bag.getOriginalAmount();
    int newSize = bag.getAmount();
    System.out.println("│ Remaining Tiles = " + newSize);
    System.out.println("│ Original Size = " + originalSize);

    assertTrue(newSize < originalSize, "[error]");

    System.out.println("│");
    System.out.println("│ Grabbed Tiles:");
    set.forEach(t -> {
      System.out.println("│");
      System.out.println("│ Grabbed Tile = [" + t.letter + ", " + t.value + "]");

      assertNotEquals(null, t, "[error]");

      System.out.println("│ Checking the tile's count in the bag...");
      int remainAmount = bag.getAmountOf(t.letter);
      int originalAmount = bag.getOriginalAmountOf(t.letter);
      System.out.println("│ Remaining amount of Tiles with this letter = " + remainAmount);
      System.out.println("│ Original amount of Tiles with this letter = " + originalAmount);

      assertTrue(remainAmount < originalAmount, "[error]");
    });

    ArrayList<Tile> tiles = new ArrayList<>();
    tiles.addAll(set);

    System.out.println("│ Exchanging the grabbed tiles with random tiles in the bag...");
    Multiset<Tile> exchangedSet = bag.exchangeTiles(tiles);

    assertTrue(!exchangedSet.isEmpty(), "[exchanged set is empty]");

    System.out.println("│");
    System.out.println("│ Exchanged Tiles:");
    exchangedSet.forEach(t -> {
      System.out.println("│");
      System.out.println("│ Grabbed Tile = [" + t.letter + ", " + t.value + "]");

      assertNotEquals(null, t, "[error]");

      System.out.println("│ Checking the tile's count in the bag...");
      int remainAmount = bag.getAmountOf(t.letter);
      int originalAmount = bag.getOriginalAmountOf(t.letter);
      System.out.println("│ Remaining amount of Tiles with this letter = " + remainAmount);
      System.out.println("│ Original amount of Tiles with this letter = " + originalAmount);

      assertTrue(remainAmount < originalAmount, "[error]");
    });

    System.out.println("└─@exchangeTilesTest END");
  }

  @BeforeAll
  static void beforeAll() {
    System.out.println("\n┌─@beforeAll START");

    System.out.println("│ Letter Bag Size = " + bag.getAmount());
    assertEquals(100, bag.getAmount(), " - bag's size != 100");

    System.out.println("└─@beforeAll END");
  }

  @BeforeEach
  void beforeEach() {
    System.out.println("\n┌─@beforeEach START");

    System.out.println("│ Letter Bag Size = " + bag.getAmount());

    System.out.println("└─@beforeEach END");
  }

  @AfterEach
  void afterEach() {
    System.out.println("\n┌─@afterEach START");

    System.out.println("│ Letter Bag Size = " + bag.getAmount());

    System.out.println("└─@afterEach END");
  }

  @AfterAll
  static void afterAll() {
    System.out.println("\n┌─@afterAll START");
    System.out.println("└─@afterAll END");
  }

}
