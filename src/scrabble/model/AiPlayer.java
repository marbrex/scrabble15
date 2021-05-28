package scrabble.model;

import com.google.common.collect.Multiset;
import scrabble.GameController;
import scrabble.game.Grid;
import scrabble.game.LetterBag;
import scrabble.game.LetterTile;
import scrabble.game.Word;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    /**
     * The letters that aiplayer uses to construct new words.
     */
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
     * Gets the controller.
     *
     * @return the controller
     */
    public GameController getController() {
        return this.gc;
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
     * Sets Game Controller.
     *
     * @author astarche
     * @param gc Game Controller
     */
    public void setController(GameController gc) {
        this.gc = gc;
    }

    /**
     * Checks if the word can be made with the available letters.
     *
     * @author astarche
     * @param letters the letters
     * @param toFind  the to find
     * @return true, if successful
     */
    public boolean findWord(List<LetterTile> letters, String toFind) {
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
     * @param word           the word
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
     * Number of.
     *
     * @param word the word
     * @param letter the letter
     * @return the int
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
     * Number of.
     *
     * @param letters the letters
     * @param letter the letter
     * @return the int
     */
    private int numberOf(List<Character> letters, char letter) {
        int count = 0;
        for (Character character : letters) {
            if (character == letter) {
                count++;
            }
        }
        return count;
    }

    /**
     * Checks for letters to append.
     *
     * @param word the word
     * @param toAppend the to append
     * @return true, if successful
     */
    private boolean hasLettersToAppend(String word, String toAppend) {
        boolean has = true;
        List<Character> lettersAsChar = getValues(ailetters);
        String clear = word.replaceAll(toAppend, "");
        for (int i = 0; i < clear.length(); i++) {
            if (!lettersAsChar.contains(clear.charAt(i)) || numberOf(lettersAsChar, clear.charAt(i)) != numberOf(clear,
                    clear.charAt(i))) {
                has = false;
                break;
            }
        }

        return has;
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
     * @param word           the word
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
     * Checks for enough letters.
     *
     * @param letters the letters
     * @param word    the word
     * @return the int
     */
    private int hasEnoughLetters(String letters, String word) {
        int diff = 0;
        List<Character> lettersAsList = new ArrayList<Character>();
        for (int i = 0; i < letters.length(); i++) {
            lettersAsList.add(letters.charAt(i));
        }
        List<Character> help = new ArrayList<Character>();
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
     * Make turn.
     *
     * @author astarche
     */
    public void makeTurn() {
        List<LetterTile> lettersOnGrid = gc.grid.getTilesInGrid();
        if (lettersOnGrid.isEmpty()) {
            firstTurn();
            return;
        }
        if (appendWord() != null) {
            return;
        }
        String word = null;
        boolean flag = false;
        List<String> placeableWords = new ArrayList<String>();
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
                        placeableWords.add(foundWord);
                        //System.out.println("BOT: I WILL USE THE WORD: " + word);
//                        flag = true;
//                        break;
                    }
                }
//                if (flag) {
//                    break;
            }
            break;
        }
        if (placeableWords.isEmpty()) {
            System.out.println("BOT: NO WORDS FOUND");
            return;
        }
        word = placeableWords.get(selectTheBestWord(placeableWords));
        System.out.println("BOT: I WILL USE THE WORD: " + word);
        LetterTile centralTile = ailetters.get(ailetters.size() - 1);
        LetterTile first = null;
        LetterTile last = null;
        ailetters.remove(ailetters.size() - 1);
        int row = gc.grid.getCellRow(centralTile);
        int col = gc.grid.getCellColumn(centralTile);
        String[] parts = word.split(Character.toString(centralTile.getLetter()), 2);
        if (wordPlacedHorizontal(centralTile)) {
            if (isSafe(col, row, parts[0].length(), "left") &&
                    isSafe(col, row, parts[1].length(), "right")) {
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
                System.out.println("NOT A SAFE ZONE");
                return;
            }
        } else {
            if (isSafe(col, row, parts[0].length(), "top") &&
                    isSafe(col, row, parts[1].length(), "bottom")) {
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
            } else {
                System.out.println("NOT A SAFE ZONE");
                return;
            }
        }
        if (word.charAt(0) == centralTile.getLetter()) {
            new Word(centralTile, last, gc);
        } else if (word.charAt(word.length() - 1) == centralTile.getLetter()) {
            new Word(first, centralTile, gc);
        } else {
            new Word(first, last, gc);
        }

    }

    /**
     * Select the best word.
     *
     * @param words the words
     * @return the int
     */
    private int selectTheBestWord(List<String> words) {
        int res = 0;
        for (int i = 1; i < words.size(); i++) {
            if (words.get(i).length() > words.get(i - 1).length()) {
                res = i;
            }
        }
        return res;
    }

    /**
     * Word placed horizontal.
     *
     * @param centralTile the central tile
     * @return true, if successful
     */
    private boolean wordPlacedHorizontal(LetterTile centralTile) {
        return gc.grid.getNeighbourCell(centralTile, "bottom") != null ||
                gc.grid.getNeighbourCell(centralTile, "top") != null;
    }

    /**
     * Append word.
     *
     * @return the word
     */
    private Word appendWord() {
        Word toAppend = null;
        String appendedWord = null;
        boolean flag = false;
        for (Word word : gc.grid.words) {
            String hl = word.getWordAsString();
            List<String> words = wordsThatContain(hl);
            for (String s : words) {
                System.out.println("CHECKING -> " + s);
                if (hasLettersToAppend(s, hl)) {
                    toAppend = word;
                    appendedWord = s;
                    flag = true;
                    break;
                } else {
                    System.out.println("CANNOT APPEND: " + s);
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
        String[] parts = appendedWord.split(toAppend.getWordAsString(), 2);
        int colFirst = gc.grid.getCellColumn(toAppend.getFirst());
        int rowFirst = gc.grid.getCellRow(toAppend.getFirst());
        int colLast = gc.grid.getCellColumn(toAppend.getLast());
        int rowLast = gc.grid.getCellRow(toAppend.getLast());
        LetterTile first = null;
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
            System.out.println("I WILL PLACE THIS WORD HORIZONTALLY");
            if (isSafe(colLast, rowLast, parts[0].length(), "right") &&
                    isSafe(colFirst, rowFirst, parts[1].length(), "left")) {
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
            }
        }
        return new Word(first, last, gc);
    }

    /**
     * Words that contain.
     *
     * @param word the word
     * @return the list
     */
    private List<String> wordsThatContain(String word) {
        List<String> foundWords = new ArrayList<String>();
        for (String dictWord : Dictionary.getWords()) {
            if (dictWord.matches(".*" + word + ".*") && !dictWord.equals(word)) {
                foundWords.add(dictWord);
            }
        }
        return foundWords;
    }

    /**
     * Checks if is safe.
     *
     * @param col the col
     * @param row the row
     * @param length the length
     * @param direction the direction
     * @return true, if is safe
     */
    private boolean isSafe(int col, int row, int length, String direction) {
        if (length == 0) {
            return true;
        }
        boolean safe = true;
        switch (direction) {
            case "top":
                for (int i = 0; i < length; i++) {
                    if ((gc.grid.getSlot(col, row).content != null) && i > 0) {
                        if (gc.grid.getSlot(col, row).getMultiplier() == null) {
                            safe = false;
                            break;
                        }
                    } else if ((gc.grid.getSlot(col + 1, row).content != null) ||
                            (gc.grid.getSlot(col - 1, row).content != null) && i > 0) {
                        if (gc.grid.getSlot(col, row).getMultiplier() == null) {
                            safe = false;
                            break;
                        }
                    }
                    row--;
                }
                break;
            case "bottom":
                for (int i = 0; i < length; i++) {
                    if ((gc.grid.getSlot(col, row).content != null) && i > 0) {
                        safe = false;
                        break;
                    } else if ((gc.grid.getSlot(col + 1, row).content != null) ||
                            (gc.grid.getSlot(col - 1, row).content != null) && i > 0) {
                        safe = false;
                        break;
                    }
                    row++;
                }
                break;
            case "left":
                for (int i = 0; i < length; i++) {
                    if ((gc.grid.getSlot(col, row).content != null) && i > 0) {
                        safe = false;
                        break;
                    } else if ((gc.grid.getSlot(col, row + 1).content != null) ||
                            (gc.grid.getSlot(col, row - 1).content != null) && i > 0) {
                        safe = false;
                        break;
                    }
                    col--;
                }
                break;
            case "right":
                for (int i = 0; i < length; i++) {
                    if ((gc.grid.getSlot(col, row).content != null) && i > 0) {
                        safe = false;
                        break;
                    } else if ((gc.grid.getSlot(col, row + 1).content != null) ||
                            (gc.grid.getSlot(col, row - 1).content != null) && i > 0) {
                        safe = false;
                        break;
                    }
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
     * First turn.
     */
    private void firstTurn() {
        List<String> foundWords = findWords(ailetters);
        if (foundWords.isEmpty()) {
            return;
        }
        String word = foundWords.get(selectTheBestWord(foundWords));
        LetterTile first = null;
        LetterTile last = null;
        if (word.length() == 2) {
            first = ailetters.get(findTile(word.charAt(0)));
            last = ailetters.get(findTile(word.charAt(1)));
            gc.grid.getSlot(7, 7).setContent(first);
            gc.grid.getSlot(7, 8).setContent(last);
            new Word(first, last, gc);
            return;
        }
        int col = 7;
        int row = 7;
        for (int i = 0; i < word.length(); i++) {
            if (i == 0) {
                first = ailetters.get(findTile(word.charAt(i)));
            }
            if (i == word.length() - 1) {
                last = ailetters.get(findTile(word.charAt(i)));
            }
            gc.grid.setSlotContent(col, row, ailetters.get(findTile(word.charAt(i))));
            row++;
        }
        new Word(first, last, gc);
    }

    /**
     * Adds tiles over the given slot.
     *
     * @param column the column of the slot
     * @param row    the row of the slot
     * @param part   string with letters that need to be added
     * @param gc     Game Controller
     * @return the letter tile
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
                    ailetters.get(findTile('\0')).setLetter(part.charAt(i));
                    if (i == part.length() - 1) {
                        tile = ailetters.get(findTile(part.charAt(i)));
                    }
                    gc.grid.setSlotContent(column, row - (i + 1), ailetters.get(findTile(part.charAt(i))));
                    System.out.println("TILE ADDED TOP");
                    System.out.println("BOT: REMOVED TILE - BLANK TILE");
                    ailetters.remove(ailetters.get(findTile(part.charAt(i))));
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
                // wordOnGrid.add(ailetters.get(findTile(part.charAt(i))));
                System.out.println("BOT: REMOVED TILE - " + ailetters.get(findTile(part.charAt(i))).getLetter());
                ailetters.remove(ailetters.get(findTile(part.charAt(i))));
            } else {
                if (findTile('\0') != -1) {
                    ailetters.get(findTile('\0')).setLetter(part.charAt(i));
                    if (i == part.length() - 1) {
                        tile = ailetters.get(findTile(part.charAt(i)));
                    }
                    gc.grid.setSlotContent(column, row + (i + 1), ailetters.get(findTile(part.charAt(i))));
                    System.out.println("TILE ADDED BOTTOM");
                    //wordOnGrid.add(ailetters.get(findTile(part.charAt(i))));
                    System.out.println("BOT: REMOVED TILE - BLANK TILE");
                    ailetters.remove(ailetters.get(findTile(part.charAt(i))));
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
                // wordOnGrid.add(ailetters.get(findTile(part.charAt(i))));
                System.out.println("BOT: REMOVED TILE - " + ailetters.get(findTile(part.charAt(i))).getLetter());
                ailetters.remove(ailetters.get(findTile(part.charAt(i))));
            } else {
                if (findTile('\0') != -1) {
                    ailetters.get(findTile('\0')).setLetter(part.charAt(i));
                    if (i == part.length() - 1) {
                        tile = ailetters.get(findTile(part.charAt(i)));
                    }
                    gc.grid.setSlotContent(column - (i + 1), row, ailetters.get(findTile(part.charAt(i))));
                    System.out.println("TILE ADDED LEFT");
                    //wordOnGrid.add(ailetters.get(findTile(part.charAt(i))));
                    System.out.println("BOT: REMOVED TILE - BLANK TILE");
                    ailetters.remove(ailetters.get(findTile(part.charAt(i))));
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
                //wordOnGrid.add(ailetters.get(findTile(part.charAt(i))));
                System.out.println("BOT: REMOVED TILE - " + ailetters.get(findTile(part.charAt(i))).getLetter());
                ailetters.remove(ailetters.get(findTile(part.charAt(i))));
            } else {
                if (findTile('\0') != -1) {
                    ailetters.get(findTile('\0')).setLetter(part.charAt(i));
                    if (i == part.length() - 1) {
                        tile = ailetters.get(findTile(part.charAt(i)));
                    }
                    gc.grid.setSlotContent(column + (i + 1), row, ailetters.get(findTile(part.charAt(i))));
                    System.out.println("TILE ADDED RIGHT");
                    // wordOnGrid.add(ailetters.get(findTile(part.charAt(i))));
                    System.out.println("BOT: REMOVED TILE - BLANK TILE");
                    ailetters.remove(ailetters.get(findTile(part.charAt(i))));
                } else {
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
    private boolean isFree(LetterTile tile, Grid grid) {
        return grid.getNeighbourCell(tile, "right") == null
                || grid.getNeighbourCell(tile, "left") == null
                || grid.getNeighbourCell(tile, "top") == null
                || grid.getNeighbourCell(tile, "bottom") == null;
    }

    /**
     * Checks if constructed word contains the tile on the grid.
     *
     * @author astarche
     * @param word   the word
     * @param letter letter
     * @return true, if successful
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
     * @author astarche
     * @param bag bag with letters that is used in the game
     */
    public void giveLettersToAiPlayer(LetterBag bag) {
        Multiset<LetterBag.Tile> tiles = bag.grabRandomTiles(7 - ailetters.size());
        for (LetterBag.Tile tile : tiles) {
            ailetters.add(new LetterTile(tile.letter, tile.value, 10, gc));
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
     * Searches for a specified tile and returns the number of this tiles in the given list.
     *
     * @author astarche
     * @param toFind  letter that needs to be found
     * @param letters list with available letter
     * @return the number of searched tiles in the list
     */
    private int hasTile(char toFind, List<LetterTile> letters) {
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
     * @author astarche
     * @param toFind letter that needs to be found
     * @return index
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
