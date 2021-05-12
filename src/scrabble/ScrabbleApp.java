package scrabble;

import java.io.File;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import scrabble.dbhandler.DBInformation;
import scrabble.dbhandler.Database;

/**
 * scrabble.ScrabbleApp class is the main class and the starting point of the application. Main
 * function is to initialize and launch the application.
 *
 * @author Eldar Kasmamytov
 */
public class ScrabbleApp extends Application {

  final private double windowWidth = 900;
  final private double windowHeight = 700;

  final private double windowMinWidth = 620;
  final private double windowMinHeight = 500;

  @Override
  public void start(Stage primaryStage) throws Exception {
    FXMLLoader loader = new FXMLLoader();
    if (DBInformation.getProfileSize() != 0) {
      loader.setLocation(getClass().getResource("fxml/ChooseProfileScene.fxml"));
    } else {
      loader.setLocation(getClass().getResource("fxml/Register.fxml"));
    }
    Parent root = loader.load();

    Scene scene = new Scene(root, windowWidth, windowHeight);
    scene.getStylesheets().add(getClass().getResource("css/changeProfile.css").toExternalForm());

    Image appIcon = new Image(getClass().getResourceAsStream("img/scrabble-icon-dark.png"));

    primaryStage.setMinWidth(windowMinWidth);
    primaryStage.setMinHeight(windowMinHeight);
    primaryStage.setTitle("Scrabble");
    primaryStage.getIcons().add(appIcon);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    Database.connectToDB();
    Database.createTables();
    launch(args);
  }
}
