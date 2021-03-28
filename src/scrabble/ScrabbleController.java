package scrabble;

import com.jfoenix.controls.JFXButton;
import java.util.Random;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import scrabble.model.Grid;
import scrabble.model.LetterBar;

/**
 * <h1>The Main Controller linked with "interface.fxml" file.</h1>
 *
 * <h2>Main functions:</h2>
 * <ul>
 * <li>Initialize cells (Rectangle) of the 15x15 grid (GridPane).
 * <li>Initialize proposed letters.
 * </ul>
 *
 * @author Eldar Kasmamytov
 */
public class ScrabbleController {

  /* Some reminders:
   * - @FXML annotation on a member declares that the FXML loader can access the member even if it is private.
   * - Only one controller is allowed per FXML document (must be specified on the root element).
   * - If the name of an accessible instance variable matches the fx:id attribute of an element,
   * the object reference from FXML is automatically copied into the controller instance variable.
   * */

  /**
   * List of currently proposed Letters.
   */
  @FXML
  private HBox lettersBlock;

  /**
   * Grid of letters.
   */
  @FXML
  private GridPane gridPaneUI;

  /**
   * OK button, which ends a player's turn.
   */
  @FXML
  private JFXButton okBtn;

  /**
   * Shuffle button, which shuffles the LetterBar.
   */
  @FXML
  private JFXButton shuffleBtn;

  /**
   * The actual data of the letter grid will be stocked here.
   */
  private Grid gridData;

  final private int gridPaneSize;
  final private int cellNumber;
  final private int gridPanePadSize;
  final private int cellSize;

  final private int lettersNumber;
  Pane[] letterSlotsUI;

  private LetterBar ltrBar;

  /**
   * Default constructor.
   */
  public ScrabbleController() {
    gridPaneSize = 825;
    cellNumber = 15;
    gridPanePadSize = 3;
    cellSize = gridPaneSize / cellNumber - gridPanePadSize;
    lettersNumber = 7;
    gridData = new Grid(cellNumber);
    letterSlotsUI = new Pane[lettersNumber];
    ltrBar = new LetterBar(lettersNumber);
  }

  /**
   * Creates a cell (Rectangle) in the letters grid (GridPane).
   *
   * @param i      Column of the cell
   * @param j      Row of the cell
   * @param effect Effect to add
   * @return Rectangle
   */
  private Rectangle initCell(int i, int j, Effect effect) {
    Rectangle rect = new Rectangle(i, j, cellSize, cellSize);
    rect.setFill(Color.AQUA);
    rect.setStroke(Color.DARKGRAY);
    rect.setArcWidth(10);
    rect.setArcHeight(10);
    rect.setEffect(effect);

    rect.setOnMouseEntered(event -> rect.setFill(Color.CRIMSON));
    rect.setOnMouseExited(event -> rect.setFill(Color.AQUA));

    rect.setOnDragOver(event -> {
      // data is dragged over the target
      System.out.println("onDragOver");

      /* accept it only if it is not dragged from the same node
       * and if it has a string data */
      if (event.getGestureSource() != rect &&
          event.getDragboard().hasString()) {
        // allow for both copying and moving, whatever user chooses
        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
      }

      event.consume();
    });

    rect.setOnDragEntered(event -> {
      // the drag-and-drop gesture entered the target
      System.out.println("onDragEntered");

      // show to the user that it is an actual gesture target
      if (event.getGestureSource() != rect &&
          event.getDragboard().hasString()) {
        rect.setFill(Color.GREEN);
      }

      event.consume();
    });

    rect.setOnDragExited(event -> {
      // mouse moved away, remove the graphical cues
      rect.setFill(Color.AQUA);

      event.consume();
    });

    rect.setOnDragDropped(event -> {
      // data dropped
      System.out.println("onDragDropped");

      // if there is a string data on dragboard, read it and use it
      Dragboard db = event.getDragboard();
      boolean success = false;
      if (db.hasString()) {

        // Getting the coordinates of the placeholder rectangle
        int x = GridPane.getColumnIndex(rect);
        int y = GridPane.getRowIndex(rect);

        // Removing the placeholder rectangle
        GridPane.clearConstraints(rect);
        gridPaneUI.getChildren().remove(rect);

        // Getting the letter data transferred by D&D
        char letter = db.getString().charAt(0);
        int points = Character.getNumericValue(db.getString().charAt(1));

        // creating an effect for the letter
        DropShadow ds = new DropShadow();
        ds.setHeight(20);
        ds.setWidth(20);
        ds.setOffsetY(-3);
        ds.setOffsetX(3);
        ds.setColor(Color.GRAY);

        // Creating a new Letter
        Button ltr = initLetter(letter, points, ds);

        // Adding the Letter to the GridPane (FRONT)
        GridPane.setConstraints(ltr, x, y);
        gridPaneUI.getChildren().add(ltr);

        // Adding the Letter to the GridData (BACK)
        gridData.setCell(x, y, letter, points);

        success = true;
      }
      /* let the source know whether the string was successfully
       * transferred and used */
      event.setDropCompleted(success);

      event.consume();
    });

    return rect;
  }

