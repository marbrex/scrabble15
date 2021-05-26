package scrabble.network;

import java.io.Serializable;

import scrabble.model.Player;
import scrabble.model.MessageType;

public class Message implements Serializable {
  /**
   * Basic network Message, identified by its Type and its owner.
   * 
   * @author hendiehl
   */

  /** type of the network message for identifying purpose */
  private MessageType type;
  /** client who sends the message **/
  private Player owner;
  private static final long serialVersionUID = 1L;

  /**
   * Constructor of the basic network message.
   * 
   * @param type type of the message to identify and react.
   * @param owner owner of the message who will send the message.
   * @author hendiehl
   */
  public Message(MessageType type, Player owner) {
    this.type = type;
    this.owner = owner;
  }

  /**
   * Getter method for the type.
   * 
   * @return type of the message
   * @author hendiehl
   */
  public MessageType getType() {
    return this.type;
  }

  /**
   * Getter method for the owner.
   * 
   * @return owner of the message
   * @author hendiehl
   */
  public Player getOwner() {
    return this.owner;
  }
}
