package scrabble.model;

public class HumanPlayer extends Player {

	// Amount of games won for statistics -> HumanPlayer
	private int gamesWon;
	// Amount of games lost for statistics -> HumanPlayer
	private int gamesLost;
	// Your average Win-Rate for statistics -> HumanPlayer
	private double winrate;

	public HumanPlayer() {
		super();
		this.gamesWon = 0;
		this.gamesLost = 0;
		this.winrate = 0.0;
	}

	public void setWinRate(double winrate) {
		this.winrate = winrate;
	}

	public double getWinRate() {
		return this.winrate;
	}

	public int getGamesLost() {
		return gamesLost;
	}

	public void setGamesLost(int gamesLost) {
		this.gamesLost = gamesLost;
	}

	public int getGamesWon() {
		return this.gamesWon;
	}

	public void setGamesWon(int gamesWon) {
		this.gamesWon = gamesWon;
	}
}
