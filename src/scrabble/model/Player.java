package scrabble.model;

import java.io.Serializable;
import java.util.Date;
import scrabble.game.LetterBar;

/**
 * scrabble.model.Player class to store important player characteristics, e.g. the name and score of
 * the player
 * 
 * @author skeskinc
 */
public abstract class Player implements Serializable {

  private String name;
  private int score;
  private LetterBar rack;
  private Date usedOvertime;
  private String imageurl;

  /**
   * Constructor of the Player class
   * 
   * @author skeskinc
   */
  public Player() {
    this.score = 0;
  }

  /**
   * Setting your Rack in the beginning or while choosing the options 'exchange' or 'play'
   * 
   * @author skeskinc
   */
  public void settingRack() {
    // Code needs to be implemented.
  }

  /**
   * Clearing the rack, e.g. in the end of the game
   * 
   * @author skeskinc
   */
  public void clearRack() {
    rack = new LetterBar();
  }

  /**
   * Returns the name of the player
   * 
   * @return Name of the player
   * @author skeskinc
   */
  public String getName() {
    return this.name;
  }

  /**
   * Setting the name for the player
   * 
   * @param name Setting up the Name
   * @author skeskinc
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the score of the player
   * 
   * @return The score of the Player
   * @author skeskinc
   */
  public int getScore() {
    return this.score;
  }

  /**
   * Setting the score of the player
   * 
   * @param score Setting up the Score of the Player
   * @author skeskinc
   */
  public void setScore(int score) {
    this.score = score;
  }

  /**
   * Setting the overtime of the Player
   *
   * @param overtime Setting up the used Overtime of one Player
   * @author skeskinc
   */
  public void setOvertime(Date overtime) {
    this.usedOvertime = overtime;
  }

  /**
   * Returns the used Overtime of the Player
   * 
   * @return Used Overtime of the Player
   * @author skeskinc
   */
  public Date getOvertime() {
    return this.usedOvertime;
  }

  /**
   * Settings the Image for the player
   * 
   * @param imageindex to set an Image-URL regarding the number
   * @author skeskinc
   */
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

  /**
   * Returns the Image-URL
   * 
   * @return Image-URL of the Player
   * @author skeskinc
   */
  public String getImage() {
    return this.imageurl;
  }

}
