package scrabble.model;

import java.io.*;
import java.util.*;

public class Dictionary {

	private static BufferedReader in;
	private static List<String> words = new ArrayList<String>();
	private static List<String> definitions = new ArrayList<String>();

	/** Setting the Dictionary -> following the Format is necessary!
	 * 
	 * @param file
	 */
	public static void setDictionary(File file) {
		try {
			in = new BufferedReader(new FileReader(file));
			char[] c;
			in.readLine();
			in.readLine();
			String msg;
			String word;
			String definition;
			while ((msg = in.readLine()) != null) {
				c = msg.toCharArray();
				for (int i = 0; i < c.length; i++) {
					if (Character.isWhitespace(c[i])) {
						word = msg.substring(0, i);
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
	/** Return the list of words
	 * 
	 * 
	 */
	public static List<String> getWords() {
		return words;
	}
	/**
	 * Returns the List of all Definitions
	 * 
	 */
	public static List<String> getDefinitions() {
		return definitions;
	}
	/**
	 * Returns the size of the Dictionary
	 *
	 */
	public static int getDictionarySize() {
		return getWords().size();
	}
	/*
	public static void main(String[] args) {

		setDictionary(new File(""));
		for (int i = 0; i < 10; i++) {
			System.out.println(definitions.get(i));
		}
		System.out.println(getDefinitions().get(279493));
		System.out.println("Size of Definitions: " + definitions.size());
	}
	*/
}
