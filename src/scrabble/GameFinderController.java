package scrabble;

import java.io.IOException;
import java.net.ConnectException;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import scrabble.network.LobbyClientProtocol;

public class GameFinderController implements LobbyController {
  /**
   * Controller of the GameFinder.fxml screen
   * 
   * @author hendiehl
   */
  @FXML
  private JFXButton joinBtn;
  @FXML
  private TextField portField;
  @FXML
  private RadioButton useOwnPort;
  @FXML
  private Label statusLabel;
  @FXML
  private CheckBox portBox;
  @FXML
  private JFXButton backButton;
  @FXML
  private Label statusLabel2;
  @FXML
  private ImageView background;
  @FXML
  private JFXButton search;
  private LobbyClientProtocol clientProtocol;



  /**
   * method to activate the control fields for a specific port given by the user activates an
   * TextField and show an Message on the screen
   */
  private void activateOwnPortControlls() {
    this.portField.setDisable(false);
    this.portField.setVisible(true);
    this.setStatusLabel("Please Type in port Number");
    this.search.setVisible(true);
    this.search.setDisable(false);
  }

  /**
   * method to deactivate the control fields for a specific port given by the user deactivates an
   * TextField and show an Message on the screen
   */
  private void deactivateOwnPortControlls() {
    this.portField.setDisable(true);
    this.portField.setVisible(false);
    this.setStatusLabel("Use Own-Port 0 to AutoSearch");
    this.search.setVisible(false);
    this.search.setDisable(true);
  }

  /**
   * method to handle the ActionEvent of the joinBtn
   */
  @FXML
  private void joinAction() {
    Platform.runLater(() -> { // Standard procedure
      this.clientProtocol.sendJoinMessage();
    });
  }

  /**
   * method to show an specific string on the statusLabel
   * 
   * @param message message to be shown
   */
  public void setStatusLabel(String message) {
    Platform.runLater(() -> {
      this.statusLabel.setText(message);
    });
  }

  /**
   * method to show an specific string on the statusLabel2
   * 
   * @param message message to be shown
   */
  public void setStatusLabel2(String message) {
    Platform.runLater(() -> {
      this.statusLabel2.setText(message);
    });
  }

  /**
   * method to activate the joinBtn on the screen and show the accessibility on an port
   */
  public void activateJoin() {
    this.joinBtn.setDisable(false);
    this.portBox.setSelected(true);
  }

  /**
   * method to check if an String matches the characteristics of an port number
   * 
   * @param port String representation of an user port
   * @return boolean about the acceptance of the String parameter
   */
  private boolean checkPortString(String port) { // not implemented yet
    System.out.println("String match port " + port.matches("^\\d{3,5}$"));
    return port.matches("^\\d{3,5}$");
  }

  /**
   * Method to activate auto search after a client changed to own port once
   * 
   * @param port String representation of 0 for identifying
   * @return about the acceptance of the String parameter
   */
  private boolean checkAutoSearch(String port) {
    System.out.println("String match 0 " + port.matches("^0$"));
    return port.matches("^0$");
  }

  /**
   * handles the actionEvent of the backButton and calls the openScreen method with the Menu.fxml
   * file
   */
  @FXML
  private void backButtonAction() {
    /*
     * System.out.println("Back Button"); this.shutdown(); FXMLLoader loader = new
     * FXMLLoader(getClass().getResource("Menu.fxml")); Stage stage = (Stage)
     * this.joinBtn.getScene().getWindow(); Scene scene; scene = new Scene(loader.load());
     * stage.setScene(scene); stage.setTitle("Scrablle"); stage.setHeight(700); stage.setWidth(900);
     * stage.setResizable(false); stage.show();
     */
    this.clientProtocol.shutdownProtocol(true); // shutdown when in GameFinder screen
    openMenu();
  }

