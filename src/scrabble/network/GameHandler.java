package scrabble.network;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import scrabble.model.AiPlayer;
import scrabble.model.GameInformationController;
import scrabble.model.Player;

public class GameHandler extends Thread {
  /**
   * GameHandler class has the function to control the turn procedure and the information
   * transmitting of a network game.
   * 
   * @author hendiehl
   */
  private GameInformationController game;
  /** boolean to control the execution of the thread */
  private boolean gameIsOn;
  /** working on a reference copy because of a deadlock */
  private ArrayList<NetworkPlayer> players;
  /** counter for turns without action */
  private int actionlessMove;
  /** actual player which is on Move */
  private NetworkPlayer actual;
  /** Counter of the player turns during a game */
  private int turn = 0;
  /** Map for saving the points */
  private HashMap<NetworkPlayer, Integer> points;
  private LobbyHostProtocol host;
  private NetworkPlayer winner;
  private boolean loading;
  private boolean bagIsEmpty;
  // is used to get a different behavior for a wanted and a unwanted ending
  private boolean isShutdown;

  // should i control a specific notify call ?
  /**
   * Constructor for the GameHandler which setting the corresponding GameInformationController
   * 
   * @param game GameInformationController
   * @param players list of all NetworkPlayers of the game.
   * @author hendiehl
   */
  public GameHandler(GameInformationController game, ArrayList<NetworkPlayer> players) {
    this.loading = true;
    this.game = game;
    this.players = players;
    this.points = new HashMap<NetworkPlayer, Integer>();
    // initialize the map and the host
    {
      for (NetworkPlayer player : this.players) {
        if (player instanceof LobbyHostProtocol) {
          this.points.put(player, 0);
          this.setHost((LobbyHostProtocol) player); // setting host;
        } else {
          this.points.put(player, 0);
        }
      }
    }
  }

  /**
   * Method of the Thread class which have the function to organize the game moves of players during
   * a game.
   * 
   * @author hendiehl
   */
  public void run() {
    System.err.println("GAME HANDLER : Started");
    this.waitMazimumTime(); // wait until all players finished loading.
    this.setGameControllers(); // setting the game controllers for the AiPlayers.
    while (gameIsOn) {
      System.out.println("GAME HANDLER : Make complete turn");
      this.makeATurn(); // Perhaps wrapping it in a synchronized method ?
    }
    if (!this.isShutdown) {
      // Here the GameHandler stop the turn procedure --> game ends.
      this.sendPrepareMessages(); // gave them time to see a information about lobby return
      this.calculateWinner(); // Calculate the winner with gained points.
      this.sendDBMessage(); // sending the win condition to the player DB.
      this.prepareLobbyReturn();
      // Create a new Instance of an GameInfoController and sending the results to other players.
    }
    System.err.println("GAME HANDLER : Outrun");
  }

  /**
   * Method to set the GameController of the game host by a AiPlayer. A AiPlayer will get grid
   * informations from the host instance.
   * 
   * @author hendiehl
   */
  private void setGameControllers() {
    System.out.println("GAME HANDLER : Set controller to Ai's");
    for (NetworkPlayer player : this.players) {
      if (player instanceof LobbyAiProtocol) {
        LobbyAiProtocol aiP = (LobbyAiProtocol) player;
        AiPlayer ai = (AiPlayer) aiP.getPlayer(); // Is always a AiPlayer by a LobbyHostProtocol.
        ai.setController(this.host.getGameScreen()); // setting the game controller of the host.
      }
    }
  }

  /**
   * Method to send the results of an network game for showing purpose and for protocol internal
   * saving.
   * 
   * @param list of players in protocol internal order = the host at the beginning.
   * @author hendiehl
   */
  private void sendResults(ArrayList<Player> list2) {
    System.out.println("GAME HANDLER : Prepare result sending");
    // Here a GameMessage can be used again because of same parameters.
    int[] points = new int[4];
    // All additional informations will be send in different arrays in same order like the player
    // list.
    // Is done this way to prevent data loose.
    // Prepare a Player instances list for after game screen.
    ArrayList<Player> list = new ArrayList<Player>();
    for (int i = 0; i < this.players.size(); i++) {
      points[i] = this.points.get(this.players.get(i)); // setting points sequence.
      list.add(players.get(i).getPlayer()); // Filling list.
    }
    // Now sending the Message to all players --> perhaps changing the screen in this moment.
    for (NetworkPlayer player : this.players) {
      if (!(player instanceof LobbyAiProtocol)) {
        // sending results to all players.
        player.sendResultMessage(list2, points, list);
        // The receive of this message will cause a screen change.
      }
    }
  }

