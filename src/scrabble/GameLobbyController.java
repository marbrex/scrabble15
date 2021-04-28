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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import scrabble.network.LobbyClientProtocol;
import scrabble.network.LobbyHostProtocol;
import scrabble.network.LobbyServer;
import scrabble.network.NetworkScreen;
import scrabble.network.PortsOccupiedException;

public class GameLobbyController implements LobbyController {
  /**
   * GameLobbyController is the controller of the GameLobby.fxml screen
   * 
   * @author hendiehl
   */
  // gui
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

  /**
   * constructor with information about the type of lobby screen => host or client
   * 
   * @param isHost true if open by a host
   */
  public GameLobbyController(boolean isHost) {
    this.isHost = isHost;
  }

  /**
   * handles the actionEvent of the backButton and calls the openScreen method with the Menu.fxml
   * file
   */
  @FXML
  private void backAction() { // need implementation of server shutdown, because server Thread is
                              // running after
    this.shutdown();
    this.openMenu(); // windows is closed
  }

  /**
   * initialize method from JavaFX, which starts the server procedure if the lobby is opened from a
   * host
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
    // just testing purpose
    this.setUpTimer();
    // this.startTimer();
  }

  /**
   * method for loading a screen from an fxml file
   * 
   * @param s path to the fxml file which should be loaded
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/Menu.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) this.backButton.getScene().getWindow();
        stage.setScene(new Scene(root, 900, 700));
        stage.setResizable(false);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });
  }

  /**
   * method which shutdown the server
   */
  public void shutdown() { // Can also be used if no host
    if (server != null) {
      server.shutdown();
    } else if (this.client != null) {
      this.client.shutdownProtocol(true);
    }

  }

  /**
   * method which set up the server
   */
  private void isHost() {
    this.activateHostControlls();
    this.server = new LobbyServer(this);
    this.server.start();
    // this.chatUser = this.host; //perhabs to fast because of thread usage ? --> need of set after
    // finish of
    // depreciated
  }

  public void setChatUser(NetworkScreen chatUser) {
    this.chatUser = chatUser;
  }

  /**
   * method to show host specific options on the screen
   */
  public void activateHostControlls() {
    this.configureButton.setDisable(false);
    this.configureButton.setVisible(true);
    this.startButton.setDisable(false);
    this.startButton.setVisible(true);
  }

  /**
   * method which sets specific player profiles visible
   * 
   * @param number number of the profile which should be shown
   */
  public void setProfileVisible(int number, String name) { // change to give complete list, to add
                                                           // the names directly from list ??
    Platform.runLater(() -> {
      switch (number) {
        case 0:
          this.profile0.setVisible(true);
          this.profile0.setDisable(false);
          this.label0.setText(name);
          /*
           * if(this.isHost) { this.kick0.setVisible(true); this.kick0.setDisable(false); }
           */
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
   * method which sets all Profiles , normally in order to represent a new arrangement
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
   * method to set up the timer label (time or other messages)
   * 
   * @param msg message which should be shown at the top of the screen
   */
  public void setTimeLabel(String msg) {
    Platform.runLater(() -> {
      this.timeLabel.setText(msg);
    });
  }

  /**
   * method to set the protocol of an client if the lobby is entered
   * 
   * @param clientProtocol
   */
  public void setProtocol(LobbyClientProtocol clientProtocol) {
    this.client = clientProtocol;
    this.chatUser = clientProtocol; // double instance for better chat usage
  }

  /**
   * method to react to an host who wants to kick an player
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
   */
  public void setHostProtocol(LobbyHostProtocol host) {
    this.host = host;
  }

  /**
   * method to start the lobby timer
   */
  private void startTimer() {
    this.timer.play();
  }

  /**
   * method to stop the lobby timer
   */
  public void stopTimer() { // needed ?
    this.timer.stop();
    this.second = 60;
  }

  /**
   * method to set up the lobby timer
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
   * method to start the enter game procedure
   */
  private void timerFinished() {
    this.setTimeLabel("Wait for other Players");
  }

  /**
   * method to react to the start button accessible from the lobby host
   */
  @FXML
  private void startButtonAction() {
    // this.stopTimer(); // test
    if (this.host != null) {
      this.host.startGame();
    }
  }

  /**
   * method to react to the configure button accessible from the lobby host. There should be the
   * option of changing extra points from positions add a dictionary and choose to a own port.
   */
  @FXML
  private void configureButtonAction() {
    // to do
  }

  /**
   * Method to print an chat message on the chat field in the lobby
   */
  public void printChatMessage(String message) {
    // TODO Auto-generated method stub
    Platform.runLater(() -> {
      this.chatArea.appendText(this.seperator + message); // adding it to the chat
    });
  }

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
   */
  private void initializeSequencepositions() {
    // Items should be set in dependence of the player amount
    this.positions = FXCollections.observableArrayList("None");
    System.out.println("PlayerAmount : " + this.chatUser.getPlayerAmount());
    for (int i = 0; i < this.chatUser.getPlayerAmount(); i++) {
      System.out.println(String.valueOf(i + 1)); // here problem
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
   * @param e
   */
  @FXML
  private void changedBoxAction(ActionEvent e) {
    JFXComboBox<String> box = (JFXComboBox<String>) e.getSource();
    String choosen = box.getValue();
    System.out.println(choosen);
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
    System.out.println("Changed");
  }

  /**
   * Method to get the values of the position election, in order of the Profiles in the lobby.
   * 
   * @return List with int representation of the position for each player chosen by the owner of the
   *         lobby
   */
  public int[] getPositionList() { // here null pointer
    int[] list = {this.values.get(position1), this.values.get(position2),
        this.values.get(position3), this.values.get(position4)};
    return list;
  }

  /**
   * Method to start the election of the player sequence.
   */
  public void startElection() {
    // TODO Auto-generated method stub
    this.startTimer();
    this.initializeSequencepositions();
  }

  /**
   * Message to start the Game
   */
  public void startGame() {
    this.setTimeLabel("Game started");
  }

  /**
   * Method to set the background image
   */
  private void loadBackground() {
    this.background.setImage(new Image(getClass().getResourceAsStream("img/GameLobby.jpg")));
  }

  /**
   * Method to set the profile picture of specific player to the GameLobby
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
}
