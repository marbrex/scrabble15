package scrabble.model;

import java.util.Random;

/**
 * LetterBar class is an internal state of the letter bar at the bottom.
 *
 * @author Eldar Kasmamytov
 */
public class LetterBar {

  /**
   * Number of Letters in the bar.
   */
  private int size;

  /**
   * Array of "Cell"s
   */
  private Cell[] slots;

  /**
   * Default Constructor. Sets the "size" of the bar to 7.
   */
  public LetterBar() {
    size = 7;
    slots = new Cell[size];
    for (int i = 0; i < size; i++) {
      slots[i] = new Cell();
    }
  }

  /**
   * Constructor which sets the number of slots to "size".
   *
   * @param size The number of slots
   */
  public LetterBar(int size) {
    this.size = size;
    slots = new Cell[size];
    for (int i = 0; i < size; i++) {
      slots[i] = new Cell();
    }
  }

  /**
   * Shuffles the letters inside the bar.
   */
  public void shuffle() {
    Random rand = new Random();

    for (int i = 0; i < size; i++) {
      int randomIndexToSwap = rand.nextInt(size);
      Cell temp = slots[randomIndexToSwap];
      slots[randomIndexToSwap] = slots[i];
      slots[i] = temp;
    }
  }

  /**
   * Displays the current internal state of the bar in the console.
   */
  public void display() {
    System.out.println("\nCellBar: ");
    for (int i = 0; i < size; i++) {
      System.out.print("[" + slots[i].getLtr() + "|" + slots[i].getPts() + "]");
      if (i < size) {
        System.out.print(" ");
      } else {
        System.out.print("\n");
      }
    }
  }

  /**
   * Getter for a slot.
   *
   * @param index Index of the slot
   * @return Cell
   */
  public Cell getSlot(int index) {
    return slots[index];
  }
}
