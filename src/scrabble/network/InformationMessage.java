package scrabble.network;

import java.io.Serializable;

import scrabble.model.GameStatusType;
import scrabble.model.MessageType;
import scrabble.model.Player;

public class InformationMessage extends Message implements Serializable{
	/**
	 * information message class used as first message which is send to connecting clients to give them informations
	 * about the lobby.
	 * @author Hendrik Diehl
	 */
	private GameStatusType status;
	private int lobbyPlayers;
	/**
	 * Constructor for the first informations which will be send to a client
	 * @param type
	 * @param owner
	 * @param status status of the lobby = in lobby or in game
	 * @param amount amount of the players in a lobby
	 */
	public InformationMessage(MessageType type, Player owner, GameStatusType status, int amount) {
		super(type, owner);
		this.status = status;
		this.lobbyPlayers = amount;
	}
	public GameStatusType getStatus() {
		return status;
	}
	public int getLobbyPlayers() {
		return lobbyPlayers;
	}
	
}
