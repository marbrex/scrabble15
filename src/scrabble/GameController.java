package scrabble;

import com.google.common.collect.Multiset;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import org.json.JSONArray;
import org.json.JSONObject;
import scrabble.game.Grid;
import scrabble.game.LeaderBoard;
import scrabble.game.LetterBag;
import scrabble.game.LetterBag.Tile;
import scrabble.game.LetterBar;
import scrabble.game.LetterTile;
import scrabble.game.Slot;
import scrabble.game.Word;
import scrabble.model.Dictionary;
import scrabble.model.HumanPlayer;
import scrabble.model.Player;
import scrabble.model.Profile;
import scrabble.network.LobbyClientProtocol;
import scrabble.network.LobbyHostProtocol;
import scrabble.network.NetworkGame;
import scrabble.network.NetworkScreen;

/**
 * The Main Game Controller linked with "interface.fxml" file. It manages every element in the game
 * field.
 *
 * @author ekasmamy
 */
public class GameController {

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
   * Protocol for Network communication during a Network Game.
   */
  private NetworkScreen protocol;

  /**
   * Boolean variable to control Host specific actions in a network game.
   */
  private boolean isHost;

  public ArrayList<Player> players;

  public int nbPlayers;

  public LetterBag bag;

  public NetworkGame api;

  @FXML
  public Label timerLabel;

  public Timer timer;

  public final int roundTime = 2;

  @FXML
  public VBox playersBlock;

  String mapContent;

  @FXML
  public Label roundLabel;

  public boolean letterDrag = false;

  public int minViewOrder = 0;

  String dictContent;

  public BorderPane chatBlock;

  public JFXTextArea chat;

  GameController thisController = this;

  ArrayList<Label> scoreLabels = new ArrayList<>();

  int playerID;

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
   * Constructor for Network games.
   *
   * @param protocol   protocol for server communication.
   * @param isHost     variable for host detection.
   * @param mapContent content of a specific field multiplier file.
   * @param players    list of the game members.
   * @param dictionary dictionary string chosen by host.
   * @param ownID      ID of this player.
   * @author hendiehl
   */
  public GameController(NetworkScreen protocol, boolean isHost, String mapContent,
      ArrayList<Player> players, String dictionary, int ownID) {
    System.out.println("GAME CONTROLER : Own id : " + ownID);
    this.players = players;
    this.roundCounter = 0;
    this.protocol = protocol;
    this.isHost = isHost;
    this.mapContent = mapContent;
    this.dictContent = dictionary;
    this.playerID = ownID;

    players.forEach(player -> {
      System.out.println("\n" + player.getName() + " HAS ID : " + player.getId());
      System.out.println(player.getName() + " HAS SCORE : " + player.getScore());
    });
  }

  /**
   * Method to shutdown the network protocol.
   *
   * @author hendiehl
   */
  public void shutdown() {
    System.out.println("GAME CONTROLLER : Shutdown initialized");
    if (this.protocol instanceof LobbyClientProtocol) {
      ((LobbyClientProtocol) this.protocol).shutdownProtocol(true);
    } else if (this.protocol instanceof LobbyHostProtocol) {
      ((LobbyHostProtocol) this.protocol).shutdown();

    }
  }

