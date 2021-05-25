package scrabble;

import java.io.IOException;
import java.util.HashMap;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import scrabble.network.LobbyClientProtocol;
import scrabble.network.LobbyHostProtocol;
import scrabble.network.LobbyServer;
import scrabble.network.NetworkScreen;

public class GameLobbyController implements LobbyController {
  /**
   * GameLobbyController is the controller of the GameLobby.fxml screen, which has the function to
   * provide a user interface for a network game before the actual game started.
   * 
   * @author hendiehl
   */
  // gui
  @FXML
  private StackPane root;
  @FXML
  private Label timeLabel;
  @FXML
  private JFXButton backButton;
  @FXML
  private VBox profile0;
  @FXML
  private VBox profile1;
  @FXML
  private VBox profile2;
  @FXML
  private VBox profile3;
  @FXML
  private Label label0;
  @FXML
  private Label label1;
  @FXML
  private Label label2;
  @FXML
  private Label label3;
  @FXML
  private JFXButton startButton;
  @FXML
  private JFXButton configureButton;
  @FXML
  private JFXButton kick0;
  @FXML
  private JFXButton kick1;
  @FXML
  private JFXButton kick2;
  @FXML
  private JFXButton kick3;
  @FXML
  private JFXTextArea chatArea;
  @FXML
  private JFXTextField chatField;
  @FXML
  private JFXButton chatBtn;
  @FXML
  private JFXComboBox<String> position1;
  @FXML
  private JFXComboBox<String> position2;
  @FXML
  private JFXComboBox<String> position3;
  @FXML
  private JFXComboBox<String> position4;
  @FXML
  private ImageView background;
  @FXML
  private ImageView profilePicture1;
  @FXML
  private ImageView profilePicture2;
  @FXML
  private ImageView profilePicture3;
  @FXML
  private ImageView profilePicture4;
  /** List of the ComboBox items, representing the positions 1, 2, 3, 4 or NONE for no position */
  private ObservableList<String> positions;
  /** List to use game sequence election */
  private HashMap<JFXComboBox<String>, Integer> values =
      new HashMap<JFXComboBox<String>, Integer>();

  // control
  private LobbyServer server;
  private boolean isHost;
  private LobbyClientProtocol client;
  private LobbyHostProtocol host;
  /** variable for the screen countdown */
  private int second = 60;
  /** timer */
  private Timeline timer;
  /** line separator for the chat */
  private String seperator;
  /** interface to send/print Message, a HostProtocol or an LobbyClientProtocol */
  private NetworkScreen chatUser;
  /** String content of an user specific game field option of multiplier, standard is empty */
  private String contentOfField = "";
  /** String content of an user specific dictionary, standard is empty */
  private String contentOfDictionary = "";
  /** boolean condition to specify a lobby return from game */
  private boolean back;


  /**
   * Constructor which specify the rights for specific actions with an boolean condition. If the
   * lobby screen is loaded for a host the parameter is true, which is used to show and enable
   * specific like a configuration button.
   * 
   * @param isHost true if open by a host
   * @author hendiehl
   */
  public GameLobbyController(boolean isHost) {
    this.isHost = isHost;
  }

  /**
   * Constructor which will be called when a host return from an network game back to the lobby. The
   * old server will be used again in this case, because it holds the list about the connected
   * clients so they can start a game again.
   * 
   * @param isHost boolean condition about an call by host
   * @param old LobbyServer which was used and started in an lobby before
   * @author hendiehl
   */
  public GameLobbyController(boolean isHost, LobbyServer old) {
    this.isHost = isHost;
    this.server = old;
  }

  /**
   * Method which handles the actionEvent of the backButton in reason to shutdown the corresponding
   * protocol or server and leave the lobby to return to menu screen.
   * 
   * @author hendiehl
   */
  @FXML
  private void backAction() {
    this.shutdown();
    this.openMenu(); // windows is closed
  }

  /**
   * Initialize method from JavaFX, which starts the server procedure if the lobby is opened from a
   * host, set up a OS specific line separator for chat usage and initialize screen specific
   * classes.
   * 
   * @author hendiehl
   */
  @FXML
  private void initialize() {
    this.loadBackground();
    if (this.isHost) {
      Platform.runLater(() -> {
        this.isHost();
      });
    }
    this.seperator = System.lineSeparator(); // getting the line separator for Unix or Windows
    this.setUpTimer();
    // this.startTimer();
  }