  /**
   * Creates and Initiates all the cells in the letter grid.
   */
  private void initCells() {

    // creating an effect for all the cells
    InnerShadow is = new InnerShadow();
    is.setColor(Color.DARKGRAY);
    is.setOffsetX(-5);
    is.setOffsetY(5);
    is.setHeight(20);
    is.setWidth(20);

    for (int i = 0; i < cellNumber; i++) {
      for (int j = 0; j < cellNumber; j++) {

        Rectangle rect = initCell(i, j, is);
        gridPaneUI.add(rect, i, j);
      }
    }
  }

  /**
   * Creates a letter (Button).
   *
   * @param letter Letter
   * @param points Points
   * @param effect Effect
   * @return Letter (Button)
   */
  private Button initLetter(char letter, int points, Effect effect) {

    Button ltr = new Button(String.valueOf(letter));
    ltr.getStyleClass().add("letter-btn");
    ltr.setPrefSize(cellSize, cellSize);
    ltr.setEffect(effect);

//    ltr.setOnMousePressed(event -> {
//      System.out
//          .println("Event.getScene: (" + event.getSceneX() + ", " + event.getSceneY() + ")");
//      System.out.println("Event.get: (" + event.getX() + ", " + event.getY() + ")\n");
//    });
//
//    ltr.setOnMouseDragged(mouseEvent -> {
//      ltr.toFront();
//      ltr.setCursor(Cursor.CLOSED_HAND);
//      ltr.setStyle("-fx-opacity: 0.6");
//
//      ltr.setLayoutX(mouseEvent.getX() + ltr.getLayoutX() - ltr.getWidth() / 2);
//      ltr.setLayoutY(mouseEvent.getY() + ltr.getLayoutY() - ltr.getHeight() / 2);
//
//      mouseEvent.consume();
//    });
//
//    ltr.setOnMouseReleased(mouseEvent -> {
//      ltr.setCursor(Cursor.DEFAULT);
//      ltr.setStyle("-fx-opacity: 1");
//
//      mouseEvent.consume();
//    });

    ltr.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> ltr.setStyle("-fx-opacity: 0.8"));
    ltr.addEventHandler(MouseEvent.MOUSE_EXITED, event -> ltr.setStyle("-fx-opacity: 1"));

    ltr.setOnDragDetected(event -> {
      // drag was detected, start drag-and-drop gesture
      System.out.println("onDragDetected");

      // allow any transfer mode
      Dragboard db = ltr.startDragAndDrop(TransferMode.ANY);

      // put a string on dragboard
      ClipboardContent content = new ClipboardContent();
      content.putString(ltr.getText().charAt(0) + "1");
      db.setContent(content);

      event.consume();
    });

    ltr.setOnDragDone(event -> {
      // the drag-and-drop gesture ended
      System.out.println("onDragDone");

      // if the data was successfully moved, clear it
      if (event.getTransferMode() == TransferMode.MOVE) {

        switch (ltr.getParent().getClass().getSimpleName()) {
          case "GridPane":
            int x = GridPane.getColumnIndex(ltr);
            int y = GridPane.getRowIndex(ltr);
            GridPane.clearConstraints(ltr);
            gridPaneUI.getChildren().remove(ltr);

            // creating an effect for all the cells
            InnerShadow is = new InnerShadow();
            is.setColor(Color.DARKGRAY);
            is.setOffsetX(-5);
            is.setOffsetY(5);
            is.setHeight(20);
            is.setWidth(20);

            // Adding the Cell to the GridPane (Update FRONT)
            Rectangle rect = initCell(x, y, is);
            GridPane.setConstraints(rect, x, y);
            gridPaneUI.getChildren().add(rect);

            // Making the Cell free (Update BACK)
            gridData.getCell(x, y).freeCell();
            gridData.display();

            break;

          case "Pane":
            for (int i = 0; i < lettersNumber; i++) {
              if (letterSlotsUI[i].getChildren().contains(ltr)) {
                // Removing the letter from FRONT
                letterSlotsUI[i].getChildren().remove(ltr);
                gridData.display();

                // Removing the letter from BACK
                ltrBar.getSlot(i).freeCell();
                ltrBar.display();

                break;
              }
            }
            break;
        }
      }

      event.consume();
    });

