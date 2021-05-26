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

  private static final long serialVersionUID = 1L;
  private int turn;
  private int id;

  /**
   * Constructor which sets the turn variable to send a client information about the turn counter
   * during a running network game and also about the id of the player himself.
   * 
   * @param type MessageType.
   * @param owner Owner of the Message.
   * @param turn turn counter of the actual game.
   * @param id Id of the player currently on move, which means it will refer always to the player
   *        which get this message.
   * @author hendiehl
   */
  public MoveMessage(MessageType type, Player owner, int turn, int id) {
    super(type, owner);
    this.turn = turn;
    this.id = id;
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

  /**
   * Getter of the player id which actually on move. Will in this case always refer to the player
   * who gets this message.
   * 
   * @return id of the receiver himself.
   */
  public int getId() {
    return id;
  }

}
