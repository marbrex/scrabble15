package scrabble.test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scrabble.model.GameInformationController;
import scrabble.model.HumanPlayer;
import scrabble.model.Player;
import scrabble.network.GameHandler;
import scrabble.network.NetworkPlayer;

class GameHandlerTest {
  /**
   * Test class of the GameHandler which is responsible for the turn sequence, the move allocation
   * and the action forwarding. Test will take some seconds to handler different call hierarchy's of
   * different threads.
   * 
   * @author hendiehl
   */


  // GameController only for constructor
  GameInformationController gameInfo = new GameInformationController();
  GameHandler game;
  ArrayList<NetworkPlayer> player;

  @BeforeEach
  public void setUpHandler() {
    player = new ArrayList<NetworkPlayer>();
    for (int i = 0; i < 4; i++) {
      TestProtocol tester = new TestProtocol();
      tester.getPlayer().setId(i + 1);
      player.add(tester);
    }
    game = new GameHandler(gameInfo, player);
  }

  /**
   * Method to start the overall start procedure and his expected behavior
   * 
   * @author hendiehl
   */
  @Test
  synchronized void testStartGame() {
    game.startGame();
    try {
      this.wait(1000);
      // Is needed because the call hierarchy of the Junit Method is faster than
      // the procedure of starting an other Thread
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // The GameHandler is now started but until not all player confirm their loading the handler
    // should wait.
    assertEquals(Thread.State.WAITING, game.getState());
    // In this phase no player should have started an move or received an other notification
    for (NetworkPlayer tester : player) {
      TestProtocol test = (TestProtocol) tester;
      assertFalse(test.movePerformed);
      assertFalse(test.otherPerformed);
    }
  }

  /**
   * Method to check if a player can end his move , which means that his actions will be transmitted
   * to the other players and the turn procedure give the next player in list the move.
   * 
   * @author hendiehl
   */
  @Test
  synchronized void testEndMoveForTime() {
    game.startGame(); // starting the handler
    try {
      this.wait(1000);
      // Is needed because the call hierarchy of the Junit Method is faster than
      // the procedure of starting an other Thread
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    game.invokeTurnPhase(); // starting turn phase
    try {
      this.wait(1000);
      // Is needed because the call hierarchy of the Junit Method is faster than
      // the procedure of starting an other Thread
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    String move = "Action";
    // At this point the first TestProtocol of the list should be on the move and the thread is
    // waiting for his move actions.
    this.game.endMoveForTime(move, 1000); // normally called by GameInformationsController
    /*
     * The first player now have ended his move and all other players should be informed about the
     * actions of the last player.
     */
    try {
      this.wait(1000);
      // Is needed because the call hierarchy of the Junit Method is faster than
      // the procedure of starting an other Thread
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    for (int i = 1; i < player.size(); i++) {
      TestProtocol tester = (TestProtocol) player.get(i);
      assertTrue(tester.actionPerformed); // they got informed
      assertTrue(tester.action.equals(move)); // they received the move action
      assertTrue(1000 == tester.points); // they received the points of the move
    }
    // Also the second player should now be on the move.
    TestProtocol second = (TestProtocol) player.get(1);
    assertTrue(second.movePerformed);
    // And the thread should be waiting again
    assertEquals(Thread.State.WAITING, game.getState());
  }

  /**
   * Method for checking the function of the GameHandler shutdown, which should stop the GameHandler
   * thread from running.
   * 
   * @author hendiehl
   */
  @Test
  synchronized void testShutdown() {
    game.startGame(); // starting the handler
    try {
      this.wait(1000);
      // Is needed because the call hierarchy of the Junit Method is faster than
      // the procedure of starting an other Thread
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // Until here the result is the same as in the testStartGame method
    // But after the GameInformationsController start the the turn phase should start.
    game.invokeTurnPhase();
    // The thread have now given a player the turn and waits for his response.
    // Calling the shutdown method should wake the thread and stop the turn procedure
    game.shutdown();
    try {
      this.wait(1000);
      // Is needed because the call hierarchy of the Junit Method is faster than
      // the procedure of starting an other Thread
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // Now the thread should be left the turn loop and stop running because of an outrun of the run
    // method
    assertEquals(Thread.State.TERMINATED, game.getState());
  }

  /**
   * Method to check the start of the turn phase. Checks if the right players are given turn and if
   * the others be informed about that.
   * 
   * @author hendiehl
   */
  @Test
  synchronized void testInvokeTurnPhase() {
    game.startGame(); // starting the handler
    try {
      this.wait(1000);
      // Is needed because the call hierarchy of the Junit Method is faster than
      // the procedure of starting an other Thread
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // Until here the result is the same as in the testStartGame method
    // But after the GameInformationsController start the the turn phase should start.
    game.invokeTurnPhase();
    try {
      this.wait(1000);
      // Is needed because the call hierarchy of the Junit Method is faster than
      // the procedure of starting an other Thread
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
    }
    /*
     * Proofing the right behavior with an getState equation is quit difficult because of different
     * thread states But logically we can proof the right behavior because after the turn phase is
     * invoked the first player in list should be on the move
     */
    TestProtocol test = (TestProtocol) this.player.get(0); // first player in list.
    assertTrue(test.movePerformed);
    // All other players should not have received an move message in this state.
    for (int i = 1; i < player.size(); i++) {
      TestProtocol tester = (TestProtocol) player.get(i);
      assertFalse(tester.movePerformed);
      // But they should have received an other-message to inform them about the move of the first
      // player in list.
      assertTrue(tester.otherPerformed);
    }
    // The handler should now be sleeping again to wait until the player ends his move.
    assertEquals(Thread.State.WAITING, game.getState());
  }

  private class TestProtocol implements NetworkPlayer {

    private boolean actionPerformed;
    private boolean otherPerformed;
    private boolean movePerformed;
    private HumanPlayer player = new HumanPlayer();
    private String action;
    private int points;

    private TestProtocol() {
      this.player.setName("Tester");
    }

    @Override
    public Player getPlayer() {
      return this.player;
    }

    @Override
    public void updateLobbyinformation(ArrayList<Player> playersArrayList) {
      // TODO Auto-generated method stub

    }

    @Override
    public void addSequence(int i) {
      // TODO Auto-generated method stub

    }

    @Override
    public int getSequencePos() {
      // TODO Auto-generated method stub
      return 0;
    }

    @Override
    public void sendFullMessage() {
      // TODO Auto-generated method stub

    }

    @Override
    public void sendStartMessage() {
      // TODO Auto-generated method stub

    }

    @Override
    public void sendGameMessage(ArrayList<Player> players) {
      // TODO Auto-generated method stub

    }

    @Override
    public void startMove(int turn, int id) {
      this.movePerformed = true;
      System.out.println("Move performed");
    }

    @Override
    public void informOther(int turn, int id) {
      this.otherPerformed = true;
    }

    @Override
    public void sendFieldMessage(String path) {
      // TODO Auto-generated method stub

    }

    @Override
    public void sendDictionaryMessage(String dictionaryContent) {
      // TODO Auto-generated method stub

    }

    @Override
    public void sendActionMessage(String action, int points, int id) {
      this.actionPerformed = true;
      this.action = action;
      this.points = points;
    }

    @Override
    public void resetGameInfoCon(GameInformationController game) {
      // TODO Auto-generated method stub

    }

  }
}
