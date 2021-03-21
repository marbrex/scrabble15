package scrabble;

import com.jfoenix.controls.JFXBadge;
import com.jfoenix.controls.JFXButton;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController {

    // Some reminders:
    // - @FXML annotation on a member declares that the FXML loader can access the member even if it is private.
    // - Only one controller is allowed per FXML document (must be specified on the root element).
    // - If the name of an accessible instance variable matches the fx:id attribute of an element,
    // the object reference from FXML is automatically copied into the controller instance variable.

    @FXML
    private HBox lettersBlock;

//    @FXML
//    // List of currently proposed Letters
//    private ArrayList<JFXButton> letters;

    @FXML
    // Grid of letters
    private GridPane grid;

    @FXML
    private JFXButton okBtn;

    @FXML
    // The location of the FXML document
    private URL location;

    @FXML
    private ResourceBundle resources;

    // Default constructor
    public MainController() {}

    private int gridSize = 825;
    private int cellNumber = 15;
    private int gridPadSize = 5;
    private double cellSize = gridSize/cellNumber - gridPadSize;

    private int lettersNumber = 7;

    @FXML
    // The FXML loader will call the initialize() method after the loading of the FXML document is complete
    private void initialize () {

        grid.setPadding(new Insets(gridPadSize));
        grid.setHgap(gridPadSize);
        grid.setVgap(gridPadSize);

        for (int i=0; i<cellNumber; i++) {
            for (int j=0; j<cellNumber; j++) {
                Rectangle rect = new Rectangle(i, j, cellSize, cellSize);
                rect.setFill(Color.AQUA);
                rect.setStroke(Color.DARKGRAY);
                rect.setArcWidth(10);
                rect.setArcHeight(10);

                InnerShadow is = new InnerShadow();
                is.setColor(Color.DARKGRAY);
                is.setOffsetX(-5);
                is.setOffsetY(5);
                is.setHeight(20);
                is.setWidth(20);

                rect.setEffect(is);

                rect.setOnMouseEntered(event -> rect.setFill(Color.CRIMSON));
                rect.setOnMouseExited(event -> rect.setFill(Color.AQUA));

                GridPane.setConstraints(rect, i, j);

                grid.getChildren().add(rect);
            }
        }

        for (int i=0; i<lettersNumber; i++) {
            Button ltr = new Button("S");
            ltr.getStyleClass().add("letter-btn");
            ltr.setPrefSize(cellSize, cellSize);

            DropShadow ds = new DropShadow();
            ds.setHeight(20);
            ds.setWidth(20);
            ds.setOffsetY(-3);
            ds.setOffsetX(3);
            ds.setColor(Color.GRAY);

            ltr.setEffect(ds);

            ltr.setOnMousePressed(event -> {
                ltr.setStyle("-fx-background-color: GREEN");
                System.out.println("Event.getScene: (" + event.getSceneX() + ", " + event.getSceneY() + ")");
                System.out.println("Event.get: (" + event.getX() + ", " + event.getY() + ")\n");
            });

            ltr.setOnMouseDragged(event -> {
                Bounds boundsInScene = ltr.localToScene(ltr.getBoundsInLocal());
                ltr.setTranslateX(event.getSceneX() - boundsInScene.getMinX());
                ltr.setTranslateY(event.getSceneY() - boundsInScene.getMinY());
                System.out.println("Translate: (" + ltr.getTranslateX() + ", " + ltr.getTranslateY() + ")");
                System.out.println("BoundsInScene: (" + boundsInScene.getMinX() + ", " + boundsInScene.getMinY() + ")\n");
                ltr.setCursor(Cursor.CLOSED_HAND);
            });

            ltr.setOnMouseReleased(event -> {
                ltr.setCursor(Cursor.DEFAULT);
                ltr.setStyle("-fx-background-color: ORANGE");
            });

            ltr.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> ltr.setStyle("-fx-opacity: 0.8"));
            ltr.addEventHandler(MouseEvent.MOUSE_EXITED, event -> ltr.setStyle("-fx-opacity: 1"));

            lettersBlock.getChildren().add(ltr);
        }
    }
}
