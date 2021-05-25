package scrabble.network;

import java.io.Serializable;

import scrabble.model.GameStatusType;
import scrabble.model.MessageType;
import scrabble.model.Player;

public class InformationMessage extends Message implements Serializable {
  /**
   * Information message class used as first message which is send to connecting clients to give
   * them informations about the lobby.
   * 
   * @author hendiehl
   */

  private static final long serialVersionUID = 1L;
  private GameStatusType status;
  private int lobbyPlayers;

  /**
   * Constructor for the first informations which will be send to a client.
   * 
   * @param type MessageType of the message.
   * @param owner Owner of the message.
   * @param status Status of the lobby = in lobby or in game.
   * @param amount Amount of the players in a lobby.
   * @author hendiehl
   */
  public InformationMessage(MessageType type, Player owner, GameStatusType status, int amount) {
    super(type, owner);
    this.status = status;
    this.lobbyPlayers = amount;
  }

  /**
   * Getter of the MessageType to identify a join possibility from the start.
   * 
   * @return status GameStatusType
   * @author hendiehl
   */
  public GameStatusType getStatus() {
    return status;
  }

  /**
   * Getter of the amount of players in a lobby.
   * 
   * @return player amount
   * @author hendiehl
   */
  public int getLobbyPlayers() {
    return lobbyPlayers;
  }

}
