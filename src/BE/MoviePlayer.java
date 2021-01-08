package BE;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

import java.io.File;

public class MoviePlayer {

    protected MediaPlayer mediaPlayer;
    protected Media media;
    protected Movie movie;
    protected MediaView mediaView;


    /**
     * Get the value of Media.
     *
     * @return the value of Media.
     */
    public Media getMedia() {
        return media;
    }

    public MediaView getMediaView() {
        return mediaView;
    }

    /**
     * Get the id of song
     *
     * @return the value of song.
     */
    public Movie getMovie() {
        return movie;
    }

    /**
     * Set the value of song
     *
     * @param movie new value of song
     */
    public void setMovie(Movie movie) {
        if (movie != null && this.movie != movie) {
            this.movie = movie;
            if (!movie.getFilePath().isBlank()) {
                media = new Media(new File(movie.getFilePath()).toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                mediaView = new MediaView(mediaPlayer);
            }
        }
    }

    /**
     * Get the id of Media player
     *
     * @return the value of Media player
     */
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    /**
     * Sets the standard volume to 0.
     */
    public void mute() {
        setVolume(0);
    }

    /**
     * If possible, make it play.
     */
    public void play() {
        if (mediaPlayer != null)
            mediaPlayer.play();
    }

    /**
     * If possible, make it pause.
     */
    public void pause() {
        if (mediaPlayer != null)
            mediaPlayer.pause();
    }

    /**
     * If possible, make it stop.
     */
    public void stop() {
        if (mediaPlayer != null)
            mediaPlayer.stop();
    }

    /**
     * Get the value of Volume.
     *
     * @return the value of Volume.
     */
    public double getVolume() {
        if (mediaPlayer != null)
            return mediaPlayer.getVolume();
        return 0;
    }

    /**
     * Set the value of Volume
     *
     * @param volume new value of volume
     */
    public void setVolume(double volume) {
        if (mediaPlayer != null)
            mediaPlayer.setVolume(volume);
    }
}
