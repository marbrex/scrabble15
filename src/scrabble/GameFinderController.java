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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import scrabble.network.LobbyClientProtocol;

public class GameFinderController implements LobbyController {
  /**
   * Controller of the GameFinder.fxml screen, which have the function to search specific lobbys in the
   * network and gave the player the option to join them if possible.
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
  @FXML
  private StackPane root;
  private LobbyClientProtocol clientProtocol;



  /**
   * Method to activate the control fields for a specific port given by the user and inform the user
   * that he can know type in a specific port.
   * @author hendiehl
   */
  private void activateOwnPortControlls() {
    this.portField.setDisable(false);
    this.portField.setVisible(true);
    this.setStatusLabel("Please Type in port Number");
    this.search.setVisible(true);
    this.search.setDisable(false);
  }

  /**
   * Method to deactivate the control fields for a specific port given by the user and inform the player how
   * he can activate the normal procedure again.
   * @author hendiehl
   */
  private void deactivateOwnPortControlls() {
    this.portField.setDisable(true);
    this.portField.setVisible(false);
    this.setStatusLabel("Use Own-Port 0 to AutoSearch");
    this.search.setVisible(false);
    this.search.setDisable(true);
  }

  /**
   * Method to handle the ActionEvent of the joinBtn in reason to join a founded lobby in the network.
   * @author hendiehl
   */
  @FXML
  private void joinAction() {
    Platform.runLater(() -> { // Standard procedure
      this.clientProtocol.sendJoinMessage();
    });
  }

  /**
   * Method to show an specific string on the statusLabel.
   * 
   * @param message message to be shown
   * @author hendiehl
   */
  public void setStatusLabel(String message) {
    Platform.runLater(() -> {
      this.statusLabel.setText(message);
    });
  }

  /**
   * Method to show an specific string on the statusLabel2.
   * 
   * @param message message to be shown
   * @author hendiehl
   */
  public void setStatusLabel2(String message) {
    Platform.runLater(() -> {
      this.statusLabel2.setText(message);
    });
  }

  /**
   * Method to activate the joinBtn on the screen and show the accessibility of an port.
   * @author hendiehl
   */
  public void activateJoin() {
    this.joinBtn.setDisable(false);
    this.portBox.setSelected(true);
  }

  /**
   * Method to check if an String matches the characteristics of an port number, which means that it have to be a number of a minimum length of 4.
   * 
   * @param port String representation of an user port
   * @return boolean about the acceptance of the String parameter
   * @author hendiehl
   */
  private boolean checkPortString(String port) { // not implemented yet
    System.out.println("String match port " + port.matches("^\\d{4,5}$"));
    return port.matches("^\\d{4,5}$");
  }

  /**
   * Method to activate auto search after a client changed to own port once.
   * 
   * @param port String representation of 0 for identifying
   * @return about the acceptance of the String parameter
   * @author hendiehl
   */
  private boolean checkAutoSearch(String port) {
    System.out.println("String match 0 " + port.matches("^0$"));
    return port.matches("^0$");
  }

  /**
   * Method which handles the actionEvent of the backButton and calls the openScreen method with the Menu.fxml and shut down a running client protocol.
   * @author hendiehl
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
  /**
   * Method to change the scene to Menu.fxml for network options.
   * @author hendiehl 
   */
  public void openMenu() {
    Platform.runLater(() -> {
      try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/Menu.fxml"));
        Parent root = loader.load();
        /*
        Stage stage = (Stage) this.backButton.getScene().getWindow();
        stage.setScene(new Scene(root, this.root.getScene().getWidth(), this.root.getScene().getWidth()));
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
   * Initialize method of javaFx in which the joinProtokol automatically search for an game Lobby.
   * in an local Network. If an protocol is generated and found a server informations will be shown.
   * @author hendiehl
   */
  @FXML
  private void initialize() {
    this.loadBackground();
    System.out.println("GAME FINDER : Activate auto search");
    clientProtocol = new LobbyClientProtocol(this);
    clientProtocol.start();
    this.useOwnPort.setDisable(false);
  }
  /**
   * Method which will be called after a lobby is found and connected successfully, the join button will then be enabled.
   * @author hendiehl
   */
  public void connectSucessful() {
    Platform.runLater(() -> {
      this.joinBtn.setDisable(false);
    });
  }
  /**
   * Method which will be called when no lobby is founded, the player will be informed that no lobby connection is created.
   * @author hendiehl
   */
  public void connectNotSucessful() {
    Platform.runLater(() -> {
      this.statusLabel.setText("No game at standart port");
      //this.useOwnPort.setDisable(false); // activate or not
    });
  }

  /**
   * Method which handles the useOwnPort ActionEvents and activates or deactivates the controls for an user input.
   * @author hendiehl
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
   * Method which handles the searchButton ActionEvent and try to create an instances of an client protocol
   * with an specific port given by the user. The input is controlled and checked.
   * A valid port has to be a port number in range or the 0 for standard port.
   * The player is informed about the success.
   * @author hendiehl
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
   * Method to shutdown the client protocol.
   * @author hendiehl
   */
  @Override
  public void shutdown() {
    System.out.println("GAME LOBBY : Shutdown");
    if (this.clientProtocol != null) {
      this.clientProtocol.shutdownProtocol(true);
    }

  }

  /**
   * Method to show port informations of an successful connections on the screen.
   * 
   * @param port specific port given by the user or one of the standard ports
   * @author hendiehl
   */
  public void setPortInformation(int port) {
    Platform.runLater(() -> {
      this.portBox.setSelected(true);
      this.portBox.setText("Port : " + port);
    });
  }

  /**
   * Method to navigate from the GameFinder screen to the GameLobby screen as a client, which sets also the client protocol for the lobby controller.
   * @author hendiehl
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
     //   Stage stage = (Stage) this.joinBtn.getScene().getWindow();
        /*
        stage.setScene(new Scene(root, this.root.getScene().getWidth(), this.root.getScene().getHeight()));
        stage.setMinWidth(900);
        stage.setMinHeight(800);
        stage.setOnHidden(e -> {
          lobbyController.shutdown();
        });
        */
        ScrabbleApp.getStage().setOnHidden(e -> { 
          lobbyController.shutdown(); 
          });
        ScrabbleApp.getScene().getStylesheets().clear();
        ScrabbleApp.getScene().setRoot(root);
        if(!ScrabbleApp.getStage().isFullScreen()) {
          ScrabbleApp.getStage().setMinWidth(900);
          ScrabbleApp.getStage().setMinHeight(800);
        }

      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });
  }

  /**
   * Method to set the background image.
   * @author hendiehl
   */
  private void loadBackground() {
    this.background.setImage(new Image(getClass().getResourceAsStream("img/GameFinder.jpg")));
  }
}
