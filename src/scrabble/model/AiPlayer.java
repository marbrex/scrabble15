package scrabble.model;

public class AiPlayer extends Player {
	
	/**
	 * scrabble.model.AiPlayer extends from the Player class and is necessary to determine the difficulty
	 * of each AI Player
	 * 
	 * @author Sergen Keskincelik
	 */

	/** Difficulty-Array for the Ai */
	private String[] difficulty = { "easy", "hard" };
	/** Difficulty chosen for the Ai */
	private String chosenDifficulty;

	/** Constructor of the AiPlayer class */
	public AiPlayer() {
		super();
	}
	/** Setting the difficulty of the AI-Player */
	public void setDifficulty(String diff) {
		switch (diff) {
		case ("easy"):
			this.chosenDifficulty = difficulty[0];
			break;
		case ("hard"):
			this.chosenDifficulty = difficulty[1];
			break;
		default:
			this.chosenDifficulty = null;
			break;
		}
	}
	/** Returns the difficulty of the AI-Player */
	public String getDifficulty() {
		return this.chosenDifficulty;
	}
}
