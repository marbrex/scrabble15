package scrabble.network;

import scrabble.model.MessageType;
import scrabble.model.Player;

public class AceptedMessage extends Message{
  /** port of the ChatServer */
  private int port;
  /**
   * Constructor for the AceptedMessage, send when a player successfully joined a lobby 
   * @param type MessageType of the Message for identifying 
   * @param owner HumanPlayer which sends the message.
   * @param port Port on which the ChatServer is running.
   */
  public AceptedMessage(MessageType type, Player owner, int port) {
    super(type, owner);
    this.port = port;
  }
  /**
   * Getter Method for the port Integer
   * @return port of a running chat server
   */
  public int getPort() {
    return port;
  }
  
}
