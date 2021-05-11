package scrabble.dbhandler;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import scrabble.model.*;

/**
 * scrabble.dbhandler.Database class to connect to the Database, creating and being able to delete
 * tables and updating player statistics
 * 
 * @author mraucher
 * @author skeskinc  
 */

public class Database {

  protected static Connection connection = null;
  protected static Statement stmt = null;
  protected static PreparedStatement pstmt = null;

  /**
   * Connecting to DB and creating DB File if it doesn't exist (SQLite .jar file needed!)
   * 
   * @author mraucher
   * @author skeskinc
   */
  public static void connectToDB() {
    try {
      Class.forName("org.sqlite.JDBC");
      new File(System.getProperty("user.home") + System.getProperty("file.separator") + ".Scrabble")
          .mkdir();
      // connection = DriverManager.getConnection("jdbc:sqlite:" + System.getProperty("user.dir")
      // + "\\resources\\" + "scrabble\\db\\Scrabble15.db");
      connection = DriverManager.getConnection(
          "jdbc:sqlite:" + System.getProperty("user.home") + System.getProperty("file.separator")
              + ".Scrabble" + System.getProperty("file.separator") + "Scrabble15.db");
    } catch (Exception e) {
      System.out.println("Connection failed!");
      System.exit(0);
    }
    System.out.println("Successfully connected to DB!");
  }

