package scrabble.network;

import java.util.ArrayList;

import scrabble.model.HumanPlayer;
import scrabble.model.Player;
import scrabble.GameLobbyController;
import scrabble.model.GameInformationController;

public class LobbyHostProtocol implements NetworkPlayer {
  /**
   * Dummy protocol of an host which handle the screen update in an lobby/game
   * 
   * @author hendiehl
   */
  /** controller of the gameLobby */
  private GameLobbyController gameLobby;
  /** controller for game information */
  private GameInformationController gameInfoController;
  /** player instance of the protocol host are a human player */
  private HumanPlayer player;
  /** Variable to compare the position in the list to create a game sequence of human player */
  private int sequencePos;

  /**
   * Constructor
   * 
   * @param gameLobby controller of the screen
   */
  public LobbyHostProtocol(GameLobbyController gameLobby, GameInformationController gameInfo) {
    this.gameLobby = gameLobby;
    this.gameInfoController = gameInfo;
    this.loadPlayer();
  }

  /**
   * method to get Player from Database
   */
  private void loadPlayer() {
    this.player = new HumanPlayer();
    this.player.setName("Host"); // dummy representation

  }

  @Override
  public void transformProtocol() {
    // TODO Auto-generated method stub
    // implement ?
  }

  /**
   * method to get the player class instance.
   */
  @Override
  public Player getPlayer() {
    return this.player;
  }

  /**
   * method to update the player profiles on the screen.
   */
  @Override
  public void updateLobbyinformation(ArrayList<Player> players) {
    this.gameLobby.resetProfileVisibility();
    for (int i = 0; i < players.size(); i++) {
      gameLobby.setProfileVisible(i, players.get(i).getName());
    }

  }

  /**
   * method to delete an player from the lobby
   * 
   * @param i player want to deleted
   */
  public void kickPlayer(int i) {
    if (i > 0) { // at the moment the host is 0 (every time), will be changed in future,
      this.gameInfoController.kickPlayer(i);
    }

  }

  /**
   * method to adding points to the internal positions in the game
   */
  public void addSequence(int i) {
    this.sequencePos += i;

  }

  /**
   * method to return the number representing the sequence election points
   */
  public int getSequencePos() {
    return this.sequencePos;
  }

  /**
   * starting the lobby maximum procedure
   */
  public void sendFullMessage() {
    this.gameLobby.startTimer();
    // do the game things -> not implemented yet
  }

}
