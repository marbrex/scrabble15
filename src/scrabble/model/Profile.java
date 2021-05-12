package scrabble.model;

import java.util.*;
import scrabble.dbhandler.DBInformation;
import scrabble.dbhandler.Database;

/**
 * scrabble.model.Profile class to receive current Player Profile and its statistics
 * 
 * @author skeskinc
 */
public class Profile {

  private static HumanPlayer player;
  private static List<String> statistics;

  /** 
   * Setting the player and his statistics 
   * 
   * @author skeskinc
   */
  public static void setPlayer(HumanPlayer humanplayer) {
    player = humanplayer;
    if (player != null) {
      statistics = DBInformation.getPlayerStatistic(player);
    }
  }

  /**
   * Returns a Human-Player object
   * 
   * @author skeskinc
   */
  public static HumanPlayer getPlayer() {
    return player;
  }

  /**
   * Returns the statistics of a humanplayer object
   * 
   * @author skeskinc
   */
  public static List<String> getStatistics() {
    return statistics;
  }

}
