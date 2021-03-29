package scrabble.model;

public class HumanPlayer extends Player {

	/** Amount of games won for statistics -> HumanPlayer */
	private int gamesWon;
	/** Amount of games lost for statistics -> HumanPlayer */
	private int gamesLost;

	/** Constructor of the Humanplayer class */
	public HumanPlayer() {
		super();
		this.gamesWon = 0;
		this.gamesLost = 0;
	}
	/**Returns Win-Rate of the player */
	public double getWinRate() {
		return Math.round((gamesWon*100.0)/gamesLost)/100.0;
	}
	/** Returns the amount of games lost in total */
	public int getGamesLost() {
		return gamesLost;
	}
	/** Setting the amount of games lost in total -> Database */
	public void setGamesLost(int gamesLost) {
		this.gamesLost = gamesLost;
	}
	/** Returns the amount of games won in total */
	public int getGamesWon() {
		return this.gamesWon;
	}
	/** Setting the amount of games won in total -> Database */
	public void setGamesWon(int gamesWon) {
		this.gamesWon = gamesWon;
	}
}
