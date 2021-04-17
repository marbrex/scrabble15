package scrabble.network;

import java.io.Serializable;

import scrabble.model.Player;
import scrabble.model.MessageType;

public class Message implements Serializable {
  /**
   * Basic network Message, identified by its Type and its owner
   * 
   * @author hendiehl
   */
  /** type of the network message for identifying purpose */
  private MessageType type;
  /** client who sends the message **/
  private Player owner;

  /**
   * Constructor for a basic network Message
   * 
   * @param type MessageType of the Message
   * @param owner client which sends the Message
   */
  /**
   * constructor of the basic network message
   * 
   * @param type type of the message to identify and react
   * @param owner owner of the message who sends it
   */
  public Message(MessageType type, Player owner) {
    this.type = type;
    this.owner = owner;
  }

  /**
   * getter method for the type
   * 
   * @return type of the message
   */
  public MessageType getType() {
    return this.type;
  }

  /**
   * getter method for the owner
   * 
   * @return owner of the message
   */
  public Player getOwner() {
    return this.owner;
  }
}
