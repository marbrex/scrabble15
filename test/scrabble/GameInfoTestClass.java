package scrabble.test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import scrabble.model.AiPlayer;
import scrabble.model.GameInformationController;
import scrabble.model.GameStatusType;
import scrabble.model.HumanPlayer;
import scrabble.model.Player;
import scrabble.network.NetworkPlayer;

class GameInfoTestClass {
  /**
   * Test class for the main functions of the GameInfoController. The test will take a minute
   * because some methods have an implemented waiting time for the lobby election.
   * 
   * @author hendiehl
   */


  GameInformationController game = new GameInformationController(); // test constructor

  /**
   * Method to check the addPlayer method which is responsible for adding players to the intern list
   * in dependency of the maximum lobby members allowed.
   * 
   * @author hendiehl
   */
  @Test
  void testAddPlayer() {
    TestProtocol test1 = new TestProtocol();
    // which protocol is added doesn't matter because it is developed for the interface.
    assertEquals(true, this.game.addPlayer(test1));
    TestProtocol test2 = new TestProtocol();
    assertEquals(true, this.game.addPlayer(test2));
    TestProtocol test3 = new TestProtocol();
    assertEquals(true, this.game.addPlayer(test3));
    TestProtocol test4 = new TestProtocol();
    assertEquals(true, this.game.addPlayer(test4));
    // 4 players allowed for a network Lobby.
    TestProtocol test5 = new TestProtocol();
    assertEquals(false, this.game.addPlayer(test5));
  }

  /**
   * Method to check if the getPlayerAmount method is executed right and return the amount of
   * players in the intern player list.
   * 
   * @author hendiehl
   */
  @Test
  void testGetPlayerAmount() {
    // try to add 10 instances of protocols and add them to the player list.
    for (int i = 0; i < 10; i++) {
      TestProtocol tester = new TestProtocol();
      this.game.addPlayer(tester);
    }
    // check if the internal list have the right player amount after adding.
    assertEquals(4, this.game.getPlayerAmount());
  }

  /**
   * Method to check the updateAllLobby method and check if all lobby members will receive the same
   * list of Players.
   * 
   * @author hendiehl
   */
  @Test
  void testUpdateAllLobbys() {
    // List of the tested network players
    ArrayList<TestProtocol> player = new ArrayList<TestProtocol>();
    // filling the list.
    for (int i = 0; i < 4; i++) {
      TestProtocol tester = new TestProtocol();
      // Add a player class to the Protocol
      // adding them to test list.
      player.add(tester);
      // adding them to GameInfoController
      this.game.addPlayer(tester);
    }
    // performing method
    this.game.updateAllLobbys();
    // now all protocols should get the same List.
    assertEquals(player.get(0).players, player.get(1).players);
    assertEquals(player.get(1).players, player.get(2).players);
    assertEquals(player.get(2).players, player.get(3).players);
    assertEquals(player.get(3).players, player.get(0).players);
  }

