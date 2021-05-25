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
  private boolean won;

  /**
   * Constructor to set the win condition to the message.
   *
   * @param type MessageType of the message.
   * @param owner Owner of the message.
   * @param won boolean condition about the game win of the player.
   * @author hendiehl
   */
  public DBMessage(MessageType type, Player owner, boolean won) {
    super(type, owner);
    this.won = won;
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
