package scrabble;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import java.util.ResourceBundle;
import scrabble.dbhandler.DBInformation;
import scrabble.dbhandler.DBUpdate;
import scrabble.dbhandler.Database;
import scrabble.model.HumanPlayer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import scrabble.model.Profile;

/**
 * The controller class for the Statistics.fxml screen.
 * 
 * @author mraucher
 */

public class StatisticsController implements Initializable {

  @FXML
  private JFXTextArea txtName;
  @FXML
  private JFXTextArea txtWon;
  @FXML
  private JFXTextArea txtLost;
  @FXML
  private JFXTextArea txtGamesPlayed;
  @FXML
  private JFXTextArea txtRatio;
  @FXML
  private PieChart pieChart;
  @FXML
  private JFXButton backBtn;
  @FXML
  private BorderPane root;
  @FXML
  private ImageView img;

  private HumanPlayer plyr;


  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    this.plyr = Profile.getPlayer();

    List<String> val = DBInformation.getPlayerStatistic(plyr);
    int gamesPlayed = Integer.valueOf(val.get(1)) + Integer.valueOf(val.get(2));

    this.txtName.setText(plyr.getName());
    this.txtGamesPlayed.setText("How many games have been played? " + gamesPlayed);
    this.txtWon.setText("Games won: " + plyr.getGamesWon());
    this.txtLost.setText("Games lost: " + plyr.getGamesLost());
    this.txtRatio.setText("Win/Lose Ratio: " + plyr.getWinRate() + "");

    this.img.setImage(new Image(getClass().getResourceAsStream("/img/" + plyr.getImage())));

    /*
     * try { String fileSeperator = System.getProperty("file.separator"); this.img.setImage(new
     * Image(new FileInputStream("." + fileSeperator + "src" + fileSeperator + "main" +
     * fileSeperator + "resources" + fileSeperator + "img" + fileSeperator + plyr.getImage()))); }
     * catch (FileNotFoundException e) { e.printStackTrace(); }
     */


    // pie chart with wins vs. losses will only be shown if at least one game was played.
    if (plyr.getGamesLost() + plyr.getGamesWon() > 0) {
      ObservableList<PieChart.Data> pieChartData =
          FXCollections.observableArrayList(new PieChart.Data("Lost", plyr.getGamesLost()),
              new PieChart.Data("Win", plyr.getGamesWon()));

      pieChart.setData(pieChartData);
    }
  }

  /**
   * Listener for the update button. The players name will be updated in the database.
   * 
   * @param event the ActionEvent if the delete profile button is clicked
   * @author mraucher
   */
  @FXML
  public void updateBtnOnAction(ActionEvent event) {
    String newName = txtName.getText();
    DBUpdate.updatePlayerName(plyr, newName);
  }


  /**
   * Listener for the delete profile button. The current player will be deleted from database and
   * the user will be routed to the register screen.
   * 
   * @param event the ActionEvent if the delete profile button is clicked
   * @author mraucher
   */
  @FXML
  public void delBtnOnAction(ActionEvent event) {
    int id = DBInformation.getSettingsId(plyr);
    Database.removePlayer(plyr);
    Database.removeSettings(id);
    try {

      Parent root = FXMLLoader.load(getClass().getResource("/fxml/ChooseProfileScene.fxml"));
      /*
       * Stage stage = (Stage) this.backBtn.getScene().getWindow(); Scene scene = new Scene(root,
       * this.root.getScene().getWidth(), this.root.getScene().getHeight());
       * scene.getStylesheets().add(getClass().getResource("css/changeProfile.css").toExternalForm()
       * ); stage.setScene(scene);
       */
      ScrabbleApp.getScene().getStylesheets().clear();
      ScrabbleApp.getScene().getStylesheets()
          .add(getClass().getResource("/css/changeProfile.css").toExternalForm());
      ScrabbleApp.getScene().setRoot(root);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Listener for the back button. The user will be routed back to the main page.
   * 
   * @param event the ActionEvent if the back button is clicked
   * @author mraucher
   */
  @FXML
  public void backBtnOnAction(ActionEvent event) {
    try {
      Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainPage.fxml"));
      /*
       * Stage stage = (Stage) this.backBtn.getScene().getWindow(); Scene scene = new Scene(root,
       * this.root.getScene().getWidth(), this.root.getScene().getHeight());
       * scene.getStylesheets().add(getClass().getResource("css/mainMenu.css").toExternalForm());
       * stage.setScene(scene);
       */
      ScrabbleApp.getScene().getStylesheets().clear();
      ScrabbleApp.getScene().getStylesheets()
          .add(getClass().getResource("/css/mainMenu.css").toExternalForm());
      ScrabbleApp.getScene().setRoot(root);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
