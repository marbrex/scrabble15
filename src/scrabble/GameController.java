package scrabble;

import com.jfoenix.controls.JFXButton;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import scrabble.game.Grid;
import scrabble.game.LeaderBoard;
import scrabble.game.LetterBar;
import scrabble.game.Word;
import scrabble.model.Dictionary;
import scrabble.model.HumanPlayer;
import scrabble.model.Player;

/**
 * <h1>The Main Game Controller linked with "interface.fxml" file.</h1>
 *
 * <h2>Main functions:</h2>
 * <ul>
 * <li>Initialize cells (Rectangle) of the 15x15 grid (GridPane).</li>
 * <li>Initialize proposed letters.</li>
 * </ul>
 *
 * @author Eldar Kasmamytov
 */
public class GameController {

  /* Some reminders:
   * - @FXML annotation on a member declares that the FXML loader can
   * access the member even if it is private.
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

  @FXML
  public HBox mainBlock;

  @FXML
  public BorderPane sideBar;

  @FXML
  public ImageView quitGame;

  /**
   * The actual data of the letter grid will be stocked here.
   */
  public Grid grid;

  public LetterBar letterBar;

  public LeaderBoard leaderBoard;

  /**
   * Default constructor.
   */
  public GameController() {
  }

  public void initGrid(String mapPath) {
    grid = new Grid(gridPaneUI, mapPath, 15, this);
    grid.initCells();
  }

  public void initGrid() {
    grid = new Grid(gridPaneUI, 15, this);
    grid.initCells();
  }

  public void initDictionary(String dictPath) {
    // Setting the Dictionary (should be set only once, an error otherwise)
    InputStream in = getClass().getResourceAsStream(dictPath);
    Dictionary.setDictionary(in);
  }

  public void initDictionary() {
    // Setting the Dictionary (should be set only once, an error otherwise)
    InputStream in = getClass().getResourceAsStream("/scrabble/dictionaries/english-default.txt");
    Dictionary.setDictionary(in);
  }

  public void changeScene(String resource, String style, Event event) {
    try {
      System.out.println(resource);
      Parent root = FXMLLoader.load(getClass().getResource(resource));
      ImageView btn = ((ImageView) event.getSource());
      Stage stage = (Stage) btn.getScene().getWindow();
      Scene scene = new Scene(root, this.root.getScene().getWidth(),
          this.root.getScene().getHeight());
      scene.getStylesheets().add(getClass().getResource(style).toExternalForm());
      stage.setScene(scene);
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Error: " + e.getMessage());
    }
  }

  public void initPlayers(List<Player> players) {
    leaderBoard = new LeaderBoard(players);
  }

  /**
   * The FXML loader will call the initialize() method after the loading of the FXML document is
   * complete. Initializes both grid cells and proposed letters.
   */
  @FXML
  private void initialize() {

    initDictionary();

    initGrid();

    letterBar = new LetterBar(this);

    shuffleBtn.setOnMouseClicked(event -> {
      letterBar.shuffle();
    });

    // Binding GridPane Wrapper's Height to be always equal to its Width
    gridWrapper.widthProperty().addListener((observable, oldValue, newValue) -> {
      gridWrapper.setMaxHeight(newValue.doubleValue());
    });

    // Binding GridPane Wrapper's Width to be always equal to its Parent Node's Height
    mainBlock.heightProperty().addListener((observable, oldValue, newValue) -> {
      gridWrapper.setMaxWidth(newValue.doubleValue());
    });

//    gridPaneUI.heightProperty().addListener((observable, oldValue, newValue) -> {
//      sideBar.setMaxHeight(newValue.doubleValue());
//    });

    sideBar.maxHeightProperty().bind(mainBlock.heightProperty());

    quitGame.setOnMouseClicked(event -> {
      changeScene("fxml/MainPage.fxml", "css/mainMenu.css", event);
    });

  }
}
