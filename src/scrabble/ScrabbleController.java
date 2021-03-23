/**
 * The Main Controller linked with "interface.fxml" file.
 *
 * Main functions:
 * <ul>
 * <li>Initialize cells (Rectangle) of the 15x15 grid (GridPane).
 * <li>Initialize proposed letters.
 * </ul>
 *
 * @author      Eldar Kasmamytov
 */

package scrabble;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import scrabble.model.Grid;

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
  private Pane lettersBlock;

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

  private Grid gridData;

  final private int gridPaneSize;
  final private int cellNumber;
  final private int gridPanePadSize;
  final private int cellSize;

  final private int lettersNumber;

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
  }

  /**
   * The FXML loader will call the initialize() method
   * after the loading of the FXML document is complete.
   * Initializes both grid cells and proposed letters.
   */
  @FXML
  private void initialize() {

    gridPaneUI.setPadding(new Insets(gridPanePadSize));
    gridPaneUI.setHgap(gridPanePadSize);
    gridPaneUI.setVgap(gridPanePadSize);

    for (int i = 0; i < cellNumber; i++) {
      for (int j = 0; j < cellNumber; j++) {
        Rectangle rect = new Rectangle(i, j, cellSize, cellSize);
        rect.setFill(Color.AQUA);
        rect.setStroke(Color.DARKGRAY);
        rect.setArcWidth(10);
        rect.setArcHeight(10);

        InnerShadow is = new InnerShadow();
        is.setColor(Color.DARKGRAY);
        is.setOffsetX(-5);
        is.setOffsetY(5);
        is.setHeight(20);
        is.setWidth(20);

        rect.setEffect(is);

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
            int x = GridPane.getColumnIndex(rect);
            int y = GridPane.getRowIndex(rect);
            GridPane.clearConstraints(rect);
            gridPaneUI.getChildren().remove(rect);

            char letter = db.getString().charAt(0);
            int points = (int) db.getString().charAt(1);

            String s = String.valueOf(letter);
            Button l = new Button(s);
            l.getStyleClass().add("letter-btn");
            l.setPrefSize(cellSize, cellSize);

            DropShadow ds = new DropShadow();
            ds.setHeight(20);
            ds.setWidth(20);
            ds.setOffsetY(-3);
            ds.setOffsetX(3);
            ds.setColor(Color.GRAY);

            l.setEffect(ds);

            GridPane.setConstraints(l, x, y);
            gridPaneUI.getChildren().add(l);

            success = true;
          }
          /* let the source know whether the string was successfully
           * transferred and used */
          event.setDropCompleted(success);

          event.consume();
        });

        gridPaneUI.add(rect, i, j);
      }
    }

    for (int i = 0; i < lettersNumber; i++) {
      /* Using Pane here because it doesn't layout the objects inside of it,
       * and to be able to set explicitly the layout of its children,
       * e.g. to use setLayout(x,y) method of its children (needed for Drag&Drop feature).
       * Other containers like HBox position their children automatically, and thus
       * changing the layoutX and layoutY of their children doesn't actually move them.
       * */

      Button ltr = new Button("S");
      ltr.getStyleClass().add("letter-btn");
      ltr.setPrefSize(cellSize, cellSize);

      DropShadow ds = new DropShadow();
      ds.setHeight(20);
      ds.setWidth(20);
      ds.setOffsetY(-3);
      ds.setOffsetX(3);
      ds.setColor(Color.GRAY);

      ltr.setEffect(ds);

      ltr.setOnMousePressed(event -> {
        System.out
            .println("Event.getScene: (" + event.getSceneX() + ", " + event.getSceneY() + ")");
        System.out.println("Event.get: (" + event.getX() + ", " + event.getY() + ")\n");
      });

      ltr.setOnMouseDragged(mouseEvent -> {
        ltr.toFront();
        ltr.setCursor(Cursor.CLOSED_HAND);
        ltr.setStyle("-fx-opacity: 0.6");

        ltr.setLayoutX(mouseEvent.getX() + ltr.getLayoutX() - ltr.getWidth() / 2);
        ltr.setLayoutY(mouseEvent.getY() + ltr.getLayoutY() - ltr.getHeight() / 2);
      });

      ltr.setOnMouseReleased(mouseEvent -> {
        ltr.setCursor(Cursor.DEFAULT);
        ltr.setStyle("-fx-opacity: 1");
      });

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

        Thread newThread = new Thread(() -> {
          ltr.setOnMouseMoved(mouseEvent -> {
            ltr.toFront();
            ltr.setCursor(Cursor.CLOSED_HAND);
            ltr.setStyle("-fx-opacity: 0.6");

            ltr.setLayoutX(mouseEvent.getX() + ltr.getLayoutX() - ltr.getWidth() / 2);
            ltr.setLayoutY(mouseEvent.getY() + ltr.getLayoutY() - ltr.getHeight() / 2);
          });
        });
        newThread.start();
      });

      ltr.setOnDragDone(event -> {
        // the drag-and-drop gesture ended
        System.out.println("onDragDone");

        // if the data was successfully moved, clear it
        if (event.getTransferMode() == TransferMode.MOVE) {
          lettersBlock.getChildren().remove(ltr);
        }

        event.consume();
      });

      lettersBlock.getChildren().add(ltr);
    }
  }
}
