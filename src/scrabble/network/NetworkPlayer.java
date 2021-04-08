package scrabble.network;

import java.util.ArrayList;

public interface NetworkPlayer {
	/**
	 * Interface for a network player. Can be a lobby or game client or a game/lobby host.
	 * Provide methods which all types of network players should have.
	 * @author Hendrik Diehl
	 */
	public void transformProtocol();
	public String getPlayer();
	public void updateLobbyinformation(ArrayList<String> playersArrayList);
}
