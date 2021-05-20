package scrabble;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import scrabble.dbhandler.DBInformation;
import scrabble.dbhandler.Database;

/**
 * scrabble.ScrabbleApp class is the main class and the starting point of the application. Main
 * function is to initialize and launch the application.
 *
 * @author ekasmamy
 */
public class ScrabbleApp extends Application {

  final private double windowWidth = 900;
  final private double windowHeight = 700;

  final private double windowMinWidth = 620;
  final private double windowMinHeight = 500;
  
  private static Scene scene;
  private static Stage stage;

  /**
   * Choosing Start-Resource and opening Window.
   * 
   * @author ekasmamy
   */
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
//    primaryStage.setFullScreen(true);
    primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
    primaryStage.show();
    setScene(scene);
    setStage(primaryStage);
  }
  
  public void setScene(Scene scene) {
    ScrabbleApp.scene = scene;
  }
  
  public static Scene getScene() {
    return scene;
  }
  
  public void setStage(Stage stage) {
    ScrabbleApp.stage = stage;
  }
  
  public static Stage getStage() {
    return stage;
  }

  /**
   * Launching the application in this class.
   * 
   * @param args
   * @author ekasmamy
   * @author skeskinc
   */
  public static void main(String[] args) {
    Database.connectToDB();
    Database.createTables();
    launch(args);
  }
}
