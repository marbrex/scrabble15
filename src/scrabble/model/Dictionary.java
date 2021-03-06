package scrabble.model;

import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import scrabble.game.Word;

/**
 * scrabble.model.Dictionary class to set-up the Dictionary.
 * 
 * @author skeskinc
 */
public class Dictionary {

  private static BufferedReader in;
  private static List<String> words = new ArrayList<String>();
  private static List<String> definitions = new ArrayList<String>();
  private static LinkedHashMap<String, String> dictionary = new LinkedHashMap<String, String>();

  /**
   * Setting the Dictionary.
   * 
   * @param file to read the File
   * @author skeskinc
   */
  public static void setDictionary(InputStream file) {
    try {
      in = new BufferedReader(new InputStreamReader(file));
      char[] c;
      in.readLine();
      in.readLine();
      String msg;
      String word = null;
      String definition;
      while ((msg = in.readLine()) != null) {
        c = msg.toCharArray();
        word = null;
        // Looking at the words
        for (int i = 0; i < c.length; i++) {
          if (Character.isWhitespace(c[i])) {
            word = msg.substring(0, i);
          } else if (i == c.length - 1) {
            word = msg.substring(0, i + 1);
          }
          if (word != null && word.length() <= 15 && word.matches("[a-zA-Z]+")) {
            words.add(word);
            break;
          }
        }
        if (word.matches("[a-zA-Z]+") && word.length() <= 15) {
          // Looking at definitions
          for (int i = 0; i < c.length - 1; i++) {
            if (Character.isWhitespace(c[i]) && !Character.isWhitespace(c[i + 1])) {
              definition = msg.substring(i + 1, c.length);
              definitions.add(definition);
              dictionary.put(word, definition);
              break;
            } else if (Character.isWhitespace(c[i]) && Character.isWhitespace(c[i + 1])) {
              definitions.add("No definition found.");
              dictionary.put(word, "No definition found.");
              break;
            } else if (i == c.length - 2 && Character.isDefined(c[i + 1])) {
              definitions.add("No definition found.");
              dictionary.put(word, "No definition found.");
              break;
            }
          }
        }
      }
      // Removing duplicated words and definitions.
      removeDuplicates();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Returns a list of words.
   * 
   * @return list of words
   * @author skeskinc
   */
  public static List<String> getWords() {
    return words;
  }

  /**
   * Returns a list of all Definitions.
   * 
   * @return all definitions which are present in the File
   * @author skeskinc
   */
  public static List<String> getDefinitions() {
    return definitions;
  }

  /**
   * Returns the size of the Dictionary.
   * 
   * @return Size of the Dictionary
   * @author skeskinc
   */
  public static int getDictionarySize() {
    return getWords().size();
  }

  /**
   * Checks, if given word matches a word of the Dictionary.
   * 
   * @param word Matching given word with words from Dictionary
   * @author skeskinc
   */
  public static boolean matches(Word word) {
    for (String dictionaryWord : words) {
      if (word.getWordAsString().equals(dictionaryWord)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Adds a Word into the Word-List.
   * 
   * @param word Adding given word to dictionary
   * @author skeskinc
   */
  public static void addWord(Word word) {
    if (!matches(word)) {
      words.add(word.getWordAsString());
    }
  }

  /**
   * Removes Word from the Word-List.
   * 
   * @param word Removing given word from dictionary
   * @author skeskinc
   */
  public static void removeWord(Word word) {
    for (int i = 0; i < words.size(); i++) {
      if (words.get(i).equals(word.getWordAsString())) {
        words.remove(i);
        break;
      }
    }
  }

  /**
   * Returns the definition of the given word.
   * 
   * @param word Given word to search the definition for
   * @return Definition of a word
   * @author skeskinc
   */
  public static String getDefinition(String word) {
    String definition = dictionary.get(word);
    return definition;
  }

  /**
   * Removing duplicate words.
   * 
   * @author skeskinc
   */
  public static void removeDuplicates() {
    words = new ArrayList<String>(dictionary.keySet());
    definitions = new ArrayList<String>(dictionary.values());
  }

}
