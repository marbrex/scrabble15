package scrabble.model;

public class HumanPlayer extends Player {
	
	//Amount of games won for statistics -> HumanPlayer
	private int gamesWon;
	//Amount of games lost for statistics -> HumanPlayer
	private int gamesLost;
	//Your average Win-Rate
	private double winrate;
	
	public HumanPlayer(String name) {
		super(name);
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
	
	private int getGamesWon() {
		return this.gamesWon;
	}
	public void gamesWon(int gamesWon) {
		this.gamesWon = gamesWon;
	}

	/*
	public static void main(String[] args) {
		HumanPlayer hp = new HumanPlayer("Scrabble Player 1");
		hp.setScore(2);
		System.out.println("Current Player: " + hp.getName());
		System.out.println("Current Score: " + hp.getScore());
	}
	*/

}
