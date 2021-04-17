package scrabble.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * scrabble.model.AiPlayer extends from the Player class and is necessary to determine the
 * difficulty of each AI Player
 * 
 * @author Sergen Keskincelik
 */

public class AiPlayer extends Player implements Serializable {

  /** Difficulty-Array for the Ai */
  private String[] difficulty = {"easy", "hard"};
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

  /**
   * Checks if the word can be made with the available letters
   *
   * @author Alexander Starchenkov
   */
  public boolean findWord(List<Letter> letters, String toFind) {
    boolean found = false;
    if (this.hasLettersOf(this.getValues(letters), toFind)) {
      String hl = clear(getValues(letters).toString(), toFind);
      if (hl.length() >= toFind.length()) {
        found = true;
      }
    }
    return found;
  }

  /**
   * Returns a list with all possible words from the dictionary that can be made with the given
   * letters
   *
   * @author Alexander Starchenkov
   */
  public List<String> findWords(List<Letter> letters, List<String> words) {
    List<String> foundWords = new ArrayList<String>();
    for (int i = 0; i < words.size(); i++) {
      if (this.findWord(letters, words.get(i))) {
        foundWords.add(words.get(i));
      }
    }
    return foundWords;
  }

  /**
   * Checks if all letters to make the searched word are present
   *
   * @author Alexander Starchenkov
   */
  public boolean hasLettersOf(List<Character> currentLetters, String word) {
    boolean res = true;
    for (int i = 0; i < word.length(); i++) {
      if (!currentLetters.contains(word.charAt(i))) {
        res = false;
        break;
      }
    }
    return res;
  }

  /**
   * Returns a list with char values of the letters
   *
   * @author Alexander Starchenkov
   */
  public List<Character> getValues(List<Letter> letters) {
    List<Character> values = new ArrayList<Character>();
    for (int i = 0; i < letters.size(); i++) {
      values.add(letters.get(i).getLetter());
    }
    return values;
  }

  /**
   * Removes all the letters that are not used in the searched word
   *
   * @author Alexander Starchenkov
   */
  public String clear(String currentLetters, String word) {
    String cleared = "";
    for (int i = 0; i < currentLetters.length(); i++) {
      if (word.contains(currentLetters.charAt(i) + "")) {
        cleared += currentLetters.charAt(i);
      }
    }
    return cleared;
  }
}