  /**
   * Method to calculate the winner of an network game.
   * 
   * @author hendiehl
   */
  private void calculateWinner() {
    System.out.println("GAME HANDLER : Sort list after game.");
    this.players.sort(new Comparator<NetworkPlayer>() {

      // Method to sort them in reversed sequence
      @Override
      public int compare(NetworkPlayer arg0, NetworkPlayer arg1) {
        if (points.get(arg0) > points.get(arg1)) {
          return -1;
        } else if (points.get(arg0) < points.get(arg1)) {
          return 1;
        } else {
          if (players.indexOf(arg0) > players.indexOf(arg1)) {
            return -1;
          } else if (players.indexOf(arg0) < players.indexOf(arg1)) {
            return 1;
          } else {
            return 0;
          }
        }
      }

    });
    // Control prints
    for (NetworkPlayer player : this.players) {
      System.out.println("GAMA INFO : " + player.getPlayer().getName() + " with "
          + this.points.get(player) + " points");
    }
    this.winner = this.players.get(0); // First player has most points
  }

  /**
   * Method to send each Player his gained points in reason to save them in the Database.
   * 
   * @author hendiehl
   */
  private void sendDBMessage() {
    for (NetworkPlayer player : this.players) {
      if (!(player instanceof LobbyAiProtocol)) { // only non AiPlayers
        player.sendDBMessage(player.equals(winner));
        // inform them about their result
      }
    }

  }

  /**
   * Method to start the game procedure.
   * 
   * @author hendiehl
   */
  public void startGame() {
    this.gameIsOn = true;
    this.start(); // starting the thread --> run method
  }

  /**
   * Method representing a full play turn through all players.
   * 
   * @author hendiehl
   */
  private void makeATurn() {
    for (NetworkPlayer player : this.players) {
      this.checkRunning(); // check if a game should stop
      if (!this.gameIsOn) { // leaving the loop if a game stopped
        break;
      }
      System.out.println("GAME HANDLER : Player turn : " + player.getPlayer().getName());
      if (player instanceof LobbyAiProtocol) {
        System.out.println("GAME HANDLER : AI move");
        this.actual = player;
        this.informOthers();
        this.waitAiTime(); // Only in purpose to show AiPlayer on the field --> 10sek
        LobbyAiProtocol ai = (LobbyAiProtocol) player; // special move method of AiPlayer
        String action = ai.aiMove(); // calculating a move by the AiPlayer.
        System.out.println("GAME HANDLER : Ai action = " + action);
        // points have to be calculated.
        this.informAiActions(action); // special inform fo AiPlayer moves.
      } else { // In case : LobbyHost- or LobbyServerProtocol
        System.out.println("GAME HANDLER : Human move");
        this.actual = player;
        player.startMove(this.turn, this.actual.getPlayer().getId()); // here adding the ID.
        this.informOthers();
        this.waitMazimumTime(); // change to approach only by HumanPlayer
        // here work with player move info
        this.checkRunning(); // check if a game should be ended --> any condition
        // Perhaps here not needed
      }
      this.turn++; // increase the turn counter
      this.game.sendBagSize(); // after every turn.
    }

  }

  /**
   * Special Method to inform about AiPlayer actions. They only need to send to other HumanPLayers
   * which are not the host, because they work on the host gird instance. They also need a special
   * way to calculate move points, because the work on the host grid.
   * 
   * @param action action String of an AiPlayer.
   * @param i points gained by an AiPlayer.
   * @author hendiehl
   */
  private void informAiActions(String action) {
    System.out.println("GAME HANDLER : Inform about AiAction");
    JSONObject data = new JSONObject(action);
    JSONArray words = data.getJSONArray("words");
    // AiPlayer points have to be calculated on a special way
    if (words.length() == 0) {
      System.out.println("GAME HANDLER : Actionless Ai move");
      this.actionlessMove++;
    } else {
      for (NetworkPlayer player : this.players) {
        if (player instanceof LobbyServerProtocol) {
          player.sendActionMessage(action, 0, this.actual.getPlayer().getId());
          // Only LobbyServerProtocols need to be informed.
        }
      }
      this.actionlessMove = 0;

    }

  }

