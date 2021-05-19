package scrabble.model;

import scrabble.GameController;
import scrabble.game.Grid;
import scrabble.game.LetterBar;
import scrabble.game.LetterTile;
import scrabble.game.Word;

import java.io.Serializable;
import java.util.*;

/**
 * scrabble.model.AiPlayer extends from the Player class and is necessary to determine the
 * difficulty of each AI Player.
 * 
 * @author astarche
 * @author skeskinc
 */

public class AiPlayer extends Player implements Serializable {

  private String[] difficulty = {"easy", "hard"};
  private String chosenDifficulty;
  
  /** The letters that aiplayer uses to construct new words. */
  private List<LetterTile> ailetters = new ArrayList<LetterTile>();

  /**
   * Constructor of the AiPlayer class.
   *
   * @author skeskinc
   */
  public AiPlayer() {
    super();
    this.setName("AI");
    this.setImage(0);
  }

  /**
   * Setting the difficulty of the AI-Player.
   *
   * @author skeskinc
   * @param diff Setting the difficulty of the AI-Player
   */
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

  /**
   * Returns the difficulty of the AI-Player.
   *
   * @author skeskinc
   * @return Difficulty of the AI-Player
   */
  public String getDifficulty() {
    return this.chosenDifficulty;
  }

  /**
   * Checks if the word can be made with the available letters.
   *
   * @author astarche
   * @param letters the letters
   * @param toFind the to find
   * @return true, if successful
   */
  public boolean findWord(List<LetterTile> letters, String toFind) {
    boolean found = false;
    if (hasLettersOf(getValues(letters), toFind)) {
      String hl = clear(getValues(letters).toString(), toFind);
      if (toFind.length() - hl.length() == 0) {
        System.out.println("THE WORD " + toFind + " CAN BE MADE WITHOUT BLANK TILES");
        found = true;
      }else if(toFind.length() - hl.length() == 1){
        if (hasTile('\0', letters) >= 1){
          System.out.println("THE WORD " + toFind + " CAN BE MADE WITH ONE BLANK TILE");
          found = true;
        }
      }else if(toFind.length() - hl.length() == 2){
        if (hasTile( '\0', letters) >= 2){
          System.out.println("THE WORD " + toFind + " CAN BE MADE WITH TWO BLANK TILES");
          found = true;
        }
      }
    }
    return found;
  }

  /**
   * Returns a list with all possible words from the dictionary that can be made with the given
   * letters.
   *
   * @author astarche
   * @param letters the letters
   * @return the list
   */
  public List<String> findWords(List<LetterTile> letters) {
    List<String> foundWords = new ArrayList<String>();
    for (String word : Dictionary.getWords()) {
      if (this.findWord(letters, word)) {
        foundWords.add(word);
      }
    }
    return foundWords;
  }

  /**
   * Checks if all letters to make the searched word are present.
   *
   * @author astarche
   * @param currentLetters the current letters
   * @param word the word
   * @return true, if successful
   */
  public boolean hasLettersOf(List<Character> currentLetters, String word) {
    List<Character> wordList = new ArrayList<Character>();
    for (int i = 0; i < word.length(); i++) {
      wordList.add(word.charAt(i));
    }
    return currentLetters.containsAll(wordList);
  }

  /**
   * Returns a list with char values of the letters.
   *
   * @author astarche
   * @param letters the letters
   * @return the values
   */
  public List<Character> getValues(List<LetterTile> letters) {
    List<Character> values = new ArrayList<Character>();
    for (LetterTile letter : letters) {
      values.add(letter.getLetter());
    }
    return values;
  }

  /**
   * Removes all the letters that are not used in the searched word.
   *
   * @author astarche
   * @param currentLetters the current letters
   * @param word the word
   * @return string with letters that are only used in the searched word
   */
  public String clear(String currentLetters, String word) {
    StringBuilder cleared = new StringBuilder();
    for (int i = 0; i < currentLetters.length(); i++) {
      if (word.contains(Character.toString(currentLetters.charAt(i)))) {
        cleared.append(currentLetters.charAt(i));
      }
    }
    return cleared.toString();
  }

