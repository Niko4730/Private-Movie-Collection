package BE;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.MapChangeListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class Song {
    private SimpleIntegerProperty id;
    private StringProperty title;
    protected StringProperty artist;
    protected SimpleDoubleProperty duration;
    protected SimpleStringProperty durationString;
    protected StringProperty filePath;
    protected SimpleIntegerProperty categoryId;
    protected SimpleStringProperty categoryName;
    protected Media media;
    protected boolean isInitialized;

    private void initialize() {
        this.id = new SimpleIntegerProperty(-1);
        this.title = new SimpleStringProperty("");
        this.artist = new SimpleStringProperty("");
        this.filePath = new SimpleStringProperty("");
        this.categoryId = new SimpleIntegerProperty(-1);
        this.categoryName = new SimpleStringProperty("");
        duration = new SimpleDoubleProperty();
        durationString = new SimpleStringProperty("");
    }

    public Song() {
        initialize();
    }

    /**
     * Initialize a new Song instance.
     *
     * @param id       The song id
     * @param title    The song title
     * @param filePath The filepath of the song
     */
    public Song(int id, String title, String filePath) {
        initialize();
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.artist = new SimpleStringProperty();
        this.filePath = new SimpleStringProperty(filePath);
        this.categoryId = new SimpleIntegerProperty(-1);
        getMeta();
    }

    /**
     * Initialize a new Song instance.
     *
     * @param title    The song title
     * @param filePath The filepath of the song
     */
    public Song(String title, String filePath) {
        initialize();
        this.id = new SimpleIntegerProperty(-1);
        this.title = new SimpleStringProperty(title);
        this.artist = new SimpleStringProperty();
        this.filePath = new SimpleStringProperty(filePath);
        this.categoryId = new SimpleIntegerProperty(-1);
        getMeta();
    }

    /**
     * Initialize a new Song instance.
     *
     * @param id           song id
     * @param title        song title
     * @param filePath     song filepath
     * @param categoryName song category
     */
    public Song(int id, String title, String filePath, String categoryName) {
        initialize();
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.artist = new SimpleStringProperty();
        this.filePath = new SimpleStringProperty(filePath);
        this.categoryId = new SimpleIntegerProperty(-1);
        this.categoryName = new SimpleStringProperty(categoryName);
        duration = new SimpleDoubleProperty();
        getMeta();
    }


    /**
     * Initialize a new Song instance.
     *
     * @param id           song id
     * @param title        song title
     * @param artist       song artist
     * @param filePath     song filepath
     * @param categoryName song categoryName
     */
    public Song(int id, String title, String artist, String filePath, String categoryName) {
        initialize();
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.artist = new SimpleStringProperty(artist);
        this.filePath = new SimpleStringProperty(filePath);
        this.categoryId = new SimpleIntegerProperty(-1);
        this.categoryName = new SimpleStringProperty(categoryName);
        getMeta();
    }

    /**
     * Initialize a new Song instance.
     *
     * @param id       song id
     * @param title    song title
     * @param artist   song artist
     * @param filePath song filepath
     */
    public Song(int id, String title, String filePath, String artist, int categoryId) {
        initialize();

        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.artist = new SimpleStringProperty(artist);
        this.filePath = new SimpleStringProperty(filePath);
        this.categoryId = new SimpleIntegerProperty(categoryId);
        this.categoryName = new SimpleStringProperty();
        getMeta();
    }

    /**
     * Initialize a new Song instance.
     *
     * @param id       song id
     * @param title    song title
     * @param artist   song artist
     * @param filePath song filepath
     */
    public Song(int id, String title, String filePath, String artist, int categoryId, double duration) {
        initialize();
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.artist = new SimpleStringProperty(artist);
        this.filePath = new SimpleStringProperty(filePath);
        this.categoryId = new SimpleIntegerProperty(categoryId);
        this.categoryName = new SimpleStringProperty();
        this.duration = new SimpleDoubleProperty(duration);
        getMeta();
    }

    /**
     * Initialize a new Song instance.
     *
     * @param id           song id
     * @param title        song title
     * @param artist       song artist
     * @param filePath     song filepath
     * @param categoryName song categoryName
     */
    public Song(int id, String title, String artist, String filePath, int categoryId, String categoryName) {
        initialize();
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.artist = new SimpleStringProperty(artist);
        this.filePath = new SimpleStringProperty(filePath);
        this.categoryId = new SimpleIntegerProperty(categoryId);
        this.categoryName = new SimpleStringProperty(categoryName);
        getMeta();
    }

    /**
     * Initialize a new Song instance.
     *
     * @param id           song id
     * @param title        song title
     * @param artist       song artist
     * @param filePath     song filepath
     * @param categoryName song categoryName
     */
    public Song(int id, String title, String artist, String filePath, int categoryId, String categoryName, double duration) {
        initialize();
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.artist = new SimpleStringProperty(artist);
        this.filePath = new SimpleStringProperty(filePath);
        this.categoryId = new SimpleIntegerProperty(categoryId);
        this.categoryName = new SimpleStringProperty(categoryName);
        this.duration = new SimpleDoubleProperty(duration);
        getMeta();
    }


    /**
     * Gets the value of the metadata.
     */
    public void getMeta() {
        if (getFilePath() != null) {
            var file = new File(getFilePath());
            if (file.exists()) {
                media = new Media(file.toURI().toString());
                var mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setOnReady(() -> {
                    var duration = media.getDuration();
                    setDuration(duration.toMinutes());
                    setDurationString(String.format("%02d", (int) duration.toMinutes()) + ":" + String.format("%02d", ((int) duration.toSeconds() % 60)));
                });

                media.getMetadata().addListener((MapChangeListener.Change<? extends String, ? extends Object> c) -> {
                    if (c.wasAdded()) {
                        if ("artist".equals(c.getKey()) && getArtist()!=null && getArtist().isEmpty()) {
                            setArtist(c.getValueAdded().toString());
                        }
                        if ("title".equals(c.getKey()) && getTitle().isEmpty()) {
                            setTitle(c.getValueAdded().toString());
                        }
                        if ("album".equals(c.getKey())) {
                            //album = c.getValueAdded().toString();
                        }
                    }
                    isInitialized = true;
                });
            }
        }
    }


    /**
     * Get the value of id
     *
     * @return the value of id
     */
    public int getId() {
        return this.id.get();
    }

    /**
     * Gets the id property
     *
     * @return the id property of the song
     */
    public SimpleIntegerProperty idProperty() {
        return this.id;
    }

    /**
     * Sets the id
     *
     * @param id the new id of the song
     */
    public void setId(int id) {
        this.id.set(id);
    }

    /**
     * Gets the categoryId
     *
     * @return the id.
     */
    public int getCategoryId() {
        return this.categoryId.get();
    }

    /**
     * Gets the categoryIdProperty
     *
     * @return the category id property of the song
     */
    public SimpleIntegerProperty categoryIdProperty() {
        return this.categoryId;
    }

    /**
     * Sets the category id
     *
     * @param id the id of the song
     */
    public void setCategoryId(int id) {
        this.categoryId.set(id);
    }

    /**
     * Get the value of Duration
     *
     * @return the value of Duration.
     */
    public double getDuration() {
        return this.duration.get();
    }

    public String getDurationString() {
        return durationString.get();
    }

    /**
     * Gets the duration property
     *
     * @return duration
     */
    public SimpleDoubleProperty durationProperty() {
        return this.duration;
    }

    public SimpleStringProperty durationStringProperty() {
        return durationString;
    }

    public void setDurationString(String s) {
        durationString.set(s);
    }

    /**
     * Set the value of duration
     *
     * @param duration new value of duration
     */
    public void setDuration(double duration) {
        this.duration.set(duration);
    }

    /**
     * Get the value of Artist
     *
     * @return the value of Artist
     */
    public String getArtist() {
        return this.artist.get();
    }

    /**
     * Gets the artist property
     *
     * @return artist
     */
    public StringProperty artistProperty() {
        return this.artist;
    }

    /**
     * Sets the value of Artist
     *
     * @param artist new value of Artist
     */
    public void setArtist(String artist) {
        this.artist.set(artist);
    }

    /**
     * Gets the value of Title
     *
     * @return the value of Title
     */
    public String getTitle() {
        return this.title.get();
    }

    /**
     * Gets the title property
     *
     * @return Title
     */
    public StringProperty titleProperty() {
        return title;
    }

    /**
     * Set the value of title
     *
     * @param title new value of title.
     */
    public void setTitle(String title) {
        this.title.set(title);
    }

    /**
     * Get the value of filePath
     *
     * @return the value of filepath
     */
    public String getFilePath() {
        return this.filePath.get();
    }

    /**
     * Set the value of filepath
     *
     * @param filePath new value of filepath
     */
    public void setFilePath(String filePath) {
        this.filePath.setValue(filePath);
    }

    /**
     * Get the value of CategoryId
     *
     * @return the value of CategoryId
     */
    public String getCategoryName() {
        return this.categoryName.get();
    }

    /**
     * Gets the category name property
     *
     * @return the category name property of the song
     */
    public SimpleStringProperty categoryNameProperty() {
        return this.categoryName;
    }


    /**
     * Set the value of CategoryId
     *
     * @param newCategoryName new value of CategoryName
     */
    public void setCategoryName(String newCategoryName) {
        this.categoryName.set(newCategoryName);
    }

    /**
     * Get the media associated with this Song.
     *
     * @return
     */
    public Media getMedia() {
        return media;
    }

    /**
     * Is the song ready? Must be checked before adding or editing songs to ensure all meta tags get loaded properly.
     *
     * @return
     */
    public boolean getIsInitialized() {
        return isInitialized;
    }
}
