package scrabble.network;

import java.io.Serializable;
import scrabble.model.MessageType;
import scrabble.model.Player;

public class EndMessage extends Message implements Serializable {
  /**
   * EndMessages will be send after a player ends his move, they have the function to inform the
   * other players about the actions a player performed. It will also send the points of a player
   * turn for saving them at the server.
   * 
   * @author hendiehl
   */
  
  private static final long serialVersionUID = 1L;
  private int points;
  private String action;

  /**
   * Constructor which sets the action string which holds information about the action a player
   * performed in his latest move and the points he gained through this action.
   * 
   * @param type MessageType of the message.
   * @param owner Owner of the message.
   * @param action move information in string format.
   * @param points points a player gained in his latest move.
   * @author hendiehl
   */
  public EndMessage(MessageType type, Player owner, String action, int points) {
    super(type, owner);
    this.action = action;
    this.points = points;
  }

  /**
   * Getter for the points a player performed in his move.
   * 
   * @return points int representation
   */
  public int getPoints() {
    return points;
  }

  /**
   * Getter for the string representation of actions a player performed in his move.
   * 
   * @return action string
   */
  public String getAction() {
    return action;
  }

}
