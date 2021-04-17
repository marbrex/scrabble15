package scrabble;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;

import scrabble.dbhandler.DBInformation;
import scrabble.dbhandler.Database;
import scrabble.model.HumanPlayer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

// @author mraucher

public class StatisticsController {


  Database db;
  DBInformation dbi;

  @FXML
  private ImageView img;
  @FXML
  private JFXTextArea txtName;
  @FXML
  private JFXTextArea txtWon;
  @FXML
  private JFXTextArea txtLost;
  @FXML
  private JFXTextArea txtGamesPlayed;
  @FXML
  private JFXTextArea txtLastGame;
  @FXML
  private JFXTextArea txtRatio;
  @FXML
  private PieChart pieChart;
  @FXML
  private JFXButton backBtn;
  @FXML
  private StackPane root;

  private HumanPlayer plyr;

  public void init(HumanPlayer plyr) throws FileNotFoundException {
    /*
     * this.plyr = plyr;
     * 
     * db = new Database(); dbi = new DBInformation();
     * 
     * List<String> val = dbi.getPlayerStat(plyr.getId());
     * 
     * this.txtName.setText(plyr.getName());
     * this.txtGamesPlayed.setText("How many games have been played? "+val.get(0));
     * this.txtLastGame.setText("When the last game was played? "+val.get(1));
     * this.txtWon.setText("Games won: "+plyr.getGamesWon());
     * this.txtLost.setText("Games lost: "+plyr.getGamesLost());
     * this.txtRatio.setText("Win/Lose Ratio: "+plyr.getWinRate()+""); this.img.setImage(new
     * Image(new FileInputStream("resources/images/"+plyr.getUser()+".png")));
     * //@../../../resources/images/ProfilePicture.png
     * 
     * int win = (int) Math.round(plyr.getWinRate()); int lost = 100-win;
     * 
     * ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList( new
     * PieChart.Data("Lost", lost), new PieChart.Data("Win", win));
     * 
     * pieChart.setData(pieChartData);
     */
    // out commented for compiling purpose --> need for DB update in Future

  }

  @FXML
  public void updateBtnOnAction(ActionEvent event) {
    /*
     * String name = txtName.getText();
     * 
     * new Database().updatePlayerName(plyr, name);
     */
    // out commented for compiling purpose --> need for DB update in Future
  }



  @FXML
  public void delBtnOnAction(ActionEvent event) {
    /*
     * db.deleteProfile(plyr);
     * 
     * try {
     * 
     * Parent root=FXMLLoader.load((getClass().getResource("fxml/signin_signup.fxml")));
     * 
     * Stage stage = new Stage(); stage.initStyle(StageStyle.UNDECORATED); stage.setScene(new
     * Scene(root, 800, 600)); stage.show();
     * 
     * 
     * Stage stage1 = (Stage) txtName.getScene().getWindow(); stage1.close();
     * 
     * } catch (IOException e) { e.printStackTrace(); }
     */
    // out commented for compiling purpose --> need for DB update in Future
  }

  @FXML
  public void backBtnOnAction(ActionEvent event) {
    /*
     * FXMLLoader loader = new FXMLLoader( getClass().getResource( "fxml/home.fxml" ) );
     * 
     * Stage stage = new Stage(); try { stage.setScene( new Scene(loader.load(), 800, 600) ); }
     * catch (IOException e) { e.printStackTrace(); }
     * 
     * HomeController controller = loader.getController(); controller.init(plyr);
     * 
     * Stage stage1 = (Stage) txtName.getScene().getWindow(); stage1.close();
     * 
     * stage.show();
     */
    // out commented for compiling purpose --> need for DB update in Future
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/MainPage.fxml"));
      Parent root = loader.load();
      Stage stage = (Stage) this.backBtn.getScene().getWindow();
      Scene scene =
          new Scene(root, this.root.getScene().getWidth(), this.root.getScene().getHeight());
      scene.getStylesheets().add(getClass().getResource("css/mainMenu.css").toExternalForm());
      stage.setScene(scene);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
