package scrabble;

import com.jfoenix.controls.JFXBadge;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController {

    // Some reminders:
    // - @FXML annotation on a member declares that the FXML loader can access the member even if it is private.
    // - Only one controller is allowed per FXML document (must be specified on the root element).
    // - If the name of an accessible instance variable matches the fx:id attribute of an element,
    // the object reference from FXML is automatically copied into the controller instance variable.

    @FXML
    // List of currently proposed Letters
    private List<Button> letters;

    @FXML
    // The location of the FXML document
    private URL location;

    @FXML
    private ResourceBundle resources;

    // Default constructor
    public MainController() {}

    @FXML
    // The FXML loader will call the initialize() method after the loading of the FXML document is complete
    private void initialize () {

//        for (int i=0; i<letters.size(); i++) {
//            Button ltr = letters.get(i);
//
//        }

    }
}
