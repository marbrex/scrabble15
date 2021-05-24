package scrabble.model;

import java.util.List;
import scrabble.dbhandler.DBInformation;

/**
 * scrabble.model.Profile class to receive current Player Profile and its statistics.
 * 
 * @author skeskinc
 */
public class Profile {

  private static HumanPlayer player;
  private static List<String> statistics;

  /** 
   * Setting the player and his statistics. 
   * 
   * @param humanplayer Current given profile
   * @author skeskinc
   */
  public static void setPlayer(HumanPlayer humanplayer) {
    player = humanplayer;
    if (player != null) {
      statistics = DBInformation.getPlayerStatistic(player);
    }
  }

  /**
   * Returns a Human-Player object.
   * 
   * @return current profile
   * @author skeskinc
   */
  public static HumanPlayer getPlayer() {
    return player;
  }

  /**
   * Returns the statistics of a Human-player object.
   * 
   * @return statistics of the current profile
   * @author skeskinc
   */
  public static List<String> getStatistics() {
    return statistics;
  }

}
