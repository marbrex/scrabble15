package scrabble;

import com.jfoenix.controls.JFXButton;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import scrabble.game.Grid;
import scrabble.game.LeaderBoard;
import scrabble.game.LetterBag;
import scrabble.game.LetterBar;
import scrabble.model.Dictionary;
import scrabble.model.HumanPlayer;
import scrabble.model.Player;
import scrabble.network.LobbyClientProtocol;
import scrabble.network.LobbyHostProtocol;
import scrabble.network.NetworkGame;
import scrabble.network.NetworkScreen;

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

  @FXML
  public BorderPane popupBlankBlock;

  @FXML
  public FlowPane popupBlankMessage;

  /**
   * The actual data of the letter grid will be stocked here.
   */
  public Grid grid;

  public LetterBar letterBar;

  public LeaderBoard leaderBoard;

  public int roundCounter;

  /**
   * protocol for Network communication during a Network Game
   */
  private NetworkScreen protocol;

  /**
   * boolean variable to control Host specific actions in a network game
   */
  private boolean isHost;

  public ArrayList<Player> players;

  public int nbPlayers;

  public LetterBag bag;

  public NetworkGame api;

  public Label timerLabel;

  public Timer timer;

  public final int roundTime = 1;

  /**
   * Default constructor.
   */
  public GameController() {
    roundCounter = 0;
    bag = LetterBag.getInstance();
  }

  public GameController(LetterBag bag) {
    roundCounter = 0;
    this.bag = bag;
  }

  /**
   * Constructor for Network games
   *
   * @param protocol protocol for server communication
   * @param isHost   variable for host detection
   * @author hendiehl
   */
  public GameController(NetworkScreen protocol, boolean isHost) {
    this.roundCounter = 0;
    this.protocol = protocol;
    this.isHost = isHost;
  }

  /**
   * Method to shutdown the network protocol
   *
   * @author hendiehl
   */
  public void shutdown() {
    if (this.protocol instanceof LobbyClientProtocol) {
      ((LobbyClientProtocol) this.protocol).shutdownProtocol(true);
    } else if (this.protocol instanceof LobbyHostProtocol) {
      ((LobbyHostProtocol) this.protocol).shutdown();

    }
  }

  private void initApi() {

    this.api = new NetworkGame() {

      @Override
      public void startMove() {
        // TODO: first need to draw tiles from the bag if it's necessary

        // enabling every LetterTile
        grid.getTilesInGrid().forEach(tile -> {
          tile.setMouseTransparent(false);
        });
        letterBar.getTilesInBar().forEach(tile -> {
          tile.setMouseTransparent(false);
        });
        // enabling every action button
        okBtn.setMouseTransparent(false);
        shuffleBtn.setMouseTransparent(false);

        // starting the timer (10 minutes for each turn)
        timer = new Timer();
        TimerTask task = new TimerTask() {
          @Override
          public void run() {
            // ending the current player's move after the end of the timer
            endMove();
          }
        };
        timer.schedule(task, 1000 * 60 * roundTime);

        // Every second the Label will decrement
        timerLabel.setText(roundTime + ":00");
        TimerTask updateLabel = new TimerTask() {
          @Override
          public void run() {
            Platform.runLater(new Runnable() {
              @Override
              public void run() {
                String[] minSec = timerLabel.getText().split(":");
                int min = Integer.parseInt(minSec[0]);
                int sec = Integer.parseInt(minSec[1]);
                if (min == 0 && sec == 0) {
                } else {
                  if (sec - 1 < 0) {
                    min--;
                    sec = 59;
                  } else {
                    sec--;
                  }
                }
                timerLabel
                    .setText((min < 10 ? "0" + min : min) + ":" + (sec < 10 ? "0" + sec : sec));
                if (min >= 5) {
                  timerLabel.setTextFill(Paint.valueOf("green"));
                } else if (min >= 3) {
                  timerLabel.setTextFill(Paint.valueOf("orange"));
                } else if (min >= 0) {
                  timerLabel.setTextFill(Paint.valueOf("red"));
                }
              }
            });
          }
        };
        timer.scheduleAtFixedRate(updateLabel, 0, 1000);
      }

      @Override
      public void endMove() {

        // terminating the current player's turn
        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            // verifying the player's input
            boolean validInput = grid.verifyWordsValidity();

            if (!validInput) {
              // if the input is invalid

              System.out.println("Time is over - moving tiles in grid back to bar..");
              // moving all tiles in grid back to bar
              letterBar.putTilesBackToBar();

              // disabling every LetterTile in bar
              letterBar.getTilesInBar().forEach(tile -> {
                tile.setMouseTransparent(true);
              });
            }
          }
        });

        // disabling every action button
        okBtn.setMouseTransparent(true);
        shuffleBtn.setMouseTransparent(true);

        // TODO: update the LeaderBoard
        // TODO: pass the turn to the next player
      }

      @Override
      public void sendMoveInformation() {

      }

      @Override
      public void getOpponentsInfo() {

      }

      @Override
      public void endMoveActions() {

      }

      @Override
      public void printChatMessage(String message) {

      }

      @Override
      public void sendChatMessage(String message) {

      }

    };
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

  public void initPlayers() {
    players = new ArrayList<>();

    HumanPlayer host = new HumanPlayer();
    players.add(host);
    for (int i = 0; i < nbPlayers - 1; i++) {
      players.add(new HumanPlayer());
    }

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

    shuffleBtn.setOnMouseClicked(event -> letterBar.shuffle());

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

    okBtn.setOnMouseClicked(event -> grid.verifyWordsValidity());

    initPlayers();

    initApi();
    api.startMove();

  }
}
