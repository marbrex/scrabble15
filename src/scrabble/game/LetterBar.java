package scrabble.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javafx.scene.input.Dragboard;
import scrabble.GameController;
import scrabble.game.LetterBag.Tile;

/**
 * <h1>scrabble.game.LetterBar</h1>
 *
 * <p>LetterBar class is an internal state of the letter bar at the bottom.</p>
 *
 * @author Eldar Kasmamytov
 */
public class LetterBar {

  GameController controller;

  /**
   * Number of Letters in the bar.
   */
  private int size;

  /**
   * Array of "Cell"s
   */
  private Slot[] slots;

  /**
   * Initiates the FRONT-end part. Creates a Slot and a LetterTile and puts the slot in
   * "lettersBlock" (HBox)
   */
  public void initBar() {

    LetterBag bag = LetterBag.getInstance();

    int i = 0;
    for (Tile t : bag.grabRandomTiles(size)) {
      char randLetter = t.letter;
      int ltrPoints = t.value;

      // Creating a new LetterTile
      LetterTile tile = new LetterTile(randLetter, ltrPoints, controller.grid.cellSize, controller);

      Slot slot = new Slot(tile, controller);

      slot.container.setOnDragDropped(event -> {
        // data dropped
        // System.out.println("onDragDropped");

        // if there is a string data on dragboard, read it and use it
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {

          Map<String, String> params = new HashMap<>();
          String paramsString = db.getString();
          String[] pairs = paramsString.split("&");
          for (String p : pairs) {
            String[] pair = p.split("=");
            params.put(pair[0], pair[1]);
          }

          LetterTile ltrTile;
          if (Boolean.parseBoolean(params.get("isBlank"))) {
            ltrTile = new LetterTile(controller);
            ltrTile.isBlank = true;
          } else {
            char letter = params.get("letter").charAt(0);
            int points = Integer.parseInt(params.get("points"));
            ltrTile = new LetterTile(letter, points, controller.grid.cellSize, controller);
            ltrTile.isBlank = false;
          }
          slot.setContent(ltrTile);

          success = true;
        }
        /* let the source know whether the string was successfully
         * transferred and used */
        event.setDropCompleted(success);

        event.consume();
      });

      slots[i] = slot;
      controller.lettersBlock.getChildren().add(slots[i].container);

      i++;
    }

    display();
  }

  /**
   * Constructor that sets the "size" of the bar to 7.
   *
   * @param controller GameController
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
   * @param size       The number of slots
   * @param controller GameController
   */
  public LetterBar(int size, GameController controller) {
    this.size = size;
    slots = new Slot[size];

    this.controller = controller;

    initBar();
  }

  /**
   * Default Constructor.
   */
  public LetterBar() {
    size = 7;
    slots = new Slot[size];
  }

  /**
   * Constructor without need to specify "GameController"
   *
   * @param size The number of Slots
   */
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
   * Checks whether all slots of the Letter Bar are occupied
   *
   * @return True if full, False otherwise
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
   * @param index Index of the slot
   * @return Slot
   */
  public Slot getSlot(int index) {
    return slots[index];
  }

  /**
   * Set a Slot.
   *
   * @param index Index of the slot
   * @param slot  Slot
   */
  public void setSlot(int index, Slot slot) {
    slots[index] = slot;
  }

  /**
   * Get a Slot that contains the specified LetterTile
   *
   * @param tile LetterTile
   * @return Slot
   */
  public Slot getSlotThatContains(LetterTile tile) {
    for (int i = 0; i < size; i++) {
      if (slots[i].content == tile) {
        return slots[i];
      }
    }
    return null;
  }
}