  /**
   * Method to give the game fields time to show the move of an AiPlayer.
   * 
   * @author hendiehl
   */
  private synchronized void waitAiTime() {
    try {
      this.wait(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Method to inform other players that a player is on the move.
   * 
   * @author hendiehl
   */
  private void informOthers() {
    System.out.println("GAME HANDLER : Inform others about move");
    for (NetworkPlayer player : this.players) { // go through them
      if (!player.equals(this.actual)) {
        player.informOther(this.turn, this.actual.getPlayer().getId()); // here sending the id + //
                                                                        // turn
      }
    }
  }

  /**
   * Method to break the loop if the game should stop.
   * 
   * @author hendiehl
   */
  private void checkRunning() {
    if (this.actionlessMove == 6) { // six successive turns without action.
      System.out.println("GAME HANDLER : Game end : actionless moves");
      this.gameIsOn = false; // ending the game.
    } else if (this.bagIsEmpty) { // The LetterBag is empty.
      System.out.println("GAME HANDLER : Game end : empty LetterBag");
      this.gameIsOn = false;
    }
  }

  /**
   * Method to waiting for a player turn. Interrupted by a NetworkPlayer to inform the server that
   * he finished his move.
   * 
   * @author hendiehl
   */
  private synchronized void waitMazimumTime() { // here a illegal monitor state exception occurs
    System.out.println("GAME HANDLER : Wait move time");
    try {
      this.wait(); // wait until player inform server
    } catch (InterruptedException e) {
      // Doing something change to a other approach different message for endTurn and forceEndTurn
    }
  }

  /**
   * Method to interrupt a waiting gameHandler during a move. Will be called when a player ends his
   * move.
   * 
   * @param action String representation of the action a player performed in his last move
   * @param points Points a player gain with his last move action.
   * @author hendiehl
   */
  public synchronized void endMoveForTime(String action, int points) {
    // here adding a actual condition
    System.out.println("GAME HANDLER : End move in time");
    this.processAction(action, points); // work with the action
    this.notify(); // wake handler for next move
  }

  /**
   * Method to process the Action a HumanPlayer has performed. If no action was performed its not
   * needed to send a message.
   * 
   * @param action Action performed by an HumanPlayer.
   * @param points Points gained by an HumanPlayer.
   * @author hendiehl
   */
  private void processAction(String action, int points) {
    System.out.println("GAME HANDLER : Action : " + action);
    JSONObject data = new JSONObject(action);
    JSONArray words = data.getJSONArray("words");
    int add = points + this.points.get(actual);
    this.points.replace(actual, add);
    if (words.length() == 0) { // Move without action move
      this.actionlessMove++;
      System.out.println("GAME HANDLER : Move without action");
      System.out.println("GAME HANDLER : Actionless counter : " + this.actionlessMove);
    } else { // move with action
      this.actionlessMove = 0;
      this.informActions(action, points);
      System.out.println("GAME HANDLER : Move with action");
    }
    System.out.println("GAME HANDLER : Poinst of actual : " + this.points.get(actual));
  }

  /**
   * Method to inform other players about the action a player performed during his last move.
   * Information will be send to other players to update their game field.
   * 
   * @param action String representation of last move action
   * @param points Points gained through the last move
   * @author hendiehl
   */
  private void informActions(String action, int points) {
    for (NetworkPlayer player : this.players) { // go through them
      if (!player.equals(this.actual)) { // not the actual
        if (!(player instanceof LobbyAiProtocol)) { // no AiPlayers they get information from host
                                                    // grid
          player.sendActionMessage(action, points, this.actual.getPlayer().getId());
        }
      }
    }
  }

  /**
   * Method to shut down the GameHandler.
   * 
   * @author hendiehl
   */
  public void shutdown() {
    this.isShutdown = true;
    this.gameIsOn = false;
    this.invokeTurnPhase(); // In case the thread is waiting
  }

  /**
   * Method to wake the thread after all players finishing loading their game field.
   */
  public synchronized void invokeTurnPhase() {
    if (this.loading) { // in case the method is called in the turn phase.
      this.notify();
      this.loading = false;
    }
  }

  /**
   * Method to return from the game field to the lobby after the game finished. Will prepare a new
   * list of members without the LobbyAiProtocols. Will also start the result sending to change
   * leave the game screen.
   * 
   * @author hendiehl
   */
  private void prepareLobbyReturn() {
    ArrayList<NetworkPlayer> list = new ArrayList<NetworkPlayer>(); // new list
    list.add(this.host); // Adding host always first for the lobby
    for (NetworkPlayer player : this.players) { // Free list from AiPlayers
      if (player instanceof LobbyServerProtocol) {
        list.add(player); // Adding only LobbyServerProtocols
      }
    }
    this.game.prepareLobbyReturn(list); // changing the GameInfoController and the screen by all
                                        // members
    // The List is now ordered in protocol intern order.
    ArrayList<Player> list2 = new ArrayList<Player>(); // need to select the player classes.
    for (NetworkPlayer player : list) {
      list2.add(player.getPlayer()); // only real players.
    }
    this.sendResults(list2);
  }

  /**
   * Method to inform the GameHandler that a player has left the game.
   * 
   * @param player player deleted from the list
   */
  public synchronized void playerDeleted(NetworkPlayer player) {
    this.points.remove(player);
    // Player should be deleted from list because of same
    if (player.equals(actual)) { // the actual player has left the game
      if (this.getState() == Thread.State.WAITING) { // Thread waits for end move
        this.invokeTurnPhase(); // just notify the thread so the method can be used
      }
    }
    // Now the players have to be informed.
    for (NetworkPlayer other : this.players) {
      other.sendDeleteMessage(player.getPlayer().getId());
    }
  }

  /**
   * Method to inform all players that they will leave the game screens in a couple of seconds.
   * 
   * @author hendiehl
   */
  public synchronized void sendPrepareMessages() {
    for (NetworkPlayer player : this.players) {
      if (!(player instanceof LobbyAiProtocol)) {
        player.sendPrepMessageChange();
      }
    }
    // Gave them time to wait --> 5 seconds
    try {
      this.wait(5000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to inform the GameHandler that the corresponding LetterBag is now empty.
   * 
   * @author hendiehl
   */
  public void informAboutBag() {
    this.bagIsEmpty = true;
  }

  /**
   * Method to set the host of the lobby, to get his GameController instance for the AiPlayers. Is
   * mainly extracted as own method to be used in the GameHandlerTest class.
   * 
   * @author hendiehl
   */
  public void setHost(LobbyHostProtocol host) {
    this.host = host;
  }
}
