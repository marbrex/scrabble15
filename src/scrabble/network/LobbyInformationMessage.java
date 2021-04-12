package scrabble.network;

import java.io.Serializable;
import java.util.ArrayList;

import scrabble.model.Player;
import scrabble.model.MessageType;

public class LobbyInformationMessage extends Message implements Serializable{
	/**
	 * Message for more detailed information. Used primarily to update and show new clients in a lobby if a
	 * new one join the lobby.
	 * @author Hendrik Diehl
	 */
	private ArrayList<Player> players;
	/**
	 * Constructor for a message which will inform joined clients about the members of a lobby
	 * @param type
	 * @param owner
	 * @param players list of all players in the lobby.
	 */
	public LobbyInformationMessage(MessageType type, Player owner, ArrayList<Player> players) {
		super(type, owner);
		this.players = players;
	}
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
}