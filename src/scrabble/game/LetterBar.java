package scrabble.game;

import java.util.Random;
import javafx.scene.input.Dragboard;
import scrabble.ScrabbleController;

/**
 * LetterBar class is an internal state of the letter bar at the bottom.
 *
 * @author Eldar Kasmamytov
 */
public class LetterBar {

  ScrabbleController controller;

  /**
   * Number of Letters in the bar.
   */
  private int size;

  /**
   * Array of "Cell"s
   */
  private Slot[] slots;

  public void initBar() {

    // This alphabet will be used to pick letters from
    Random rand = new Random();
    String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    for (int i = 0; i < size; i++) {

      // Picking random letter from the alphabet declared above
      char randLetter = alphabet.charAt(rand.nextInt(alphabet.length()));
      int ltrPoints = 2;

      // Creating new letter
      LetterTile tile = new LetterTile(randLetter, ltrPoints, controller.grid.cellSize, controller);

      Slot slot = new Slot(tile, controller);

      slot.container.setOnDragDropped(event -> {
        // data dropped
        // System.out.println("onDragDropped");

        // if there is a string data on dragboard, read it and use it
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {

          char letter = db.getString().charAt(0);
          int points = Character.getNumericValue(db.getString().charAt(1));

          slot.setContent(new LetterTile(letter, points, controller.grid.cellSize, controller));

          success = true;
        }
        /* let the source know whether the string was successfully
         * transferred and used */
        event.setDropCompleted(success);

        event.consume();
      });

      slots[i] = slot;
      controller.lettersBlock.getChildren().add(slots[i].container);
    }

    display();
  }

  /**
   * Default Constructor. Sets the "size" of the bar to 7.
   */
  public LetterBar(ScrabbleController controller) {
    size = 7;
    slots = new Slot[size];

    this.controller = controller;

    initBar();
  }

  /**
   * Constructor which sets the number of slots to "size".
   *
   * @param size The number of slots
   */
  public LetterBar(int size, ScrabbleController controller) {
    this.size = size;
    slots = new Slot[size];

    this.controller = controller;

    initBar();
  }

  public LetterBar() {
    size = 7;
    slots = new Slot[size];
  }

  public LetterBar(int size) {
    this.size = size;
    slots = new Slot[size];
  }

  /**
   * Shuffles the letters inside the bar.
   */
  public void shuffle() {

    System.out.println("In shuffle !");
    Random rand = new Random();

    for (int i = 0; i < size; i++) {
      int randomIndexToSwap = rand.nextInt(size);
      LetterTile temp = slots[randomIndexToSwap].content;
      slots[randomIndexToSwap].setContent(slots[i].content);
      slots[i].setContent(temp);
    }

    System.out.println("New Letter order in LetterBar: ");
    display();
  }

  /**
   * Displays the current internal state of the bar in the console.
   */
  public void display() {
    System.out.println("\nLetter Bar: ");
    for (int i = 0; i < size; i++) {
      System.out
          .print("[" + slots[i].content.getLetter() + "|" + slots[i].content.getPoints() + "]");
      if (i == size - 1) {
        System.out.print("\n\n");
      } else {
        System.out.print(" ");
      }
    }
  }

  /**
   * Getter for a slot.
   *
   * @param index Index of the slot
   * @return Cell
   */
  public Slot getSlot(int index) {
    return slots[index];
  }

  public void setSlot(int index, Slot slot) {
    slots[index] = slot;
  }

  public Slot getSlotThatContains(LetterTile tile) {
    for (int i = 0; i < size; i++) {
      if (slots[i].content == tile) {
        return slots[i];
      }
    }
    return null;
  }
}