  /**
   * Method to return back to the Menu.fxml for network game options.
   * 
   * @param s path to the fxml file which should be loaded
   * @author hendiehl
   */
  public void openMenu() {
    /*
     * FXMLLoader loader = new FXMLLoader(getClass().getResource(s)); Stage stage = (Stage)
     * this.backButton.getScene().getWindow(); Scene scene; scene = new Scene(loader.load());
     * stage.setScene(scene); stage.setTitle("Scrabble"); stage.setHeight(600); stage.setWidth(800);
     * stage.setResizable(false); stage.show();
     */
    Platform.runLater(() -> {
      try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Menu.fxml"));
        Parent root = loader.load();
        /*
         * Stage stage = (Stage) this.backButton.getScene().getWindow(); stage.setScene( new
         * Scene(root, this.root.getScene().getWidth(), this.root.getScene().getHeight()));
         */
        ScrabbleApp.getScene().getStylesheets().clear();
        ScrabbleApp.getScene().setRoot(root);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });
  }

  /**
   * Method which shutdown the network classes in dependence of the host condition. If the host
   * protocol instance is not null, the host shutdown have to be performed with an server shutdown.
   * If the client protocol instance is not null, the shutdown of the client protocol is performed.
   * A GameLobbyController has always a corresponding host or client protocol.
   * 
   * @author hendiehl
   */
  public void shutdown() { // Can also be used if no host
    System.out.println("GAME LOBBY : Shutdown");
    if (this.host != null) {
      this.host.shutdown();
    } else if (this.client != null) {
      this.client.shutdownProtocol(true);
    }

  }

  /**
   * Method to set the content of an specific dictionary. Used by the configure controller.
   * 
   * @param content content of an specific dictionary file
   * @param hostChange boolean condition which decides if the string is actually set or send to the
   *        server
   */
  public void setContentOfDictionary(String content, boolean hostChange) {
    if (hostChange) {
      this.host.setDictionaryMessage(content);
    } else {
      if (!content.equals("")) { // not the standard
        this.contentOfDictionary = content;
        // set the dictionary
      }
    }
  }

  /**
   * Method to set the content of an specific multiplier field. Used by the configure controller.
   * 
   * @param path content of an specific file
   * @param hostChange boolean condition which decides if the string is actually set or send to the
   *        server
   */
  public void setContentOfFile(String path, boolean hostChange) {
    if (hostChange) {
      this.host.setFieldMessage(path);
    } else {
      this.contentOfField = path;
    }
  }

  /**
   * Method which sets up host specific actions like activating host controls and starting a server.
   * 
   * @author hendiehl
   */
  private void isHost() {
    this.activateHostControlls(); // activating the host controls
    if (!back) { // condition if host return from an network game to lobby
      this.server = new LobbyServer(this); // starting the server
      this.server.start();
    }
  }

  /**
   * Method to set a host or client protocol instance, mainly for chat actions like sending a chat
   * message.
   * 
   * @param chatUser protocol which have the permission to send chat messages via the chat server.
   */
  public void setChatUser(NetworkScreen chatUser) {
    this.chatUser = chatUser;
  }

  /**
   * Method to show and enable host specific controls like server configuration and earlier game
   * starting.
   * 
   * @author hendiehl
   */
  public void activateHostControlls() {
    this.configureButton.setDisable(false);
    this.configureButton.setVisible(true);
    this.startButton.setDisable(false);
    this.startButton.setVisible(true);
  }

