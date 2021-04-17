package scrabble.network;

import java.io.Serializable;
import java.util.ArrayList;

import scrabble.model.Player;
import scrabble.model.MessageType;

public class LobbyInformationMessage extends Message implements Serializable {
  /**
   * Message for more detailed information. Used primarily to update and show new clients in a lobby
   * if a new one join the lobby.
   * 
   * @author hendiehl
   */
  /** list of all human player in the lobby */
  private ArrayList<Player> players;

  /**
   * Constructor for a message which will inform joined clients about the members of a lobby
   * 
   * @param type
   * @param owner
   * @param players list of all players in the lobby.
   */
  /**
   * constructor for setting the list of players in a lobby
   * 
   * @param type type of the network message
   * @param owner owner of the network message
   * @param players list of human lobby players
   */
  public LobbyInformationMessage(MessageType type, Player owner, ArrayList<Player> players) {
    super(type, owner);
    this.players = players;
  }

  /**
   * getter method for the player list
   * 
   * @return list of the players in the lobby
   */
  public ArrayList<Player> getPlayers() {
    return players;
  }

}
