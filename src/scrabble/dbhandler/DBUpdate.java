package scrabble.dbhandler;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import scrabble.model.*;

/**
 * scrabble.dbhandler.DBUpdate class to update player statistics and settings
 * 
 * @author mraucher
 * @author skeskinc  
 */

public class DBUpdate {

  private static Statement stmt = null;
  private static PreparedStatement pstmt = null;

/**
   * Updating the amount of games won in the Database and in the player's object
   * 
   * @param player The Human-Player to update games won
   * @author mraucher
   * @author skeskinc
   */
  public static void updateGamesWon(HumanPlayer player) {
    try {
      stmt = Database.getConnection().createStatement();
      pstmt = Database.getConnection().prepareStatement(
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
      stmt = Database.getConnection().createStatement();
      pstmt = Database.getConnection().prepareStatement(
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
      pstmt = Database.getConnection().prepareStatement(
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
        stmt = Database.getConnection().createStatement();
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
      stmt = Database.getConnection().createStatement();
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
      stmt = Database.getConnection().createStatement();
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
      stmt = Database.getConnection().createStatement();
      stmt.executeUpdate(
          "UPDATE Settings SET SoundOn = " + soundOn + " WHERE Settings_Id = " + settings_id + ";");
    } catch (SQLException e) {
      e.printStackTrace();
    }
   }
 }
