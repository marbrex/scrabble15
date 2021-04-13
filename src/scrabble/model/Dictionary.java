package scrabble.model;

import java.io.*;
import java.util.*;
import scrabble.game.Word;

/**
 * scrabble.model.Dictionary class to import the word list, adding or removing words from the
 * Dictionary
 * 
 * @author Sergen Keskincelik
 */

public class Dictionary {

  /** BufferedReader to read each line of the Dictionary */
  private static BufferedReader in;
  /** ArrayList to store all the words in the Dictionary */
  private static List<String> words = new ArrayList<String>();
  /** ArrayList to store all the definitions in the Dictionary */
  private static List<String> definitions = new ArrayList<String>();

  /** Setting the Dictionary -> following the Format is necessary! */
  public static void setDictionary(File file) {
    try {
      in = new BufferedReader(new FileReader(file));
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
          if (word != null && word.length() <= 15) {
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

  /** Return the list of words */
  public static List<String> getWords() {
    return words;
  }

  /** Returns the list of all Definitions */
  public static List<String> getDefinitions() {
    return definitions;
  }

  /** Returns the size of the Dictionary */
  public static int getDictionarySize() {
    return getWords().size();
  }

  /** Checks, if given word matches a word of the Dictionary */
  public static boolean matches(Word word) {
    for (String dictionaryWord : words) {
      if (word.getWordAsString().equals(dictionaryWord)) {
        return true;
      }
    }
    return false;
  }

  /** Adds a Word into the Word-List */
  public static void addWord(Word word) {
    if (!matches(word)) {
      words.add(word.getWordAsString());
      Collections.sort(words);
    }
  }

  /** Removes Word from the Word-List */
  public static void removeWord(Word word) {
    for (int i = 0; i < words.size(); i++) {
      if (words.get(i).equals(word.getWordAsString())) {
        words.remove(i);
        break;
      }
    }
  }
}