  public void openMenu() {
    Platform.runLater(() -> {
      try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/Menu.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) this.backButton.getScene().getWindow();
        stage.setScene(new Scene(root, 900, 700));
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });
  }

  /**
   * initialize method of javaFx in which the a joinProtokol automatically search for an game Lobby
   * in an local Network. If an protocol is generated the joinBtn are activated. If not the option
   * to input a user specific port is accessible.
   */
  @FXML
  private void initialize() {
    this.loadBackground();
    System.out.println("GAME FINDER : Activate auto search");
    clientProtocol = new LobbyClientProtocol(this);
    clientProtocol.start();
  }

  public void connectSucessful() {
    Platform.runLater(() -> {
      this.joinBtn.setDisable(false);
    });
  }

  public void connectNotSucessful() {
    Platform.runLater(() -> {
      this.statusLabel.setText("No game at standart port");
      this.useOwnPort.setDisable(false); // activate or not
    });
  }

  /**
   * handles the useOwnPort ActionEvents and activates or deactivates the controls for an user input
   */
  @FXML
  private void ownPortWanted() {
    // System.out.println("Selected");
    if (this.useOwnPort.isSelected()) {
      this.activateOwnPortControlls();
    } else {
      this.deactivateOwnPortControlls();
    }
  }

  /**
   * handles the searchButton ActionEvent and try to create an instances of an GameFinderProtocol
   * with an specific port given by the user
   */
  @FXML
  private void searchAction() {
    System.out.println("GAME FINDER : Own port string = " + this.portField.getText());
    if (this.checkPortString(this.portField.getText())) {
      System.out.println("GAME FINDER : Port input accepted");
      int port = Integer.valueOf(this.portField.getText());
      if (port <= 65535) {
        this.clientProtocol.shutdownProtocol(true);
        try {
          this.clientProtocol = new LobbyClientProtocol(this, port);
          this.clientProtocol.start();
          System.out.println("GAME FINDER : Connection on port");
          // this.joinBtn.setDisable(false);
        } catch (ConnectException e) {
          System.out.println("GAME FINDER : No connection on own port");
          this.statusLabel.setText("No connection at this port");
        } catch (IOException e) {
          // TODO Auto-generated catch block
          // e.printStackTrace();
          System.out.println("GAME FINDER : No connection on own port");
          this.statusLabel.setText("No connection at this port");
          this.setStatusLabel2("");
        }
      } else {
        System.out.println("GAME FINDER : No valid port");
        this.setStatusLabel2("No valid port");
      }
    } else if (this.checkAutoSearch(this.portField.getText())) {
      System.out.println("GAME FINDER : Activate auto search");
      this.clientProtocol.shutdownProtocol(true);
      this.clientProtocol = new LobbyClientProtocol(this);
      this.clientProtocol.start();
      this.setStatusLabel2("Search Network Game");
    } else {
      System.out.println("GAME FINDER : No valid port");
      this.setStatusLabel2("No valid port");
    }
  }

  /**
   * method to shutdown the client protocol
   */
  @Override
  public void shutdown() {
    System.out.println("GAME LOBBY : Shutdown");
    if (this.clientProtocol != null) {
      this.clientProtocol.shutdownProtocol(true);
    }

  }

  /**
   * method to show port informations on the screen
   * 
   * @param port specific port given by the user or one of the standard ports
   */
  public void setPortInformation(int port) {
    Platform.runLater(() -> {
      this.portBox.setSelected(true);
      this.portBox.setText("Port : " + port);
    });
  }

  /**
   * method to navigate from the GameFinder screen to the GameLobby screen as client
   */
  public void goInLobby() {
    Platform.runLater(() -> {
      try {/*
            * FXMLLoader loader = new FXMLLoader(getClass().getResource("GameLobby.fxml"));
            * loader.setControllerFactory(c -> { return new GameLobbyController(false); });
            * GameLobbyController lobbyController; Stage stage = (Stage)
            * this.joinBtn.getScene().getWindow(); Scene scene; scene = new Scene(loader.load());
            * lobbyController = loader.<GameLobbyController>getController();
            * lobbyController.setProtocol(this.clientProtocol); if(lobbyController == null) {
            * System.out.println("LobbyController == null"); }
            * this.clientProtocol.setLobbyController(lobbyController); stage.setScene(scene);
            * stage.setTitle("Scrablle"); stage.setHeight(700); stage.setWidth(900);
            * stage.setResizable(false); stage.show();
            */
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/GameLobby.fxml"));
        loader.setControllerFactory(c -> {
          return new GameLobbyController(false);
        });
        Parent root = loader.load();
        GameLobbyController lobbyController = loader.<GameLobbyController>getController();
        lobbyController.setProtocol(this.clientProtocol);
        this.clientProtocol.setLobbyController(lobbyController);
        Stage stage = (Stage) this.joinBtn.getScene().getWindow();
        stage.setScene(new Scene(root, 900, 750));
        stage.setOnHidden(e -> {
          lobbyController.shutdown();
        });
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });
  }

  /**
   * Method to set the background image
   */
  private void loadBackground() {
    this.background.setImage(new Image(getClass().getResourceAsStream("img/GameFinder.jpg")));
  }
}
