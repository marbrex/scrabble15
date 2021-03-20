package model;

public class AiPlayer extends Player {
	
	//Difficulty-Array for the Ai
	private String[] difficulty = {"easy","hard"};
	//Difficulty chosen for the Ai
	private String chosenDifficulty;

	public void setDifficulty(String diff) {
		switch(diff) {
		case("easy"): this.chosenDifficulty = difficulty[0]; break;
		case("hard"): this.chosenDifficulty = difficulty[1]; break;
		default: this.chosenDifficulty = null; break;
		}
	}
	
	public String getDifficulty() {
		return this.chosenDifficulty;
	}
	
	public AiPlayer(String name) {
		super(name);
	}
	/*
	public static void main(String[] args) {
		AiPlayer ai = new AiPlayer("Ai 1");
		ai.setDifficulty("easy");
		System.out.println("Current Difficulty: " + ai.getDifficulty());
		ai.setDifficulty("hard");
		System.out.println("Current Difficulty: " + ai.getDifficulty());
	}
	*/
}