  /**
   * Creates essential methods for the move actions' updates between players.
   *
   * @author ekasmamy
   */
  private void initApi() {
    this.api = new NetworkGame() {

      /**
       * This function will be executed when the server gives the turn permission to the player.
       *
       * @author ekasmamy
       */
      @Override
      public void startMove(int turn, int id) {
        System.out.println("GAME CONTROLLER : startMove called");
        Platform.runLater(() -> {

          setRound();
          if (protocol != null) {
            setPlayerActive(id);
          }

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
              protocol.grabRandomTiles(freeSlotsCount);
            }
          }

          // enabling every LetterTile in Bar
          letterBar.getTilesInBar().forEach(tile -> {
            tile.setMouseTransparent(false);
            tile.isFrozen = false;
          });

          // enabling every action button
          okBtn.setMouseTransparent(false);
          shuffleBtn.setMouseTransparent(false);

          // starting the timer (10 minutes for each turn)
          timer = new Timer(true);

          TimerTask endMove = new TimerTask() {
            @Override
            public void run() {
              Platform.runLater(() -> {

//                System.out.println("Time is over - moving tiles in grid back to bar..");
                // moving all tiles in grid back to bar
                letterBar.putTilesBackToBar();

                // disabling every LetterTile in bar
                letterBar.getTilesInBar().forEach(tile -> {
                  tile.setMouseTransparent(true);
                  tile.isFrozen = true;
                });

              });

              // ending the current player's move after the end of the timer
              endMove();

            }
          };

          timer.schedule(endMove, 1000 * 60 * roundTime);

          // Every second the Label will decrement
          timerLabel.setText(roundTime + ":00");

          TimerTask updateLabel = new TimerTask() {
            @Override
            public void run() {
              Platform.runLater(() -> {

                String[] minSec = timerLabel.getText().split(":");
                int min = Integer.parseInt(minSec[0]);
                int sec = Integer.parseInt(minSec[1]);

                if (min != 0 || sec != 0) {
                  if (sec - 1 < 0) {
                    min--;
                    sec = 59;
                  } else {
                    sec--;
                  }
                }

                timerLabel
                    .setText((min < 10 ? "0" + min : min) + ":" + (sec < 10 ? "0" + sec : sec));

                if (min >= 3) {
                  timerLabel.setTextFill(Paint.valueOf("green"));
                } else if (min >= 1) {
                  timerLabel.setTextFill(Paint.valueOf("orange"));
                } else if (min >= 0) {
                  timerLabel.setTextFill(Paint.valueOf("red"));
                }

              });
            }
          };

          timer.scheduleAtFixedRate(updateLabel, 0, 1000);

        });
      }

      /**
       * This function will be executed when the player's turn is over.
       * By time over or by manual turn ending.
       *
       * @author ekasmamy
       */
      @Override
      public void endMove() {
        System.out.println("GAME CONTROLLER : Prepare to end move");
        Platform.runLater(() -> {

          // verifying the player's input
          boolean validInput = grid.verifyWordsValidity();

          if (validInput) {

            // disabling every LetterTile in bar
            letterBar.getTilesInBar().forEach(tile -> {
              tile.setMouseTransparent(true);
              tile.isFrozen = true;
            });

            // disabling every action button
            okBtn.setMouseTransparent(true);
            shuffleBtn.setMouseTransparent(true);

            timer.cancel();

            if (protocol != null) {
              // Network Game

              ArrayList<Word> wordsInGrid = grid.words;
              int score = 0;
              StringBuilder action = new StringBuilder("{\n"
                  + " \"nb\": \"" + wordsInGrid.size() + "\",\n"
                  + " \"words\": [\n");

              for (int j = 0; j < wordsInGrid.size(); j++) {
                Word word = wordsInGrid.get(j);
                if (word.newlyPlaced) {
                  String spelling = word.getWordAsString();
                  action.append("  {\n")
                      .append("   \"word\": \"").append(spelling).append("\",\n")
                      .append("   \"points\": \"").append(word.getPoints()).append("\",\n")
                      .append("   \"tiles\": [\n");

                  for (int i = 0; i < word.getWordLength(); i++) {
                    LetterTile letterTile = word.getLetter(i);
                    if (letterTile.isNewlyPlaced) {
                      char letter = letterTile.getLetter();
                      int value = letterTile.getPoints();
                      int row = grid.getCellRow(letterTile);
                      int col = grid.getCellColumn(letterTile);
                      boolean isBlank = letterTile.isBlank;

                      action.append("    {\n")
                          .append("     \"letter\": \"").append(letter).append("\",\n")
                          .append("     \"value\": \"").append(value).append("\",\n")
                          .append("     \"row\": \"").append(row).append("\",\n")
                          .append("     \"col\": \"").append(col).append("\",\n")
                          .append("     \"isBlank\": \"").append(isBlank).append("\"\n")
                          .append("    }");

                      if (i == word.getWordLength() - 1) {
                        action.append("\n");
                      } else {
                        action.append(",\n");
                      }

                      letterTile.isNewlyPlaced = false;
                    }
                  }

                  action.append("   ]");

                  if (j == wordsInGrid.size() - 1) {
                    action.append("\n" + "  }\n");
                  } else {
                    action.append(",\n");
                  }

                  word.newlyPlaced = false;

                  score += word.getPoints();
                }
              }

              action.append(" ],\n")
                  .append(" \"score\": \"").append(score).append("\"\n")
                  .append("}");

              System.out.println(action);
              addToScoreOfPlayer(playerID, score);

              protocol.sendEndMessage(action.toString(), score);
            }

          }

          // TODO: update the LeaderBoard

        });
      }

