package scrabble;

import com.jfoenix.controls.JFXButton;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import javafx.fxml.FXML;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import scrabble.game.Grid;
import scrabble.game.LetterBar;
import scrabble.game.Word;
import scrabble.model.Dictionary;

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
public class GameController {

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
  public HBox lettersBlock;

  /**
   * Grid of letters.
   */
  @FXML
  public GridPane gridPaneUI;

  /**
   * OK button, which ends a player's turn.
   */
  @FXML
  public JFXButton okBtn;

  /**
   * Shuffle button, which shuffles the LetterBar.
   */
  @FXML
  public JFXButton shuffleBtn;

  /**
   * Pane which stores time (located at the top of Side Panel)
   */
  @FXML
  public Pane timePane;

  /**
   * The root element in FXML.
   */
  @FXML
  public BorderPane root;

  @FXML
  public StackPane gridWrapper;

  /**
   * The actual data of the letter grid will be stocked here.
   */
  public Grid grid;

  public LetterBar letterBar;

  public ArrayList<Word> wordsInGrid;

  /**
   * Default constructor.
   */
  public GameController() {
    wordsInGrid = new ArrayList<Word>();
  }

  /**
   * The FXML loader will call the initialize() method after the loading of the FXML document is
   * complete. Initializes both grid cells and proposed letters.
   */
  @FXML
  private void initialize() {

    // Setting the Dictionary (should be set only once, an error otherwise)
    URL dictPath = getClass().getResource("dictionaries/english-default.txt");
    File dict = new File(dictPath.getFile());
    Dictionary.setDictionary(dict);

    grid = new Grid(gridPaneUI, 15, this);
    grid.initCells();

    letterBar = new LetterBar(this);

    shuffleBtn.setOnMouseClicked(event -> {
      letterBar.shuffle();
    });

  }
}
