/**
 * The Main Controller linked with "interface.fxml" file.
 * Main function is to initialize and launch the application.
 *
 * @author      Eldar Kasmamytov
 */

package scrabble;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ScrabbleApp extends Application {

  final private double windowWidth = 1075;
  final private double windowHeight = 905;

  @Override
  public void start(Stage primaryStage) throws Exception {
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource("fxml/interface.fxml"));
    Parent root = loader.load();

    Scene scene = new Scene(root, windowWidth, windowHeight);
    scene.getStylesheets().add(ScrabbleApp.class.getResource("css/style.css").toExternalForm());

    Image appIcon = new Image(ScrabbleApp.class.getResourceAsStream("img/scrabble-icon-dark.png"));

    primaryStage.setTitle("Scrabble");
    primaryStage.getIcons().add(appIcon);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
