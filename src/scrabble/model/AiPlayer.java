package scrabble.model;

import com.google.common.collect.Multiset;
import scrabble.GameController;
import scrabble.game.Grid;
import scrabble.game.LetterBag;
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
  private GameController gc;
  
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
   * Sets Game Controller
   *
   * @author astarche
   * @param gc Game Controller
   */
  public void setController(GameController gc){
    this.gc = gc;
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
      if (hasEnoughLetters(hl, toFind) == 0) {
        System.out.println("THE WORD " + toFind + " CAN BE MADE WITHOUT BLANK TILES");
        found = true;
      }else if(hasEnoughLetters(hl, toFind) == 1){
        if (hasTile('\0', letters) >= 1){
          System.out.println("THE WORD " + toFind + " CAN BE MADE WITH ONE BLANK TILE");
          found = true;
        }
      }else if(hasEnoughLetters(hl, toFind) == 2){
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
   * Help class to compare words.
   */
  private static class Letter{

    /** The letter. */
    private final char letter;

    /** The freq. */
    private int freq;

    /**
     * Instantiates a new letter.
     *
     * @param letter the letter
     * @param freq the freq
     */
    public Letter(char letter, int freq){
      this.letter = letter;
      this.freq = freq;
    }

    /**
     * Gets the char.
     *
     * @return the char
     */
    public char getChar(){
      return this.letter;
    }

    /**
     * Gets the freq.
     *
     * @return the freq
     */
    public int getFreq(){
      return this.freq;
    }

    /**
     * Inc freq.
     */
    public void incFreq(){
      this.freq += 1;
    }

    /**
     * Contains.
     *
     * @param c the c
     * @param arr the arr
     * @return true, if successful
     */
    public static boolean contains(char c, List<Letter> arr){
      boolean result = false;
      for (Letter l : arr){
        if (l.getChar() == c){
          result = true;
          break;
        }
      }
      return result;
    }
    /**
     * Sets the freq.
     *
     * @param c the c
     * @param arr the arr
     */
    public static void setFreq(char c, List<Letter> arr){
      for (int i = 0;i < arr.size();i++){
        if (arr.get(i).getChar() == c){
          arr.get(i).incFreq();
        }
      }
    }
    /**
     * Gets the index.
     *
     * @param c the c
     * @param arr the arr
     * @return the index
     */
    public static int getIndex(char c, List<Letter> arr){
      int indx = 0;
      for (int i = 0;i < arr.size();i++){
        if(arr.get(i).getChar() == c){
          indx = i;
        }
      }
      return indx;
    }
  }

  /**
   * Checks for enough letters.
   *
   * @param letters the letters
   * @param word the word
   * @return the int
   */
  private int hasEnoughLetters(String letters, String word){
    List<Letter> lettersarr = new ArrayList<Letter>();
    List<Letter> wordarr = new ArrayList<Letter>();
    for (int i = 0;i < letters.length();i++){
      if (!Letter.contains(letters.charAt(i), lettersarr)){
        lettersarr.add(new Letter(letters.charAt(i), 0));
      }else{
        Letter.setFreq(letters.charAt(i), lettersarr);
      }
    }

    for (int i = 0;i < word.length();i++){
      if (!Letter.contains(word.charAt(i), wordarr)){
        wordarr.add(new Letter(word.charAt(i), 0));
      }else{
        Letter.setFreq(word.charAt(i), wordarr);
      }
    }
    int diff = 0;
    for(Letter l : lettersarr){
      if (l.getFreq() != wordarr.get(Letter.getIndex(l.getChar(), wordarr)).getFreq()){
        diff++;
      }
    }
    return diff;
  }

  /**
   * Make turn.
   *
   * @author astarche
   * @return constructed word in form of json
   */
  public void makeTurn() {
    List<LetterTile> lettersOnGrid = gc.grid.getTilesInGrid();
    if (lettersOnGrid.isEmpty()){
      firstWord();
      gc.grid.verifyWordsValidity();
      return;
    }
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
          if (hasLetter(foundWord, ailetters.get(ailetters.size() - 1).getLetter())) {
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
      return;
    }
    LetterTile centralTile = ailetters.get(ailetters.size() - 1);
    // List<LetterTile> wordOnGrid = new ArrayList<LetterTile>();
    // wordOnGrid.add(tile);
    LetterTile first = null;
    LetterTile last = null;
    ailetters.remove(ailetters.size() - 1);
    int row = gc.grid.getCellRow(centralTile);
    int column = gc.grid.getCellColumn(centralTile);
    String[] parts = word.split(Character.toString(centralTile.getLetter()), 2);
    if (parts.length > 1 && !parts[0].equals("")){
      if (gc.grid.getNeighbourCell(centralTile, "bottom") == null && gc.grid.getNeighbourCell(centralTile, "top") == null){
        first = addTilesTop(column, row, parts[0], gc);
        last = addTilesBottom(column, row, parts[1], gc);
      }else if (gc.grid.getNeighbourCell(centralTile, "left") == null && gc.grid.getNeighbourCell(centralTile, "right") == null){
        first = addTilesLeft(column, row, parts[0], gc);
        last = addTilesRight(column, row, parts[1], gc);
      }
    }else if(parts.length == 1){
      if (gc.grid.getNeighbourCell(centralTile, "top") == null){
        first = addTilesTop(column, row, parts[0], gc);
      }else if (gc.grid.getNeighbourCell(centralTile, "left") == null){
        first = addTilesLeft(column, row, parts[0], gc);
      }
    }else {
      if (gc.grid.getNeighbourCell(centralTile, "bottom") == null){
        last = addTilesBottom(column, row, parts[1], gc);
      }else if (gc.grid.getNeighbourCell(centralTile, "right") == null){
        last = addTilesRight(column, row, parts[1], gc);
      }
    }

    if (word.charAt(0) == centralTile.getLetter()){
      System.out.println("Last letter -> " + last.getLetter());
      new Word(centralTile, last, gc);
    }else if (word.charAt(word.length() - 1) == centralTile.getLetter()){
      System.out.println("First letter -> " + first.getLetter());
      new Word(first, centralTile, gc);
    }else{
      System.out.println("First letter -> " + first.getLetter());
      System.out.println("Last letter -> " + last.getLetter());
      new Word(first, last, gc);
    }

    //Word foundWord = new Word(wordOnGrid.get(0), wordOnGrid.get(wordOnGrid.size() - 1), gc);
    gc.grid.verifyWordsValidity();
    // System.out.println("BOT: Word length -> " + foundWord.getWordLength());
  }
  /**
   * Returns changes done to the grid during the last turn in form of json.
   *
   * @author astarche
   * @return string with changes in form of json
   */
  public String createJsonString() {
    ArrayList<Word> wordsInGrid = gc.grid.words;
    int score = 0;
    String action = "{\n"
            + " \"nb\": \"" + wordsInGrid.size() + "\",\n"
            + " \"words\": [\n";

    for (int j = 0; j < wordsInGrid.size(); j++) {
      Word word = wordsInGrid.get(j);
      if (word.newlyPlaced) {
        String spelling = word.getWordAsString();
        action += "  {\n"
                + "   \"word\": \"" + spelling + "\",\n"
                + "   \"points\": \"" + word.getPoints() + "\",\n"
                + "   \"tiles\": [\n";

        for (int i = 0; i < word.getWordLength(); i++) {
          LetterTile letterTile = word.getLetter(i);
          if (letterTile.isNewlyPlaced) {
            char letter = letterTile.getLetter();
            int value = letterTile.getPoints();
            int row = gc.grid.getCellRow(letterTile);
            int col = gc.grid.getCellColumn(letterTile);
            boolean isBlank = letterTile.isBlank;

            action += "    {\n"
                    + "     \"letter\": \"" + letter + "\",\n"
                    + "     \"value\": \"" + value + "\",\n"
                    + "     \"row\": \"" + row + "\",\n"
                    + "     \"col\": \"" + col + "\",\n"
                    + "     \"isBlank\": \"" + isBlank + "\"\n"
                    + "    }";

            if (i == word.getWordLength() - 1) {
              action += "\n";
            } else {
              action += ",\n";
            }

            letterTile.isNewlyPlaced = false;
          }
        }

        action += "   ]";

        if (j == wordsInGrid.size() - 1) {
          action += "\n"
                  + "  }\n";
        } else {
          action += ",\n";
        }

        word.newlyPlaced = false;

        score += word.getPoints();
      }
    }

    action += " ],\n"
            + " \"score\": \"" + score + "\"\n"
            + "}";
    return action;
  }

  /**
   * First word.
   */
  private void firstWord(){
    List<String> foundWords = findWords(ailetters);
    if (foundWords.isEmpty()){
      return;
    }
    String word = foundWords.get(0);
    LetterTile first = null;
    LetterTile last = null;
    int col = 7;
    int row = 7;
    for (int i = 0;i < word.length();i++){
      if (i == 0){
        first = ailetters.get(findTile(word.charAt(i)));
      }
      if (i == word.length() - 1){
        last = ailetters.get(findTile(word.charAt(i)));
      }
      gc.grid.setSlotContent(col, row, ailetters.get(findTile(word.charAt(i))));
      row++;
    }
    Word foundWord = new Word(first, last, gc);
  }

  /**
   * Adds tiles over the given slot.
   *
   * @param column the column of the slot
   * @param row the row of the slot
   * @param part string with letters that need to be added
   * @param gc Game Controller
   * @return the letter tile
   */
  private LetterTile addTilesTop(int column, int row, String part, GameController gc){
    part = new StringBuilder(part).reverse().toString();
    LetterTile tile = null;
    for (int i = 0; i < part.length();i++) {
      if (findTile(part.charAt(i)) != -1) {
        if (i == part.length() - 1){
          tile = ailetters.get(findTile(part.charAt(i)));
        }
        gc.grid.setSlotContent(column, row - (i + 1), ailetters.get(findTile(part.charAt(i))));
        System.out.println("TILE ADDED TOP");
        //wordOnGrid.add(ailetters.get(findTile(part.charAt(i))));
        System.out.println("BOT: REMOVED TILE - " + ailetters.get(findTile(part.charAt(i))).getLetter());
        ailetters.remove(ailetters.get(findTile(part.charAt(i))));
      }else{
        if (findTile('\0') != -1) {
          ailetters.get(findTile('\0')).setLetter(part.charAt(i));
          if (i == part.length() - 1){
            tile = ailetters.get(findTile(part.charAt(i)));
          }
          gc.grid.setSlotContent(column, row - (i + 1), ailetters.get(findTile(part.charAt(i))));
          System.out.println("TILE ADDED TOP");
          //wordOnGrid.add(ailetters.get(findTile(part.charAt(i))));
          System.out.println("BOT: REMOVED TILE - BLANK TILE");
          ailetters.remove(ailetters.get(findTile(part.charAt(i))));
        }else{
          return null;
        }
      }
    }
    return tile;
  }

  /**
   * Adds tiles below the given slot.
   *
   * @param column the column of the slot
   * @param row the row of the slot
   * @param part string with letters that need to be added
   * @param gc Game Controller
   * @return the letter tile
   */
  private LetterTile addTilesBottom(int column, int row, String part, GameController gc){
    LetterTile tile = null;
    for (int i = 0; i < part.length();i++) {
      if (findTile(part.charAt(i)) != -1) {
        if (i == part.length() - 1){
          tile = ailetters.get(findTile(part.charAt(i)));
        }
        gc.grid.setSlotContent(column, row + (i + 1), ailetters.get(findTile(part.charAt(i))));
        System.out.println("TILE ADDED BOTTOM");
        // wordOnGrid.add(ailetters.get(findTile(part.charAt(i))));
        System.out.println("BOT: REMOVED TILE - " + ailetters.get(findTile(part.charAt(i))).getLetter());
        ailetters.remove(ailetters.get(findTile(part.charAt(i))));
      }else{
        if (findTile('\0') != -1) {
          ailetters.get(findTile('\0')).setLetter(part.charAt(i));
          if (i == part.length() - 1){
            tile = ailetters.get(findTile(part.charAt(i)));
          }
          gc.grid.setSlotContent(column, row + (i + 1), ailetters.get(findTile(part.charAt(i))));
          System.out.println("TILE ADDED BOTTOM");
          //wordOnGrid.add(ailetters.get(findTile(part.charAt(i))));
          System.out.println("BOT: REMOVED TILE - BLANK TILE");
          ailetters.remove(ailetters.get(findTile(part.charAt(i))));
        }else{
          return null;
        }
      }
    }
    return tile;
  }

  /**
   * Adds the tiles on the left of the given slot.
   *
   * @param column the column of the slot
   * @param row the row of the slot
   * @param part string with letters that need to be added
   * @param gc Game Controller
   * @return the letter tile
   */
  private LetterTile addTilesLeft(int column, int row, String part, GameController gc){
    LetterTile tile = null;
    part = new StringBuilder(part).reverse().toString();
    for (int i = 0; i < part.length();i++){
      if (findTile(part.charAt(i)) != -1) {
        if (i == part.length() - 1){
          tile = ailetters.get(findTile(part.charAt(i)));
        }
        gc.grid.setSlotContent(column - (i + 1), row, ailetters.get(findTile(part.charAt(i))));
        System.out.println("TILE ADDED LEFT");
        // wordOnGrid.add(ailetters.get(findTile(part.charAt(i))));
        System.out.println("BOT: REMOVED TILE - " + ailetters.get(findTile(part.charAt(i))).getLetter());
        ailetters.remove(ailetters.get(findTile(part.charAt(i))));
      }else{
        if (findTile('\0') != -1) {
          ailetters.get(findTile('\0')).setLetter(part.charAt(i));
          if (i == part.length() - 1){
            tile = ailetters.get(findTile(part.charAt(i)));
          }
          gc.grid.setSlotContent(column - (i + 1), row, ailetters.get(findTile(part.charAt(i))));
          System.out.println("TILE ADDED LEFT");
          //wordOnGrid.add(ailetters.get(findTile(part.charAt(i))));
          System.out.println("BOT: REMOVED TILE - BLANK TILE");
          ailetters.remove(ailetters.get(findTile(part.charAt(i))));
        }else {
          return null;
        }
      }
    }
    return tile;
  }

  /**
   * Adds the tiles on the right of the given slot.
   *
   * @param column the column of the slot
   * @param row the row of the slot
   * @param part string with letters that need to be added
   * @param gc Game Controller
   * @return the letter tile
   */
  private LetterTile addTilesRight(int column, int row, String part, GameController gc){
    LetterTile tile = null;
    for (int i = 0; i < part.length();i++){
      if (findTile(part.charAt(i)) != -1) {
        if (i == part.length() - 1){
          tile = ailetters.get(findTile(part.charAt(i)));
        }
        gc.grid.setSlotContent(column + (i + 1), row, ailetters.get(findTile(part.charAt(i))));
        System.out.println("TILE ADDED RIGHT");
        //wordOnGrid.add(ailetters.get(findTile(part.charAt(i))));
        System.out.println("BOT: REMOVED TILE - " + ailetters.get(findTile(part.charAt(i))).getLetter());
        ailetters.remove(ailetters.get(findTile(part.charAt(i))));
      }else {
        if (findTile('\0') != -1){
          ailetters.get(findTile('\0')).setLetter(part.charAt(i));
          if (i == part.length() - 1){
            tile = ailetters.get(findTile(part.charAt(i)));
          }
          gc.grid.setSlotContent(column + (i + 1), row, ailetters.get(findTile(part.charAt(i))));
          System.out.println("TILE ADDED RIGHT");
          // wordOnGrid.add(ailetters.get(findTile(part.charAt(i))));
          System.out.println("BOT: REMOVED TILE - BLANK TILE");
          ailetters.remove(ailetters.get(findTile(part.charAt(i))));
        }else{
          return null;
        }
      }
    }
    return tile;
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
   * @param letter letter
   * @return true, if successful
   */
  private boolean hasLetter(String word, char letter){
    boolean contains = false;
    for (int i = 0;i < word.length();i++){
        if (word.charAt(i) == letter){
          contains = true;
          break;
      }
    }
    return contains;
  }
  /**
   * Generates random tiles and adds them to the list with the letters of the aiplayer
   *
   * @author astarche
   * @param gc Game Controller
   */
  public void giveLettersToAiPlayer(){
    String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    Random random = new Random();
    int sz = ailetters.size();
    for (int i = 0;i < 5 - sz;i++){
      ailetters.add(new LetterTile(alphabet.charAt(random.nextInt(alphabet.length())), 1, 10,
              gc));
    }
    ailetters.add(new LetterTile('\0', 0, 10, gc));
    ailetters.add(new LetterTile('\0', 0, 10, gc));
  }
  /**
   * Grabs random tiles from the bag and gives them to the aiplayer
   *
   * @author astarche
   * @param bag bag with letters that is used in the game
   */
  public void giveLettersToAiPlayer(LetterBag bag){
    Multiset<LetterBag.Tile> tiles = bag.grabRandomTiles(7 - ailetters.size());
    for (LetterBag.Tile tile :tiles){
      ailetters.add(new LetterTile(tile.letter, tile.value, 10, gc));
    }
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
   * @return the string with possible words
   */
  public String helpPoorHuman() {
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