      /**
       * This function will be executed after a player has ended his turn.
       * By time over or by manual turn ending.
       *
       * @author ekasmamy
       */
      @Override
      public void getOpponentsInfo(String action, int points, int id) {
        Platform.runLater(() -> {
          // here the actions of a other player will be received
          System.out.println("GAME CONTROLLER : Other action received in controller");

          addToScoreOfPlayer(id, points);
          ArrayList<LetterTile> ltrTiles = new ArrayList<>();

          JSONObject data = new JSONObject(action);
          JSONArray words = data.getJSONArray("words");
          for (int i = 0; i < words.length(); i++) {
            JSONObject word = words.getJSONObject(i);
            JSONArray tiles = word.getJSONArray("tiles");

            for (int j = 0; j < tiles.length(); j++) {
              JSONObject tile = tiles.getJSONObject(j);

              int col = tile.getInt("col");
              int row = tile.getInt("row");
              char letter = tile.getString("letter").charAt(0);
              int value = tile.getInt("value");
              boolean isBlank = tile.getBoolean("isBlank");

              Slot slot = grid.getSlot(col, row);

              System.out.println("Slot is: " + slot);
              System.out.println(" is free? " + slot.isFree());
              System.out.println(" is frozen? " + slot.isFrozen);
              if (slot.isFree()) {
                LetterTile ltrTile = new LetterTile(letter, value, grid.cellSize, thisController);
                ltrTile.setMouseTransparent(true);
                ltrTile.isFrozen = true;
                ltrTile.container.getStyleClass().add("letter-btn-frozen");
                ltrTile.isNewlyPlaced = false;
                ltrTile.isBlank = isBlank;
                if (isBlank) {
                  ltrTile.setPointsVisible(false);
                  ltrTile.setLetterVisible(true);
                }

                slot.isFrozen = true;
                slot.setContent(ltrTile);
                slot.removeEffect();

                ltrTiles.add(ltrTile);
              } else {
                ltrTiles.add(slot.content);
              }
            }

            System.out.println("First: " + ltrTiles.get(0));
            System.out.println("Last: " + ltrTiles.get(ltrTiles.size() - 1));

            Word w = new Word(ltrTiles.get(0), ltrTiles.get(ltrTiles.size() - 1), thisController);
            w.newlyPlaced = false;
            w.frozen = true;
            w.setMouseTransparent(true);
            gridWrapper.getChildren().remove(w.container);
          }

          System.out.println("grid.words: ");
          grid.words.forEach(word -> {
            word.getWordAsString();
            System.out.print(" ");
          });
          System.out.print("\n");
        });
      }

