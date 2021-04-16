package scrabble.dbhandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import scrabble.model.*;

/**
 * scrabble.dbhandler.Database class to connect to the Database, creating and being able to delete
 * tables and updating player statistics
 * 
 * @author Sergen Keskincelik
 * @author Moritz Raucher
 */

public class Database {

  protected static Connection connection = null;
  protected static Statement stmt = null;
  protected static PreparedStatement pstmt = null;

  /**
   * Connecting to DB and creating DB File if it doesn't exist (SQLite .jar file needed!)
   */
  public static void connectToDB() {
    try {
      Class.forName("org.sqlite.JDBC");
      connection = DriverManager.getConnection("jdbc:sqlite:" + System.getProperty("user.dir")
          + "\\resources\\" + "scrabble\\db\\Scrabble15.db");
    } catch (Exception e) {
      // TODO Auto-generated catch block
      System.out.println("Connection failed!");
      System.exit(0);
    }
    System.out.println("Successfully connected to DB!");
  }

  /** Disconnecting from DB */
  public static void disconnectDB() {
    try {
      stmt = null;
      pstmt = null;
      connection.close();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    System.out.println("Disconnected from DB");
  }

  /** Creating tables for the Database */
  public static void createTables() {
    List<String> sqlstatements = new ArrayList<String>();
    sqlstatements.add(
        "CREATE TABLE IF NOT EXISTS Statistics (Statistic_Id INT PRIMARY KEY, Player_Id references Players (Id));");
    sqlstatements.add(
        "CREATE TABLE IF NOT EXISTS Settings (SettingsId INT PRIMARY KEY, SoundOn BOOLEAN NOT NULL,"
            + "SoundLevel INT NOT NULL,  SceneHeight DOUBLE NOT NULL, SceneWidth DOUBLE NOT NULL, SceneMode VARCHAR(25) NOT NULL, AIDifficulty VARCHAR(25) NOT NULL);");
    sqlstatements
        .add("CREATE TABLE IF NOT EXISTS Players (Id INT PRIMARY KEY, Name VARCHAR(15) NOT NULL, "
            + " GamesWon INT NOT NULL, GamesLost INT NOT NULL, Winrate DOUBLE NOT NULL, Image VARCHAR(25), SettingsId references Settings (Id));");
    try {
      stmt = connection.createStatement();
      for (String sql : sqlstatements) {
        stmt.executeUpdate(sql);
      }
      System.out.println("All tables created successfully");
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /** Deleting all tables which are present in our Database */
  public static void dropAllTables() {
    dropPlayerTable();
    dropStatisticsTable();
    dropSettingsTable();
    System.out.println("All tables dropped.");
  }

  /** Dropping Statistics Table */
  private static void dropStatisticsTable() {
    try {
      stmt = connection.createStatement();
      stmt.executeUpdate("DROP TABLE Statistics;");
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /** Dropping Player table */
  private static void dropPlayerTable() {
    try {
      stmt = connection.createStatement();
      stmt.executeUpdate("DROP TABLE Players;");
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /** Dropping Statistic table */
  private static void dropSettingsTable() {
    try {
      stmt = connection.createStatement();
      stmt.executeUpdate("DROP TABLE Settings;");
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /** Fills in player table with Player Data */
  private static void fillPlayerTable(int id, String name) {
    try {
      pstmt = connection.prepareStatement(
          "INSERT INTO Players (Id,Name,GamesWon,GamesLost,Winrate,SettingsId) VALUES (?,?,?,?,?,?);");
      pstmt.setInt(1, id);
      pstmt.setString(2, name);
      pstmt.setInt(3, 0);
      pstmt.setInt(4, 0);
      pstmt.setDouble(5, 0.0);
      pstmt.setInt(6, id);
      pstmt.executeUpdate();

    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /** Fills in settings table with default values */
  private static void fillSettingsTable(int id) {
    try {
      pstmt = connection.prepareStatement(
          "INSERT INTO Settings (SettingsId,SoundOn,SoundLevel,SceneHeight,SceneWidth,SceneMode,AIDifficulty) VALUES (?,?,?,?,?,?,?);");
      pstmt.setInt(1, id);
      pstmt.setBoolean(2, true);
      pstmt.setInt(3, 50);
      pstmt.setDouble(4, 800);
      pstmt.setDouble(5, 800);
      pstmt.setString(6, "Fullscreen");
      pstmt.setString(7, "Easy");
      pstmt.executeUpdate();

    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /** Filling all tables */
  public static void fillTables(int id, String name) {
    fillSettingsTable(id);
    fillPlayerTable(id, name);
    System.out.println("All tables filled successfully");
  }


  /**
   * Fills in statistics table with Statistic Data
   * 
   * @throws SQLException
   */
  public static void fillStatisticTable(String[][] statisticData) throws SQLException {
    pstmt = connection
        .prepareStatement("INSERT INTO Statistic_Id (Statistic_Id, Player_Id) VALUES (?,?);");
    for (int i = 0; i < statisticData.length; i++) {
      pstmt.setInt(1, Integer.parseInt(statisticData[i][0]));
      pstmt.setInt(2, Integer.parseInt(statisticData[i][1]));
      pstmt.executeUpdate();
    }
  }

  /**
   * Updating the amount of games won in your Database and in the player's object
   */
  public static void updateGamesWon(HumanPlayer player) {
    try {
      stmt = connection.createStatement();
      pstmt = connection.prepareStatement(
          "UPDATE Players SET GamesWon = ? WHERE Name = '" + player.getName() + "';");
      pstmt.setInt(1, player.getGamesWon() + 1);
      pstmt.executeUpdate();
      ResultSet rs =
          stmt.executeQuery("SELECT * FROM Players WHERE Name = '" + player.getName() + "';");
      while (rs.next()) {
        player.setGamesWon(rs.getInt("GamesWon"));
      }
      updateWinRate(player);
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Updating the amount of games lost in your Database and in the player's object
   */
  public static void updateGamesLost(HumanPlayer player) {
    try {
      stmt = connection.createStatement();
      pstmt = connection.prepareStatement(
          "UPDATE Players SET GamesLost = ? WHERE Name = '" + player.getName() + "';");
      pstmt.setInt(1, player.getGamesLost() + 1);
      pstmt.executeUpdate();
      ResultSet rs =
          stmt.executeQuery("SELECT * FROM Players WHERE Name = '" + player.getName() + "';");
      while (rs.next()) {
        player.setGamesLost(rs.getInt("GamesLost"));
      }
      updateWinRate(player);
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /** Updating the Win-Rate in the Database */
  public static void updateWinRate(HumanPlayer player) {
    try {
      pstmt = connection.prepareStatement(
          "UPDATE Players SET Winrate = ? WHERE Name = '" + player.getName() + "';");
      pstmt.setDouble(1, player.getWinRate());
      pstmt.executeUpdate();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /** Updating the Name if Player wants to change it */
  public static void updatePlayerName(HumanPlayer player, String newName) {
    if (!DBInformation.containsName(newName)) {
      try {
        stmt = connection.createStatement();
        stmt.executeUpdate(
            "UPDATE Players SET Name = '" + newName + "' WHERE Name = '" + player.getName() + "';");
        player.setName(newName);
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  /** Updates the Resolution of the screen */
  public void updateResolution(int settings_id, double height, double width) {
    try {
      stmt = connection.createStatement();
      stmt.executeUpdate("UPDATE Settings SET SceneHeight = " + height + ", SceneWidth =  " + width
          + " WHERE Settings_Id = " + settings_id + ";");
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /** Updates the AI Difficulty */
  public void updateAIDifficulty(int settings_id, String difficulty) {
    try {
      stmt = connection.createStatement();
      stmt.executeUpdate("UPDATE Settings SET AIDifficulty = " + difficulty
          + " WHERE Settings_Id = " + settings_id + ";");
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /** Updates the Sound-Level of the Game */
  public void updateSoundLevel(int settings_id, int soundlevel) {
    try {
      stmt = connection.createStatement();
      stmt.executeUpdate("UPDATE Settings SET SoundLevel = " + soundlevel + " WHERE Settings_Id = "
          + settings_id + ";");
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /** Updates the Switch sound state */
  public void updateSoundSwitcher(int settings_id, boolean soundOn) {
    try {
      stmt = connection.createStatement();
      stmt.executeUpdate(
          "UPDATE Settings SET SoundOn = " + soundOn + " WHERE Settings_Id = " + settings_id + ";");
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }


  /** Removes Player data from the Database */
  public static void removePlayer(int id, HumanPlayer player) {
    try {
      pstmt = connection.prepareStatement("DELETE FROM Players WHERE Id =" + id);
      pstmt.executeUpdate();

      pstmt = connection.prepareStatement("DELETE FROM Statistics WHERE Player_Id = " + id);
      pstmt.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    }

  }

  /** Returns the Connection for the DBInformation class */
  public static Connection getConnection() {
    return connection;
  }

}
