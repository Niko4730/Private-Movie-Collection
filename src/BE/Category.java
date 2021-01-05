package BE;

import BLL.CategoryManager;
import javafx.beans.property.*;

public class Category {

    private int playlistId;
    private String playListName;
    private StringProperty playListNameProperty;
    protected SimpleDoubleProperty playlistDurationProperty;
    protected SimpleStringProperty playlistDurationStringProperty;
    private ObjectProperty<Integer> playlistSize = new SimpleObjectProperty<>();



    /**
     * Constructor with playlistName
     * Carlo tjek det her
     * @param playListName
     */
    public Category(String playListName) {
        initialize();
        setCategoryName(playListName);
    }

    /**
     * Constructor with id and playlistName
     *
     * @param id
     * @param playListName
     */
    public Category(int id, String playListName) {
        initialize();
        setPlaylistId(id);
        setCategoryName(playListName);
    }

    /**
     * Constructor with id, playlist name and total duration.
     * @param id
     * @param playListName
     * @param totalDuration
     */
    public Category(int id, String playListName, double totalDuration) {
        initialize();
        setPlaylistId(id);
        setCategoryName(playListName);
        setPlaylistDurationProperty(totalDuration);
        setPlaylistDurationStringProperty(totalDuration);
    }

    /**
     * initializes the variables
     */
    private void initialize() {
        playListNameProperty = new SimpleStringProperty();
        playlistDurationProperty = new SimpleDoubleProperty();
        playlistDurationStringProperty = new SimpleStringProperty();
    }

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getCategoryName() {
        return playListName;
    }

    /**
     * Get the value of name property
     *
     * @return the value of name property
     */
    public StringProperty getcategoryNameProperty() {
        return playListNameProperty;
    }

    /**
     * Set the value of name.
     *
     * @param playListName new value of name
     */
    public void setCategoryName(String playListName) {
        this.playListName = playListName;
        this.playListNameProperty.setValue(playListName);
    }

    /**
     * Get the value of id
     *
     * @return the value of id
     */
    public int getCategoryId() {
        return playlistId;
    }

    /**
     * Set the value of id
     *
     * @param playlistId new value of id
     */
    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    /**
     * Get the playlist duration string property.
     * @return
     */
    public SimpleStringProperty playlistDurationPropertyString() {
        return playlistDurationStringProperty;
    }

    /**
     * Set the playlist string property. Time gets automatically formatted.
     * @param duration
     */
    public void setPlaylistDurationStringProperty(double duration) {
        var min = (int) duration;
        var sec = (int) duration / 60;
        String str = String.format("%d:%02d", min, sec);
        playlistDurationStringProperty.set(str);
    }

    /**
     * Get the playlist duration property.
     * @return
     */
    public SimpleDoubleProperty playlistDurationProperty() {
        return playlistDurationProperty;
    }

    /**
     * Set the playlist duration property.
     * @param duration
     */
    public void setPlaylistDurationProperty(double duration) {
        playlistDurationProperty.set(duration);
    }

    /**
     * Get the total duration of the playlist.
     * @return
     */
    public double getTotalDuration() {
        return playlistDurationProperty.get();
    }

    /**
     * Get the formatted total duration of the playlist for GUI.
     * @return
     */
    public String getTotalDurationString() {
        return playlistDurationStringProperty.get();
    }

    /**
     * Get the total amount of songs in the playlist.
     * @return
     */
    public ObjectProperty<Integer> getCategorySize() {
        CategoryManager categoryManager = new CategoryManager();
        try {
            playlistSize.setValue(categoryManager.loadMoviesInCategory(this.getCategoryId()).size());
            return playlistSize;
        } catch (Exception e) {
            e.printStackTrace();
            return new SimpleObjectProperty<>(0);
        }
    }
}
