package scrabble;

import com.google.common.collect.Multiset;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import scrabble.game.*;
import scrabble.game.LetterBag.Tile;
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
    
    private AiPlayer aiPlayer;

    private final List<String> dialogLines = new ArrayList<String>();

    private TextArea dialogWindow;

    private ImageView im;

    private BorderPane dialogPane;

    private static int indx = 0;

    /**
     * Initialize narrator.
     *
     * @return the border pane
     */
    private BorderPane initializeNarrator() {
        VBox mainBlock = new VBox();
        this.dialogPane = new BorderPane();
        mainBlock.setAlignment(Pos.CENTER);
        this.dialogWindow = new TextArea();
        dialogWindow.setOpacity(0.8);
        dialogWindow.setWrapText(true);
        dialogWindow.setFont(new Font("System", 30));
        dialogWindow.setEditable(false);
        this.dialogPane.getStyleClass().add("popup-error-block");
        this.im = new ImageView(new Image(getClass().getResourceAsStream("/img/anonyms.png")));
        im.setFitWidth(250);
        im.setFitHeight(250);
        mainBlock.getChildren().add(im);
        mainBlock.getChildren().add(dialogWindow);
        this.dialogPane.setCenter(mainBlock);
        return this.dialogPane;
    }

    /**
     * Show narrator.
     */
    private void showNarrator() {
        this.gridWrapper.getChildren().add(this.dialogPane);
    }

    /**
     * Hide narrator.
     */
    private void hideNarrator() {
        this.gridWrapper.getChildren().remove(this.dialogPane);
    }

    /**
     * Show rules.
     */
    private void showRules() {
        if (!this.gridWrapper.getChildren().contains(this.dialogPane)) {
            showNarrator();
        }
        indx = 2;
        this.dialogWindow.setText(dialogLines.get(1));
        this.im.setOnMouseClicked(mouseEvent -> {
            if (indx < 16) {
                this.dialogWindow.setText(dialogLines.get(indx));
                indx++;
            } else {
                hideNarrator();
            }
        });
    }

    /**
     * Sets the listeners.
     */
    private void setListeners() {
        okBtn.setOnAction(event -> {
            grid.verifyWordsValidity();
            setRound();
            //addToScoreOfPlayer(0, 10);
            grabRandomTilesAnswer(bag.grabRandomTiles(7 - letterBar.getTilesInBar().size()));
            setPlayerActive(2);
            Platform.runLater(() -> {
                this.aiPlayer.makeTurn();
                grid.verifyWordsValidity();
                this.aiPlayer.giveLettersToAiPlayer(bag);
            });
            setRound();
            setPlayerActive(1);
        });
    }


    /**
     * Initialize dialog.
     */
    private void initializeDialog() {
        try {
            String path = getClass().getResource("/dialog/dialog.txt").toString();
            StringBuilder sb = new StringBuilder(path);
            sb.delete(0, 5);
            File file = new File(sb.toString());
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                dialogLines.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Replace bar.
     */
    private void replaceBar() {
        Multiset<LetterBag.Tile> tiles = bag.grabRandomTiles(7);
        int indx = 0;
        for (LetterBag.Tile tile : tiles) {
            letterBar.getSlot(indx).setContent(new LetterTile(tile.letter, tile.value, 10, this));
            indx++;
        }
    }

    /**
     * Play guide.
     */
    private void playGuide() {
        showNarrator();
        initializeDialog();
        dialogWindow.appendText("Welcome, " + Profile.getPlayer().getName() +
                "!\nThis is Anonymous! Today I am going to " +
                "teach you how to play scrabble! Click on me to continue!");
        this.im.setOnMouseClicked(mouseEvent -> {
            if (indx < 23) {
                dialogWindow.setText(dialogLines.get(indx));
                indx++;
            } else {
                //startMove();
                hideNarrator();
            }
        });
        ScrabbleApp.getScene().setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.R) {
                if (!this.gridWrapper.getChildren().contains(this.dialogPane)) {
                    showNarrator();
                }
                showRules();
            } else if (keyEvent.getCode() == KeyCode.H) {
                if (!this.gridWrapper.getChildren().contains(this.dialogPane)) {
                    showNarrator();
                }
                dialogWindow.setText(aiPlayer.helpPoorHuman());
                if (aiPlayer.helpPoorHuman().equals("YOU CANNOT MAKE ANY WORDS WITH THESE LETTERS!")) {
                    dialogWindow.appendText("\nNo words, huh? Let me help you");

                }
            }
        });
        okBtn.setOnAction(event -> {
            if (grid.verifyWordsValidity()) {
                setRound();
                Platform.runLater(() -> {
                    aiPlayer.makeTurn();
                    aiPlayer.giveLettersToAiPlayer(bag);
                    grid.verifyWordsValidity();
                    dialogWindow.setText(dialogLines.get(23));
                    showNarrator();
                    setListeners();
                });
                grabRandomTilesAnswer(bag.grabRandomTiles(7 -
                        letterBar.getTilesInBar().size()));
            }
        });
    }


    /**
     * Start move.
     */
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
      //  timer = new Timer();

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

        //timer.schedule(endMove, 1000 * 60 * roundTime);

        // Every second the Label will decrement
        timerLabel.setText(roundTime + ":00");

