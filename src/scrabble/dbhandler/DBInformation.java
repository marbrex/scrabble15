package scrabble.dbhandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import scrabble.model.HumanPlayer;

/**
 * scrabble.dbhandler.DBInformation class to receive player statistics from the Database.
 * 
 * @author skeskinc
 * @author mraucher
 */

public class DBInformation {

  private static Statement stmt = null;

  /**
   * Player profiles to choose from in UI.
   * 
   * @return A list of Player profiles
   * @author skeskinc
   */
  public static List<HumanPlayer> getPlayerProfiles() {
    try {
      List<HumanPlayer> playerProfiles = new ArrayList<HumanPlayer>();
      stmt = Database.getConnection().createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM Players;");
      while (rs.next()) {
        HumanPlayer player = new HumanPlayer();
        player.setName(rs.getString("Name"));
        player.setGamesWon(rs.getInt("GamesWon"));
        player.setGamesLost(rs.getInt("GamesLost"));
        player.setImage(rs.getInt("Image"));
        playerProfiles.add(player);
      }
      return playerProfiles;
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Checks, if Database contains already the name.
   * 
   * @return true if Player table from Database contains name, else false
   * @author skeskinc
   */
  public static boolean containsName(String name) {
    List<String> playerNames = getPlayerNames();
    if (playerNames.contains(name)) {
      return true;
    } else {
      return false;
    }

  }

  /**
   * Returns a List, which contains all Player names saved in the Database.
   * 
   * @return A list of player names in database
   * @author skeskinc
   */
  private static List<String> getPlayerNames() {
    try {
      List<String> playernames = new ArrayList<String>();
      stmt = Database.getConnection().createStatement();
      ResultSet rs = stmt.executeQuery("SELECT Name FROM Players;");
      while (rs.next()) {
        playernames.add(rs.getString("Name"));
      }
      return playernames;
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      return null;
    }
  }

  /**
   * Checks, if one specific Identification number is in Statistics table.
   * 
   * @param id checking given id
   * @return true, if id is in Database, else false
   * @author skeskinc
   */
  public static boolean containsIdentification(int id) {
    try {
      stmt = Database.getConnection().createStatement();
      ResultSet rs = stmt.executeQuery("SELECT SettingsId FROM Settings;");
      while (rs.next()) {
        if (id == rs.getInt("SettingsId")) {
          return true;
        }
      }
      return false;
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      return false;
    }
  }

  /**
   * Loads one player profile on given index.
   * 
   * @param index loading profile regarding given index
   * @return human-player profile, which was loaded from Database
   * @author skeskinc
   */
  public static HumanPlayer loadProfile(int index) {
    List<HumanPlayer> playerProfiles = getPlayerProfiles();
    HumanPlayer player = playerProfiles.get(index);
    if (player != null) {
      player.setImage(index);
      return player;
    } else {
      return null;
    }
  }

  /**
   * Returns all statistics of an Player in a List.
   * 
   * @param player getting statistics from given human-player
   * @return a list of all player statistics from one player
   * @author skeskinc
   */
  public static List<String> getPlayerStatistic(HumanPlayer player) {
    List<String> playerStatistic = new ArrayList<String>();
    playerStatistic.add(player.getName());
    playerStatistic.add(Integer.toString(player.getGamesWon()));
    playerStatistic.add(Integer.toString(player.getGamesLost()));
    playerStatistic.add(Double.toString(player.getWinRate()));
    return playerStatistic;
  }

  /**
   * Returns all settings of a Player in a List.
   * 
   * @param player the humanPlayer
   * @return List of player settings
   * @author mraucher
   */
  private static List<String> getPlayerSettings(HumanPlayer player) {
    try {
      stmt = Database.getConnection().createStatement();
      ResultSet rs = stmt.executeQuery(
          "SELECT SoundOn, SoundLevel, SceneMode, AIDifficulty " + "FROM Settings s, Players p "
              + "WHERE p.Name = '" + player.getName() + "' AND p.SettingsId = s.SettingsId;");
      List<String> playerSettings = new ArrayList<String>(4);
      playerSettings.add(rs.getString("SoundOn"));
      playerSettings.add(rs.getString("SoundLevel"));
      playerSettings.add(rs.getString("SceneMode"));
      playerSettings.add(rs.getString("AIDifficulty"));
      return playerSettings;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }

  }

  /**
   * Checks the SoundOn/SoundOff setting of this player from the DB.
   * 
   * @param player the HumanPlayer
   * @return true if SoundOn==1 in DB, false if SoundOn==0 in DB
   * @author mraucher
   */
  public static boolean isSoundOn(HumanPlayer player) {
    // If DB should hold anything else than "1".
    // (e. g. "null") the default value would be false=="sound off".
    return (getPlayerSettings(player).get(0).equals("1") ? true : false);
  }

  /**
   * Gets the Soundlevel setting of this player from the DB.
   * 
   * @param player the HumanPlayer
   * @return the soundlevel setting from DB
   * @author mraucher
   * @author skeskinc
   */
  public static double getSoundLevel(HumanPlayer player) {
    return Double.valueOf(getPlayerSettings(player).get(1));
  }

  /**
   * Checks the SceneMode setting of this player from the DB.
   * 
   * @param player the HumanPlayer
   * @return true if SceneMode=="Fullscreen" in DB, false if SceneMode!="Fullscreen"
   * @author mraucher
   */
  public static boolean isFullscreen(HumanPlayer player) {
    // If DB should hold anything else than "Fullscreen"
    // (e. g. "null") the default would be false=="not fullscreen"
    return (getPlayerSettings(player).get(2).equals("Fullscreen") ? true : false);
  }

  /**
   * Checks the AIDifficulty setting of this player from the DB.
   * 
   * @param player the HumanPlayer
   * @return true if AIDifficulty=="Hard" in DB, false if SceneMode!="Hard"
   * @author mraucher
   */
  public static boolean isAiDifficultyHard(HumanPlayer player) {
    // If DB should hold anything else than "Hard"
    // (e. g. "null") the default value would be false=="not hard"
    return (getPlayerSettings(player).get(3).equals("Hard") ? true : false);
  }

  /**
   * Gets the Settings-ID of a Player.
   * 
   * @param player given Human-player to get the id
   * @return Settings-ID of the given Human-Player
   * @author skeskinc
   * @author mraucher
   */
  public static int getSettingsId(HumanPlayer player) {
    int id = 0;
    try {
      stmt = Database.getConnection().createStatement();
      ResultSet rs = stmt
          .executeQuery("SELECT SettingsId FROM Players WHERE Name = '" + player.getName() + "';");
      id = rs.getInt("SettingsId");
      return id;
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      return 0;
    }
  }

  /**
   * Returns the amount of player profiles.
   * 
   * @return size of the player-table
   * @author skeskinc
   */
  public static int getProfileSize() {
    return getPlayerProfiles().size();
  }

  /**
   * Returns the Ratio of Win-/Loserate.
   * 
   * @param player current human-player for win-/lose-ratio
   * @return win-/loserate of a player
   * @author skeskinc
   * @author mraucher
   */
  public double ratioWnL(HumanPlayer player) {
    double ratio = 0.0;
    double winR = 0.0;
    double lossR = 0.0;
    if (containsName(player.getName())) {
      try {
        stmt = Database.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT GamesWon, GamesLost FROM Players WHERE = '" + player.getName() + "';");
        while (rs.next()) {
          winR = (rs.getInt("GamesWon") / totGamePlayed(player.getName())) * 100;
          lossR = (rs.getInt("GamesLost") / totGamePlayed(player.getName())) * 100;
          ratio = (winR + lossR) / 2;
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return ratio;
  }

  /**
   * Returns the total game played by any player.
   * 
   * @param name given Name
   * @return total games played of a player
   * @author mraucher
   */
  public int totGamePlayed(String name) {
    int gamePlayed = 0;
    if (!containsName(name)) {
      try {
        stmt = Database.getConnection().createStatement();
        ResultSet rs =
            stmt.executeQuery("SELECT GamesWon, GamesLost FROM Players WHERE = '" + name + "';");
        while (rs.next()) {
          gamePlayed = gamePlayed + rs.getInt("GamesWon") + rs.getInt("GamesLost");
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return gamePlayed;
  }

}
