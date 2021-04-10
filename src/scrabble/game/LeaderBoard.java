package scrabble.game;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.List;
import scrabble.model.AiPlayer;
import scrabble.model.HumanPlayer;
import scrabble.model.Player;

/**
 * <h1>scrabble.game.LeaderBoard class</h1>
 *
 * @author Eldar Kasmamytov
 */
public class LeaderBoard {

  /**
   * Comparator function for the TreeSet
   */
  static class ScoreComparator implements Comparator<Player> {

    public int compare(Player p1, Player p2) {
      return Integer.compare(p1.getScore(), p2.getScore());
    }
  }

  /**
   * Sorted set of all players in a game (Humans or AI)
   */
  private TreeSet<Player> players;

  /**
   * Common constructor for MULTIPLAYER mode
   *
   * @param playersList A players' list including the host (<= 4)
   */
  public LeaderBoard(List<Player> playersList) {
    if (playersList.size() <= 4) {
      this.players = new TreeSet<Player>(new ScoreComparator());
      this.players.addAll(playersList);
    }
  }

  /**
   * Constructor for SINGLE PLAYER mode
   *
   * @param host   A real player who created the game
   * @param nbBots Number of Bots (1 <= nbBots <= 3)
   */
  public LeaderBoard(HumanPlayer host, int nbBots) {
    if (nbBots >= 1 && nbBots <= 3) {
      this.players = new TreeSet<Player>(new ScoreComparator());
      this.players.add(host);
      for (int i = 0; i < nbBots; i++) {
        this.players.add(new AiPlayer());
      }
    }
  }

  /**
   * Constructor for MULTIPLAYER mode with ONLY REAL PLAYERS
   *
   * @param host   A real player who created the game
   * @param guests Real players who have join the game (<= 3)
   */
  public LeaderBoard(HumanPlayer host, List<HumanPlayer> guests) {
    if (guests.size() <= 3) {
      this.players = new TreeSet<Player>(new ScoreComparator());
      this.players.add(host);
      this.players.addAll(guests);
    }
  }

  /**
   * Constructor for MULTIPLAYER MIXED mode
   *
   * @param host   A real player who created the game
   * @param guests Real players who have join the game (guests.size() + bots.size() <= 3)
   * @param bots   Bots (guests.size() + bots.size() <= 3)
   */
  public LeaderBoard(HumanPlayer host, List<HumanPlayer> guests, List<AiPlayer> bots) {
    if (guests.size() + bots.size() <= 3) {
      this.players = new TreeSet<Player>(new ScoreComparator());
      this.players.add(host);
      this.players.addAll(guests);
      this.players.addAll(bots);
    }
  }

  /**
   * Getter
   *
   * @return A player with a highest score
   */
  public Player getGreatest() {
    return players.last();
  }

  /**
   * Getter
   *
   * @return A player with a lowest score
   */
  public Player getLowest() {
    return players.first();
  }

  /**
   * Removes the specified player (Human or AI)
   *
   * @param player Player (Human or AI)
   */
  public void removePlayer(Player player) {
    players.remove(player);
  }

  /**
   * Getter
   *
   * @return A number of players in a game
   */
  public int getNumberOfPlayers() {
    return players.size();
  }

  // TODO: Getter for the game Host

}
