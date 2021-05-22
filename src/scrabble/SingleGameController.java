package scrabble;

import com.google.common.collect.Multiset;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import scrabble.game.LeaderBoard;
import scrabble.game.LetterBag;
import scrabble.game.LetterBag.Tile;
import scrabble.game.LetterBar;
import scrabble.model.AiPlayer;
import scrabble.model.HumanPlayer;
import scrabble.model.Profile;

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
public class SingleGameController extends GameController {

  public void startMove() {

    int id = Profile.getPlayer().getId();

    setRound();
    setPlayerActive(id);
    setActions(true);

    // Filling the empty slots in the LetterBar if it's the case
    int freeSlotsCount = letterBar.getCountFreeSlots();
    if (freeSlotsCount > 0) {

        letterBar.fillGaps(LetterBag.getInstance().grabRandomTiles(freeSlotsCount));
        letterBar.display();
    }

    // starting the timer (10 minutes for each turn)
    timer = new Timer();

    TimerTask endMove = new TimerTask() {
      @Override
      public void run() {

        // System.out.println("Time is over - moving tiles in grid back to bar..");
        // moving all tiles in grid back to bar
        letterBar.putTilesBackToBar();

        setActions(false);

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

      }
    };

    timer.scheduleAtFixedRate(updateLabel, 0, 1000);

  }

  public void endMove() {

    // verifying the player's input
    boolean validInput = grid.verifyWordsValidity();

    if (validInput) {

      setActions(false);

      timer.cancel();

    }

    // TODO: update the LeaderBoard

  }

  public void setActions(boolean active) {
    // enabling every LetterTile in Bar
    letterBar.getTilesInBar().forEach(tile -> {
      tile.setMouseTransparent(!active);
      tile.isFrozen = !active;
    });

    // enabling every action button
    okBtn.setMouseTransparent(!active);
    shuffleBtn.setMouseTransparent(!active);
  }

  @Override
  public void initPlayers() {
    players = new ArrayList<>();

    HumanPlayer host = Profile.getPlayer();
    host.setId(1);
    players.add(host);

    for (int i = 2; i <= nbPlayers; i++) {
      AiPlayer ai = new AiPlayer();
      ai.setId(i);
      players.add(ai);
    }
  }

  public void initLeaderBoard() {

    initPlayers();

    // Creating a Leader Board
    leaderBoard = new LeaderBoard(players);

    players.forEach(player -> {
      BorderPane playerBlock = new BorderPane();
      playerBlock.getStyleClass().add("players-block");
      playerBlock.setPadding(new Insets(10, 30, 10, 30));
      System.out.println(player.getName() + " has ID: " + player.getId());
      playerBlock.setId(String.valueOf(player.getId()));

      StackPane avatarWrapper = new StackPane();
      avatarWrapper.getStyleClass().add("player-avatar-frame");
      avatarWrapper.setAlignment(Pos.CENTER);

      ImageView avatar = new ImageView(new Image(getClass().getResourceAsStream("img/" + player.getImage())));
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
  }

  /**
   * The FXML loader will call the initialize() method after the loading of the FXML document is
   * complete. Initializes both grid cells and proposed letters.
   */
  @FXML
  private void initialize() {

    initDictionary();

    initLeaderBoard();

    initGrid();

    letterBar = new LetterBar(this);

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

    startMove();

  }

  /**
   * Provider method of the grabRandomTiles method of LetterBag
   *
   * @param tiles
   */
  public void grabRandomTilesAnswer(Multiset<Tile> tiles) {
    System.out.println("GAME CONTROLLER : grabRandomTilesAnswer received");
    Platform.runLater(() -> {
      // updating the LetterBar
      letterBar.fillGaps(tiles);
      letterBar.display();
    });
  }

  public void setPlayerActive(int id) {
    System.out.println("ID: " + id);
    playersBlock.getChildren().forEach(block -> {
      System.out.println("Current player block ID: " + block.getId());
      BorderPane playerBlock = (BorderPane) block;
      playerBlock.getLeft().getStyleClass().remove("player-avatar-frame-active");

      if (Integer.parseInt(block.getId()) == id) {
        System.out.println("Found! ID: " + block.getId());
        playerBlock.getLeft().getStyleClass().add("player-avatar-frame-active");
      }
    });
  }

  public void setRound() {
    roundLabel.setText(String.valueOf(++roundCounter));
  }

  public void otherPlayerOnMove(int id) {

    setRound();
    setPlayerActive(id);
  }
}
