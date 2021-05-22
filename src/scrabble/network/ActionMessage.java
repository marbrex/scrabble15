package scrabble.network;

import scrabble.model.MessageType;
import scrabble.model.Player;

public class ActionMessage extends Message {
  /**
   * Message class for sending move informations to all other players. Will send action informations
   * and gained points in reason to update the game field of others.
   * 
   * @author hendiehl
   */

  private int points;
  private String action;
  private int id;

  /**
   * Constructor which sets the action string which holds information about the action a player
   * performed in his latest move and the points he gained through this action. Also the id of the
   * player will be send in reason to add the action of an other player on the own game field
   * 
   * @param type MessageType of the message
   * @param owner Owner of the message
   * @param action move information in string format
   * @param points points a player gained in his latest move
   * @param id Player id of the performer of the actions
   * @author hendiehl
   */
  public ActionMessage(MessageType type, Player owner, String action, int points, int id) {
    super(type, owner);
    this.action = action;
    this.points = points;
    this.id = id;
  }

  /**
   * Getter for the points a player performed in his move.
   * 
   * @return points int representation
   * @author hendiehl
   */
  public int getPoints() {
    return points;
  }

  /**
   * Getter for the string representation of actions a player performed in his move
   * 
   * @return action string
   * @author hendiehl
   */
  public String getAction() {
    return action;
  }

  /**
   * Getter method of the id of the player which performed the action.
   * 
   * @return id of the player which performed the last action
   * @author hendiehl
   */
  public int getId() {
    return id;
  }


}
