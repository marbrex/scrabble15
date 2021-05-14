package scrabble;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MenuController {
  /**
   * Controller of the network game menu screen, which have the function to give a player the option to choose
   * between hosting a game or joining a game.
   * 
   * @author hendiehl
   */
  // gui
  @FXML
  private JFXButton joinGameButton;
  @FXML
  private JFXButton hostGameButton;
  @FXML
  private JFXButton backButton;
  @FXML
  private StackPane root;
  @FXML
  private ImageView background;
  /**
   * Initialize method of JavaFX which loads the background
   * @author hendiehl
   */
  @FXML
  public void initialize() {
    this.loadBackground();
  }
  /**
   * Method which handles the ActionEvent of the back button in reason to return to the main screen.
   * @author hendiehl
   */
  @FXML
  private void backButtonAction() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/MainPage.fxml"));
      Parent root = loader.load();
      Stage stage = (Stage) this.backButton.getScene().getWindow();
      Scene scene =
          new Scene(root, this.root.getScene().getWidth(), this.root.getScene().getHeight());
      scene.getStylesheets().add(getClass().getResource("css/mainMenu.css").toExternalForm());
      stage.setScene(scene);
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

  }


  /**
   * Method to react to the host game button and open up the GameLobby screen with specific
   * constructor and setting up a method if the window is closed.
   * @author hendiehl
   */
  @FXML
  private void hostGameAction() {
    try {/*
          * GameLobbyController lobbyController; FXMLLoader loader = new
          * FXMLLoader(getClass().getResource("GameLobby.fxml")); loader.setControllerFactory(c -> {
          * return new GameLobbyController(true); }); Stage stage = (Stage)
          * this.joinGameButton.getScene().getWindow(); Scene scene = new Scene(loader.load());
          * lobbyController = loader.<GameLobbyController>getController(); stage.setScene(scene);
          * stage.setTitle("Scrabble"); stage.setHeight(600); stage.setWidth(800);
          * stage.setResizable(false); stage.setOnHidden(e -> {lobbyController.shutdown();}); //what
          * if gamescreen is shown stage.show(); //Set up Host
          */
      FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/GameLobby.fxml"));
      loader.setControllerFactory(c -> {
        return new GameLobbyController(true);
      });
      Parent root = loader.load();
      GameLobbyController lobbyController = loader.<GameLobbyController>getController();
      Stage stage = (Stage) this.hostGameButton.getScene().getWindow();
      stage.setScene(new Scene(root, 900, 750)); // before 700, but need of space
      stage.setOnHidden(e -> {
        lobbyController.shutdown();
      });
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to react to the join game button and open the GameFinder screen and setting up an method
   * if the window is closed.
   * @author hendiehl
   */
  @FXML
  private void joinGameAction() {
    try {/*
          * GameFinderController finderController; FXMLLoader loader = new
          * FXMLLoader(getClass().getResource("GameFinder.fxml")); Stage stage = (Stage)
          * this.joinGameButton.getScene().getWindow(); Scene scene = new Scene(loader.load());
          * finderController = loader.<GameFinderController>getController(); stage.setScene(scene);
          * stage.setTitle("Scrabble"); stage.setHeight(600); stage.setWidth(800);
          * stage.setResizable(false); stage.setOnHidden(e -> {finderController.shutdown();});
          * stage.show();
          */
      FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/GameFinder.fxml"));
      Parent root = loader.load();
      GameFinderController finderController = loader.<GameFinderController>getController();
      Stage stage = (Stage) this.joinGameButton.getScene().getWindow();
      stage.setScene(new Scene(root, 900, 700));
      stage.setOnHidden(e -> {
        finderController.shutdown();
      });
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to set the background image
   * @author hendiehl
   */
  private void loadBackground() {
    this.background.setImage(new Image(getClass().getResourceAsStream("img/Menu.jpg")));
  }
}
