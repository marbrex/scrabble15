package scrabble.network;

import scrabble.model.MessageType;
import scrabble.model.Player;

public class OtherMessage extends Message{
  /**
   * Message to inform players in a game which players is actually on move
   * 
   * @author hendiehl
   */
  private int i;
  /**
   * Constructor to set the position of the player who is actually on the move
   * Send to the other players
   * @param type Messagetype
   * @param owner Owner of the message
   * @param i position of the player on the move in the list
   * @author hendiehl
   */
  public OtherMessage(MessageType type, Player owner, int i) {
    super(type, owner);
    this.i = i;
  }
  /**
   * Getter for the int representation of the player position in the list
   * @return position of the player on move in the list
   * @author hendiehl
   */
  public int getI() {
    return i;
  }
  
}
