package scrabble;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/** Test class to launch Settings
 *
 * @author astarche
 * */
public class Settings extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("fxml/Settings.fxml"));
            Pane root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
