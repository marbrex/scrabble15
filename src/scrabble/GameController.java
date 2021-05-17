package scrabble;

import com.google.common.collect.Multiset;
import com.jfoenix.controls.JFXButton;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import scrabble.game.Grid;
import scrabble.game.LeaderBoard;
import scrabble.game.LetterBag;
import scrabble.game.LetterBar;
import scrabble.game.LetterBag.Tile;
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

  @FXML
  public Label timerLabel;

  public Timer timer;

  public final int roundTime = 1;

  @FXML
  public VBox playersBlock;

  String mapContent;

  @FXML
  public Label roundLabel;

  /**
   * Default constructor.
   */
  public GameController() {
    roundCounter = 1;
    bag = LetterBag.getInstance();
  }

  public GameController(LetterBag bag) {
    roundCounter = 1;
    this.bag = bag;
  }

  /**
   * Constructor for Network games
   *
   * @param protocol   protocol for server communication
   * @param isHost     variable for host detection
   * @param mapContent content of a specific field multiplier file
   * @param players    list of the game members
   * @author hendiehl
   */
  public GameController(NetworkScreen protocol, boolean isHost, String mapContent,
      ArrayList<Player> players) {
    this.players = players;
    this.roundCounter = 1;
    this.protocol = protocol;
    this.isHost = isHost;
    this.mapContent = mapContent;
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

        // Filling the empty slots in the LetterBar if it's the case
        int freeSlotsCount = letterBar.getCountFreeSlots();
        if (freeSlotsCount > 0) {

          if (protocol == null) {
            // Local Game

            letterBar.fillGaps(LetterBag.getInstance().grabRandomTiles(freeSlotsCount));
            letterBar.display();
          } else {
            // Network Game

            // Sending a request to the server, which on his turn will return a response
            // that will be accessible only in GameController.grabRandomTilesAnswer method
            Platform.runLater(() -> {
              protocol.grabRandomTiles(freeSlotsCount);
            });
          }
        }

        Platform.runLater(() -> {
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
        });

        // starting the timer (10 minutes for each turn)
        timer = new Timer();
        TimerTask task = new TimerTask() {
          @Override
          public void run() {

            Platform.runLater(() -> {
              System.out.println("Time is over - moving tiles in grid back to bar..");
              // moving all tiles in grid back to bar
              letterBar.putTilesBackToBar();

              // disabling every LetterTile in bar
              letterBar.getTilesInBar().forEach(tile -> {
                tile.setMouseTransparent(true);
              });
            });

            // ending the current player's move after the end of the timer
            endMove();
          }
        };
        timer.schedule(task, 1000 * 60 * roundTime);

        // Every second the Label will decrement
        Platform.runLater(() -> timerLabel.setText(roundTime + ":00"));
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

            if (validInput) {
              // disabling every LetterTile in bar
              letterBar.getTilesInBar().forEach(tile -> {
                tile.setMouseTransparent(true);
              });

              // disabling every action button
              okBtn.setMouseTransparent(true);
              shuffleBtn.setMouseTransparent(true);

              timer.cancel();

              if (protocol != null) {
                // Network Game
                protocol.sendEndMessage();
              }
            }
          }
        });

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

  private void setButtonActions() {

    shuffleBtn.setOnMouseClicked(event -> letterBar.shuffle());

    quitGame.setOnMouseClicked(event -> {
      changeScene("fxml/MainPage.fxml", "css/mainMenu.css", event);
    });

    okBtn.setOnMouseClicked(event -> {
      api.endMove();
    });

  }

  String saveMultiplierMap(String content) {
    String home = System.getProperty("user.home");
    String slash = System.getProperty("file.separator");

    String fileName = "custom-map";
    String fileExt = ".txt";

    int counter = 1;
    while (Files.exists(
        Paths.get(home + slash + ".Scrabble" + slash + (fileName + "-" + counter) + fileExt))) {
      counter++;
    }

    String path = home + slash + ".Scrabble" + slash + (fileName + "-" + counter) + fileExt;

    Path p = Paths.get(path);

    try {
      Path pathToSave = Files.createFile(p);
      File f = pathToSave.toFile();

      BufferedWriter writer = new BufferedWriter(new FileWriter(f));
      writer.write(content);
      writer.close();
    } catch (Exception error) {
      System.err.println("Error on saving the custom map into a file");
      System.err.println("Error code: " + error.getMessage());
    }

    return path;
  }

  /**
   * The FXML loader will call the initialize() method after the loading of the FXML document is
   * complete. Initializes both grid cells and proposed letters.
   */
  @FXML
  private void initialize() {

    initDictionary();

    if (protocol == null) {
      // Local Game

      initGrid();
    } else {
      // Network Game

      if (mapContent.isEmpty()) {
        // if map content transferred by the lobby is empty => use default map
        initGrid();
      } else {
        // use the map transferred by the lobby
        String pathToMap = saveMultiplierMap(mapContent);
        initGrid(pathToMap);
      }
    }

    letterBar = new LetterBar(this);

    initApi();

    // Binding GridPane Wrapper's Height to be always equal to its Width
    gridWrapper.widthProperty().addListener((observable, oldValue, newValue) -> {
      gridWrapper.setMaxHeight(newValue.doubleValue());
    });

    // Binding GridPane Wrapper's Width to be always equal to its Parent Node's Height
    mainBlock.heightProperty().addListener((observable, oldValue, newValue) -> {
      gridWrapper.setMaxWidth(newValue.doubleValue());
    });

    sideBar.maxHeightProperty().bind(mainBlock.heightProperty());

    setButtonActions();

    if (protocol == null) {
      api.startMove();
    }

  }

  /*
   * The following methods will be automatically called from the protocol after LetterBag methods
   * will be called on the protocol for network use. They will provide the answer from the actual
   * global LetterBag instance
   *
   *
   * WARNING !!!!!!!!!!!!!!!!!!!!!!!!! If any of the following methods should change something on
   * the screen, like changing the text of a label or any other thing, the action have to be in the
   * body of a : Platform.runLater(() -> {do it}); because only controller threads can change JavaFx
   * objects, but these methods will be called by the protocol
   */

  /**
   * Provider method of the grabRandomTile method of LetterBag
   *
   * @param tile
   */
  public void grabRandomTileAnswer(Tile tile) {
    // do something with Tile
  }

  /**
   * Provider method of the getValueOf method of LetterBag
   *
   * @param value
   */
  public void getValueOfAnswer(int value) {
    // do something with value
  }

  /**
   * Provider method of the getRemainingVowels method of LetterBag
   *
   * @param tiles
   */
  public void getRemainingVowelsAnswer(Multiset<Tile> tiles) {
    // do something with Multiset
  }

  /**
   * Provider method of the getRemainingConsonants method of LetterBag
   *
   * @param tiles
   */
  public void getRemainingConsonantsAnswer(Multiset<Tile> tiles) {
    // do something with Multiset
  }

  /**
   * Provider method of the getRemainingBlanks method of LetterBag
   *
   * @param tiles
   */
  public void getRemainingBlanksAnswer(Multiset<Tile> tiles) {
    // do something with Multiset
  }

  /**
   * Provider method of the grabRandomTiles method of LetterBag
   *
   * @param tiles
   */
  public void grabRandomTilesAnswer(Multiset<Tile> tiles) {
    Platform.runLater(() -> {
      // updating the LetterBar
      letterBar.fillGaps(tiles);
      letterBar.display();
    });
  }

  /**
   * Provider method of the getRemainingTiles method of LetterBag
   *
   * @param tiles
   */
  public void getRemainingTilesAnswer(Multiset<Tile> tiles) {
    // do something with Multiset
  }

  /**
   * Provider method of the getAmount method of LetterBag
   *
   * @param amount
   */
  public void getAmountAnswer(int amount) {
    // do something with amount
  }

  //testing
  public void otherPlayerOnMove(int i) {
    System.err.println("Inform received");
    System.err.println("On move : " + this.players.get(i).getName());
  }
}