    return ltr;
  }

  /**
   * Creates and Initiates currently proposed letters.
   */
  private void initLetters() {

    // creating an effect for the letter
    DropShadow ds = new DropShadow();
    ds.setHeight(20);
    ds.setWidth(20);
    ds.setOffsetY(-3);
    ds.setOffsetX(3);
    ds.setColor(Color.GRAY);

    // This alphabet will be used to pick letters from
    Random rand = new Random();
    String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    for (int i = 0; i < lettersNumber; i++) {
      /* Using Pane here because it doesn't layout the objects inside of it,
       * and to be able to set explicitly the layout of its children,
       * e.g. to use setLayout(x,y) method of its children (needed for Drag&Drop feature).
       * Other containers like HBox position their children automatically, and thus
       * changing the layoutX and layoutY of their children doesn't actually move them.
       * */
      Pane pane = new Pane();
      pane.setPrefSize(cellSize, cellSize);
      lettersBlock.getChildren().add(pane);
      pane.getStyleClass().add("letter-slot");

      pane.setOnDragOver(event -> {
        // data is dragged over the target
        System.out.println("onDragOver");

        /* accept it only if it is not dragged from the same node
         * and if it has a string data */
        if (event.getGestureSource() != pane &&
            event.getDragboard().hasString()) {
          // allow for both copying and moving, whatever user chooses
          event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }

        event.consume();
      });

      pane.setOnDragEntered(event -> {
        // the drag-and-drop gesture entered the target
        System.out.println("onDragEntered");

        // show to the user that it is an actual gesture target
        if (event.getGestureSource() != pane &&
            event.getDragboard().hasString()) {
          pane.getStyleClass().add("letter-slot-drag-entered");
        }

        event.consume();
      });

      pane.setOnDragExited(event -> {
        // mouse moved away, remove the graphical cues
        pane.getStyleClass().remove("letter-slot-drag-entered");

        event.consume();
      });

      pane.setOnDragDropped(event -> {
        // data dropped
        System.out.println("onDragDropped");

        // if there is a string data on dragboard, read it and use it
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {

          char letter = db.getString().charAt(0);
          int points = Character.getNumericValue(db.getString().charAt(1));

          Button ltr = initLetter(letter, points, ds);

          // Adding the Letter (FRONT)
          pane.getChildren().add(ltr);

          // Getting an index of letter slot for BACK
          int index = lettersBlock.getChildren().indexOf(pane);

          // Adding it to the LetterBar (BACK)
          ltrBar.getSlot(index).setCell(letter, points);

          success = true;
        }
        /* let the source know whether the string was successfully
         * transferred and used */
        event.setDropCompleted(success);

        event.consume();
      });

      // Picking random letter from the alphabet declared above
      char randLetter = alphabet.charAt(rand.nextInt(alphabet.length()));
      int ltrPoints = 1;

      // Creating new letter
      Button ltr = initLetter(randLetter, ltrPoints, ds);

      // Adding the created letter to its slot (FRONT)
      letterSlotsUI[i] = pane;
      letterSlotsUI[i].getChildren().add(ltr);

      // Adding it to the LetterBar (BACK)
      ltrBar.getSlot(i).setCell(randLetter, ltrPoints);
    }
    // Verifying the internal state
    ltrBar.display();
  }

  /**
   * Shuffles letters in the Letter Bar and updates both Back and Front parts.
   */
  private void shuffleLetters() {
    // creating an effect for the letter
    DropShadow ds = new DropShadow();
    ds.setHeight(20);
    ds.setWidth(20);
    ds.setOffsetY(-3);
    ds.setOffsetX(3);
    ds.setColor(Color.GRAY);

    ltrBar.shuffle();
    ltrBar.display();

    for (int i = 0; i < lettersNumber; i++) {
      if (!letterSlotsUI[i].getChildren().isEmpty()) {
        letterSlotsUI[i].getChildren().remove(0);
      }

      if (!ltrBar.getSlot(i).isFree()) {
        Button l = initLetter(ltrBar.getSlot(i).getLtr(), ltrBar.getSlot(i).getPts(), ds);
        letterSlotsUI[i].getChildren().add(l);
      }
    }
  }

  /**
   * The FXML loader will call the initialize() method after the loading of the FXML document is
   * complete. Initializes both grid cells and proposed letters.
   */
  @FXML
  private void initialize() {

    // setting the padding of the entire grid
    // and margins for each cell
    gridPaneUI.setPadding(new Insets(gridPanePadSize));
    gridPaneUI.setHgap(gridPanePadSize);
    gridPaneUI.setVgap(gridPanePadSize);

    // Init all the cells
    initCells();

    // Init currently proposed letters
    initLetters();

    shuffleBtn.setOnMouseClicked(event -> {
      shuffleLetters();
    });

  }
}
