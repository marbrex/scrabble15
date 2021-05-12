package scrabble.dbhandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import scrabble.model.HumanPlayer;

/**
 * scrabble.dbhandler.DBInformation class to receive player statistics from the Database
 * 
 * @author skeskinc
 * @author mraucher
 */

public class DBInformation {

  private static Statement stmt = null;

  /**
   * Player profiles to choose from in UI
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
   * Checks, if Database contains already the name
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
   * Returns a List, which contains all Player names saved in the Database
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
   * Checks, if one specific Identification number is in Statistics table
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
   * Loads one player profile on given index
   * 
   * @return human-player profile, which was loaded from Database
   * @param index loading profile regarding given index
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
   * Returns all statistics of an Player in a List
   * 
   * @return a list of all player statistics from one player
   * @param player getting statistics from given human-player
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
   * Gets the Settings-ID of a Player
   * 
   * @param player given Human-player to get the id
   * @return Settings-ID of the given Human-Player
   * @author skeskinc
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
   * Returns the amount of player profiles
   * 
   * @return size of the player-table
   * @author skeskinc
   */
  public static int getProfileSize() {
    return getPlayerProfiles().size();
  }

  /**
   * Returns the Ratio of Win-/Loserate
   * 
   * @return win-/loserate of a player
   * @param player current human-player for win-/lose-ratio
   * @author skeskinc
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
   * Returns the total game played by any player
   * 
   * @return total games played of a player
   * @param name given Name
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