  /**
   * Make turn.
   *
   * @author astarche
   * @param gc Game Controller
   * @return constructed word
   */
  public Word makeTurn(GameController gc) {
    List<LetterTile> lettersOnGrid = gc.grid.getTilesInGrid();
    String word = null;
    boolean flag = false;
    for (LetterTile letterTile : lettersOnGrid) {
      if (isFree(letterTile, gc.grid)) {
        ailetters.add(letterTile);
        System.out.println("BOT: TRYING TO MAKE SOMETHING WITH " + letterTile.getLetter());
        List<String> foundWords = findWords(ailetters);
        if (foundWords.isEmpty()) {
          System.out.println("BOT: CANT MAKE ANYTHING WITH LETTER " + letterTile.getLetter());
          ailetters.remove(ailetters.size() - 1);
          continue;
        }
        for (String foundWord : foundWords) {
          if (hasLetter(foundWord, gc.grid)) {
            word = foundWord;
            System.out.println("BOT: I WILL USE THE WORD: " + word);
            flag = true;
            break;
          }
        }
        if (flag){
          break;
        }
      }
    }
    if (word == null){
      System.out.println("BOT: NO WORDS FOUND");
      return null;
    }
    LetterTile tile = ailetters.get(ailetters.size() - 1);
    List<LetterTile> wordOnGrid = new ArrayList<LetterTile>();
    wordOnGrid.add(tile);
    int row = gc.grid.getCellRow(tile);
    int column = gc.grid.getCellColumn(tile);
    String[] parts = word.split(Character.toString(tile.getLetter()), 2);
    if (parts.length > 1 && !parts[0].equals("")){
      if (gc.grid.getNeighbourCell(tile, "bottom") == null && gc.grid.getNeighbourCell(tile, "top") == null){
        addTilesTop(column, row, parts[0], gc, wordOnGrid);
        addTilesBottom(column, row, parts[1], gc, wordOnGrid);
      }else if (gc.grid.getNeighbourCell(tile, "left") == null && gc.grid.getNeighbourCell(tile, "right") == null){
        addTilesLeft(column, row, parts[0], gc, wordOnGrid);
        addTilesRight(column, row, parts[1], gc, wordOnGrid);
      }
    }else if(parts.length == 1){
      if (gc.grid.getNeighbourCell(tile, "top") == null){
        addTilesTop(column, row, parts[0], gc, wordOnGrid);
      }else if (gc.grid.getNeighbourCell(tile, "left") == null){
        addTilesLeft(column, row, parts[0], gc, wordOnGrid);
      }
    }else {
      if (gc.grid.getNeighbourCell(tile, "bottom") == null){
        addTilesBottom(column, row, parts[1], gc, wordOnGrid);
      }else if (gc.grid.getNeighbourCell(tile, "right") == null){
        addTilesRight(column, row, parts[1], gc, wordOnGrid);
      }
    }
    for (int i = 0;i < wordOnGrid.size();i++){
      if (wordOnGrid.get(i).getLetter() != word.charAt(i)){
        for (int j = i; j < wordOnGrid.size();j++){
          if (wordOnGrid.get(j).getLetter() == word.charAt(i)){
            LetterTile tmp = null;
            tmp = wordOnGrid.get(i);
            wordOnGrid.set(i, wordOnGrid.get(j));
            wordOnGrid.set(j, tmp);
          }
        }
      }
    }
    ailetters.remove(ailetters.size() - 1);
    return new Word(wordOnGrid.get(0), wordOnGrid.get(wordOnGrid.size() - 1), gc);
  }

  /**
   * Adds tiles over the given slot.
   *
   * @param column the column of the slot
   * @param row the row of the slot
   * @param part string with letters that need to be added
   * @param gc Game Controller
   * @param wordOnGrid list in which the constructed word will be saved
   */
  private void addTilesTop(int column, int row, String part, GameController gc, List<LetterTile> wordOnGrid){
    part = new StringBuilder(part).reverse().toString();
    for (int i = 0; i < part.length();i++) {
      if (ailetters.get(findTile(part.charAt(i))) != null) {
        gc.grid.setSlotContent(column, row - (i + 1), ailetters.get(findTile(part.charAt(i))));
        System.out.println("TILE ADDED TOP");
        wordOnGrid.add(ailetters.get(findTile(part.charAt(i))));
        System.out.println("BOT: REMOVED TILE - " + ailetters.get(findTile(part.charAt(i))).getLetter());
        ailetters.remove(ailetters.get(findTile(part.charAt(i))));
      }else{
        if (ailetters.get(findTile('0')) != null) {
          gc.grid.setSlotContent(column, row - (i + 1), new LetterTile(part.charAt(i), 0, 10, gc));
          System.out.println("TILE ADDED TOP");
          wordOnGrid.add(ailetters.get(findTile(part.charAt(i))));
          System.out.println("BOT: REMOVED TILE - BLANK TILE");
          ailetters.remove(ailetters.get(findTile('0')));
        }else{
          return;
        }
      }
    }
  }