  /**
   * Method to show player profiles on the lobby screen in dependence of a server intern list
   * sequence.
   * 
   * @param number number of the profile which should be shown
   * @param name name of the player profile which should be shown
   * @author hendiehl
   */
  public void setProfileVisible(int number, String name) {
    Platform.runLater(() -> {
      switch (number) {
        case 0:
          this.profile0.setVisible(true);
          this.profile0.setDisable(false);
          this.label0.setText(name);
          break;
        case 1:
          this.profile1.setVisible(true);
          this.profile1.setDisable(false);
          this.label1.setText(name);
          if (this.isHost) {
            this.kick1.setVisible(true);
            this.kick1.setDisable(false);
          }
          break;
        case 2:
          this.profile2.setVisible(true);
          this.profile2.setDisable(false);
          this.label2.setText(name);
          if (this.isHost) {
            this.kick2.setVisible(true);
            this.kick2.setDisable(false);
          }
          break;
        case 3:
          this.profile3.setVisible(true);
          this.profile3.setDisable(false);
          this.label3.setText(name);
          if (this.isHost) {
            this.kick3.setVisible(true);
            this.kick3.setDisable(false);
          }
          break;
      }
    });
  }

  /**
   * Method to reset the visibility of all profile fields on the lobby screen. Because after a lobby
   * leave a complete new instance of a profile list is send to the members it is needed to reset
   * them before the new ones will shown again.
   * 
   * @author hendiehl
   */
  public void resetProfileVisibility() {
    Platform.runLater(() -> {
      this.profile0.setVisible(false);
      this.profile0.setDisable(true);

      this.profile1.setVisible(false);
      this.profile1.setDisable(true);

      this.profile2.setVisible(false);
      this.profile2.setDisable(true);

      this.profile3.setVisible(false);
      this.profile3.setDisable(true);
    });
  }

  /*
   * public void setProfileNotVisible(int number) { Platform.runLater(()-> { switch(number) { case
   * 0: this.profile0.setVisible(false); this.profile0.setDisable(true); if(this.isHost) {
   * this.kick0.setVisible(false); this.kick0.setDisable(true); } break; case 1:
   * this.profile1.setVisible(false); this.profile1.setDisable(true); if(this.isHost) {
   * this.kick1.setVisible(false); this.kick1.setDisable(true); } break; case 2:
   * this.profile2.setVisible(false); this.profile2.setDisable(true); if(this.isHost) {
   * this.kick2.setVisible(false); this.kick2.setDisable(true); } break; case 3:
   * this.profile3.setVisible(false); this.profile3.setDisable(true); if(this.isHost) {
   * this.kick3.setVisible(false); this.kick3.setDisable(true); } break; } }); }
   */

  /**
   * Method to set up the time label on the top of the lobby screen. Is mainly used for important
   * messages or the timer when the lobby is about to change to a game.
   * 
   * @param msg message which should be shown at the top of the screen
   * @author hendiehl
   */
  public void setTimeLabel(String msg) {
    Platform.runLater(() -> {
      this.timeLabel.setText(msg);
    });
  }

  /**
   * Method to set up the client protocol after a client find and join a lobby through the finder
   * screen.
   * 
   * @param clientProtocol protocol of an client for network communication.
   * @author hendiehl
   */
  public void setProtocol(LobbyClientProtocol clientProtocol) {
    this.client = clientProtocol;
    this.chatUser = clientProtocol; // double instance for better chat usage
  }

  /**
   * Method which handles the ActionEvent of an kick button, clicked by an host in reason to kick a
   * specific lobby member from the lobby.
   * 
   * @param e ActionEvent of an specific kick button from the screen.
   * @author hendiehl
   */
  @FXML
  private void kickBtn(ActionEvent e) { // adding the runLater ???
    JFXButton btn = (JFXButton) e.getSource();
    String id = btn.getId().substring(4);
    System.out.println(id);
    int i = Integer.valueOf(id);
    System.out.println(i);
    Platform.runLater(() -> {
      if (this.host != null) {
        this.host.kickPlayer(i);
      }
    });
  }

  /**
   * Method to set the Host Protocol in case the Lobby is owned by an host
   * 
   * @param host protocol of the host
   * @author hendiehl
   */
  public void setHostProtocol(LobbyHostProtocol host) {
    this.host = host;
  }

  /**
   * Method to start the lobby timer
   * 
   * @author hendiehl
   */
  private void startTimer() {
    this.timer.play();
  }

  /**
   * Method to stop the lobby timer
   * 
   * @author hendiehl
   */
  public void stopTimer() { // needed ?
    this.timer.stop();
    this.second = 60;
  }

