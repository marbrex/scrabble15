package scrabble.network;

import scrabble.model.MessageType;
import scrabble.model.Player;

public class DBMessage extends Message {
  /**
   * DBMessage which will be send after an network game to inform a player about his gained points
   * and his game win.
   * 
   * @author hendiehl
   */

  private static final long serialVersionUID = 1L;
  private int points;
  private boolean won;

  /**
   * Constructor to set the gained game points and the won condition to the message.
   * 
   * @param type MessageType of the message
   * @param owner Owner of the message
   * @param points points gained in a network game.
   * @param won boolean condition about the game win of the player
   * @author hendiehl
   */
  public DBMessage(MessageType type, Player owner, int points, boolean won) {
    super(type, owner);
    this.points = points;
    this.won = won;
  }

  /**
   * Getter of the points variable.
   * 
   * @return gained points of an network game
   */
  public int getPoints() {
    return points;
  }

  /**
   * Getter of the boolean win condition.
   * 
   * @return boolean condition about player win
   */
  public boolean isWon() {
    return won;
  }

}
