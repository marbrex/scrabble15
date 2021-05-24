package scrabble.network;

import scrabble.model.MessageType;
import scrabble.model.Player;

public class OtherMessage extends Message {
  /**
   * Message to inform players in a game which players is actually on move
   * 
   * @author hendiehl
   */
  private int turn;
  private int id;

  /**
   * Constructor to set the position of the player who is actually on the move and the a turn
   * counter Send to the other players
   * 
   * @param type MessageType
   * @param owner Owner of the message
   * @param turn turn counter of the actual game status
   * @param id id of the player who is actually on move
   * @author hendiehl
   */
  public OtherMessage(MessageType type, Player owner, int turn, int id) {
    super(type, owner);
    this.turn = turn;
    this.id = id;
  }

  /**
   * Getter for the int representation of the player position in the list
   * 
   * @return turn counter
   * @author hendiehl
   */
  public int getTurn() {
    return this.turn;
  }

  /**
   * Getter of the id of the player which was given the move action
   * 
   * @return player id during the game
   */
  public int getId() {
    return id;
  }

}
