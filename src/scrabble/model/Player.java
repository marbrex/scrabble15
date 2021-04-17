package scrabble.model;

import java.io.Serializable;
import java.util.Date;
import scrabble.game.LetterBar;

/**
 * scrabble.model.Player class to store important player characteristics, e.g. the name and score of
 * the player
 * 
 * @author Sergen Keskincelik
 */

public abstract class Player implements Serializable {

  /** Identification of the Player */
  private int id;
  /** Name of the Player */
  private String name;
  /** Amount of points which a player has received in the game */
  private int score;
  /** Rack to place your tiles */
  private LetterBar rack;
  /** Necessary to determine, if one player used more than 10 minutes of overtime */
  private Date usedOvertime;
  /** ImageURL to set the image for the Player */
  private String imageurl;

  /** Constructor of the Player class */
  public Player() {
    this.score = 0;
  }
  
  /** Setting the ID for the Player */
  public void setID(int id) {
    this.id = id;
  }
  
  /** Returns the identification number of the Player */
  public int getID() {
    return this.id;
  }

  /** Setting your Rack in the beginning or while choosing the options 'exchange' or 'play' */
  public void settingRack() {
    // Code needs to be implemented.
  }

  /** Clearing the rack, e.g. in the end of the game */
  public void clearRack() {
    rack = new LetterBar();
  }

  /** Returns the name of the player */
  public String getName() {
    return this.name;
  }

  /** Setting the name for the player */
  public void setName(String name) {
    this.name = name;
  }

  /** Returns the score of the player */
  public int getScore() {
    return this.score;
  }

  /** Setting the score of the player */
  public void setScore(int score) {
    this.score = score;
  }

  /** Setting the overtime of the Player */
  public void setOvertime(Date overtime) {
    this.usedOvertime = overtime;
  }

  /** Returns the used Overtime of the Player */
  public Date getOvertime() {
    return this.usedOvertime;
  }

  /** Settings the Image for the player */
  public void setImage(int imageindex) {
    switch (imageindex) {
      case 0:
        this.imageurl = "male.png";
        break;
      case 1:
        this.imageurl = "female.png";
        break;
      case 2:
        this.imageurl = "anonyms.png";
        break;
      case 3:
        this.imageurl = "animal.png";
        break;
      default:
        this.imageurl = "male.png";
        break;
    }
  }

  /** Returns the Image-URL */
  public String getImage() {
    return this.imageurl;
  }

}
