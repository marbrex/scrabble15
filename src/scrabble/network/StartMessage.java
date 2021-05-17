package scrabble.network;

import scrabble.model.MessageType;
import scrabble.model.Player;

public class StartMessage extends Message {
  /**
   * Message which will be send to inform the players that a game will be started soon. Will be used
   * to inform the lobby members that there election result should be send to the server to start.
   * 
   * @author hendiehl
   */
  /** array representation of the player sequence chosen by a member of a lobby */
  private int[] sequence;

  /**
   * Constructor to create a StartMessage, which have the purpose to transmit the player sequence
   * chosen by a specific member of a lobby
   * 
   * @param type Type of the Message, here type START
   * @param owner Owner of the Message
   * @param sequence array representation of the Message
   * @author hendiehl
   */
  public StartMessage(MessageType type, Player owner, int[] sequence) {
    super(type, owner);
    this.sequence = sequence;
  }

  /**
   * Getter of the array representation of the player sequence
   * 
   * @return
   * @author hendiehl
   */
  public int[] getSequence() {
    return sequence;
  }
}