  /** 
   * Disconnecting from DB 
   * 
   * @author mraucher
   * @author skeskinc
   */
  public static void disconnectDB() {
    try {
      stmt = null;
      pstmt = null;
      connection.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Disconnected from DB");
  }

  /** 
   * Creating tables for the Database 
   * 
   * @author mraucher
   * @author skeskinc
   */
  public static void createTables() {
    List<String> sqlstatements = new ArrayList<String>();
    sqlstatements.add(
        "CREATE TABLE IF NOT EXISTS Statistics (Statistic_Id INT PRIMARY KEY, Player_Name references Players (Name));");
    sqlstatements.add(
        "CREATE TABLE IF NOT EXISTS Settings (SettingsId INT PRIMARY KEY, SoundOn BOOLEAN NOT NULL,"
            + "SoundLevel INT NOT NULL, SceneMode VARCHAR(25) NOT NULL, AIDifficulty VARCHAR(25) NOT NULL);");
    sqlstatements.add("CREATE TABLE IF NOT EXISTS Players (Name VARCHAR(15) PRIMARY KEY NOT NULL, "
        + " GamesWon INT NOT NULL, GamesLost INT NOT NULL, Winrate DOUBLE NOT NULL, Image INT NOT NULL, SettingsId references Settings (Id));");
    try {
      stmt = connection.createStatement();
      for (String sql : sqlstatements) {
        stmt.executeUpdate(sql);
      }
      System.out.println("All tables created successfully");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /** Deleting all tables which are present in the Database 
   * 
   * @author mraucher
   * @author skeskinc
   */
  public static void dropAllTables() {
    dropPlayerTable();
    dropStatisticsTable();
    dropSettingsTable();
    System.out.println("All tables dropped.");
  }

  /** 
   * Dropping Statistics Table 
   *
   * @author mraucher
   * @author skeskinc
   */
  private static void dropStatisticsTable() {
    try {
      stmt = connection.createStatement();
      stmt.executeUpdate("DROP TABLE Statistics;");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /** Dropping Player table 
   * 
   * @author mraucher
   */
  private static void dropPlayerTable() {
    try {
      stmt = connection.createStatement();
      stmt.executeUpdate("DROP TABLE Players;");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /** Dropping Statistic table 
   * 
   * @author mraucher
   */
  private static void dropSettingsTable() {
    try {
      stmt = connection.createStatement();
      stmt.executeUpdate("DROP TABLE Settings;");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Fills in player table with Player Data
   * 
   * @param settingsId the id for the settings table
   * @param name name of the player
   * @param imageindex index of the image of the player
   * @author mraucher
   */
  private static void fillPlayerTable(int settingsId, String name, int imageindex) {
    try {
      pstmt = connection.prepareStatement(
          "INSERT INTO Players (Name,GamesWon,GamesLost,Winrate,Image,SettingsId) VALUES (?,?,?,?,?,?);");
      pstmt.setString(1, name);
      pstmt.setInt(2, 0);
      pstmt.setInt(3, 0);
      pstmt.setDouble(4, 0.0);
      pstmt.setInt(5, imageindex);
      pstmt.setInt(6, settingsId);
      pstmt.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Fills in settings table with default values
   * 
   * @param id settingsId
   * @author mraucher
   * @author skeskinc
   */
  private static void fillSettingsTable(int id) {
    try {
      pstmt = connection.prepareStatement(
          "INSERT INTO Settings (SettingsId,SoundOn,SoundLevel,SceneMode,AIDifficulty) VALUES (?,?,?,?,?);");
      pstmt.setInt(1, id);
      pstmt.setBoolean(2, true);
      pstmt.setInt(3, 50);
      pstmt.setString(4, "Fullscreen");
      pstmt.setString(5, "Easy");
      pstmt.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Filling all tables
   * 
   * @param id SettingsId and PlayerId
   * @param name player name
   * @param imageindex index of the image of the player
   * @author mraucher
   * @author skeskinc
   */
  public static void fillTables(int id, String name, int imageindex) {
    fillSettingsTable(id);
    fillPlayerTable(id, name, imageindex);
    System.out.println("All tables filled successfully");
  }


  /**
   * Fills in statistics table with Statistic Data
   * 
   * @throws SQLException
   * @author mraucher
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
   * Updating the amount of games won in the Database and in the player's object
   * 
   * @param player The Human-Player to update games won
   * @author mraucher
   * @author skeskinc
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
      e.printStackTrace();
    }
  }

  /**
   * Updating the amount of games lost in the Database and in the player's object
   * 
   * @param player The Human-Player to update the games lost
   * @author mraucher
   * @author skeskinc
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
      e.printStackTrace();
    }
  }

  /**
   * Updating the Win-Rate in the Database
   * 
   * @param player Updating Win-Rate of the Human-Player
   * @author mraucher
   * @author skeskinc
   */
  public static void updateWinRate(HumanPlayer player) {
    try {
      pstmt = connection.prepareStatement(
          "UPDATE Players SET Winrate = ? WHERE Name = '" + player.getName() + "';");
      pstmt.setDouble(1, player.getWinRate());
      pstmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Updating the name of the player in the database and the player's object
   * 
   * @param player the human player to be updated
   * @param newName the new name of the human player
   * @author mraucher
   */
  public static void updatePlayerName(HumanPlayer player, String newName) {
    if (!DBInformation.containsName(newName)) {
      try {
        stmt = connection.createStatement();
        stmt.executeUpdate(
            "UPDATE Players SET Name = '" + newName + "' WHERE Name = '" + player.getName() + "';");
        player.setName(newName);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Updates the AI Difficulty
   * 
   * @param settings_id the settingsId to identify the settings entry to be updated
   * @param difficulty the new difficulty of AI to be updated in the database
   * @author mraucher
   * @author skeskinc
   */
  public void updateAIDifficulty(int settings_id, String difficulty) {
    try {
      stmt = connection.createStatement();
      stmt.executeUpdate("UPDATE Settings SET AIDifficulty = " + difficulty
          + " WHERE Settings_Id = " + settings_id + ";");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Updates the Sound-Level of the Game
   * 
   * @param settings_id the settingsId to identify the settings entry to be updated
   * @param the new soundlevel to be updated in the database
   * @author mraucher
   * @author skeskinc
   */
  public void updateSoundLevel(int settings_id, int soundlevel) {
    try {
      stmt = connection.createStatement();
      stmt.executeUpdate("UPDATE Settings SET SoundLevel = " + soundlevel + " WHERE Settings_Id = "
          + settings_id + ";");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Updates the Switch sound state
   * 
   * @param settings_id the settingsId to identify the settings entry to be updated
   * @param soundOn the new flag for soundOn (=true) / soundOf (=false)
   * @author mraucher
   * @author skeskinc
   */
  public void updateSoundSwitcher(int settings_id, boolean soundOn) {
    try {
      stmt = connection.createStatement();
      stmt.executeUpdate(
          "UPDATE Settings SET SoundOn = " + soundOn + " WHERE Settings_Id = " + settings_id + ";");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }


  /**
   * Removes Player data from the Database
   * 
   * @param player the human player to be deleted from database
   * @author mraucher
   */
  public static void removePlayer(HumanPlayer player) {
    try {
      // delete from table Players - finding player by name
      pstmt = connection
          .prepareStatement("DELETE FROM Players WHERE Name = '" + player.getName() + "'");
      pstmt.executeUpdate();

      // delete from table Statistics - finding statistic by player_name
      pstmt = connection.prepareStatement(
          "DELETE FROM Statistics WHERE Player_Name = '" + player.getName() + "'");
      pstmt.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns the Connection for the DBInformation class
   * 
   * @return The current connection to the Database
   * @author mraucher
   * @author skeskinc
   */
  public static Connection getConnection() {
    return connection;
  }

}