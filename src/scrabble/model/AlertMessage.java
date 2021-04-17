package scrabble.model;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

// @author mraucher

public class AlertMessage {

  public static void showErrorMessage(String message) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setContentText(message);
    alert.showAndWait();
  }

  public static void showInfoMessage(String message) {
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setContentText(message);
    alert.showAndWait();
  }

  public static void showConfirmationMessage(String message) {
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setContentText(message);
    alert.showAndWait();
  }

  public static void showWarningMessage(String message) {
    Alert alert = new Alert(AlertType.WARNING);
    alert.setContentText(message);
    alert.showAndWait();
  }
}
