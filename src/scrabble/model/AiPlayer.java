package scrabble.model;

import com.google.common.collect.Multiset;
import scrabble.GameController;
import scrabble.dbhandler.DBInformation;
import scrabble.game.Grid;
import scrabble.game.LetterBag;
import scrabble.game.LetterTile;
import scrabble.game.Word;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * scrabble.model.AiPlayer extends from the Player class and is necessary to determine the
 * difficulty of each AI Player.
 *
 * @author astarche
 * @author skeskinc
 */

public class AiPlayer extends Player implements Serializable {

    private GameController gc;

    /**
     * The letters that aiplayer uses to construct new words.
     */
    private ArrayList<LetterTile> ailetters = new ArrayList<LetterTile>();


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
     * Gets the controller.
     *
     * @return the controller
     */
    public GameController getController() {
        return this.gc;
    }

    /**
     * Sets Game Controller.
     *
     * @param gc Game Controller
     * @author astarche
     */
    public void setController(GameController gc) {
        this.gc = gc;
    }

    /**
     * Checks if the word can be made with the available letters.
     *
     * @param letters the letters
     * @param toFind  the to find
     * @return true, if successful
     * @author astarche
     */
    private boolean findWord(ArrayList<LetterTile> letters, String toFind) {
        boolean found = false;
        if (hasLettersOf(getValues(letters), toFind)) {
            String hl = clear(getValues(letters).toString(), toFind);
            if (hasEnoughLetters(hl, toFind) == 0) {
                System.out.println("THE WORD " + toFind + " CAN BE MADE WITHOUT BLANK TILES");
                found = true;
            } else if (hasEnoughLetters(hl, toFind) == 1) {
                if (hasTile('\0', letters) >= 1) {
                    System.out.println("THE WORD " + toFind + " CAN BE MADE WITH ONE BLANK TILE");
                    found = true;
                }
            } else if (hasEnoughLetters(hl, toFind) == 2) {
                if (hasTile('\0', letters) >= 2) {
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
     * @param letters the letters
     * @return the list
     * @author astarche
     */
    private ArrayList<String> findWords(ArrayList<LetterTile> letters) {
        ArrayList<String> foundWords = new ArrayList<String>();
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
     * @param currentLetters the current letters
     * @param word           the word
     * @return true, if successful
     * @author astarche
     */
    private boolean hasLettersOf(ArrayList<Character> currentLetters, String word) {
        ArrayList<Character> wordList = new ArrayList<Character>();
        for (int i = 0; i < word.length(); i++) {
            wordList.add(word.charAt(i));
        }
        return currentLetters.containsAll(wordList);
    }

    /**
     * Checks for a number of specified letters in the word.
     * It is used to find duplicates.
     *
     * @param word   word
     * @param letter specified letter
     * @return the number of specified letters in the word
     * @author astarche
     */
    private int numberOf(String word, char letter) {
        int count = 0;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == letter) {
                count++;
            }
        }
        return count;
    }

    /**
     * Checks for a number of specified letters in the list of letters
     * It is used to find duplicates.
     *
     * @param letters the letters
     * @param letter  the letter
     * @return the int
     * @author astarche
     */
    private int numberOf(ArrayList<Character> letters, char letter) {
        int count = 0;
        for (Character character : letters) {
            if (character == letter) {
                count++;
            }
        }
        return count;
    }

    /**
     * Gives letters that are needed to complement the word
     * in order to make a new word
     *
     * @param word     word that needs to be complemented
     * @param toAppend new word
     * @return true, if successful
     * @author astarche
     */
    private String giveLettersToComplement(String word, String toAppend) {
        ArrayList<Character> lettersAsChar = getValues(ailetters);
        String clear = word.replaceAll(toAppend, "");
        if (clear.isEmpty()) {
            clear = toAppend;
        }
        return clear;
    }

    /**
     * Returns a list with char values of the letters.
     *
     * @param letters the letters
     * @return the values
     * @author astarche
     */
    private ArrayList<Character> getValues(ArrayList<LetterTile> letters) {
        ArrayList<Character> values = new ArrayList<Character>();
        for (LetterTile letter : letters) {
            values.add(letter.getLetter());
        }
        return values;
    }

    /**
     * Removes all the letters that are not used in the searched word.
     *
     * @param currentLetters the current letters
     * @param word           the word
     * @return string with letters that are only used in the searched word
     * @author astarche
     */
    private String clear(String currentLetters, String word) {
        StringBuilder cleared = new StringBuilder();
        for (int i = 0; i < currentLetters.length(); i++) {
            if (word.contains(Character.toString(currentLetters.charAt(i)))) {
                cleared.append(currentLetters.charAt(i));
            }
        }
        return cleared.toString();
    }

    /**
     * Checks for there are enough letters to build the specified word.
     *
     * @param letters letters
     * @param word    word that needs to be constructed
     * @return number of blank tiles that are needed to make the word, 0 if no blank tiles are needed
     * @author astarche
     */
    private int hasEnoughLetters(String letters, String word) {
        int diff = 0;
        ArrayList<Character> lettersAsList = new ArrayList<Character>();
        for (int i = 0; i < letters.length(); i++) {
            lettersAsList.add(letters.charAt(i));
        }
        ArrayList<Character> help = new ArrayList<Character>();
        for (int i = 0; i < word.length(); i++) {
            if (help.contains(word.charAt(i))) {
                continue;
            }
            if (numberOf(lettersAsList, word.charAt(i)) < numberOf(word, word.charAt(i))) {
                help.add(word.charAt(i));
                diff += Math.abs(numberOf(lettersAsList, word.charAt(i)) - numberOf(word, word.charAt(i)));
            }
        }
        return diff;
    }

    /**
     * Gives a list with all the words that can be made with one tile
     * without breaking the rules.
     *
     * @param words       found with the specified tile
     * @param centralTile the specified tile
     * @return a list with safe words
     * @author astarche
     */
    private ArrayList<String> giveSafeWords(ArrayList<String> words, LetterTile centralTile) {
        ArrayList<String> safeWords = new ArrayList<String>();
        for (String word : words) {
            System.out.println("CHECKING WORD: " + word);
            String[] parts = word.split(Character.toString(centralTile.getLetter()), 2);
            int col = gc.grid.getCellColumn(centralTile);
            int row = gc.grid.getCellRow(centralTile);
            if (wordPlacedHorizontal(centralTile)) {
                if (isSafe(col, row, parts[0].length(), "left") &&
                        isSafe(col, row, parts[1].length(), "right")
                        && hasLetter(word, centralTile.getLetter())) {
                    safeWords.add(word);
                }
            } else {
                if (isSafe(col, row, parts[0].length(), "top") &&
                        isSafe(col, row, parts[1].length(), "bottom")
                        && hasLetter(word, centralTile.getLetter())) {
                    safeWords.add(word);
                }
            }
        }
        return safeWords;
    }

    /**
     * This method is used to make turn of AI player. Firstly, it tries to complement
     * already an existing word to make a new one. If AI does not manage that, it tries
     * to make a word with one free tile.
     *
     * @return placed word, null if no words were placed
     * @author astarche
     */
    public Word makeTurn() {
        if (!DBInformation.isAiDifficultyHard(Profile.getPlayer())) {
            Random random = new Random();
            if (random.nextInt(9) == 5) { // 10% chance that aiplayer will skip his turn
                System.out.println("BOT: RANDOM SKIP!");
                return null;
            }
        }
        ArrayList<LetterTile> lettersOnGrid = gc.grid.getTilesInGrid();
        if (lettersOnGrid.isEmpty()) {
            return firstTurn();
        }
        Word cmp = complementWord();
        if (cmp != null) {
            return cmp;
        }
        String word;
        ArrayList<String> placeableWords = new ArrayList<String>();
        for (LetterTile letterTile : lettersOnGrid) {
            if (isFree(letterTile, gc.grid)) {
                ailetters.add(letterTile);
                System.out.println("BOT: TRYING TO MAKE SOMETHING WITH " + letterTile.getLetter());
                ArrayList<String> foundWords = findWords(ailetters);
                if (foundWords.isEmpty()) {
                    System.out.println("BOT: CANT MAKE ANYTHING WITH LETTER " + letterTile.getLetter());
                    System.out.println("BOT: REMOVE - " + letterTile.getLetter());
                    ailetters.remove(ailetters.size() - 1);
                    continue;
                }
                for (String foundWord : foundWords) {
                    if (hasLetter(foundWord, ailetters.get(ailetters.size() - 1).getLetter())) {
                        placeableWords.add(foundWord);
                    }
                }
            }
            placeableWords = giveSafeWords(placeableWords, letterTile);
            if (!placeableWords.isEmpty()) {
                break;
            } else {
                if (!ailetters.isEmpty()) {
                    ailetters.remove(ailetters.size() - 1);
                    System.out.println("BOT: REMOVE - " + letterTile.getLetter());
                }
            }
        }
        if (placeableWords.isEmpty()) {
            System.out.println("BOT: NO WORDS FOUND");
            return null;
        }
        if (DBInformation.isAiDifficultyHard(Profile.getPlayer())) {
            word = placeableWords.get(selectTheBestWord(placeableWords));
        } else {
            word = placeableWords.get(0);
        }
        System.out.println("BOT: I WILL USE THE WORD: " + word);
        LetterTile centralTile = ailetters.get(ailetters.size() - 1);
        LetterTile first;
        LetterTile last;
        ailetters.remove(ailetters.size() - 1);
        System.out.println("CENTRAL TILE: " + centralTile.getLetter());
        int row = gc.grid.getCellRow(centralTile);
        int col = gc.grid.getCellColumn(centralTile);
        String[] parts = word.split(Character.toString(centralTile.getLetter()), 2);
        if (wordPlacedHorizontal(centralTile)) {
            if (!parts[0].isEmpty() && !parts[1].isEmpty()) {
                first = addTilesLeft(col, row, parts[0], gc);
                last = addTilesRight(col, row, parts[1], gc);
            } else if (parts[1].isEmpty()) {
                first = addTilesLeft(col, row, parts[0], gc);
                last = centralTile;
            } else {
                first = centralTile;
                last = addTilesRight(col, row, parts[1], gc);
            }
        } else {
            if (!parts[0].isEmpty() && !parts[1].isEmpty()) {
                first = addTilesTop(col, row, parts[0], gc);
                last = addTilesBottom(col, row, parts[1], gc);
            } else if (parts[1].isEmpty()) {
                first = addTilesTop(col, row, parts[0], gc);
                last = centralTile;
            } else {
                first = centralTile;
                last = addTilesBottom(col, row, parts[1], gc);
            }
        }
        if (word.charAt(0) == centralTile.getLetter()) {
            return new Word(centralTile, last, gc);
        } else if (word.charAt(word.length() - 1) == centralTile.getLetter()) {
            return new Word(first, centralTile, gc);
        } else {
            return new Word(first, last, gc);
        }

    }

    /**
     * Finds the longest word in the list of words
     *
     * @param words list of words
     * @return index of the longest word in the list
     * @author astarche
     */
    private int selectTheBestWord(ArrayList<String> words) {
        int res = 0;
        for (int i = 1; i < words.size(); i++) {
            if (words.get(i).length() > words.get(i - 1).length()) {
                res = i;
            }
        }
        return res;
    }

    /**
     * Checks if word will be placed horizontal
     *
     * @param centralTile the central tile
     * @return true, if horizontal
     * @author astarche
     */
    private boolean wordPlacedHorizontal(LetterTile centralTile) {
        return gc.grid.getNeighbourCell(centralTile, "bottom") != null ||
                gc.grid.getNeighbourCell(centralTile, "top") != null;
    }

    private boolean safeToComplement(String word, Word toCmp) {
        int colFirst = gc.grid.getCellColumn(toCmp.getFirst());
        int rowFirst = gc.grid.getCellRow(toCmp.getFirst());
        int colLast = gc.grid.getCellColumn(toCmp.getLast());
        int rowLast = gc.grid.getCellRow(toCmp.getLast());
        String[] parts = word.split(toCmp.getWordAsString(), 2);
        boolean safe = false;
        if (toCmp.isHorizontal()) {
            if (isSafe(colFirst, rowFirst, parts[0].length(), "left") &&
                    isSafe(colLast, rowLast, parts[1].length(), "right")) {
                safe = true;
            }
        } else {
            if (isSafe(colFirst, rowFirst, parts[0].length(), "top") &&
                    isSafe(colLast, rowLast, parts[1].length(), "bottom")) {
                safe = true;
            }
        }
        return safe;
    }

    /**
     * Complements already an existing word to make a new one.
     *
     * @return new word
     * @author astarche
     */
    private Word complementWord() {
        Word toAppend = null;
        String appendedWord = null;
        boolean flag = false;
        for (Word word : gc.grid.words) {
            String hl = word.getWordAsString();
            ArrayList<String> words = wordsThatContain(hl);
            for (String s : words) {
                String letters = giveLettersToComplement(s, hl);
                if (findWord(ailetters, letters) && safeToComplement(s, word)) {
                    toAppend = word;
                    appendedWord = s;
                    flag = true;
                    break;
                }

            }
            if (flag) {
                break;
            }
        }
        if (appendedWord == null) {
            System.out.println("BOT: SKIP TURN");
            return null;
        }
        System.out.println("BOT: I WILL USE THE WORD - " + appendedWord);
        String[] parts = appendedWord.split(toAppend.getWordAsString(), 2);
        int colFirst = gc.grid.getCellColumn(toAppend.getFirst());
        int rowFirst = gc.grid.getCellRow(toAppend.getFirst());
        int colLast = gc.grid.getCellColumn(toAppend.getLast());
        int rowLast = gc.grid.getCellRow(toAppend.getLast());
        LetterTile first;
        LetterTile last = null;
        if (toAppend.isVertical()) {
            System.out.println("BOT: I WILL PLACE THIS WORD VERTICALLY");
            if (isSafe(colFirst, rowFirst, parts[0].length(), "top") &&
                    isSafe(colLast, rowLast, parts[1].length(), "bottom")) {
                if (!parts[0].isEmpty() && !parts[1].isEmpty()) {
                    first = addTilesTop(colFirst, rowFirst, parts[0], gc);
                    last = addTilesBottom(colLast, rowLast, parts[1], gc);
                } else if (parts[1].isEmpty()) {
                    first = addTilesTop(colFirst, rowFirst, parts[0], gc);
                    last = toAppend.getLast();
                } else {
                    first = toAppend.getFirst();
                    last = addTilesBottom(colLast, rowLast, parts[1], gc);
                }
            } else {
                System.out.println("NOT A SAFE ZONE");
                return null;
            }
        } else {
            System.out.println("BOT: I WILL PLACE THIS WORD HORIZONTALLY");
            if (isSafe(colFirst, rowFirst, parts[0].length(), "left") &&
                    isSafe(colLast, rowLast, parts[1].length(), "right")) {
                if (!parts[0].isEmpty() && !parts[1].isEmpty()) {
                    first = addTilesLeft(colFirst, rowFirst, parts[0], gc);
                    last = addTilesRight(colLast, rowLast, parts[1], gc);
                } else if (parts[1].isEmpty()) {
                    first = addTilesLeft(colFirst, rowFirst, parts[0], gc);
                    last = toAppend.getLast();
                } else {
                    first = toAppend.getFirst();
                    last = addTilesRight(colLast, rowLast, parts[1], gc);
                }
            } else {
                System.out.println("NOT A SAFE ZONE");
                return null;
            }
        }
        return new Word(first, last, gc);
    }

    /**
     * Gives a list of words that contain the specified word in it.
     *
     * @param word the specified word
     * @return the list
     * @author astarche
     */
    private ArrayList<String> wordsThatContain(String word) {
        ArrayList<String> foundWords = new ArrayList<String>();
        for (String dictWord : Dictionary.getWords()) {
            if (dictWord.matches(".*" + word + ".*{1}") && !dictWord.equals(word)) {
                foundWords.add(dictWord);
            }
        }
        return foundWords;
    }

    /**
     * Checks if placed word is gonna be safe (will not break any rules of the game).
     *
     * @param col       the column
     * @param row       the row
     * @param length    the length of word
     * @param direction the direction of the word
     * @return true, if the word is safe
     * @author astarche
     */
    private boolean isSafe(int col, int row, int length, String direction) {
        boolean safe = true;
        if (col + length > 14 || row + length > 14 || col - length < 0 || row - length < 0) {
            return false;
        }
        System.out.println("BOUNDS +: " + (gc.grid.getGlobalIndex(col, row) + length));
        System.out.println("BOUNDS -: " + (gc.grid.getGlobalIndex(col, row) - length));
        if (length == 0) {
            switch (direction) {
                case "top":
                    if (row - 1 > -1) {
                        if (gc.grid.getSlot(col, row - 1).content != null) {
                            safe = false;
                            break;
                        }
                    }
                    break;
                case "bottom":
                    if (row + 1 < 15) {
                        if (gc.grid.getSlot(col, row + 1).content != null) {
                            safe = false;
                            break;
                        }
                    }
                    break;
                case "left":
                    if (col - 1 > -1) {
                        if (gc.grid.getSlot(col - 1, row).content != null) {
                            safe = false;
                            break;
                        }
                    }
                    break;
                case "right":
                    if (col + 1 < 15) {
                        if (gc.grid.getSlot(col + 1, row).content != null) {
                            safe = false;
                            break;
                        }
                    }
                    break;
            }
            return safe;
        }
        switch (direction) {
            case "top":
                row -= 1;
                System.out.println("BOT: CHECKING TOP");
                for (int i = 0; i < length; i++) {
                    if (i == length - 1 && row - 1 > -1) {
                        if (gc.grid.getSlot(col, row - 1).content != null) {
                            safe = false;
                            break;
                        }
                    }
                    System.out.println("BOT: CHECKING " + "(" + col + "," + row + ")");
                    if ((gc.grid.getSlot(col, row).content != null)) {
                        System.out.println("1)BOT: (" + col + "," + row + ") is not safe");
                        safe = false;
                        break;
                    } else if ((gc.grid.getSlot(col + 1, row).content != null) ||
                            (gc.grid.getSlot(col - 1, row).content != null)) {
                        System.out.println("2)BOT: (" + col + "," + row + ") is not safe");
                        safe = false;
                        break;
                    }
                    System.out.println("BOT: (" + col + "," + row + ") is safe");
                    row--;
                }
                break;
            case "bottom":
                row += 1;
                System.out.println("BOT: CHECKING BOTTOM");
                for (int i = 0; i < length; i++) {
                    if (i == length - 1 && row + 1 < 15) {

                        if (gc.grid.getSlot(col, row + 1).content != null) {
                            safe = false;
                            break;
                        }
                    }
                    System.out.println("BOT: CHECKING " + "(" + col + "," + row + ")");
                    if ((gc.grid.getSlot(col, row).content != null)) {
                        System.out.println("1)BOT: (" + col + "," + row + ") is not safe");
                        safe = false;
                        break;
                    } else if ((gc.grid.getSlot(col + 1, row).content != null) ||
                            (gc.grid.getSlot(col - 1, row).content != null)) {
                        System.out.println("2)BOT: (" + col + "," + row + ") is not safe");
                        safe = false;
                        break;
                    }
                    System.out.println("BOT: (" + col + "," + row + ") is safe");
                    row++;
                }
                break;
            case "left":
                col -= 1;
                for (int i = 0; i < length; i++) {
                    if (i == length - 1 && col - 1 > -1) {
                        if (gc.grid.getSlot(col - 1, row).content != null) {
                            safe = false;
                            break;
                        }
                    }

                    System.out.println("BOT: CHECKING " + "(" + col + "," + row + ")");
                    if ((gc.grid.getSlot(col, row).content != null)) {
                        System.out.println("1)BOT: (" + col + "," + row + ") is not safe");
                        safe = false;
                        break;
                    } else if ((gc.grid.getSlot(col, row + 1).content != null) ||
                            (gc.grid.getSlot(col, row - 1).content != null)) {
                        System.out.println("2)BOT: (" + col + "," + row + ") is not safe");
                        safe = false;
                        break;
                    }
                    System.out.println("BOT: (" + col + "," + row + ") is safe");
                    col--;
                }
                break;
            case "right":
                col += 1;
                System.out.println("BOT: CHECKING RIGHT");
                for (int i = 0; i < length; i++) {
                    if (i == length - 1 && col + 1 < 15) {
                        if (gc.grid.getSlot(col + 1, row).content != null) {
                            safe = false;
                            break;
                        }
                    }
                    System.out.println("BOT: CHECKING " + "(" + col + "," + row + ")");
                    if ((gc.grid.getSlot(col, row).content != null)) {
                        System.out.println("1)BOT: (" + col + "," + row + ") is not safe");
                        safe = false;
                        break;
                    } else if ((gc.grid.getSlot(col, row + 1).content != null) ||
                            (gc.grid.getSlot(col, row - 1).content != null)) {
                        System.out.println("2)BOT: (" + col + "," + row + ") is not safe");
                        safe = false;
                        break;
                    }
                    System.out.println("BOT: (" + col + "," + row + ") is safe");
                    col++;
                }
                break;
            default:
                break;
        }
        return safe;
    }

    /**
     * Returns changes done to the grid during the last turn in form of json.
     * Method is used in multiplayer.
     *
     * @return string with changes in form of json
     * @author ekasmamy
     */
    public String createJsonString() {

        ArrayList<Word> wordsInGrid = gc.grid.words;
        int score = 0;
        StringBuilder action = new StringBuilder(
                "{\n" + " \"nb\": \"" + wordsInGrid.size() + "\",\n" + " \"words\": [\n");

        for (int j = 0; j < wordsInGrid.size(); j++) {
            Word word = wordsInGrid.get(j);
            System.out.println("\nCurrent word: " + word.getWordAsString());
            System.out.println("is newly placed: " + word.newlyPlaced);
            System.out.println("is frozen: " + word.frozen);
            System.out.println("letters:");

            if (word.newlyPlaced) {
                String spelling = word.getWordAsString();
                action.append("  {\n").append("   \"word\": \"").append(spelling).append("\",\n")
                        .append("   \"points\": \"").append(word.getPoints()).append("\",\n")
                        .append("   \"tiles\": [\n");

                for (int i = 0; i < word.getWordLength(); i++) {
                    LetterTile letterTile = word.getLetter(i);
                    System.out.println("Current letter: " + letterTile.getLetter());
                    System.out.println("is newly placed: " + letterTile.isNewlyPlaced);
                    System.out.println("is frozen: " + letterTile.isFrozen);

                    if (letterTile.isNewlyPlaced) {
                        char letter = letterTile.getLetter();
                        int value = letterTile.getPoints();
                        int row = gc.grid.getCellRow(letterTile);
                        int col = gc.grid.getCellColumn(letterTile);
                        boolean isBlank = letterTile.isBlank;

                        action.append("    {\n").append("     \"letter\": \"").append(letter)
                                .append("\",\n").append("     \"value\": \"").append(value)
                                .append("\",\n").append("     \"row\": \"").append(row).append("\",\n")
                                .append("     \"col\": \"").append(col).append("\",\n")
                                .append("     \"isBlank\": \"").append(isBlank).append("\"\n")
                                .append("    }");

                        if (i == word.getWordLength() - 1) {
                            action.append("\n");
                        } else {
                            action.append(",\n");
                        }
                    }
                }

                action.append("   ]\n" + "  }");

                if (j == wordsInGrid.size() - 1) {
                    action.append("\n");
                } else {
                    action.append(",\n");
                }
                score += word.getPoints();
            }
        }

        action.append(" ],\n").append(" \"score\": \"").append(score).append("\"\n")
                .append("}");

        System.out.println(action);
        return action.toString();
    }

    /**
     * If grid is empty, ai makes first turn.
     *
     * @return placed word
     * @author astarche
     * @author astarche
     */
    private Word firstTurn() {
        ArrayList<String> foundWords = findWords(ailetters);
        if (foundWords.isEmpty()) {
            return null;
        }
        String word;
        if (DBInformation.isAiDifficultyHard(Profile.getPlayer())) {
            word = foundWords.get(selectTheBestWord(foundWords));
        } else {
            word = foundWords.get(0);
        }
        System.out.println("BOT: I WILL USE THE WORD - " + word);
        LetterTile first = null;
        LetterTile last = null;
        int col = 7;
        int row = 7;
        for (int i = 0; i < word.length(); i++) {
            if (findTile(word.charAt(i)) == -1) {
                System.out.println("ADDING BLANK TILE");
                ailetters.remove(ailetters.get(findTile('\0')));
                LetterTile blank = new LetterTile(word.charAt(i), 0, 10, gc);
                if (i == 0) {
                    first = blank;
                }

                if (i == word.length() - 1) {
                    last = blank;
                }
                gc.grid.setSlotContent(col, row, blank);
                row++;
                continue;
            }
            if (i == 0) {
                first = ailetters.get(findTile(word.charAt(i)));
            }
            if (i == word.length() - 1) {
                last = ailetters.get(findTile(word.charAt(i)));
            }
            gc.grid.setSlotContent(col, row, ailetters.get(findTile(word.charAt(i))));
            ailetters.remove(ailetters.get(findTile(word.charAt(i))));
            row++;
        }
        return new Word(first, last, gc);
    }

    /**
     * Adds tiles over the given slot.
     *
     * @param column the column of the slot
     * @param row    the row of the slot
     * @param part   string with letters that need to be added
     * @param gc     Game Controller
     * @return the letter tile
     * @author astarche
     */
    private LetterTile addTilesTop(int column, int row, String part, GameController gc) {
        part = new StringBuilder(part).reverse().toString();
        LetterTile tile = null;
        for (int i = 0; i < part.length(); i++) {
            if (findTile(part.charAt(i)) != -1) {
                if (i == part.length() - 1) {
                    tile = ailetters.get(findTile(part.charAt(i)));
                }
                gc.grid.setSlotContent(column, row - (i + 1), ailetters.get(findTile(part.charAt(i))));
                System.out.println("TILE ADDED TOP");
                System.out.println("BOT: REMOVED TILE - " + ailetters.get(findTile(part.charAt(i))).getLetter());
                ailetters.remove(ailetters.get(findTile(part.charAt(i))));
            } else {
                if (findTile('\0') != -1) {
                    LetterTile blank = new LetterTile(part.charAt(i), 0, 10, gc);
                    ailetters.remove(ailetters.get(findTile('\0')));
                    if (i == part.length() - 1) {
                        tile = blank;
                    }
                    gc.grid.setSlotContent(column, row - (i + 1), blank);
                    System.out.println("TILE ADDED TOP");
                    System.out.println("BOT: REMOVED TILE - BLANK TILE");
                } else {
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
     * @param row    the row of the slot
     * @param part   string with letters that need to be added
     * @param gc     Game Controller
     * @return the letter tile
     * @author astarche
     */
    private LetterTile addTilesBottom(int column, int row, String part, GameController gc) {
        LetterTile tile = null;
        for (int i = 0; i < part.length(); i++) {
            if (findTile(part.charAt(i)) != -1) {
                if (i == part.length() - 1) {
                    tile = ailetters.get(findTile(part.charAt(i)));
                }
                gc.grid.setSlotContent(column, row + (i + 1), ailetters.get(findTile(part.charAt(i))));
                System.out.println("TILE ADDED BOTTOM");
                System.out.println("BOT: REMOVED TILE - " + ailetters.get(findTile(part.charAt(i))).getLetter());
                ailetters.remove(ailetters.get(findTile(part.charAt(i))));
            } else {
                if (findTile('\0') != -1) {
                    LetterTile blank = new LetterTile(part.charAt(i), 0, 10, gc);
                    ailetters.remove(ailetters.get(findTile('\0')));
                    if (i == part.length() - 1) {
                        tile = blank;
                    }
                    gc.grid.setSlotContent(column, row + (i + 1), blank);
                    System.out.println("TILE ADDED BOTTOM");
                    System.out.println("BOT: REMOVED TILE - BLANK TILE");
                } else {
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
     * @param row    the row of the slot
     * @param part   string with letters that need to be added
     * @param gc     Game Controller
     * @return the letter tile
     * @author astarche
     */
    private LetterTile addTilesLeft(int column, int row, String part, GameController gc) {
        LetterTile tile = null;
        part = new StringBuilder(part).reverse().toString();
        for (int i = 0; i < part.length(); i++) {
            if (findTile(part.charAt(i)) != -1) {
                if (i == part.length() - 1) {
                    tile = ailetters.get(findTile(part.charAt(i)));
                }
                gc.grid.setSlotContent(column - (i + 1), row, ailetters.get(findTile(part.charAt(i))));
                System.out.println("TILE ADDED LEFT");
                System.out.println("BOT: REMOVED TILE - " + ailetters.get(findTile(part.charAt(i))).getLetter());
                ailetters.remove(ailetters.get(findTile(part.charAt(i))));
            } else {
                if (findTile('\0') != -1) {
                    LetterTile blank = new LetterTile(part.charAt(i), 0, 10, gc);
                    ailetters.remove(ailetters.get(findTile('\0')));
                    if (i == part.length() - 1) {
                        tile = blank;
                    }
                    gc.grid.setSlotContent(column - (i + 1), row, blank);
                    System.out.println("TILE ADDED LEFT");
                    System.out.println("BOT: REMOVED TILE - BLANK TILE");
                } else {
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
     * @param row    the row of the slot
     * @param part   string with letters that need to be added
     * @param gc     Game Controller
     * @return the letter tile
     * @author astarche
     */
    private LetterTile addTilesRight(int column, int row, String part, GameController gc) {
        LetterTile tile = null;
        for (int i = 0; i < part.length(); i++) {
            if (findTile(part.charAt(i)) != -1) {
                if (i == part.length() - 1) {
                    tile = ailetters.get(findTile(part.charAt(i)));
                }
                gc.grid.setSlotContent(column + (i + 1), row, ailetters.get(findTile(part.charAt(i))));
                System.out.println("TILE ADDED RIGHT");
                System.out.println("BOT: REMOVED TILE - " + ailetters.get(findTile(part.charAt(i))).getLetter());
                ailetters.remove(ailetters.get(findTile(part.charAt(i))));
            } else {
                if (findTile('\0') != -1) {
                    LetterTile blank = new LetterTile(part.charAt(i), 0, 10, gc);
                    ailetters.remove(ailetters.get(findTile('\0')));
                    if (i == part.length() - 1) {
                        tile = blank;
                    }
                    gc.grid.setSlotContent(column + (i + 1), row, blank);
                    System.out.println("TILE ADDED RIGHT");
                    System.out.println("BOT: REMOVED TILE - BLANK TILE");
                } else {
                    return null;
                }
            }
        }
        return tile;
    }


    /**
     * Checks if the letter tile is free (has exactly 2 neighbours vertically or horizontally).
     *
     * @param tile the tile
     * @param grid the grid
     * @return true, if is free
     * @author astarche
     */
    private boolean isFree(LetterTile tile, Grid grid) {
        return (grid.getNeighbourCell(tile, "right") == null
                && grid.getNeighbourCell(tile, "left") == null)
                || (grid.getNeighbourCell(tile, "top") == null
                && grid.getNeighbourCell(tile, "bottom") == null);
    }

    /**
     * Checks if constructed word contains the tile on the grid.
     *
     * @param word   the word
     * @param letter letter
     * @return true, if successful
     * @author astarche
     */
    private boolean hasLetter(String word, char letter) {
        boolean contains = false;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == letter) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    /**
     * Grabs random tiles from the bag and gives them to the aiplayer.
     *
     * @param bag bag with letters that is used in the game
     * @author astarche
     */
    public void giveLettersToAiPlayer(LetterBag bag) {
        if (bag.getAmount() >= 7 - ailetters.size()) {
            Multiset<LetterBag.Tile> tiles = bag.grabRandomTiles(7 - ailetters.size());
            for (LetterBag.Tile tile : tiles) {
                ailetters.add(new LetterTile(tile.letter, tile.value, 10, gc));
            }
        }
    }

    /**
     * Displays tiles, that the aiplayer currently has.
     *
     * @author astarche
     */
    public void displayTiles() {
        System.out.print("BOT: MY TILES ARE: ");
        for (LetterTile tile : ailetters) {
            System.out.print(tile.getLetter());
        }
        System.out.println();
    }

    /**
     * Gives human player some words that he can construct with his letter bar.
     * Can be used only in single player.
     *
     * @return the string with possible words
     * @author astarche
     */
    public String helpPoorHuman() {
        ArrayList<LetterTile> freeLettersOnGrid = new ArrayList<>();
        for (int i = 0; i < gc.grid.getTilesInGrid().size(); i++) {
            if (isFree(gc.grid.getTilesInGrid().get(i), gc.grid)) {
                freeLettersOnGrid.add(gc.grid.getTilesInGrid().get(i));
            }
        }
        StringBuilder s = new StringBuilder("Try this words:\n");
        ArrayList<LetterTile> barAsList = gc.letterBar.getTilesInBar();
        if (freeLettersOnGrid.isEmpty()) {
            ArrayList<String> foundWords = findWords(barAsList);
            if (foundWords.isEmpty()) {
                return "Did not find any words :(";
            } else {
                for (String st : foundWords) {
                    s.append(st).append("\n");
                }
                return s.toString();
            }
        }
        for (LetterTile tile : freeLettersOnGrid) {
            barAsList.add(tile);
            ArrayList<String> foundWords = findWords(barAsList);
            if (foundWords.isEmpty()) {
                System.out.println("BOT: CANT MAKE ANYTHING WITH LETTER " + tile.getLetter());
                barAsList.remove(ailetters.size() - 1);
                continue;
            }
            for (String foundWord : foundWords) {
                if (hasLetter(foundWord, barAsList.get(barAsList.size() - 1).getLetter())) {
                    s.append(foundWord).append("\n");
                }
            }
        }
        return s.toString();
    }


    /**
     * Searches for a specified tile and returns the number of this tiles in the given list.
     *
     * @param toFind  letter that needs to be found
     * @param letters list with available letter
     * @return the number of searched tiles in the list
     * @author astarche
     */
    private int hasTile(char toFind, ArrayList<LetterTile> letters) {
        int found = 0;
        for (LetterTile tile : letters) {
            if (tile.getLetter() == toFind) {
                found++;
            }
        }
        return found;
    }

    /**
     * Finds a specified tile and returns its index.
     *
     * @param toFind letter that needs to be found
     * @return index
     * @author astarche
     */
    private int findTile(char toFind) {
        int indx = -1;
        for (int i = 0; i < ailetters.size(); i++) {
            if (ailetters.get(i).getLetter() == toFind) {
                indx = i;
            }
        }
        return indx;
    }
}