  /**
   * Adds tiles below the given slot.
   *
   * @param column the column of the slot
   * @param row the row of the slot
   * @param part string with letters that need to be added
   * @param gc Game Controller
   * @param wordOnGrid list in which the constructed word will be saved
   */
  private void addTilesBottom(int column, int row, String part, GameController gc, List<LetterTile> wordOnGrid){
    for (int i = 0; i < part.length();i++) {
      if (ailetters.get(findTile(part.charAt(i))) != null) {
        gc.grid.setSlotContent(column, row + (i + 1), ailetters.get(findTile(part.charAt(i))));
        System.out.println("TILE ADDED BOTTOM");
        wordOnGrid.add(ailetters.get(findTile(part.charAt(i))));
        System.out.println("BOT: REMOVED TILE - " + ailetters.get(findTile(part.charAt(i))).getLetter());
        ailetters.remove(ailetters.get(findTile(part.charAt(i))));
      }else{
        if (ailetters.get(findTile('0')) != null) {
          gc.grid.setSlotContent(column, row + (i + 1), new LetterTile(part.charAt(i), 0, 10, gc));
          System.out.println("TILE ADDED BOTTOM");
          wordOnGrid.add(ailetters.get(findTile(part.charAt(i))));
          System.out.println("BOT: REMOVED TILE - BLANK TILE");
          ailetters.remove(ailetters.get(findTile('0')));
        }else{
          return;
        }
      }
    }
  }

  /**
   * Adds the tiles on the left of the given slot.
   *
   * @param column the column of the slot
   * @param row the row of the slot
   * @param part string with letters that need to be added
   * @param gc Game Controller
   * @param wordOnGrid list in which the constructed word will be saved
   */
  private void addTilesLeft(int column, int row, String part, GameController gc, List<LetterTile> wordOnGrid){
    part = new StringBuilder(part).reverse().toString();
    for (int i = 0; i < part.length();i++){
      if (ailetters.get(findTile('0')) != null) {
        gc.grid.setSlotContent(column - (i + 1), row, ailetters.get(findTile(part.charAt(i))));
        System.out.println("TILE ADDED LEFT");
        wordOnGrid.add(ailetters.get(findTile(part.charAt(i))));
        System.out.println("BOT: REMOVED TILE - " + ailetters.get(findTile(part.charAt(i))).getLetter());
        ailetters.remove(ailetters.get(findTile(part.charAt(i))));
      }else{
        if (ailetters.get(findTile('0')) != null) {
          gc.grid.setSlotContent(column - (i + 1), row, new LetterTile(part.charAt(i), 0, 10, gc));
          System.out.println("TILE ADDED LEFT");
          wordOnGrid.add(ailetters.get(findTile(part.charAt(i))));
          System.out.println("BOT: REMOVED TILE - BLANK TILE");
          ailetters.remove(ailetters.get(findTile('0')));
        }else {
          return;
        }
      }
    }
  }