      /**
       * Method to print an incoming chat message from the chat server to the chat field on the game
       * field.
       *
       * @param message chat string from the chat server.
       *
       * @author hendiehl
       */
      @Override
      public void printChatMessage(String message) {
        Platform.runLater(() -> {
          chat.appendText(chat.getText().isEmpty() ? message : (System.lineSeparator() + message));
        });
      }

      /**
       * Method to send an chat message to the chat server.
       *
       * @param message chat message of an lobby member.
       *
       * @author hendiehl
       */
      @Override
      public void sendChatMessage(String message) {
        protocol.sendChatMessage(message); // sending the message
      }

    };
  }

  /**
   * Initializes the Grid with a custom Multiplier Map.
   *
   * @param mapPath Path to the custom multiplier map.
   * @author ekasmamy
   */
  public void initGrid(String mapPath) {
    grid = new Grid(gridPaneUI, mapPath, 15, this);
    grid.initCells();
  }

  /**
   * Default initializer for the Grid.
   *
   * @author ekasmamy
   */
  public void initGrid() {
    grid = new Grid(gridPaneUI, 15, this);
    grid.initCells();
  }

  /**
   * Initializes the Dictionary with a custom dictionary. The custom dictionary's content should
   * follow the defined format. You can find a sample in "resources/dictionaries/dictionary-format.txt".
   *
   * @param dictContent Actual content of the custom dictionary.
   * @author ekasmamy
   */
  public void initDictionary(String dictContent) {
    // Setting the Dictionary (should be set only once, an error otherwise)
    InputStream in = new ByteArrayInputStream(dictContent.getBytes());
    Dictionary.setDictionary(in);
  }

  /**
   * Default initializer for the Dictionary.
   *
   * @author ekasmamy
   */
  public void initDictionary() {
    // Setting the Dictionary (should be set only once, an error otherwise)
    InputStream in = getClass().getResourceAsStream("/dictionaries/english-default.txt");
    Dictionary.setDictionary(in);
  }

  /**
   * The function responsible for JavaFX Scene switching.
   *
   * @param resource FXML file.
   * @param style    CSS file.
   * @author ekasmamy
   * @author skeskinc
   */
  public void changeScene(String resource, String style) {
    try {
      Parent root = FXMLLoader.load(getClass().getResource(resource));
      ScrabbleApp.getScene().getStylesheets().clear();
      ScrabbleApp.getScene().getStylesheets().add(getClass().getResource(style).toExternalForm());
      ScrabbleApp.getScene().setRoot(root);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Initializes the players array for the single player mode.
   *
   * @author ekasmamy
   */
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
   * Sets the "onMouseClicked" event of buttons.
   *
   * @author ekasmamy
   */
  protected void setButtonActions() {

    shuffleBtn.setOnMouseClicked(event -> letterBar.shuffle());

    quitGame.setOnMouseClicked(event -> {
      changeScene("/fxml/MainPage.fxml", "/css/mainMenu.css");
    });

    okBtn.setOnMouseClicked(event -> {
      api.endMove();
    });

  }

  /**
   * Saves the custom multiplier map in the "{user-dir}/.Scrabble" directory.
   *
   * @param content Content of the multiplier map to save.
   * @return Returns the path to the created map file.
   * @author ekasmamy
   */
  protected String saveMultiplierMap(String content) {
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
//      System.err.println("Error on saving the custom map into a file");
//      System.err.println("Error code: " + error.getMessage());
    }

    return path;
  }

  /**
   * Initializes the chat for multiplayer mode.
   *
   * @author ekasmamy
   */
  private void initChat() {
    chatBlock.setPadding(new Insets(10));

    JFXTextArea chat = new JFXTextArea();
    chat.setFocusColor(Paint.valueOf("transparent"));
    chat.setUnFocusColor(Paint.valueOf("transparent"));
    chat.setEditable(false);
    chat.setId("chat");
    chat.setPrefColumnCount(15);
    this.chat = chat;
    chatBlock.setCenter(chat);

    HBox chatActions = new HBox();
    chatActions.setAlignment(Pos.CENTER);
    chatBlock.setBottom(chatActions);

    JFXTextField chatField = new JFXTextField();
    chatField.setId("chat-input");
    chatField.setFocusColor(Paint.valueOf("transparent"));
    chatField.setUnFocusColor(Paint.valueOf("transparent"));
    chatField.setFocusTraversable(false);
    chatActions.getChildren().add(chatField);

    String path = "/img/send-msg-icon.png";
    Image im = new Image(getClass().getResource(path).toExternalForm());
    ImageView chatSend = new ImageView(im);
    chatSend.setId("chat-send");
    chatSend.setFitHeight(30);
    chatSend.setFitWidth(30);
    chatActions.getChildren().add(chatSend);

    chatSend.setOnMouseClicked(mouseEvent -> {
      // On Clicking send button
      String message = chatField.getText();
      if (!message.isEmpty()) {
        this.api.sendChatMessage(message);
        chatField.clear();
      }
    });
  }

  /**
   * The FXML loader will call the initialize() method after the loading of the FXML document is
   * complete. Initializes both grid cells and proposed letters.
   *
   * @author ekasmamy
   */
  @FXML
  private void initialize() {

    if (protocol == null) {
      // Local Game

      initDictionary();

      initPlayers();

      initGrid();

    } else {
      // Network Game

      initChat();

      this.protocol.setGameScreen(this); //setting controller to protocol

      if (dictContent.isEmpty()) {
        // if dictionary content transferred by the lobby is empty => use default dictionary
        initDictionary();
      } else {
        // use the dictionary transferred by the lobby
        initDictionary(dictContent);
      }

      if (mapContent.isEmpty()) {
        // if map content transferred by the lobby is empty => use default map
        initGrid();
      } else {
        // use the map transferred by the lobby
        String pathToMap = saveMultiplierMap(mapContent);
        initGrid(pathToMap);
      }

      // Creating a Leader Board
      leaderBoard = new LeaderBoard(players);

      players.forEach(player -> {

        BorderPane playerBlock = new BorderPane();
        playerBlock.getStyleClass().add("players-block");
        playerBlock.setPadding(new Insets(10, 30, 10, 30));
        System.out.println(player.getName() + " has ID: " + player.getId());
        playerBlock.setId("player-block-" + String.valueOf(player.getId()));

        StackPane avatarWrapper = new StackPane();
        avatarWrapper.getStyleClass().add("player-avatar-frame");
        avatarWrapper.setAlignment(Pos.CENTER);

        ImageView avatar = new ImageView(
            new Image(getClass().getResourceAsStream("/img/" + player.getImage())));
        avatar.setFitHeight(60);
        avatar.setFitWidth(60);

        Label nickname = new Label(player.getName());
        nickname.getStyleClass().add("players-name");
        Label score = new Label(String.valueOf(player.getScore()));
        score.getStyleClass().add("players-score");
        System.out.println("ID transmitted: " + player.getId());
        System.out.println("ID profile: " + playerID);

        avatarWrapper.getChildren().add(avatar);
        playerBlock.setLeft(avatarWrapper);
        playerBlock.setCenter(nickname);
        playerBlock.setRight(score);
        BorderPane.setAlignment(avatarWrapper, Pos.CENTER);
        BorderPane.setAlignment(nickname, Pos.CENTER);
        BorderPane.setAlignment(score, Pos.CENTER);

        playersBlock.getChildren().add(playerBlock);
      });

    }

    letterBar = new LetterBar(this);

    initApi();
    if (this.protocol != null) { //Have to be checked again
      this.protocol.loadFinished();
    }
    System.out.println("GAME CONTROLLER : Finished loading");

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
      api.startMove(0, 0);
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
   * Provider method of the grabRandomTile method of LetterBag.
   *
   * @param tile Grabbed random tile.
   */
  public void grabRandomTileAnswer(Tile tile) {
    // do something with Tile
  }

  /**
   * Provider method of the getValueOf method of LetterBag.
   *
   * @param value Value returned.
   */
  public void getValueOfAnswer(int value) {
    // do something with value
  }

  /**
   * Provider method of the getRemainingVowels method of LetterBag.
   *
   * @param tiles Multiset of tiles.
   */
  public void getRemainingVowelsAnswer(Multiset<Tile> tiles) {
    // do something with Multiset
  }

  /**
   * Provider method of the getRemainingConsonants method of LetterBag.
   *
   * @param tiles Multiset of tiles.
   */
  public void getRemainingConsonantsAnswer(Multiset<Tile> tiles) {
    // do something with Multiset
  }

  /**
   * Provider method of the getRemainingBlanks method of LetterBag.
   *
   * @param tiles Multiset of tiles.
   */
  public void getRemainingBlanksAnswer(Multiset<Tile> tiles) {
    // do something with Multiset
  }

  /**
   * Provider method of the grabRandomTiles method of LetterBag.
   *
   * @param tiles Multiset of tiles.
   */
  public void grabRandomTilesAnswer(Multiset<Tile> tiles) {
    System.out.println("GAME CONTROLLER : grabRandomTilesAnswer received");
    Platform.runLater(() -> {
      // updating the LetterBar
      letterBar.fillGaps(tiles);
      letterBar.display();
    });
  }

  /**
   * Provider method of the getRemainingTiles method of LetterBag.
   *
   * @param tiles Multiset of tiles.
   */
  public void getRemainingTilesAnswer(Multiset<Tile> tiles) {
    // do something with Multiset
  }

  /**
   * Provider method of the getAmount method of LetterBag.
   *
   * @param amount Amount of remaining tiles in bag.
   */
  public void getAmountAnswer(int amount) {
    // do something with amount
  }

  /**
   * Draws a green circle around the current player's avatar.
   *
   * @param id Player's ID.
   * @author ekasmamy
   */
  public void setPlayerActive(int id) {
    System.out.println("ID: " + id);
    playersBlock.getChildren().forEach(block -> {
      System.out.println("Current player block ID: " + block.getId());
      BorderPane playerBlock = (BorderPane) block;
      playerBlock.getLeft().getStyleClass().remove("player-avatar-frame-active");

      if (getIntID(block.getId()) == id) {
        System.out.println("Found! ID: " + block.getId());
        playerBlock.getLeft().getStyleClass().add("player-avatar-frame-active");
      }
    });
  }

  public int getIntID(String id) {
    String[] split = id.split("-");
    return Integer.parseInt(split[split.length-1]);
  }

  /**
   * Changes the round label.
   *
   * @author ekasmamy
   */
  public void setRound() {
    roundLabel.setText(String.valueOf(++roundCounter));
  }

  /**
   * Changes the score and the label of this player.
   *
   * @param points Points to add to the current score.
   *
   * @author ekasmamy
   */
  public void addToScoreOfPlayer(int playerId, int points) {
    playersBlock.getChildren().forEach(block -> {
      System.out.println("Current player block ID: " + block.getId());
      BorderPane playerBlock = (BorderPane) block;
      Label scoreLabel = (Label) playerBlock.getRight();

      if (getIntID(block.getId()) == playerId) {
        System.out.println("Found! ID: " + block.getId());
        int currentScore = Integer.parseInt(scoreLabel.getText());
        scoreLabel.setText(String.valueOf(currentScore + points));
      }
    });
  }

  /**
   * This function is called when any other player is on move.
   *
   * @param turn Current round.
   * @param id   ID of the player that is on move.
   * @author ekasmamy
   */
  public void otherPlayerOnMove(int turn, int id) {
    System.out
        .println("GAME CONTROLLER : Other on move ! Turn : " + turn + " with player id : " + id);

    Platform.runLater(() -> {

      setRound();
      setPlayerActive(id);

    });
  }
}
