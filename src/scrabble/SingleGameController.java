package scrabble;

import animatefx.animation.GlowText;
import animatefx.animation.Pulse;
import com.google.common.collect.Multiset;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.Console;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Pair;
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
import scrabble.model.AiPlayer;
import scrabble.model.Dictionary;
import scrabble.model.HumanPlayer;
import scrabble.model.Player;
import scrabble.model.Profile;
import scrabble.network.LobbyClientProtocol;
import scrabble.network.LobbyHostProtocol;
import scrabble.network.LobbyServer;
import scrabble.network.NetworkGame;
import scrabble.network.NetworkScreen;

/**
 * The Main Game Controller linked with "interface.fxml" file. It manages every element in the game
 * field for the single player mode.
 *
 * @author ekasmamy
 */
public class SingleGameController extends GameController {

  private int nbBots;

  private ArrayList<AiPlayer> bots;

  private int nbPlayers;

  private ArrayList<Player> players;

  private HumanPlayer player;

  private int currentPlayerId;

  private final int roundTime = 10;

  private void initData(int nbBots) {
    this.nbBots = nbBots;
    this.nbPlayers = nbBots + 1;

    this.player = Profile.getPlayer();
    this.player.setId(0);

    this.players = new ArrayList<>();
    this.players.add(player);

    this.bots = new ArrayList<>();

    System.out.println("Bots' IDs are: ");
    for (int i = 1; i <= nbBots; i++) {
      AiPlayer bot = new AiPlayer();
      bot.setId(i);
      bot.setController(this);
      System.out.println(bot.getId());

      this.bots.add(bot);
      this.players.add(bot);
    }
  }

  /**
   * Default constructor.
   *
   * @author ekasmamy
   */
  public SingleGameController() {
    roundCounter = 0;
    currentPlayerId = 0;
    bag = LetterBag.getInstance();
  }

  /**
   * Default constructor.
   *
   * @author ekasmamy
   */
  public SingleGameController(int nbBots) {
    roundCounter = 0;
    currentPlayerId = 0;
    this.nbBots = nbBots;
    bag = LetterBag.getInstance();
  }

