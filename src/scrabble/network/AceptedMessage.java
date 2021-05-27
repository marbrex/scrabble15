package scrabble.network;

import java.io.Serializable;
import scrabble.model.MessageType;
import scrabble.model.Player;

public class AceptedMessage extends Message implements Serializable {
  /**
   * Message to inform a player that his join was successful. Will start the screen change from the
   * finder screen to the lobby screen.
   * 
   * @author hendiehl
   */

  private static final long serialVersionUID = 1L;
  /** port of the ChatServer */
  private int port;

  /**
   * Constructor for the AceptedMessage, send when a player successfully joined a lobby.
   * 
   * @param type MessageType of the Message for identifying
   * @param owner HumanPlayer which sends the message.
   * @param port Port on which the ChatServer is running.
   * @author hendiehl
   */
  public AceptedMessage(MessageType type, Player owner, int port) {
    super(type, owner);
    this.port = port;
  }

  /**
   * Getter Method for the chat port Integer.
   * 
   * @return port of a running chat server
   * @author hendiehl
   */
  public int getPort() {
    return port;
  }

}
