package scrabble.game;

import com.google.common.collect.Multiset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import javafx.scene.input.Dragboard;
import scrabble.GameController;
import scrabble.game.LetterBag.Tile;

/**
 * scrabble.game.LetterBar class is an internal state of the letter bar at the bottom.
 *
 * @author ekasmamy
 */
public class LetterBar {

  GameController controller;

  /**
   * Number of Letters in the bar.
   */
  private int size;

  /**
   * Array of "Cell"s.
   */
  private Slot[] slots;

  /**
   * Initiates the FRONT-end part. Creates a Slot and a LetterTile and puts the slot in
   * "lettersBlock" (HBox).
   *
   * @author ekasmamy
   */
  public void initBar() {

    for (int i = 0; i < size; i++) {

      Slot slot = new Slot(controller);

      slots[i] = slot;
      controller.lettersBlock.getChildren().add(slots[i].container);
    }

    display();
  }

  /**
   * Constructor that sets the "size" of the bar to 7.
   *
   * @param controller GameController.
   * @author ekasmamy
   */
  public LetterBar(GameController controller) {
    size = 7;
    slots = new Slot[size];

    this.controller = controller;

    initBar();
  }

  /**
   * Constructor which sets the number of slots to "size".
   *
   * @param size       The number of slots.
   * @param controller GameController.
   * @author ekasmamy
   */
  public LetterBar(int size, GameController controller) {
    this.size = size;
    slots = new Slot[size];

    this.controller = controller;

    initBar();
  }

  /**
   * Default Constructor.
   *
   * @author ekasmamy
   */
  public LetterBar() {
    size = 7;
    slots = new Slot[size];
  }

  /**
   * Constructor without need to specify "GameController".
   *
   * @param size The number of Slots.
   * @author ekasmamy
   */
  public LetterBar(int size) {
    this.size = size;
    slots = new Slot[size];
  }

  /**
   * Shuffles the letters inside the bar.
   *
   * @author ekasmamy
   */
  public void shuffle() {

    System.out.println("In shuffle !");
    Random rand = new Random();

    for (int i = 0; i < size; i++) {
      int randomIndexToSwap = rand.nextInt(size);
      while (randomIndexToSwap == i) {
        randomIndexToSwap = rand.nextInt(size);
      }
      System.out.println("\n@shuffle - Swapping " + i + " with " + randomIndexToSwap);
      LetterTile temp = slots[randomIndexToSwap].content;
      slots[randomIndexToSwap].setContent(slots[i].content);
      slots[i].setContent(temp);
      System.out.println("@shuffle - " + i + " is now " + slots[i].content.getLetter());
      System.out.println("@shuffle - " + i + " slot " + slots[i]);
      System.out.println("@shuffle - " + i + " slot of tile " + slots[i].content.slot);
      System.out.println(
          "@shuffle - " + randomIndexToSwap + " is now " + slots[randomIndexToSwap].content
              .getLetter());
      System.out.println("@shuffle - " + randomIndexToSwap + " slot " + slots[randomIndexToSwap]);
      System.out.println("@shuffle - " + randomIndexToSwap + " slot of tile "
          + slots[randomIndexToSwap].content.slot);
      display();
    }

    System.out.println("New Letter order in LetterBar: ");
    display();
  }

  /**
   * Checks whether all slots of the Letter Bar are occupied.
   *
   * @return True if full, False otherwise.
   * @author ekasmamy
   */
  public boolean isFull() {
    int counter = 0;
    for (Slot slot : slots) {
      if (!slot.isFree()) {
        counter++;
      }
    }

    return counter == size;
  }

  /**
   * Displays the current internal state of the bar in the console.
   *
   * @author ekasmamy
   */
  public void display() {
    System.out.println("\nLetter Bar: ");
    for (int i = 0; i < size; i++) {
      if (slots[i].content != null) {
        System.out
            .print("[" + slots[i].content.getLetter() + "|" + slots[i].content.getPoints() + "]");
      } else {
        System.out
            .print("[   ]");
      }
      if (i == size - 1) {
        System.out.print("\n\n");
      } else {
        System.out.print(" ");
      }
    }
  }

  /**
   * Get a Slot.
   *
   * @param index Index of the slot.
   * @return Slot.
   * @author ekasmamy
   */
  public Slot getSlot(int index) {
    return slots[index];
  }

