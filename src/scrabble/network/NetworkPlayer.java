package scrabble.network;

import java.util.ArrayList;

import scrabble.model.Player;

public interface NetworkPlayer {
	/**
	 * Interface for a network player. Can be a lobby or game client or a game/lobby host.
	 * Provide methods which all types of network players should have.
	 * @author Hendrik Diehl
	 */
	public void transformProtocol();
	public Player getPlayer();
	public void updateLobbyinformation(ArrayList<Player> playersArrayList);
	public void addSequence(int i);
	public int getSequencePos();
	public void sendFullMessage();
}
