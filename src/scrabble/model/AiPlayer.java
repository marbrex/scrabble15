package scrabble.model;

public class AiPlayer extends Player {

	// Difficulty-Array for the Ai
	private String[] difficulty = { "easy", "hard" };
	// Difficulty chosen for the Ai
	private String chosenDifficulty;

	public AiPlayer() {
		super();
	}

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

	public String getDifficulty() {
		return this.chosenDifficulty;
	}
}
