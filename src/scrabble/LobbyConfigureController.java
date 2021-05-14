package scrabble;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import scrabble.network.LobbyServer;

public class LobbyConfigureController {
  /**
   * Controller of the configure screen of a network lobby only accessible by an host. Is used to
   * set up specific options of the lobby like the wanted port, new game field extra points or a new
   * dictionary.
   * 
   * @author hendiehl
   */
  @FXML
  private JFXRadioButton autoPort;
  @FXML
  private JFXButton accept;
  @FXML
  private JFXButton cancel;
  @FXML
  private JFXTextField port;
  @FXML
  private Label portInfo;
  @FXML
  private Label current;
  @FXML
  private Label info;
  @FXML
  private Label warning;
  @FXML
  private JFXRadioButton dictionary;
  @FXML
  private JFXRadioButton multiplier;
  @FXML
  private Label dictionaryLabel;
  @FXML
  private Label multiplierLabel;

  private boolean autoSet;
  private int runningPort;
  private LobbyServer server;
  private String seperator;
  private GameLobbyController corresponding;
  private String multiplierNorm = "";

  /**
   * Constructor with the needed informations about the lobby controller for screen access and the
   * actual server of the lobby.
   * 
   * @param server actual corresponding server of the lobby.
   * @param corresponding controller of the lobby screen.
   * @author hendiehl
   */
  public LobbyConfigureController(LobbyServer server, GameLobbyController corresponding) {
    autoSet = server.portIsAutoSet();
    runningPort = server.getRunningPort();
    this.server = server;
    this.seperator = System.lineSeparator();
    this.corresponding = corresponding;
  }

  /**
   * Initialize method of JavaFx which sets the port controls in dependence of the present selection
   * of the server port.
   * 
   * @author hendiehl
   */
  @FXML
  private void initialize() {
    if (this.autoSet) { // Server runs on standard port
      this.current.setText("Current port : " + this.runningPort);
      this.autoPort.setSelected(true);
      this.info.setVisible(false);
      this.port.setVisible(false);
      this.portInfo.setVisible(false);
    } else {
      this.autoPort.setSelected(false);
      this.current.setText("Current Port : " + this.runningPort);
      this.info.setVisible(false);
      this.port.setVisible(false);
      this.portInfo.setVisible(false);
    }
    this.warning.setText("Warning : Pressing Accept will" + this.seperator
        + "start an new server on a other port." + this.seperator
        + "This means every player will be" + this.seperator + "kicked from the lobby.");
    this.warning.setVisible(false);
    // Also add a warning if the Running thread is 0 => Problem
  }

  /**
   * Method which handles the ActionEvent of the cancel button in reason to close the configure
   * screen.
   * 
   * @author hendiehl
   */
  @FXML
  private void cancelButtonAction() {
    this.close();
  }

  /**
   * Method to show information about the actual port choosing.
   * 
   * @param s message wants to be shown on the screen
   * @author hendiehl
   */
  private void setPortInfo(String s) {
    this.portInfo.setText(s);
  }

  /**
   * Method which handles the ActionEvent of the accept button. In dependence of the option chosen
   * by the host a new server will be started or game specific configures will be saved for the
   * game.
   * 
   * @author hendiehl
   */
  @FXML
  private void acceptButtonAction() {
    if (this.autoSet) {
      System.out.println("CONFIGURE : Change from autoSet to ownPort");
      String port = this.port.getText();
      if (this.checkPortString(port)) { // port is a number
        int number = Integer.valueOf(port);
        if (number <= 65535) { // port is a number in range
          System.out.println("CONFIGURE : Port accepted");
          // this.server.shutdown(); // shutdown old Server
          // Perhaps load first the new one, if there isn't a connection the old one is still closed
          try {
            // Problem with the Lobby
            LobbyServer newOne = new LobbyServer(this.corresponding, number);
            System.out.println("CONFIGURE : New Server created");
            newOne.start();
            System.out.println("CONFIGURE : Prepare to shut down old one");
            this.server.shutdown(); // shutdown Old
            System.out.println("CONFIGURE : Replace Server");
            // this.corresponding.resetProfileVisibility(); //reset all Profiles
            this.corresponding.setNewServer(newOne); // is the Problem the Player
                                                     // ??????????????????????????
            // this.server = newOne;
            this.close();
          } catch (BindException e) {
            this.setPortInfo("Sorry this port isnt available");
            this.portInfo.setTextFill(Color.RED);
            e.printStackTrace();
          } catch (ConnectException e) {
            this.setPortInfo("Sorry this port isnt available");
            this.portInfo.setTextFill(Color.RED);
            e.printStackTrace();
          } catch (IOException e) {
            this.setPortInfo("Sorry this port isnt available");
            this.portInfo.setTextFill(Color.RED);
            e.printStackTrace();
          }
        } else { // port isn't a number in range
          this.setPortInfo("Not a valid port");
          this.portInfo.setTextFill(Color.RED);
        }
      } else { // port isn't a number
        this.setPortInfo("Not a valid port");
        this.portInfo.setTextFill(Color.RED);
      }
    } else {
      System.out.println("Configure Change from ownPort to autoSet");
      LobbyServer newOne = new LobbyServer(this.corresponding);
      System.out.println("CONFIGURE : New Server created");
      newOne.start();
      System.out.println("CONFIGURE : Prepare to shut down old one");
      this.server.shutdown(); // shutdown Old
      System.out.println("CONFIGURE : Replace Server");
      this.corresponding.setNewServer(newOne);
      this.close();
    }
  }

