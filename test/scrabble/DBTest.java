package scrabble;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import scrabble.dbhandler.DBInformation;
import scrabble.dbhandler.DBUpdate;
import scrabble.dbhandler.Database;
import scrabble.model.HumanPlayer;

/**
 * scrabble.test.DBTest to test some functionalities of the Database
 * 
 * @author skeskinc
 */
public class DBTest {

  private static Connection connection;
  private static HumanPlayer john;
  private static HumanPlayer jac;

  /**
   * Setting the connection before testing.
   * 
   * @author skeskinc
   */
  @BeforeAll
  public static void setDatabase() {
    Database.connectToDb();
    Database.setConnection("Scrabble15Test.db");
    connection = Database.getConnection();
    Database.createTables();
    john = new HumanPlayer();
    jac = new HumanPlayer();
    john.setName("John");
    jac.setName("Jac");
  }

  /**
   * Testing the connection of the Database.
   * 
   * @author skeskinc
   */
  @Test
  public void connectionTest() {
    try {
      assertEquals(!connection.isClosed(), true);
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Testing, if all Tables have been created properly.
   * 
   * @author skeskinc
   */
  @Test
  public void checkTables() {
    try {
      // All tables have to be written in Upper-Case, it is possible that the testing might throw
      // some failures.
      boolean playersExists =
          connection.getMetaData().getTables(null, null, "PLAYERS", null).next();
      boolean settingsExists =
          connection.getMetaData().getTables(null, null, "SETTINGS", null).next();
      boolean statisticsExists =
          connection.getMetaData().getTables(null, null, "STATISTICS", null).next();
      final boolean multiplayerExists =
          connection.getMetaData().getTables(null, null, "MULTIPLAYER", null).next();
      assertEquals(playersExists, true);
      assertEquals(settingsExists, true);
      assertEquals(statisticsExists, true);
      assertEquals(multiplayerExists, false);
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  /**
   * Filling tables with default content and prepared names.
   * 
   * @author skeskinc
   */
  @BeforeEach
  public void fillingPlayerTable() {
    Database.fillTables(50, "John", 3);
    Database.fillTables(51, "Jac", 2);
    john = DBInformation.loadProfile(0);
    jac = DBInformation.loadProfile(1);
  }

  /**
   * Checking after filling the Tables, if names and settings_id exists.
   * 
   * @author skeskinc
   */
  @Test
  public void checkPrimarykey() {
    assertEquals(DBInformation.containsName("John"), true);
    assertEquals(DBInformation.containsIdentification(50), true);
    assertEquals(DBInformation.containsName("Jack"), false);
    assertEquals(DBInformation.containsIdentification(51), true);
  }
  
  /**
   * Checking the size of the profiles.
   * 
   * @author skeskinc
   */
  @Test
  public void checkProfileSize() {
    assertEquals(DBInformation.getProfileSize(), 2);
    assertEquals(DBInformation.getProfileSize() == 3, false);
  }
  
  /**
   * Testing, if amount of games won/lost are saved and updated correctly.
   * 
   * @author skeskinc
   */
  @Test
  public void checkContent() {
    // Testing amount of games won of john
    assertEquals(Integer.parseInt(DBInformation.getPlayerStatistic(john).get(1)) == 0, true);
    assertEquals(Integer.parseInt(DBInformation.getPlayerStatistic(john).get(1)) == 1, false);
    // Updating amount of games one by one of Jac
    DBUpdate.updateGamesWon(jac);
    assertEquals(Integer.parseInt(DBInformation.getPlayerStatistic(jac).get(1)) == 0, false);
    assertEquals(Integer.parseInt(DBInformation.getPlayerStatistic(jac).get(1)) == 1, true);
    // Updating games lost and testing it + win-rate of john
    DBUpdate.updateGamesLost(john);
    assertEquals(Integer.parseInt(DBInformation.getPlayerStatistic(john).get(2)) == 0, false);
    assertEquals(Integer.parseInt(DBInformation.getPlayerStatistic(john).get(2)) == 1, true);
    assertEquals(Double.parseDouble(DBInformation.getPlayerStatistic(john).get(3)) == 0.0, true);
  }

  /**
   * Removing players from previous methods.
   * 
   * @author skeskinc
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
   * @author skeskinc
   */
  @AfterAll
  public static void closingConnection() {
    Database.disconnectDb();
    File file = new File(System.getProperty("user.home") + System.getProperty("file.separator")
        + ".Scrabble" + System.getProperty("file.separator") + "Scrabble15Test.db");
    if (file.exists()) {
      file.delete();
    }
  }
}
