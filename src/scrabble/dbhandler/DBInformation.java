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
 * @author Sergen Keskincelik
 * @author Moritz Raucher
 */

public class DBInformation {

  private static Statement stmt = null;

  /** Player profiles to choose from in UI */
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
        playerProfiles.add(player);
      }
      return playerProfiles;
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }
  }

  /** Checks, if Database contains already the name (e.g. for Player Profile) */
  public static boolean containsName(String name) {
    List<String> playerNames = getPlayerNames();
    if (playerNames.contains(name)) {
      return true;
    } else {
      return false;
    }

  }

  /** Returns a List, which contains all Player names saved in the Database */
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

  /** Returns one specific Identification number from Statistics table */
  public static boolean containsIdentification(int id) {
    try {
      stmt = Database.getConnection().createStatement();
      ResultSet rs = stmt.executeQuery("SELECT Statistic_Id FROM Statistics;");
      while (rs.next()) {
        if (id == rs.getInt("Statistic_Id")) {
          return true;
        }
      }
      return false;
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      return false;
    }
  }

  /** Loads one player profile on given index */
  public static HumanPlayer loadProfile(int index) {
    List<HumanPlayer> playerProfiles = getPlayerProfiles();
    HumanPlayer player = playerProfiles.get(index);
    if (player != null) {
      return player;
    } else {
      return null;
    }
  }

  /** Returns all statistics of an Player in a List */
  public static List<String> getPlayerStatistic(HumanPlayer player) {
    List<String> playerStatistic = new ArrayList<String>();
    playerStatistic.add(player.getName());
    playerStatistic.add(Integer.toString(player.getGamesWon()));
    playerStatistic.add(Integer.toString(player.getGamesLost()));
    playerStatistic.add(Double.toString(player.getWinRate()));
    return playerStatistic;
  }

  /** Returns the amount of player profiles */
  public static int getProfileSize() {
    return getPlayerProfiles().size();
  }

}
