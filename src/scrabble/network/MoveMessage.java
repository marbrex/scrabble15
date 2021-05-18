package scrabble.network;

import scrabble.model.MessageType;
import scrabble.model.Player;

public class MoveMessage extends Message {
  /**
   * Move message which is send to player if he is on move during a network game. Will hold a turn
   * variable to inform the player about the turn counter.
   * 
   * @author hendiehl
   */


  private int turn;

  /**
   * Constructor which sets the turn variable to send a client information about the turn counter
   * during a running network game.
   * 
   * @param type MessageType
   * @param owner Owner of the Message
   * @author hendiehl
   */
  public MoveMessage(MessageType type, Player owner, int turn) {
    super(type, owner);
    this.turn = turn;
  }

  /**
   * Getter of the turn variable.
   * 
   * @return turn int counter of the turn number during a game.
   * @author hendiehl
   */
  public int getTurn() {
    return turn;
  }

}