  /**
   * Method to initialize the timer and specify its run time.
   * 
   * @author hendiehl
   */
  private void setUpTimer() {
    this.timer = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        // TODO Auto-generated method stub
        setTimeLabel(String.valueOf(--second));
      }

    }));
    timer.setCycleCount(60);
    timer.setOnFinished(new EventHandler<ActionEvent>() {
      @Override
      public void handle(ActionEvent arg0) {
        // TODO Auto-generated method stub
        timerFinished();
      }
    });
  }

  /**
   * Method called by the timer by finishing, which only inform the player about the timer end and
   * the waiting of server actions.
   * 
   * @author hendiehl
   */
  private void timerFinished() {
    this.setTimeLabel("Wait for other Players");
  }

  /**
   * Method which handles the ActionEvent of the start button in reason to start the game procedure
   * earlier.
   * 
   * @author hendiehl
   */
  @FXML
  private void startButtonAction() {
    // this.stopTimer(); // test
    if (this.host != null) {
      this.host.startGame();
    }
  }

  /**
   * Method to react to the configure button accessible from the lobby host. There should be the
   * option of changing extra points from positions add a dictionary and choose to a own port.
   * 
   * @author hendiehl
   */
  @FXML
  private void configureButtonAction() {
    if (this.host != null) { // extra host controll
      if (this.host.isNotInGame()) {
        try {
          FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LobbyConfigure.fxml"));
          loader.setControllerFactory(c -> {
            return new LobbyConfigureController(server, this);
          });
          Parent root1 = (Parent) loader.load();
          Stage stage = new Stage();
          stage.setScene(new Scene(root1, 600, 500));
          stage.setMinHeight(500);
          stage.setMinWidth(600);
          stage.show();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Method to print an chat message on the chat field in the lobby, called by an host or client
   * protocol.
   * 
   * @author hendiehl
   */
  public void printChatMessage(String message) {
    // TODO Auto-generated method stub
    Platform.runLater(() -> {
      this.chatArea.appendText(this.seperator + message); // adding it to the chat
    });
  }

  /**
   * Method which will be called by JavaFX in order to send a chat message to all lobby members by
   * using functionalities of a host or client protocol.
   * 
   * @author hendiehl
   */
  @FXML
  private void chatBtnAction() {
    Platform.runLater(() -> {
      String message = this.chatField.getText();
      if (this.chatUser != null) {
        this.chatUser.sendChatMessage(message);
      }
      this.chatField.setText(""); // setting the text field back
    });
  }

  /*
   * Method to activate and initialize the sequence election procedure
   * 
   * @author hendiehl
   */
  private void initializeSequencepositions() {
    // Items should be set in dependence of the player amount
    this.positions = FXCollections.observableArrayList("None");
    // System.out.println("PlayerAmount : " + this.chatUser.getPlayerAmount());
    for (int i = 0; i < this.chatUser.getPlayerAmount(); i++) {
      // System.out.println(String.valueOf(i + 1)); // here problem
      this.positions.add(String.valueOf(i + 1));
    }
    // setting the items in the boxes -> all a set but not all available.
    this.position1.setItems(positions); // here null pointer
    this.position2.setItems(positions);
    this.position3.setItems(positions);
    this.position4.setItems(positions);
    // setting them in the Map to get Access and change Values --> standard is 0
    this.values.put(position1, 0);
    this.values.put(position2, 0);
    this.values.put(position3, 0);
    this.values.put(position4, 0);
    // setting the ComboBox editable and visible
    this.position1.setDisable(false);
    this.position1.setVisible(true);
    this.position2.setDisable(false);
    this.position2.setVisible(true);
    this.position3.setDisable(false);
    this.position3.setVisible(true);
    this.position4.setDisable(false);
    this.position4.setVisible(true);

  }

  /**
   * ActionHandler for the position ComboBoxes, who sets the chosen value in dependence of the item.
   * 
   * @param e ActionEvent of an specific ComboBox on the lobby screen.
   * @author hendiehl
   */
  @FXML
  private void changedBoxAction(ActionEvent e) {
    JFXComboBox<String> box = (JFXComboBox<String>) e.getSource();
    String choosen = box.getValue();
    // System.out.println(choosen);
    if (choosen.matches("\\d")) {
      this.positionSetted(choosen, box);
    } else {
      Integer old = values.get(box);
      if (old != 0) {
        this.values.replace(box, 0);
      }
    }
  }

  /**
   * Method to set the chosen position value into the ComboBox Hashmap. The change will be denied if
   * the same value is set on a other player
   * 
   * @param choosen Value the player chosen for a player
   * @param box Corresponding box of the change.
   * @author hendiehl
   */
  private void positionSetted(String choosen, JFXComboBox<String> box) {
    Integer i = Integer.valueOf(choosen);
    for (Integer v : values.values()) {
      if (i == v) {
        box.setValue("None");
        return;
      }
    }
    this.values.replace(box, i);
    // System.out.println("Changed");
  }

  /**
   * Method to get the values of the position election, in order of the Profiles in the lobby.
   * 
   * @return List with int representation of the position for each player chosen by the owner of the
   *         lobby
   * @author hendiehl
   */
  public int[] getPositionList() { // here null pointer
    int[] list = {this.values.get(position1), this.values.get(position2),
        this.values.get(position3), this.values.get(position4)};
    return list;
  }

  /**
   * Method to start the election of the player sequence.
   * 
   * @author hendiehl
   */
  public void startElection() {
    // TODO Auto-generated method stub
    this.startTimer();
    this.initializeSequencepositions();
    if (this.configureButton.isVisible()) {
      this.configureButton.setDisable(true);
      this.configureButton.setVisible(false);
    }
    if (this.startButton.isVisible()) {
      this.startButton.setDisable(true);
      this.startButton.setVisible(false);
    }
  }

  /**
   * Method to change the scene to the game screen.
   * 
   * @author hendiehl
   */
  public void startGame() {
    this.goInGameScreen();
  }

  /**
   * Method to set the background image
   * 
   * @author hendiehl
   */
  private void loadBackground() {
    this.background.setImage(new Image(getClass().getResourceAsStream("/img/GameLobby.jpg")));
  }

  /**
   * Method to set the profile picture of specific player to the GameLobby
   * 
   * @author hendiehl
   */
  public void setProfilePicture(int number, String picturePath) {
    Platform.runLater(() -> {
      switch (number) {
        case 0:
          profilePicture1.setImage(new Image(getClass().getResourceAsStream(picturePath)));
          break;
        case 1:
          profilePicture2.setImage(new Image(getClass().getResourceAsStream(picturePath)));
          break;
        case 2:
          profilePicture3.setImage(new Image(getClass().getResourceAsStream(picturePath)));
          break;
        case 3:
          profilePicture4.setImage(new Image(getClass().getResourceAsStream(picturePath)));
          break;
      }
    });
  }

  /**
   * Method to change into the GameScreen with network specific information and set up the game
   * controller in the protocol
   * 
   * @author hendiehl
   */
  public void goInGameScreen() { // Just Testing Purpose with TestScreen !!!!!!!!!!!!!!!!!!!!!!!!!!!
    Platform.runLater(() -> {
      try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoadingScreen.fxml"));
        if (this.host != null) {
          loader.setControllerFactory(c -> {
            return new LoadingController(this.host, this.isHost, this.contentOfField,
                this.host.getPlayerList(), this.contentOfDictionary, this.host.getOwnID());
          });
        } else {
          loader.setControllerFactory(c -> {
            return new LoadingController(this.client, this.isHost, this.contentOfField,
                this.client.getPlayerList(), this.contentOfDictionary, this.client.getOwnID());
          });
        }
        Parent root = loader.load();
        /*
         * GameController gameScreen = loader.<GameController>getController(); if (this.host !=
         * null) { this.host.setGameScreen(gameScreen); } else {
         * this.client.setGameScreen(gameScreen); }
         */
        ScrabbleApp.getScene().getStylesheets().clear();
        ScrabbleApp.getScene().getStylesheets()
            .add(getClass().getResource("/css/style.css").toExternalForm());
        ScrabbleApp.getScene().setRoot(root);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });
  }

  /**
   * Method to set a new Server after changing it in configure Screen
   * 
   * @author hendiehl
   */
  public void setNewServer(LobbyServer newOne) {
    this.server = newOne;
  }
}