  /**
   * Set a Slot.
   *
   * @param index Index of the slot.
   * @param slot  Slot.
   * @author ekasmamy
   */
  public void setSlot(int index, Slot slot) {
    slots[index] = slot;
  }

  /**
   * Get a Slot that contains the specified LetterTile.
   *
   * @param tile LetterTile.
   * @return Slot.
   * @author ekasmamy
   */
  public Slot getSlotThatContains(LetterTile tile) {
    for (int i = 0; i < size; i++) {
      if (slots[i].content == tile) {
        return slots[i];
      }
    }
    return null;
  }

  /**
   * Returns a list of all tiles present in the bar.
   *
   * @return List of all tiles present in the bar.
   * @author ekasmamy
   */
  public ArrayList<LetterTile> getTilesInBar() {
    ArrayList<LetterTile> list = new ArrayList<>();
    for (Slot slot : slots) {
      if (slot.content != null) {
        list.add(slot.content);
      }
    }
    return list;
  }

  /**
   * Returns a list of empty slots in the bar.
   *
   * @return List of empty slots in the bar.
   * @author ekasmamy
   */
  public ArrayList<Slot> getEmptySlots() {
    ArrayList<Slot> list = new ArrayList<>();
    for (Slot slot : slots) {
      if (slot.isFree()) {
        list.add(slot);
      }
    }
    return list;
  }

  /**
   * Returns every just placed and not frozen tiles in the grid back to the bar.
   *
   * @author ekasmamy
   */
  public void putTilesBackToBar() {
    System.out.println("@LetterBar - putTilesBackToBar()");
    Iterator<Word> it = controller.grid.words.iterator();

    while (it.hasNext()) {
      Word word = it.next();
      if (!word.frozen) {
        it.remove();
        controller.gridWrapper.getChildren().remove(word.container);
      }
    }

    ArrayList<Slot> emptySlots = getEmptySlots();
    int i = 0;
    for (LetterTile tile : controller.grid.getTilesInGrid()) {
      if (!tile.isFrozen) {
        System.out.println("@LetterBar - putTilesBackToBar() - " + emptySlots.get(i));
        LetterTile t = new LetterTile(tile.getLetter(), tile.getPoints(), controller.grid.cellSize,
            controller);
        emptySlots.get(i).setContent(t);
        i++;
        controller.grid.removeSlotContent(tile.slot);
      }
    }
  }

  /**
   * Returns a number of empty slots in the bar.
   *
   * @return Number of empty slots in the bar.
   * @author ekasmamy
   */
  public int getCountFreeSlots() {
    int counter = 0;
    for (Slot slot : slots) {
      if (slot.isFree()) {
        counter++;
      }
    }
    return counter;
  }

  /**
   * Fills the empty slots with tiles from the bag.
   *
   * @param tileSet Set of tiles from the bag.
   * @author ekasmamy
   */
  public void fillGaps(Multiset<Tile> tileSet) {
    System.out.println(this + " - @fillGaps()");
    if (tileSet.size() == getCountFreeSlots()) {
      Iterator<Slot> emptySlots = getEmptySlots().iterator();
      for (Tile tile : tileSet) {
        LetterTile tileToPaste = new LetterTile(tile.letter, tile.value, controller.grid.cellSize,
            controller);
        if (emptySlots.hasNext()) {
          emptySlots.next().setContent(tileToPaste);
        }
      }
    } else {
      System.err.println("Error while filling the empty slots in the LetterBar");
    }
  }

  /**
   * Returns the size of the bar.
   *
   * @return Size of the bar.
   * @author ekasmamy
   */
  public int getSize() {
    return size;
  }

  /**
   * Sets the letter tiles in the bar.
   *
   * @param tiles New collection of tiles.
   * @author ekasmamy
   */
  public void setTiles(Collection<Tile> tiles) {
    if (tiles.size() == size) {
      Iterator<Tile> it = tiles.iterator();
      for (Slot slot : slots) {
        if (it.hasNext()) {
          Tile tile = it.next();
          LetterTile ltrTile = new LetterTile(tile.letter, tile.value, controller.grid.cellSize,
              controller);
          slot.setContent(ltrTile);
        }
      }
    }
  }
}
