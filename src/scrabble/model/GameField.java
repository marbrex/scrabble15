package scrabble.model;

public class GameField {
	private Letter[][] gameField = new Letter[15][15];
	private int[][] gameFieldLetterMultiplikator = new int[15][15];
	private int[][] gameFieldWordMultiplicator = new int[15][15];
	private static GameField gameFieldClass = null;
	
	private GameField() {
		//During Game, Client or Server doesnt need multiple instances
		setUpGameFieldLetterMultiplicator();
		setUpGameFieldWordMultiplicator();
	}
	
	public static GameField getGameField() {
		if(gameFieldClass == null)
			gameFieldClass = new GameField();
		return gameFieldClass;
	}
	
	
	//Setting a letter on the field if free and in field range
	public boolean setLetter(int row, int column, Letter letter) {
		if((row >= 0) && (row < 15) && (column >= 0) &&(column < 15)) {
			if(this.gameField[row][column] == null) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	public int getPositionLetterMultiplicator(int row, int column) {
		if(this.gameFieldLetterMultiplikator[row][column] > 0) {
			int multiplicator = this.gameFieldLetterMultiplikator[row][column];
			this.gameFieldLetterMultiplikator[row][column] = 0; //Premium field should only count once, they are set back after using it
			return multiplicator;
		}
		else {
			return 1;
		}
	}
	
	public int getPositionWordMultiplicator(int row, int column) {
		if(this.gameFieldWordMultiplicator[row][column] > 0) {
			int multiplicator = this.gameFieldWordMultiplicator[row][column];
			this.gameFieldWordMultiplicator[row][column] = 0; //Premium field should only count once, they are set back after using it
			return multiplicator;
		}
		else {
			return 1;
		}
	}
	
	public int calculateWordPoints(Word word) {
		int points = this.calculateLetterPoints(word);
		return this.calculateWordPoints(word, points);
	}
	
	private int calculateLetterPoints(Word word) {
		int points = 0;
		for(int i = 0; i < word.getWordPosition().size(); i++) {
			int row = word.getWordPosition().get(i).getRow();
			int column = word.getWordPosition().get(i).getColumn();
			if(this.gameField[row][column] == null) {
				return 0;//Exception have to be thrown, need to be implemented
			}
			else {
				points += this.gameField[row][column].getLetterValue() * this.getPositionLetterMultiplicator(row, column);
			}
		}
		return points;
	}
	
	private int calculateWordPoints(Word word, int wordPoints) {
		for(int i = 0; i < word.getWordPosition().size(); i++) {
			int row = word.getWordPosition().get(i).getRow();
			int column = word.getWordPosition().get(i).getColumn();
			if(this.gameField[row][column] == null) {
				return 0;//Exception have to be thrown, need to be implemented
			}
			else {
				wordPoints *= this.getPositionWordMultiplicator(row, column);
			}
		}
		return wordPoints;
	}
	
	
	//Values of greater 1 or Zero if no special field
	private void setUpGameFieldLetterMultiplicator() {
		this.gameFieldLetterMultiplikator[0][3] = 2;//2x multiplicators
		this.gameFieldLetterMultiplikator[0][11] = 2;
		this.gameFieldLetterMultiplikator[2][6] = 2;
		this.gameFieldLetterMultiplikator[2][8] = 2;
		this.gameFieldLetterMultiplikator[3][0] = 2;
		this.gameFieldLetterMultiplikator[3][7] = 2;
		this.gameFieldLetterMultiplikator[3][14] = 2;
		this.gameFieldLetterMultiplikator[6][2] = 2;
		this.gameFieldLetterMultiplikator[6][6] = 2;
		this.gameFieldLetterMultiplikator[6][8] = 2;
		this.gameFieldLetterMultiplikator[6][12] = 2;
		this.gameFieldLetterMultiplikator[7][3] = 2;
		this.gameFieldLetterMultiplikator[7][11] = 2;
		this.gameFieldLetterMultiplikator[8][2] = 2;
		this.gameFieldLetterMultiplikator[8][6] = 2;
		this.gameFieldLetterMultiplikator[8][8] = 2;
		this.gameFieldLetterMultiplikator[8][12] = 2;
		this.gameFieldLetterMultiplikator[11][0] = 2;
		this.gameFieldLetterMultiplikator[11][7] = 2;
		this.gameFieldLetterMultiplikator[11][14] = 2;
		this.gameFieldLetterMultiplikator[12][6] = 2;
		this.gameFieldLetterMultiplikator[12][8] = 2;
		this.gameFieldLetterMultiplikator[14][3] = 2;
		this.gameFieldLetterMultiplikator[14][11] = 2;
		this.gameFieldLetterMultiplikator[1][5] = 3;//3x multiplicators
		this.gameFieldLetterMultiplikator[1][9] = 3;
		this.gameFieldLetterMultiplikator[5][1] = 3;
		this.gameFieldLetterMultiplikator[5][5] = 3;
		this.gameFieldLetterMultiplikator[5][9] = 3;
		this.gameFieldLetterMultiplikator[5][13] = 3;
		this.gameFieldLetterMultiplikator[9][1] = 3;
		this.gameFieldLetterMultiplikator[9][5] = 3;
		this.gameFieldLetterMultiplikator[9][9] = 3;
		this.gameFieldLetterMultiplikator[9][13] = 3;
		this.gameFieldLetterMultiplikator[13][5] = 3;
		this.gameFieldLetterMultiplikator[13][9] = 3;
	}
	
	//Values of greater 1 or Zero if no special field
	private void setUpGameFieldWordMultiplicator() {
		this.gameFieldWordMultiplicator[1][1] = 2;//2x multiplicator
		this.gameFieldWordMultiplicator[2][2] = 2;
		this.gameFieldWordMultiplicator[3][3] = 2;
		this.gameFieldWordMultiplicator[4][4] = 2;
		this.gameFieldWordMultiplicator[1][13] = 2;
		this.gameFieldWordMultiplicator[2][12] = 2;
		this.gameFieldWordMultiplicator[3][11] = 2;
		this.gameFieldWordMultiplicator[4][10] = 2;
		this.gameFieldWordMultiplicator[13][1] = 2;
		this.gameFieldWordMultiplicator[12][2] = 2;
		this.gameFieldWordMultiplicator[11][3] = 2;
		this.gameFieldWordMultiplicator[10][4] = 2;
		this.gameFieldWordMultiplicator[13][13] = 2;
		this.gameFieldWordMultiplicator[12][12] = 2;
		this.gameFieldWordMultiplicator[11][11] = 2;
		this.gameFieldWordMultiplicator[10][10] = 2;
		this.gameFieldWordMultiplicator[7][7] = 2;
		this.gameFieldWordMultiplicator[0][0] = 3;//3xmultiplicator
		this.gameFieldWordMultiplicator[0][7] = 3;
		this.gameFieldWordMultiplicator[0][14] = 3;
		this.gameFieldWordMultiplicator[7][0] = 3;
		this.gameFieldWordMultiplicator[7][14] = 3;
		this.gameFieldWordMultiplicator[14][0] = 3;
		this.gameFieldWordMultiplicator[14][7] = 3;
		this.gameFieldWordMultiplicator[14][14] = 3;
	}
}
