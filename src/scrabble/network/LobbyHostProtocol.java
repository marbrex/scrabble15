package scrabble.network;

import java.util.ArrayList;

import scrabble.GameLobbyController;
import scrabble.model.GameInformationController;

public class LobbyHostProtocol implements NetworkPlayer{
	/**
	 * Dummy protocol of an host which handle the screen update in an lobby/game
	 * @author Hendrik Diehl
	 */
	private GameLobbyController gameLobby;
	private GameInformationController gameInfoController;
	
	/**
	 * Constructor
	 * @param gameLobby controller of the screen
	 */
	public LobbyHostProtocol(GameLobbyController gameLobby, GameInformationController gameInfo) {
		this.gameLobby = gameLobby;
		this.gameInfoController = gameInfo;
	}
	@Override
	public void transformProtocol() {
		// TODO Auto-generated method stub
		//implement ?
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
		for(int i = 0; i < 4; i++) {
			gameLobby.setProfileNotVisible(i);
		}
		for(int i = 0; i < players.size(); i++) {
			gameLobby.setProfileVisible(i, players.get(i));
		}
		
	}
	/**
	 * method to delete an player from the lobby 
	 * @param i player want to deleted
	 */
	public void kickPlayer(int i) {
		if(i > 0) { //at the moment  the host is 0 (every time), will be changed in future,
			this.gameInfoController.kickPlayer(i);
		}
		
	}

}