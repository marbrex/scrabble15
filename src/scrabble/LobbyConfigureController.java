package scrabble;

import java.io.IOException;
import java.net.BindException;
import java.net.ConnectException;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import scrabble.network.LobbyServer;

public class LobbyConfigureController {
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

  private boolean autoSet;
  private int runningPort;
  private LobbyServer server;
  private String seperator;
  private GameLobbyController corresponding;

  public LobbyConfigureController(LobbyServer server, GameLobbyController corresponding) {
    autoSet = server.portIsAutoSet();
    runningPort = server.getRunningPort();
    this.server = server;
    this.seperator = System.lineSeparator();
    this.corresponding = corresponding;
  }

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
        + "start an new Server on a other Port." + this.seperator
        + "This means every player will be" + this.seperator + "kicked from the Lobby.");
    this.warning.setVisible(false);
    // Also add a warning if the Running thread is 0 => Problem
  }

  @FXML
  private void cancelButtonAction() {
    this.close();
  }

  private void setPortInfo(String s) {
    this.portInfo.setText(s);
  }

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
            //Problem with the Lobby 
            LobbyServer newOne = new LobbyServer(this.corresponding, number);
            System.out.println("CONFIGURE : New Server created");
            newOne.start();
            System.out.println("CONFIGURE : Prepare to shut down old one");
            this.server.shutdown(); // shutdown Old
            System.out.println("CONFIGURE : Replace Server");
            //this.corresponding.resetProfileVisibility(); //reset all Profiles
            this.corresponding.setNewServer(newOne); //is the Problem the Player ??????????????????????????
            //this.server = newOne;
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
    }
  }

  /**
   * Method to close the window
   */
  private void close() {
    Stage stage = (Stage) cancel.getScene().getWindow();
    stage.close();
  }

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

  private void showWarning() {
    this.warning.setVisible(true);
  }

  private void notShowWarning() {
    this.warning.setVisible(false);
  }

  private boolean checkPortString(String s) {
    return s.matches("^\\d{3,5}$");
  }
}
