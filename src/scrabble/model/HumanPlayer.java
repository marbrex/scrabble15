package scrabble.model;

import java.io.Serializable;

/**
 * scrabble.model.HumanPlayer class extends from the scrabble.model.Player class and contains game
 * statistics, which is only necessary for the HumanPlayer
 * 
 * @author Sergen Keskincelik
 */

public class HumanPlayer extends Player implements Serializable{

  /** Amount of games won for statistics -> HumanPlayer */
  private int gamesWon;
  /** Amount of games lost for statistics -> HumanPlayer */
  private int gamesLost;
  /** Determine, whether a human player is a host or not */
  private boolean host;

  /** Constructor of the Humanplayer class */
  public HumanPlayer() {
    super();
    this.gamesWon = 0;
    this.gamesLost = 0;
  }

  /** Returns Win-Rate of the player */
  public double getWinRate() {
    if (gamesLost != 0) {
      return Math.round((gamesWon * 100.0) / gamesLost) / 100.0;
    } else {
      return (double) gamesWon;
    }
  }

  /** Returns the amount of games lost in total */
  public int getGamesLost() {
    return gamesLost;
  }

  /** Setting the amount of games lost in total -> Database */
  public void setGamesLost(int gamesLost) {
    this.gamesLost = gamesLost;
  }

  /** Returns the amount of games won in total */
  public int getGamesWon() {
    return this.gamesWon;
  }

  /** Setting the amount of games won in total -> Database */
  public void setGamesWon(int gamesWon) {
    this.gamesWon = gamesWon;
  }

  /** Deciding to make human player a host */
  public void setHost(boolean host) {
    this.host = host;
  }

  /** Returns a truth value, if a human player is a host or not */
  public boolean isHost() {
    return this.host;
  }

}
