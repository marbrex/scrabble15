package scrabble.model;

import java.io.*;
import java.util.*;
import scrabble.game.Word;

/**
 * scrabble.model.Dictionary class to import the word list, adding or removing words from the
 * Dictionary
 * 
 * @author skeskinc
 */

public class Dictionary {

  private static BufferedReader in;
  private static List<String> words = new ArrayList<String>();
  private static List<String> definitions = new ArrayList<String>();

  /**
   * Setting the Dictionary -> following the Format is necessary!
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
        for (int i = 0; i < c.length - 1; i++) {
          if (Character.isWhitespace(c[i]) && !Character.isWhitespace(c[i + 1])) {
            definition = msg.substring(i + 1, c.length);
            definitions.add(definition);
            break;
          }
        }
      }
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Returns a list of words
   * 
   * @return list of words
   * @author skeskinc
   */
  public static List<String> getWords() {
    return words;
  }

  /**
   * Returns a list of all Definitions
   * 
   * @return all definitions which are present in the File
   * @author skeskinc
   */
  public static List<String> getDefinitions() {
    return definitions;
  }

  /**
   * Returns the size of the Dictionary
   * 
   * @return Size of the Dictionary
   * @author skeskinc
   */
  public static int getDictionarySize() {
    return getWords().size();
  }

  /**
   * Checks, if given word matches a word of the Dictionary
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
   * Adds a Word into the Word-List
   * 
   * @param word Adding given word to dictionary
   * @author skeskinc
   */
  public static void addWord(Word word) {
    if (!matches(word)) {
      words.add(word.getWordAsString());
      Collections.sort(words);
    }
  }

  /**
   * Removes Word from the Word-List
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
}
