package scrabble.game;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.List;
import scrabble.model.AiPlayer;
import scrabble.model.HumanPlayer;
import scrabble.model.Player;

/**
 * scrabble.game.LeaderBoard
 *
 * @author ekasmamy
 */
public class LeaderBoard {

  /**
   * Comparator function for the TreeSet.
   *
   * @author ekasmamy
   */
  static class ScoreComparator implements Comparator<Player> {

    public int compare(Player p1, Player p2) {
      return Integer.compare(p1.getScore(), p2.getScore());
    }
  }

  /**
   * Sorted set of all players in a game (Humans or AI).
   *
   * @author ekasmamy
   */
  private TreeSet<Player> players;

  /**
   * Common constructor for MULTIPLAYER mode.
   *
   * @param playersList A players' list including the host (<= 4).
   * @author ekasmamy
   */
  public LeaderBoard(List<Player> playersList) {
    if (playersList.size() <= 4) {
      this.players = new TreeSet<>(new ScoreComparator());
      this.players.addAll(playersList);
    }
  }

  /**
   * Constructor for SINGLE PLAYER mode.
   *
   * @param host   A real player who created the game.
   * @param nbBots Number of Bots (1 <= nbBots <= 3).
   * @author ekasmamy
   */
  public LeaderBoard(HumanPlayer host, int nbBots) {
    if (nbBots >= 1 && nbBots <= 3) {
      this.players = new TreeSet<>(new ScoreComparator());
      this.players.add(host);
      for (int i = 0; i < nbBots; i++) {
        this.players.add(new AiPlayer());
      }
    }
  }

  /**
   * Constructor for MULTIPLAYER mode with ONLY REAL PLAYERS.
   *
   * @param host   A real player who created the game.
   * @param guests Real players who have join the game (<= 3).
   * @author ekasmamy
   */
  public LeaderBoard(HumanPlayer host, List<HumanPlayer> guests) {
    if (guests.size() <= 3) {
      this.players = new TreeSet<>(new ScoreComparator());
      this.players.add(host);
      this.players.addAll(guests);
    }
  }

  /**
   * Constructor for MULTIPLAYER MIXED mode.
   *
   * @param host   A real player who created the game.
   * @param guests Real players who have join the game (guests.size() + bots.size() <= 3).
   * @param bots   Bots (guests.size() + bots.size() <= 3).
   * @author ekasmamy
   */
  public LeaderBoard(HumanPlayer host, List<HumanPlayer> guests, List<AiPlayer> bots) {
    if (guests.size() + bots.size() <= 3) {
      this.players = new TreeSet<>(new ScoreComparator());
      this.players.add(host);
      this.players.addAll(guests);
      this.players.addAll(bots);
    }
  }

  /**
   * Getter for the player with a highest score.
   *
   * @return A player with a highest score.
   * @author ekasmamy
   */
  public Player getGreatest() {
    return players.last();
  }

  /**
   * Getter for the player with a lowest score.
   *
   * @return A player with a lowest score.
   * @author ekasmamy
   */
  public Player getLowest() {
    return players.first();
  }

  /**
   * Removes the specified player (Human or AI).
   *
   * @param player Player (Human or AI).
   * @author ekasmamy
   */
  public void removePlayer(Player player) {
    players.remove(player);
  }

  /**
   * Getter a number of players in a game.
   *
   * @return A number of players in a game.
   * @author ekasmamy
   */
  public int getNumberOfPlayers() {
    return players.size();
  }

}
