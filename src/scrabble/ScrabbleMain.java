package scrabble;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ScrabbleMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("fxml/interface.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1075, 905);
        scene.getStylesheets().add(ScrabbleMain.class.getResource("css/style.css").toExternalForm());

        Image appIcon = new Image(ScrabbleMain.class.getResourceAsStream("img/scrabble-icon-dark.png"));

        primaryStage.setTitle("Scrabble");
        primaryStage.getIcons().add(appIcon);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