  /**
   * Adds the tiles on the right of the given slot.
   *
   * @param column the column of the slot
   * @param row the row of the slot
   * @param part string with letters that need to be added
   * @param gc Game Controller
   * @param wordOnGrid list in which the constructed word will be saved
   */
  private void addTilesRight(int column, int row, String part, GameController gc, List<LetterTile> wordOnGrid){
    for (int i = 0; i < part.length();i++){
      if (ailetters.get(findTile(part.charAt(i))) != null) {
        gc.grid.setSlotContent(column + (i + 1), row, ailetters.get(findTile(part.charAt(i))));
        System.out.println("TILE ADDED RIGHT");
        wordOnGrid.add(ailetters.get(findTile(part.charAt(i))));
        System.out.println("BOT: REMOVED TILE - " + ailetters.get(findTile(part.charAt(i))).getLetter());
        ailetters.remove(ailetters.get(findTile(part.charAt(i))));
        }else {
          if (ailetters.get(findTile('0')) != null){
            gc.grid.setSlotContent(column + (i + 1), row, new LetterTile(part.charAt(i), 0, 10, gc));
            System.out.println("TILE ADDED RIGHT");
            wordOnGrid.add(ailetters.get(findTile(part.charAt(i))));
            System.out.println("BOT: REMOVED TILE - BLANK TILE");
            ailetters.remove(ailetters.get(findTile('0')));
          }else{
            return;
          }
      }
    }
  }

  /**
   * Checks if the letter tile is free (has no neighbours).
   *
   * @author astarche
   * @param tile the tile
   * @param grid the grid
   * @return true, if is free
   */
  private boolean isFree(LetterTile tile, Grid grid){
    return grid.getNeighbourCell(tile, "right") == null
            || grid.getNeighbourCell(tile, "left") == null
            || grid.getNeighbourCell(tile, "top") == null
            || grid.getNeighbourCell(tile, "bottom") == null;
  }

  /**
   * Checks if constructed word contains the tile on the grid.
   *
   * @author astarche
   * @param word the word
   * @param grid the grid
   * @return true, if successful
   */
  private boolean hasLetter(String word, Grid grid){
    boolean contains = false;
    for (int i = 0;i < word.length();i++){
      for (int j = 0;j < grid.getTilesInGrid().size();j++){
        if (word.charAt(i) == grid.getTilesInGrid().get(j).getLetter()){
          contains = true;
          break;
        }
      }
    }
    return contains;
  }
  /**
   * Gives letters to ai player.
   *
   * @author astarche
   * @param gc Game Controller
   */
  public void giveLettersToAiPlayer(GameController gc){
    String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    Random random = new Random();
    while (ailetters.size() != 7){
      ailetters.add(new LetterTile(alphabet.charAt(random.nextInt(alphabet.length())), 1, 10,
              gc));
    }
    ailetters.add(new LetterTile('0', 0, 10, gc));
    ailetters.add(new LetterTile('0', 0, 10, gc));
  }

  /**
   * Displays tiles, that the aiplayer currently has
   *
   * @author astarche
   */
  public void displayTiles(){
    System.out.print("BOT: MY TILES ARE: ");
    for (LetterTile tile : ailetters){
      System.out.print(tile.getLetter());
    }
    System.out.println();
  }

  /**
   * Gives human player some words that he can construct with his letters.
   * Can be used only in single player.
   *
   * @author astarche
   * @param gc the gc
   * @return the string with possible words
   */
  public String helpPoorHuman(GameController gc) {
    List<LetterTile> letters = gc.grid.getTilesInGrid();
    for (int i = 0; i < 7; i++) {
      if (gc.letterBar.getSlot(i).content != null) {
        letters.add(gc.letterBar.getSlot(i).content);
      }
    }
    List<String> foundWords = findWords(letters);
    if (!foundWords.isEmpty()) {
      StringBuilder s = new StringBuilder("TRY THIS WORDS:\n");
      for (String foundWord : foundWords) {
        s.append(foundWord).append("\n");
      }
      return s.toString();
    } else {
      return "YOU CANNOT MAKE ANY WORDS WITH THESE LETTERS!";
    }
  }

  /**
   * Searches for a specified tile and returns the number of this tiles in the given list
   *
   * @author astarche
   * @param toFind letter that needs to be found
   * @param letters list with available letter
   * @return the number of searched tiles in the list
   */
  private int hasTile(char toFind, List<LetterTile> letters){
    int found = 0;
    for (LetterTile tile : letters){
      if (tile.getLetter() == toFind){
        found++;
      }
    }
    return found;
  }

  /**
   * Finds a specified tile and returns its index
   *
   * @author astarche
   * @param toFind letter that needs to be found
   * @return index
   */
  private int findTile(char toFind){
    int indx = -1;
    for (int i = 0;i < ailetters.size();i++){
      if (ailetters.get(i).getLetter() == toFind){
        indx = i;
      }
    }
    return indx;
  }
}
