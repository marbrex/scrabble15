package scrabble.model;

public class LetterPosition {
		private int row;
		private int column;
		
		public LetterPosition(int row, int column) {
			this.row = row;
			this.column = column;
		}

		public int getRow() {
			return row;
		}

		public int getColumn() {
			return column;
		}
		
		
}
