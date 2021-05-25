package scrabble;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * scrabble.AudioPlayer is used to play mp3 or wav files
 *
 * @author astarche
 */
public class AudioPlayer {

    private Player player;
    private FileInputStream file;

    /**
     * Constructor that instantiates a new audio player.
     *
     * @param path path to the file
     */
    public AudioPlayer(String path){
        StringBuilder sb = new StringBuilder(path);
        sb.delete(0, 5);
        try {
            this.file = new FileInputStream(sb.toString());
            this.player = new Player(file);
        }catch (FileNotFoundException | JavaLayerException e){
            e.printStackTrace();
        }
    }
    public void playMp3(){
        if (this.player != null) {
            try {
                this.player.play();
            } catch (JavaLayerException jle) {
                jle.printStackTrace();
            }
        }
    }
    /**
     * Get MP3Player
     *
     * @return the player
     */
    public Player getPlayer(){
        return this.player;
    }

    /**
     * Plays a wav file.
     *
     * @param path the path
     * @throws Exception the exception
     */
    public void playWav(String path) throws Exception {
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
