package scrabble.model;

import java.io.Serializable;

/**
 * scrabble.model.HumanPlayer class extends from the scrabble.model.Player class and contains game
 * statistics, which is only necessary for the HumanPlayer.
 * 
 * @author skeskinc
 */

public class HumanPlayer extends Player implements Serializable {

  private int gamesWon;
  private int gamesLost;
  private boolean host;

  /**
   * Constructor of the Humanplayer class.
   * 
   * @author skeskinc
   */
  public HumanPlayer() {
    super();
    this.gamesWon = 0;
    this.gamesLost = 0;
  }

  /**
   * Returns Win-Rate of the player.
   * 
   * @return The Win-Rate of the Player
   * @author skeskinc
   */
  public double getWinRate() {
    if (gamesLost != 0) {
      return Math.round((gamesWon * 100.0) / gamesLost) / 100.0;
    } else {
      return (double) gamesWon;
    }
  }

  /**
   * Returns the amount of games lost in total.
   * 
   * @return Amount of games lost
   * @author skeskinc
   */
  public int getGamesLost() {
    return gamesLost;
  }

  /**
   * Setting the amount of games lost in total.
   * 
   * @param gamesLost Setting up the amount of games lost
   * @author skeskinc
   */
  public void setGamesLost(int gamesLost) {
    this.gamesLost = gamesLost;
  }

  /**
   * Returns the amount of games won in total.
   * 
   * @return Amount of games won
   * @author skeskinc
   */
  public int getGamesWon() {
    return this.gamesWon;
  }

  /**
   * Setting the amount of games won in total.
   * 
   * @param gamesWon Setting up the amount of games won
   * @author skeskinc
   */
  public void setGamesWon(int gamesWon) {
    this.gamesWon = gamesWon;
  }

  /**
   * Deciding to make human player a host.
   * 
   * @param host Determine if player is a host or not
   * @author skeskinc
   */
  public void setHost(boolean host) {
    this.host = host;
  }

  /**
   * Returns a truth value, if a human player is a host or not.
   * 
   * @return True if a player is a host, else false
   * @author skeskinc
   */
  public boolean isHost() {
    return this.host;
  }

}