  /**
   * Method to check the full lobby process. Will check if the class recognize a full lobby and
   * starts the election process with the StartHandler.
   * 
   * @author hendiehl
   */
  @Test
  synchronized void testCheckLobbySize() {
    ArrayList<TestProtocol> player = new ArrayList<TestProtocol>();
    for (int i = 0; i < 4; i++) {
      TestProtocol tester = new TestProtocol();
      // Add a player class to teh Protocol
      // adding them to test list.
      player.add(tester);
      // adding them to GameInfoController
      this.game.addPlayer(tester);
    }
    // performing the method with huge intern Impact();
    this.game.checkLobbySize();
    // check if the status is changed, the status should be now GAME
    assertEquals(GameStatusType.GAME, this.game.getStatus());
    try {
      this.wait(62000); // needed because a StartGameHandler is started always with a wait period.
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    /*
     * Now all actions should be performed. List of actions which should be performed. A
     * Full-Message is send. A Field-Message is send. A Dictionary-Message is send. A Start-Message
     * is send.
     */
    // Test Full-Message for all protocols
    for (int i = 0; i < 4; i++) {
      assertEquals(true, player.get(i).fullPerformed);
    }
    // Test Field-Message for all protocols
    for (int i = 0; i < 4; i++) {
      assertEquals(true, player.get(i).fieldPerformed);
    }
    // Test Dictionary-Message for all protocols
    for (int i = 0; i < 4; i++) {
      assertEquals(true, player.get(i).dictionaryPerformed);
    }
    // Test Start-Message for all protocols
    for (int i = 0; i < 4; i++) {
      assertEquals(true, player.get(i).startPerformed);
    }
  }

  /**
   * Method to check if the deletePlayer method works right. Checks the player deletion from the
   * intern list and if the sequence election process can handle the election even if a player is
   * deleted in the critical election process.
   * 
   * @author hendiehl
   */
  @Test
  void testDeletePlayer() {
    /*
     * To test the deletePlayer method i will fist add two players to the list and then use
     * addSequnce method to add the election result from one player. After that i will delete the
     * second player to check if the class handle the player loss and start the game properly.
     * 
     */
    TestProtocol test1 = new TestProtocol();
    TestProtocol test2 = new TestProtocol();
    this.game.addPlayer(test1);
    this.game.addPlayer(test2);
    // Now two players are in the list.
    int[] posOfTest1 = {1, 2, 0, 0}; // player 1 chose himself as first player in the election
    this.game.addSequence(posOfTest1, test1);
    // Now the second player will be deleted. The class should handle an player loss during the
    // election procedure
    // by checking if all other players (here only test1) gave their sequence. In this case this
    // should cause the
    // game start because every other player gave his sequence.
    this.game.deletePlayer(test2);
    // The game should start for test1
    assertTrue(test1.gamePerformed);
    // The game should not be started for test2 because he left the lobby
    assertFalse(test2.gamePerformed);
    // The list should now be filled with 3 AiPlayers to replace player test2.
    int aiCounter = 0;
    for (int i = 0; i < 4; i++) {
      if (test1.players.get(i) instanceof AiPlayer) {
        aiCounter++;
      }
    }
    assertTrue(aiCounter == 3);
  }

  /**
   * Test method to check if the addSequence procedure works right. Checks if the player sequence is
   * set right with the parameters of the election of every single player. Also checks if the game
   * starts after all player add their election result.
   * 
   * @author hendiehl
   */
  @Test
  void testAddSequence() {
    // here only two protocols are added to test the fill method by game start
    TestProtocol test1 = new TestProtocol();
    TestProtocol test2 = new TestProtocol();
    this.game.addPlayer(test1);
    this.game.addPlayer(test2);
    assertEquals(2, this.game.getPlayerAmount()); // should be two
    /*
     * Here the election positions form the players are imitated. Test1 and Test2 will be choose
     * Test2 as first player and Test1 as second player. It is always a list of 4 int send.
     */
    int[] posOfTest1 = {2, 1, 0, 0};
    int[] posOfTest2 = {2, 1, 0, 0};
    this.game.addSequence(posOfTest1, test1);
    // Here the game should not be started because not all players send their election result
    // This means that a game message is here not send and the lobby not filled with
    // LobbyAiProtocols.
    assertEquals(false, test1.gamePerformed); // check if the Game-Message is send
    assertEquals(false, test2.gamePerformed);
    assertFalse(4 == this.game.getPlayerAmount()); // check if the lobby size is filled
    // Here the second player in the list add his sequence which will cause : filling the lobby, set
    // a sequence and start a GameHandler
    this.game.addSequence(posOfTest2, test2);
    // Now the player list should be sort in order of the election : which means that all players
    // got the same updated player list with the test2 player instance on the first position.
    // Because if have no access to the internal player list of the GameInformationsController
    // i identify the protocols on there internal player class.
    assertEquals(test1.players, test2.players); // check if the got the same list
    assertEquals(test2.getPlayer(), test1.players.get(0)); // check if test2 is on first position by
                                                           // test1
    assertEquals(test2.getPlayer(), test2.players.get(0)); // check if test2 is on first position by
                                                           // test2
    // Also the player Id should be set yet in same order of the election order.
    assertTrue(test2.getPlayer().getId() == 1);
    assertTrue(test1.getPlayer().getId() == 2);
    // Because by start of the method there were two players in the list, the players list should
    // now be
    // filled with AIPlayers.
    assertTrue(4 == this.game.getPlayerAmount());
    // The list should now contain two AiPlayers as spot check i use test1
    int aiCounter = 0;
    for (int i = 0; i < 4; i++) {
      if (test1.players.get(i) instanceof AiPlayer) {
        aiCounter++;
      }
    }
    assertTrue(aiCounter == 2); // check if for this case the right amount of AiPlayers are in the
                                // list.
    // In last consequence a Game-Message should be send
    assertTrue(test1.gamePerformed);
    assertTrue(test2.gamePerformed);
  }


  /*
   * Test class for JUni testing, the test class has the function to control internal actions of the
   * GameInformationsController. The use of an specific class should be not i conflict with the test
   * paradigm because the GameInformationsController is written for interfaces not specific classes.
   * 
   * @author hendiehl
   *
   */
  private class TestProtocol implements NetworkPlayer {
    private ArrayList<Player> players;
    private boolean startPerformed; // imitates a startMessage sending
    private boolean gamePerformed; // imitates a gameMessage sending
    private boolean fullPerformed; // imitates a fullMessage sending
    private boolean fieldPerformed; // imitates a fieldMessage sending;
    private boolean dictionaryPerformed; // imitates a dictionaryMessage sending
    private int pos;
    private HumanPlayer profile = new HumanPlayer();

    private TestProtocol() {
      this.profile.setName("Tester");
    }


    @Override
    public Player getPlayer() {
      return this.profile;
    }

    @Override
    public void updateLobbyinformation(ArrayList<Player> playersArrayList) {
      // here the player list is updated
      this.players = playersArrayList;
    }

    @Override
    public void addSequence(int i) {
      this.pos += i;
      System.out.println("Test : Sequnece added : " + i);
    }

    @Override
    public int getSequencePos() {
      System.out.println("Test : Return sequence :" + this.pos);
      return this.pos;
    }

    @Override
    public void sendFullMessage() {
      this.fullPerformed = true;
    }

    @Override
    public void sendStartMessage() {
      this.startPerformed = true;
    }

    @Override
    public void sendGameMessage(ArrayList<Player> players) {
      this.gamePerformed = true;
      this.players = players;
    }

    @Override
    public void startMove(int turn, int id) {
      // TODO Auto-generated method stub

    }

    @Override
    public void informOther(int turn, int id) {
      // TODO Auto-generated method stub

    }

    @Override
    public void sendFieldMessage(String path) {
      this.fieldPerformed = true;
    }

    @Override
    public void sendDictionaryMessage(String dictionaryContent) {
      this.dictionaryPerformed = true;
    }

    @Override
    public void sendActionMessage(String action, int points, int id) {
      // TODO Auto-generated method stub

    }


    @Override
    public void resetGameInfoCon(GameInformationController game) {
      // TODO Auto-generated method stub

    }

  }
}