  /**
   * Method to close the window.
   * 
   * @author hendiehl
   */
  private void close() {
    Stage stage = (Stage) cancel.getScene().getWindow();
    stage.close();
  }

  /**
   * Method which handles the action event of the port selection and enables the accept button if a
   * other option is chosen than the present option.
   * 
   * @author hendiehl
   */
  @FXML
  private void autoPortAction() {
    if (this.autoPort.isSelected()) {
      if (this.autoSet) { // Selected and also changed to it again, same case do nothing
        System.out.println("CONFIGURE : Was autoSet and is yet set on autoset");
        this.accept.setDisable(true);
        this.notShowWarning();
        this.info.setVisible(false);
        this.port.setVisible(false);
        this.portInfo.setVisible(false);
      } else { // was a own port server and is yet set on AutoSet
        System.out.println("CONFIGURE : Was ownPort and is yet set on autoset");
        this.accept.setDisable(false);
        this.showWarning();
      }
    } else {
      if (this.autoSet) { // was a autoPort Server and is yet set on ownPort
        System.out.println("Configure : Was autoPort and is yet set on ownPort");
        this.info.setVisible(true);
        this.port.setVisible(true);
        this.portInfo.setVisible(true);
        this.accept.setDisable(false);
        this.showWarning();
        this.setPortInfo("Please Type in a valid port " + this.seperator + "to change the server");
      } else { // was a own port server and is set on it again => same case do nothing
        System.out.println("CONFIGURE : Was ownPort and is set on ownPort");
        this.accept.setDisable(true);
        this.notShowWarning();
      }
    }
  }

  /**
   * Method to make the warning label visible on the screen.
   * 
   * @author hendiehl
   */
  private void showWarning() {
    this.warning.setVisible(true);
  }

  /**
   * Method to make the warning label not visible on the screen.
   * 
   * @author hendiehl
   */
  private void notShowWarning() {
    this.warning.setVisible(false);
  }

  /**
   * Method to check the user input for a specific port. The input will be accepted if it is a
   * number in range.
   * 
   * @param s input want to be checked
   * @return boolean condition about the string acceptance
   * @author hendiehl
   */
  private boolean checkPortString(String s) {
    return s.matches("^\\d{3,5}$");
  }

  /**
   * Method to show a FileChooser if a host want to use a own multiplier specification for the game
   * field
   * 
   * @author hendiehl
   */
  private void showMultiplierChooser() {
    FileChooser multiChooser = new FileChooser();
    multiChooser.setTitle("Choose a muliplier file");
    multiChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    multiChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Txt", "*.txt"));
    File file = multiChooser.showOpenDialog(this.accept.getScene().getWindow());
    try {
      BufferedReader reader = new BufferedReader(new FileReader(file));
      StringBuffer buffer = new StringBuffer();
      String line = null;
      while ((line = reader.readLine()) != null) {
        buffer.append(line + this.seperator);
      }
      reader.close();
      System.out.println(buffer.toString());
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @FXML
  private void multiplierAction() {
    if (this.multiplier.isSelected()) {
      this.showMultiplierChooser();
    }
  }
}
