package scrabble;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import scrabble.model.GameInformationController;
import scrabble.model.HumanPlayer;
import scrabble.model.Player;
import scrabble.network.GameHandler;
import scrabble.network.LobbyHostProtocol;
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
      // Is needed because the call hierarchy of the Junit Method is faster than sometimes the
      // execution on other thread
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    String move = "{\n" + " \"nb\": \"2\",\n" + " \"words\": [\n" + "  {\n"
        + "   \"word\": \"BA\",\n" + "   \"points\": \"8\",\n" + "   \"tiles\": [\n" + "    {\n"
        + "     \"letter\": \"B\",\n" + "     \"value\": \"3\",\n" + "     \"row\": \"7\",\n"
        + "     \"col\": \"7\",\n" + "     \"isBlank\": \"false\"\n" + "    },\n" + "    {\n"
        + "     \"letter\": \"A\",\n" + "     \"value\": \"1\",\n" + "     \"row\": \"7\",\n"
        + "     \"col\": \"8\",\n" + "     \"isBlank\": \"false\"\n" + "    }\n" + "   ]\n"
        + "  },\n" + "  {\n" + "   \"word\": \"BAG\",\n" + "   \"points\": \"12\",\n"
        + "   \"tiles\": [\n" + "    {\n" + "     \"letter\": \"B\",\n" + "     \"value\": \"3\",\n"
        + "     \"row\": \"7\",\n" + "     \"col\": \"7\",\n" + "     \"isBlank\": \"false\"\n"
        + "    },\n" + "    {\n" + "     \"letter\": \"A\",\n" + "     \"value\": \"1\",\n"
        + "     \"row\": \"7\",\n" + "     \"col\": \"8\",\n" + "     \"isBlank\": \"false\"\n"
        + "    },\n" + "    {\n" + "     \"letter\": \"G\",\n" + "     \"value\": \"2\",\n"
        + "     \"row\": \"7\",\n" + "     \"col\": \"9\",\n" + "     \"isBlank\": \"false\"\n"
        + "    }\n" + "   ]\n" + "  }\n" + " ],\n" + " \"score\": \"20\"\n" + "}";
    // At this point the first TestProtocol of the list should be on the move and the thread is
    // waiting for his move actions.
    this.game.endMoveForTime(move, 1000); // normally called by GameInformationsController
    /*
     * The first player now have ended his move and all other players should be informed about the
     * actions of the last player.
     */
    try {
      this.wait(1000);
      // Is needed because the call hierarchy of the Junit Method is faster than sometimes the
      // execution on other thread
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
      // Is needed because the call hierarchy of the Junit Method is faster than sometimes the
      // execution on other thread
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // Now the thread should be left the turn loop and stop running because of an outrun of the run
    // method
    assertEquals(Thread.State.TERMINATED, game.getState());
  }

  /**
   * Method to test the game ending procedure, in which the players should be informed about the
   * winner and the screen change.
   * 
   * @author hendiehl
   */
  @Test
  synchronized void testGameEndProcedure() {
    game.startGame(); // starting the handler
    LobbyHostProtocol host = new LobbyHostProtocol();
    game.setHost(host);
    /*
     * The Host is here only set because the GameHandler need a host instance during lobby return
     * procedure. This is the case because normally every lobby has a corresponding host. The host
     * is here not in use because the test runs with TestProtocols with less GUI and network
     * dependency.
     */
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
      e.printStackTrace();
    }
    /*
     * The first player make a valid move and gained 1000 points.
     */
    String move = "{\n" + " \"nb\": \"2\",\n" + " \"words\": [\n" + "  {\n"
        + "   \"word\": \"BA\",\n" + "   \"points\": \"8\",\n" + "   \"tiles\": [\n" + "    {\n"
        + "     \"letter\": \"B\",\n" + "     \"value\": \"3\",\n" + "     \"row\": \"7\",\n"
        + "     \"col\": \"7\",\n" + "     \"isBlank\": \"false\"\n" + "    },\n" + "    {\n"
        + "     \"letter\": \"A\",\n" + "     \"value\": \"1\",\n" + "     \"row\": \"7\",\n"
        + "     \"col\": \"8\",\n" + "     \"isBlank\": \"false\"\n" + "    }\n" + "   ]\n"
        + "  },\n" + "  {\n" + "   \"word\": \"BAG\",\n" + "   \"points\": \"12\",\n"
        + "   \"tiles\": [\n" + "    {\n" + "     \"letter\": \"B\",\n" + "     \"value\": \"3\",\n"
        + "     \"row\": \"7\",\n" + "     \"col\": \"7\",\n" + "     \"isBlank\": \"false\"\n"
        + "    },\n" + "    {\n" + "     \"letter\": \"A\",\n" + "     \"value\": \"1\",\n"
        + "     \"row\": \"7\",\n" + "     \"col\": \"8\",\n" + "     \"isBlank\": \"false\"\n"
        + "    },\n" + "    {\n" + "     \"letter\": \"G\",\n" + "     \"value\": \"2\",\n"
        + "     \"row\": \"7\",\n" + "     \"col\": \"9\",\n" + "     \"isBlank\": \"false\"\n"
        + "    }\n" + "   ]\n" + "  }\n" + " ],\n" + " \"score\": \"20\"\n" + "}";
    game.endMoveForTime(move, 1000);
    try {
      this.wait(1000);
      // Is needed because the call hierarchy of the Junit Method is faster than
      // the procedure of starting an other Thread
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    /*
     * Now the following players make 6 turns without any action, which means that the game will end
     * because of 6 consecutively action less moves.
     */
    String actionLess =
        "{\n" + " \"nb\": \"0\",\n" + " \"words\": [\n" + " ],\n" + " \"score\": \"0\"\n" + "}";
    for (int i = 0; i < 6; i++) {
      this.game.endMoveForTime(actionLess, 0); // no action = no points
      try {
        this.wait(1000);
        // Is needed because the call hierarchy of the Junit Method is faster than sometimes the
        // execution on other thread
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    // The game should now be ended because of 6 moves without action.
    // This means that all player will be informed about a coming screen change.
    for (int i = 0; i < player.size(); i++) {
      TestProtocol tester = (TestProtocol) player.get(i);
      assertTrue(tester.preparePerformed);
    }
    try {
      this.wait(6000);
      // After the PrepareMethod the thread will wait 5 sec to give the screen the possibility to
      // inform the players about the coming screen change. With this 6sec waiting i will cover that
      // waiting period.
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // After that the winner will be calculated and the Players will receive a message to save their
    // game win in their DB.
    for (int i = 0; i < player.size(); i++) {
      TestProtocol tester = (TestProtocol) player.get(i);
      assertTrue(tester.DBPerformed);
    }
    // Also the first player should now be marked as winner because he was the only one with a move
    // with action.
    TestProtocol test = (TestProtocol) this.player.get(0);
    assertTrue(test.winner);
    // The other ones with only actionless moves will not be marked as winners.
    for (int i = 1; i < this.player.size(); i++) {
      TestProtocol test2 = (TestProtocol) this.player.get(i);
      assertFalse(test2.winner);
    }
    try {
      this.wait(1000);
      // Is needed because the call hierarchy of the Junit Method is faster than sometimes the
      // execution on other thread
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    /*
     * After the players are informed about their win condition the GameInfoControllers will be
     * reset in all necessary classes and the players will receive a message to perform a screen
     * change to the lobby screen.
     * 
     * Here will now also accrue a NullPointer, but that can be neglect in this case because it
     * appears in the reset of the GameInfoControllers while executing a server method. The server
     * is just not set because of the use of an test constructor.
     */
    for (int i = 0; i < this.player.size(); i++) {
      TestProtocol test2 = (TestProtocol) this.player.get(i);
      assertTrue(test2.resultPerformed);
    }
    // With the receive of this message the game screen will be left and the game will be ended
    // officially.
    try {
      this.wait(1000);
      // Is needed because the call hierarchy of the Junit Method is faster than sometimes the
      // execution on other thread
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // The GameHandler should now be terminated
    assertTrue(this.game.getState() == Thread.State.TERMINATED);
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
      // Is needed because the call hierarchy of the Junit Method is faster than sometimes the
      // execution on other thread
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
    private boolean preparePerformed;
    private boolean winner;
    private boolean DBPerformed;
    private boolean resultPerformed;

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

    @Override
    public void sendDBMessage(boolean won) {
      this.DBPerformed = true;
      this.winner = won;
    }

    @Override
    public void sendResultMessage(ArrayList<Player> players, int[] points,
        ArrayList<Player> ordered) {
      this.resultPerformed = true;

    }

    @Override
    public void sendBagSize(int size) {
      // TODO Auto-generated method stub

    }

    @Override
    public void sendDeleteMessage(int id) {
      // TODO Auto-generated method stub

    }

    @Override
    public void sendPrepMessageChange() {
      this.preparePerformed = true;
    }

  }
}