  public void startMove(int turn, int id) {
    System.out.println("----- START startMove() ------");
    setRound();
    setPlayerActive(id);

    // Filling the empty slots in the LetterBar if it's the case
    int freeSlotsCount = letterBar.getCountFreeSlots();
    if (freeSlotsCount > 0) {

      if (currentPlayerId == 0) {
        // player
        System.out.println("Player is on move !");
        letterBar.fillGaps(bag.grabRandomTiles(freeSlotsCount));
        letterBar.display();
      }

    }

    if (currentPlayerId != 0) {
      // bot
      System.out.println("BOT " + id + " is on move !");
      bots.forEach(bot -> {
        if (bot.getId() == id) {
          bot.giveLettersToAiPlayer(bag);
        }
      });
    }

    bagCount.setText(String.valueOf(bag.getAmount()));

    // enabling every LetterTile in Bar
    letterBar.getTilesInBar().forEach(tile -> {
      tile.setMouseTransparent(false);
      tile.isFrozen = false;
    });

    // enabling every action button
    okBtn.setMouseTransparent(false);
    exchangeBtn.setMouseTransparent(false);

    // starting the timer (10 minutes for each turn)
    timer = new Timer(true);

    TimerTask endMove = new TimerTask() {
      @Override
      public void run() {
        Platform.runLater(() -> {

          // moving all tiles in grid back to bar
          letterBar.putTilesBackToBar();

          // disabling every LetterTile in bar
          letterBar.getTilesInBar().forEach(tile -> {
            tile.setMouseTransparent(true);
            tile.isFrozen = true;
          });

          // ending the current player's move after the end of the timer
          endMove();

        });

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

            if (sec <= 15) {
              Pulse pulse = new Pulse(timerLabel);
              pulse.play();
            }
          }

        });
      }
    };

    timer.scheduleAtFixedRate(updateLabel, 0, 1000);

    if (currentPlayerId != 0) {
      // bot
      bots.forEach(bot -> {
        if (bot.getId() == id) {
          bot.makeTurn();
          boolean validMove = grid.verifyWordsValidity();
          if (validMove) {
            timer.purge();
            endMove();
          }
        }
      });
    }

    System.out.println("----- END startMove() ------");
  }

  /**
   * This function will be executed when the player's turn is over. By time over or by manual turn
   * ending.
   *
   * @author ekasmamy
   */
  public void endMove() {
    System.out.println("\n----- START @SingleGameController endMove() -----");

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
      exchangeBtn.setMouseTransparent(true);

      timer.cancel();

      ArrayList<Word> wordsInGrid = grid.words;
      int score = 0;

      for (int j = 0; j < wordsInGrid.size(); j++) {
        Word word = wordsInGrid.get(j);

        if (word.newlyPlaced) {
          score += word.getPoints();
        }
      }

      addToScoreOfPlayer(currentPlayerId, score);
      bagCount.setText(String.valueOf(bag.getAmount()));

      currentPlayerId = (currentPlayerId + 1) % nbPlayers;
      System.out.println("\nNext player ID is : " + currentPlayerId);

      if (bag.getAmount() == 0) {
      }

    }

    System.out.println("----- END @SingleGameController endMove() -----");
  }

  /**
   * Sets the "onMouseClicked" event of buttons.
   *
   * @author ekasmamy
   */
  protected void setButtonActions() {

    exchangeBtn.setOnMouseClicked(event -> {
      ArrayList<Tile> tiles = new ArrayList<>();
      for (LetterTile letterTile : letterBar.getTilesInBar()) {
        tiles.add(new Tile(letterTile.getLetter(), letterTile.getPoints()));
      }
      bag.exchangeTiles(tiles);
    });

    quitGame.setOnMouseClicked(event -> {
      bag.fillBag();
      changeScene("/fxml/MainPage.fxml", "/css/mainMenu.css");
    });

    okBtn.setOnMouseClicked(event -> {
      endMove();
    });

    bagBtn.setOnMouseClicked(mouseEvent -> {
      popupBlankBlock.setVisible(true);
      popupBlankBlock.setViewOrder(--minViewOrder);

      displayBag(bag.getAmountOfEveryTile());
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

    // Local Game

    initData(nbBots);

    initDictionary();

    initGrid();

    // Creating a Leader Board
    leaderBoard = new LeaderBoard(players);

    letterBar = new LetterBar(this);

    players.forEach(player -> {

      BorderPane playerBlock = new BorderPane();
      playerBlock.getStyleClass().add("players-block");
      playerBlock.setPadding(new Insets(10, 30, 10, 30));

      System.out.println(player.getName() + " has ID: " + player.getId());
      playerBlock.setId("player-block-" + String.valueOf(player.getId()));

      StackPane avatarWrapper = new StackPane();
      avatarWrapper.getStyleClass().add("player-avatar-frame");
      avatarWrapper.setAlignment(Pos.CENTER);

      ImageView avatar =
          new ImageView(new Image(getClass().getResourceAsStream("/img/" + player.getImage())));
      avatar.setFitHeight(60);
      avatar.setFitWidth(60);

      Label nickname = new Label(player.getName());
      nickname.getStyleClass().add("players-name");

      Label score = new Label(String.valueOf(player.getScore()));
      score.getStyleClass().add("players-score");

      avatarWrapper.getChildren().add(avatar);
      playerBlock.setLeft(avatarWrapper);
      playerBlock.setCenter(nickname);
      playerBlock.setRight(score);
      BorderPane.setAlignment(avatarWrapper, Pos.CENTER);
      BorderPane.setAlignment(nickname, Pos.CENTER);
      BorderPane.setAlignment(score, Pos.CENTER);

      playersBlock.getChildren().add(playerBlock);
    });

    setButtonActions();

    // Binding GridPane Wrapper's Height to be always equal to its Width
    gridWrapper.widthProperty().addListener((observable, oldValue, newValue) -> {
      gridWrapper.setMaxHeight(newValue.doubleValue());
    });

    // Binding GridPane Wrapper's Width to be always equal to its Parent Node's Height
    mainBlock.heightProperty().addListener((observable, oldValue, newValue) -> {
      gridWrapper.setMaxWidth(newValue.doubleValue());
    });

    sideBar.maxHeightProperty().bind(mainBlock.heightProperty());

    startMove(roundCounter, currentPlayerId);

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

        Pulse pulse = new Pulse(playerBlock.getLeft());
        pulse.setCycleCount(2);
        pulse.play();
      }
    });
  }

  /**
   * Extracts and returns an integer ID from the css id.
   *
   * @param id CSS ID property.
   * @return Integer ID.
   * @author ekasmamy
   */
  public int getIntID(String id) {
    String[] split = id.split("-");
    return Integer.parseInt(split[split.length - 1]);
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
        scoreLabel.getStyleClass().add("players-score-active");
        scoreLabel.setManaged(false);

        Pulse pulse = new Pulse(scoreLabel);
        pulse.setOnFinished(actionEvent -> {
          scoreLabel.getStyleClass().removeAll("players-score-active");
          scoreLabel.setManaged(true);
        });
        pulse.play();
      }
    });
  }

  /**
   * Displaying the bag.
   */
  public void displayBag(ArrayList<Pair<Character, Integer>> amount) {

    bagBtn.setMouseTransparent(true);

    LetterBag bag = LetterBag.getInstance();
    for (int j = 0; j < bag.getAlphabetSize(); j++) {
      char l = bag.getLetterInAlphabet(j);
      int v = bag.getValueInAlphabet(j);

      LetterTile ltrTile = new LetterTile(l, v, grid.cellSize, thisController);
      ltrTile.setPointsVisible(false);
      ltrTile.isBlank = true;
      ltrTile.container.setOnDragDetected(null);
      ltrTile.container.setOnDragDone(null);
      ltrTile.container.setOnMouseDragged(null);
      ltrTile.container.setOnMouseReleased(null);
      ltrTile.container.setOnMouseClicked(null);

      popupBlankBlock.setOnMouseClicked(event -> {
        popupBlankBlock.setVisible(false);
        okBtn.setDisable(false);
        popupBlankMessage.getChildren().clear();
        bagBtn.setMouseTransparent(false);
        popupBlankBlock.setOnMouseClicked(null);
      });

      popupBlankMessage.setOnMouseClicked(event -> {
        popupBlankBlock.setVisible(false);
        okBtn.setDisable(false);
        popupBlankMessage.getChildren().clear();
        bagBtn.setMouseTransparent(false);
        popupBlankMessage.setOnMouseClicked(null);
      });

      int remAmount = 0;
      for (Pair<Character, Integer> charIntPair : amount) {
        if (charIntPair.getKey().equals(l)) {
          remAmount = charIntPair.getValue();
          break;
        }
      }

      Label remAmountLabel = new Label(String.valueOf(remAmount));
      remAmountLabel.getStyleClass().add("remaining-amount-tile-label");

      if (remAmount == 0) {
        ltrTile.setDisable(true);
        remAmountLabel.setTextFill(Color.CRIMSON);
      }

      HBox box = new HBox(ltrTile.container, remAmountLabel);
      box.getStyleClass().add("remaining-amount-tile-block");
      box.setAlignment(Pos.CENTER);

      popupBlankMessage.getChildren().add(box);
    }
  }
}
