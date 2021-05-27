package scrabble;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scrabble.dbhandler.DBInformation;
import scrabble.dbhandler.DBUpdate;
import scrabble.dbhandler.Database;
import scrabble.model.HumanPlayer;

/**
 * Tests for the Register and Statistic Screens functionalities against test DB
 * 
 * 
 *
 */
public class RegStaTest {


  private static Connection connection;
  private static HumanPlayer john;
  private static HumanPlayer jac;

  /**
   * Setting the connection before testing.
   */
  @BeforeAll
  public static void setDatabase() {
    Database.connectToDB();
    Database.setConnection("Scrabble15Test.db");
    connection = Database.getConnection();
    Database.createTables();
    john = new HumanPlayer();
    jac = new HumanPlayer();
    john.setName("John");
    jac.setName("Jac");
  }

  /**
   * Filling tables with default content and prepared names.
   * 
   * 
   */
  @BeforeEach
  public void fillingPlayerTable() {
    Database.fillTables(50, "John", 3);
    Database.fillTables(51, "Jac", 2);
    john = DBInformation.loadProfile(0);
    jac = DBInformation.loadProfile(1);
  }


  /**
   * Tests, if the player will be deleted successfully from DB
   * 
   * @throws SQLException on a database access error
   */
  @Test
  public void removePlayerSuccessfully() throws SQLException {
    Database.removePlayer(jac);
    // checking the database with plain SQL and not with class DBInformation so that possible bugs
    // in DBInformation don't effect this test
    Statement stmt = connection.createStatement();
    ResultSet resultSet =
        stmt.executeQuery("SELECT Count(*) FROM PLAYERS WHERE Name ='" + jac.getName() + "'");
    assertEquals(0, resultSet.getInt(1));

  }

  /**
   * Tests, if a player can update its name successfully in DB
   * 
   * @throws SQLException on a database access error
   */
  @Test
  public void updatePlayerNameSuccessfully() throws SQLException {
    DBUpdate.updatePlayerName(jac, "JacNew");
    // checking the database with plain SQL and not with class DBInformation so that possible bugs
    // in DBInformation don't effect this test
    Statement stmt = connection.createStatement();
    ResultSet resultSet = stmt.executeQuery("SELECT Count(*) FROM PLAYERS WHERE Name ='JacNew'");
    assertEquals(1, resultSet.getInt(1)); // exactly one Player with name="JacNew" in DB

  }

  /**
   * Tests the update of player name, if new name is already in DB. No update will be done.
   * Expected: There will only be zero or one player with the new name.
   * 
   * @throws SQLException on a database access error
   */
  @Test
  public void updatePlayerNameFailsNameAlreadyInDB() throws SQLException {
    DBUpdate.updatePlayerName(jac, "John");
    // checking the database with plain SQL and not with class DBInformation so that possible bugs
    // in DBInformation don't effect this test
    Statement stmt = connection.createStatement();
    ResultSet resultSet = stmt.executeQuery("SELECT Count(*) FROM PLAYERS WHERE Name ='John'");
    Assertions.assertThat(resultSet.getInt(1) <= 1); // max. one player with name "John" in DB

  }

  /**
   * Tests, if the player statistics will be returned from DB successfully
   */
  @Test
  public void getPlayerStatisticReturnsCorrectStatistics() {
    List<String> playerStatistic = DBInformation.getPlayerStatistic(jac);
    assertEquals(jac.getName(), playerStatistic.get(0));
    assertEquals(jac.getGamesWon(), Integer.valueOf(playerStatistic.get(1)));
    assertEquals(jac.getGamesLost(), Integer.valueOf(playerStatistic.get(2)));
    assertEquals(jac.getWinRate(), Double.valueOf(playerStatistic.get(3)));
  }

  /**
   * Tests, if a new player can be registered into DB successfully
   * 
   * @throws SQLException on a database access error
   */
  @Test
  public void registerNewPlayerInDatabaseSuccessfully() throws SQLException {
    HumanPlayer testPlayer = new HumanPlayer();
    testPlayer.setName("testPlayer");
    Database.fillTables(2, "testPlayer", 2);
    // checking the database with plain SQL and not with class DBInformation so that possible bugs
    // in DBInformation don't effect this test
    Statement stmt = connection.createStatement();
    ResultSet resultSet = stmt.executeQuery("SELECT * FROM PLAYERS WHERE Name ='testPlayer'");
    resultSet.next();
    assertEquals(2, Integer.valueOf(resultSet.getString("Image"))); // image index 2 is stored in DB
    assertEquals(2, Integer.valueOf(resultSet.getString("SettingsId"))); // settingsId 2 is stored
                                                                         // in DB
    assertFalse(resultSet.next()); // no more rows -> i. e. exactly one player with this name

    // cleaning DB after test
    Database.removePlayer(testPlayer);
    Database.removeSettings(2);
  }



  /**
   * Removing players from previous methods.
   * 
   * 
   */
  @AfterEach
  public void removePlayers() {
    Database.removePlayer(john);
    Database.removePlayer(jac);
    Database.removeSettings(50);
    Database.removeSettings(51);
  }

  /**
   * Closing the connection after all tests.
   * 
   * 
   */
  @AfterAll
  public static void closingConnection() {
    Database.disconnectDB();
    File file = new File(System.getProperty("user.home") + System.getProperty("file.separator")
        + ".Scrabble" + System.getProperty("file.separator") + "Scrabble15Test.db");
    if (file.exists()) {
      file.delete();
    }
  }

}


