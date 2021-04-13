package scrabble;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import scrabble.network.LobbyClientProtocol;
import scrabble.network.LobbyHostProtocol;
import scrabble.network.LobbyServer;
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
  // control
  private LobbyServer server;
  private boolean isHost;
  private LobbyClientProtocol client;
  private LobbyHostProtocol host;
  /** variable for the screen countdown */
  private int second = 60;
  /** timer */
  private Timeline timer;

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
    if (this.isHost) {
      Platform.runLater(() -> {
        this.isHost();
      });
    }
    // just testing purpose
    this.setUpTimer();
    this.startTimer();
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
  }

  /**
   * method to react to an host who wants to kick an player
   */
  @FXML
  private void kickBtn(ActionEvent e) {
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
  public void startTimer() {
    this.timer.play();
  }

  /**
   * method to stop the lobby timer
   */
  public void stopTimer() {
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

  }

  /**
   * method to react to the start button accessible from the lobby host
   */
  @FXML
  private void startButtonAction() {
    this.stopTimer(); // test
  }

  /**
   * method to react to the configure button accessible from the lobby host. There should be the
   * option of changing extra points from positions add a dictionary and choose to a own port.
   */
  @FXML
  private void configureButtonAction() {
    // to do
  }
}
