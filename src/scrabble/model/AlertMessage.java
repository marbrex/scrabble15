package scrabble.model;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


/**
 * This class provides methods for showing different types of alerts
 * 
 * @author mraucher
 */
public class AlertMessage {

  /**
   * shows an Alert with AlertType.ERROR and waits for user response
   * 
   * @param message to be shown in the alert box
   * @auhtor mraucher
   */
  public static void showErrorMessage(String message) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setContentText(message);
    alert.showAndWait();
  }

  /**
   * shows an Alert with AlertType.INFORMATION and waits for user response
   * 
   * @param message to be shown in the alert box
   * @author mraucher
   */
  public static void showInfoMessage(String message) {
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setContentText(message);
    alert.showAndWait();
  }

  /**
   * shows an Alert with AlertType.CONFIRMATION and waits for user response
   * 
   * @param message to be shown in the alert box
   * @author mraucher
   */
  public static void showConfirmationMessage(String message) {
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setContentText(message);
    alert.showAndWait();
  }

  /**
   * shows an Alert with AlertType.WARNING and waits for user response
   * 
   * @param message to be shown in the alert box
   * @author mraucher
   */
  public static void showWarningMessage(String message) {
    Alert alert = new Alert(AlertType.WARNING);
    alert.setContentText(message);
    alert.showAndWait();
  }
}


