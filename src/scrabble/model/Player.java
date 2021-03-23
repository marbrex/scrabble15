package scrabble.model;

import java.util.Date;

public abstract class Player {
	
	//Name of the Player
	private String name;
	//Amount of points which a player has received in the game
	private int score;
	//Rack to place your tiles
	private String[] rack = new String[7];
	//Turn options for each player
	private String[] turnOptions = {"pass","exchange","play"};
	//Necessary to determine, if one player used more than 10 minutes of overtime
	private Date usedOvertime;
	
	
	public Player(String name) {
		this.name = name;
	}
	//Setting your Rack in the beginning or while choosing the options 'exchange' or 'play'
	public void settingRack() {
		//Code needs to be implemented.
	}
	//Clearing the rack, e.g. in the end of the game
	public void clearRack() {
		rack = new String[7];
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getScore() {
		return this.score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
}
