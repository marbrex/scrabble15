package scrabble.network;

import java.io.Serializable;

import scrabble.model.Player;
import scrabble.model.MessageType;

public class Message implements Serializable{
	/**
	 * Basic network Message, identified by its Type and its owner
	 * @author Hendrik Diehl
	 */
	private MessageType type;
	private Player owner;
	/**
	 * Constructor for a basic network Message
	 * @param type MessageType of the Message
	 * @param owner client which sends the Message
	 */
	public Message(MessageType type, Player owner) {
		this.type = type;
		this.owner = owner;
	}
	public MessageType getType() {
		return this.type;
	}
	public Player getOwner() {
		return this.owner;
	}
}
