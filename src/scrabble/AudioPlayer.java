package scrabble;

import jaco.mp3.player.MP3Player;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.io.IOException;
/**
 * scrabble.AudioPlayer is used to play mp3 or wav files
 *
 * @author astarche
 */
public class AudioPlayer {

    private final MP3Player player;

    /**
     * Constructor that instantiates a new audio player.
     */
    public AudioPlayer(){
        this.player = new MP3Player();
        this.player.setRepeat(true);
    }

    /**
     * Plays an mp3 file.
     *
     * @param path path to the mp3 file
     */
    public void playMp3(String path){
        StringBuilder sb = new StringBuilder(path);
        sb.delete(0, 5);
        File file = new File(sb.toString());
        if (this.player.getPlayList().isEmpty()){
            this.player.addToPlayList(file);
        }
        this.player.play();
    }
    /**
     * Get MP3Player
     *
     * @return the player
     */
    public MP3Player getPlayer(){
        return this.player;
    }

    /**
     * Plays a wav file.
     *
     * @param path the path
     * @throws Exception the exception
     */
    public void playWav(String path) throws Exception{
        StringBuilder sb = new StringBuilder(path);
        sb.delete(0, 5);
        File file = new File(sb.toString());
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
        int BUFFER_SIZE = 128000;
        AudioFormat audioFormat = null;
        SourceDataLine sourceLine = null;
        audioFormat = audioStream.getFormat();
        sourceLine = AudioSystem.getSourceDataLine(audioFormat);
        sourceLine.open(audioFormat);
        sourceLine.start();
        int nBytesRead = 0;
        byte[] abData = new byte[BUFFER_SIZE];
        while (nBytesRead != -1) {
            try {
                nBytesRead = audioStream.read(abData, 0, abData.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (nBytesRead >= 0) {
                int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
            }
        }
        sourceLine.drain();
        sourceLine.close();
    }
}
