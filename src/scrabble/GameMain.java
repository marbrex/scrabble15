package scrabble;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * <h1>scrabble.ScrabbleApp</h1>
 *
 * <p>
 * Class is the main class and the starting point of the application. Main function is to initialize
 * and launch the application.
 * </p>
 *
 * @author Eldar Kasmamytov
 */
public class GameMain extends Application {

  final private double windowWidth = 900;
  final private double windowHeight = 700;

  final private double windowMinWidth = 620;
  final private double windowMinHeight = 500;

  @Override
  public void start(Stage primaryStage) throws Exception {
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource("fxml/interface.fxml"));
    Parent root = loader.load();

    Scene scene = new Scene(root, windowWidth, windowHeight);
    scene.getStylesheets().add(getClass().getResource("css/style.css").toExternalForm());

    Image appIcon = new Image(getClass().getResourceAsStream("img/scrabble-icon-dark.png"));

    primaryStage.setMinWidth(windowMinWidth);
    primaryStage.setMinHeight(windowMinHeight);
    primaryStage.setTitle("Scrabble");
    primaryStage.getIcons().add(appIcon);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
