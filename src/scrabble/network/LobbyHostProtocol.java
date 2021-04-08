package scrabble.network;

import java.util.ArrayList;

import scrabble.GameLobbyController;

public class LobbyHostProtocol implements NetworkPlayer{
	/**
	 * Dummy protocol of an host which handle the screen update in an lobby/game
	 * @author Hendrik Diehl
	 */
	private GameLobbyController gameLobby;
	
	/**
	 * Constructor
	 * @param gameLobby controller of the screen
	 */
	public LobbyHostProtocol(GameLobbyController gameLobby) {
		this.gameLobby = gameLobby;
	}
	@Override
	public void transformProtocol() {
		// TODO Auto-generated method stub
		//implemnetd ?
	}

	/**
	 * method to get the player class instance.
	 */
	@Override
	public String getPlayer() {
		return null;
		// TODO Auto-generated method stub
		
	}
	/**
	 * method to update the player profiles on the screen.
	 */
	@Override
	public void updateLobbyinformation(ArrayList<String> players) {
		for(int i = 0; i < players.size(); i++) {
			gameLobby.setProfileVisible(i);
		}
		
	}

}