//        TimerTask updateLabel = new TimerTask() {
//            @Override
//            public void run() {
//
//                String[] minSec = timerLabel.getText().split(":");
//                int min = Integer.parseInt(minSec[0]);
//                int sec = Integer.parseInt(minSec[1]);
//
//                if (min != 0 || sec != 0) {
//                    if (sec - 1 < 0) {
//                        min--;
//                        sec = 59;
//                    } else {
//                        sec--;
//                    }
//                }
//                int finalMin = min;
//                int finalSec = sec;
//                Platform.runLater(() -> {
//                    timerLabel.setText((finalMin < 10 ? "0" + finalMin : finalMin) + ":" + (finalSec < 10 ? "0" + finalSec : finalSec));
//                });
//                //timerLabel.setText((min < 10 ? "0" + min : min) + ":" + (sec < 10 ? "0" + sec : sec));
//
//                if (min >= 3) {
//                    timerLabel.setTextFill(Paint.valueOf("green"));
//                } else if (min >= 1) {
//                    timerLabel.setTextFill(Paint.valueOf("orange"));
//                } else if (min >= 0) {
//                    timerLabel.setTextFill(Paint.valueOf("red"));
//                }
//
//            }
//        };

       // timer.scheduleAtFixedRate(updateLabel, 0, 1000);

    }

    /**
     * End move.
     */
    public void endMove() {

        // verifying the player's input
        boolean validInput = grid.verifyWordsValidity();

        if (validInput) {

            setActions(false);

            timer.cancel();

        }

        // TODO: update the LeaderBoard

    }

    /**
     * Sets the actions.
     *
     * @param active the new actions
     */
    public void setActions(boolean active) {
        // enabling every LetterTile in Bar
        letterBar.getTilesInBar().forEach(tile -> {
            tile.setMouseTransparent(!active);
            tile.isFrozen = !active;
        });

        // enabling every action button
        okBtn.setMouseTransparent(!active);
        exchangeBtn.setMouseTransparent(!active);
    }

    /**
     * Inits the players.
     */
    @Override
    public void initPlayers() {
        players = new ArrayList<>();

        HumanPlayer host = Profile.getPlayer();
        host.setId(1);
        players.add(host);
        players.add(this.aiPlayer);

//        for (int i = 2; i <= nbPlayers; i++) {
//            AiPlayer ai = new AiPlayer();
//            ai.setId(i);
//            players.add(ai);
//        }
    }

    /**
     * Inits the leader board.
     */
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

            ImageView avatar = new ImageView(new Image(getClass().getResourceAsStream("/img/" + player.getImage())));
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
        this.dialogPane = initializeNarrator();
        this.aiPlayer = new AiPlayer();
        this.aiPlayer.setImage(2);
        this.aiPlayer.setName("Anon");
        this.aiPlayer.setController(this);
        initDictionary();
        nbPlayers += 2;
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

        //setButtonActions();

        okBtn.setOnAction(event -> {
            grid.verifyWordsValidity();
            setRound();
            grabRandomTilesAnswer(LetterBag.getInstance().grabRandomTiles(7 - letterBar.getTilesInBar().size()));
            setPlayerActive(2);
            Platform.runLater(() -> {
                this.aiPlayer.makeTurn();
                grid.verifyWordsValidity();
                this.aiPlayer.giveLettersToAiPlayer(LetterBag.getInstance());
            });
            //grid.verifyWordsValidity();
            setRound();
            setPlayerActive(1);
        });

        startMove();
        this.aiPlayer.giveLettersToAiPlayer(LetterBag.getInstance());
        this.aiPlayer.displayTiles();
        quitGame.setOnMouseClicked(event -> {
            changeScene("/fxml/MainPage.fxml", "/css/mainMenu.css");
        });

        playGuide();

    }

    /**
     * Provider method of the grabRandomTiles method of LetterBag.
     *
     * @param tiles the tiles
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
     * Sets the player active.
     *
     * @param id the new player active
     */
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

    /**
     * Sets the round.
     */
    public void setRound() {
        roundLabel.setText(String.valueOf(++roundCounter));
    }

    /**
     * Other player on move.
     *
     * @param id the id
     */
    public void otherPlayerOnMove(int id) {

        setRound();
        setPlayerActive(id);
    }
}
