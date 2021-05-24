package scrabble;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class GameMain extends Application {

  final private double windowWidth = 900;
  final private double windowHeight = 700;

  final private double windowMinWidth = 620;
  final private double windowMinHeight = 500;

  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource("/fxml/interface.fxml"));
    Parent root = loader.load();

    Scene scene = new Scene(root, windowWidth, windowHeight);
    scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

    Image appIcon = new Image(getClass().getResourceAsStream("/img/scrabble-icon-dark.png"));

    stage.setMinWidth(windowMinWidth);
    stage.setMinHeight(windowMinHeight);
    stage.setTitle("Scrabble");
    stage.getIcons().add(appIcon);
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
