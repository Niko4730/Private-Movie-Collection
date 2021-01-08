package BE;

import javafx.beans.property.*;
import javafx.collections.MapChangeListener;
import javafx.scene.control.Alert;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;


import java.io.File;
import java.util.Date;

public class Movie {
    private SimpleIntegerProperty id;
    private StringProperty title;
    protected StringProperty rating;
    protected SimpleIntegerProperty ratingId;
    protected SimpleDoubleProperty duration;
    protected SimpleStringProperty durationString;
    protected StringProperty filePath;
    protected SimpleStringProperty lastView;
    protected SimpleIntegerProperty categoryId;
    protected SimpleStringProperty categoryName;
    protected Media media;
    protected boolean isInitialized;


    private void initialize() {
        this.id = new SimpleIntegerProperty(-1);
        this.title = new SimpleStringProperty("");
        this.rating = new SimpleStringProperty("");
        this.ratingId = new SimpleIntegerProperty(-1);
        this.filePath = new SimpleStringProperty("");
        this.lastView = new SimpleStringProperty("");
        this.categoryId = new SimpleIntegerProperty(-1);
        this.categoryName = new SimpleStringProperty("");
        duration = new SimpleDoubleProperty();
        durationString = new SimpleStringProperty("");
    }

    public Movie() {
        initialize();
    }

    /**
     * Initialize a new Song instance.
     *
     * @param id       The movie id
     * @param title    The movie title
     * @param filePath The filepath of the song
     */
    public Movie(int id, String title, String filePath) {
        initialize();

        setId(id);
        setTitle(title);
        setFilePath(filePath);
        setFilePath(filePath);
        getMeta();
    }

    /**
     * Initialize a new Song instance.
     *
     * @param title    The movie title
     * @param filePath The filepath of the song
     */
    public Movie(String title, String filePath) {
        initialize();

        setTitle(title);
        setFilePath(filePath);
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
    public Movie(int id, String title, String filePath, String categoryName) {
        initialize();

        setId(id);
        setTitle(title);
        setFilePath(filePath);
        setCategoryName(categoryName);
        getMeta();
    }


    /**
     * Initialize a new Song instance.
     *
     * @param id           song id
     * @param title        song title
     * @param rating       song artist
     * @param filePath     song filepath
     * @param categoryName song categoryName
     */
    public Movie(int id, String title, String rating, String filePath, String categoryName) {
        initialize();

        setId(id);
        setTitle(title);
        setFilePath(filePath);
        setRating(rating);
        setCategoryName(categoryName);
        getMeta();
    }

    /**
     * Initialize a new Song instance.
     *
     * @param id       song id
     * @param title    song title
     * @param rating   song artist
     * @param filePath song filepath
     */
    public Movie(int id, String title, String filePath, String rating, int categoryId) {
        initialize();

        setId(id);
        setTitle(title);
        setFilePath(filePath);
        setRating(rating);
        setCategoryId(categoryId);
        getMeta();
    }

    /**
     * Initialize a new Song instance.
     *
     * @param id       song id
     * @param title    song title
     * @param rating   song artist
     * @param filePath song filepath
     */
    public Movie(int id, String title, String filePath, String lastView, String rating, int categoryId, double duration) {
        initialize();

        setId(id);
        setTitle(title);
        setFilePath(filePath);
        setLastView(lastView);
        setRating(rating);
        setCategoryId(categoryId);
        setDuration(duration);
        getMeta();
    }

    /**
     * Initialize a new Song instance.
     *
     * @param id       song id
     * @param title    song title
     * @param rating   song artist
     * @param filePath song filepath
     */
    public Movie(int id, String title, String filePath, String rating, int categoryId, double duration) {
        initialize();

        setId(id);
        setTitle(title);
        setFilePath(filePath);
        setRating(rating);
        setCategoryId(categoryId);
        setDuration(duration);
        getMeta();
    }

    /**
     * Initialize a new Song instance.
     *
     * @param id           song id
     * @param title        song title
     * @param rating       song artist
     * @param filePath     song filepath
     * @param categoryName song categoryName
     */
    public Movie(int id, String title, String rating, String filePath, int categoryId, String categoryName) {
        initialize();

        setId(id);
        setTitle(title);
        setFilePath(filePath);
        setRating(rating);
        setCategoryId(categoryId);
        setCategoryName(categoryName);
        getMeta();
    }

    /**
     * Initialize a new Song instance.
     *
     * @param id           song id
     * @param title        song title
     * @param rating       song artist
     * @param filePath     song filepath
     * @param categoryName song categoryName
     */
    public Movie(int id, String title, String rating, String filePath, int categoryId, String categoryName, double duration) {
        initialize();

        setId(id);
        setTitle(title);
        setFilePath(filePath);
        setRating(rating);
        setCategoryId(categoryId);
        setCategoryName(categoryName);
        setDuration(duration);
        getMeta();
    }

    /**
     * Initialize a new Song instance.
     *
     * @param id           song id
     * @param title        song title
     * @param rating       song artist
     * @param filePath     song filepath
     * @param categoryName song categoryName
     */
    public Movie(int id, String title, String rating, String filePath, String lastView, int categoryId, String categoryName, double duration) {
        initialize();

        setId(id);
        setTitle(title);
        setFilePath(filePath);
        setLastView(lastView);
        setRating(rating);
        setCategoryId(categoryId);
        setCategoryName(categoryName);
        setDuration(duration);
        getMeta();
    }



    /**
     * Gets the value of the metadata.
     */
    public void getMeta() {
        try {
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
                            if ("rating".equals(c.getKey()) && getRating() != null && getRating().isEmpty()) {
                                setRating(c.getValueAdded().toString());
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
        } catch (MediaException e) {
            InputAlert.showMessageBox("Unsupported movie file", "Unsupported movie file detected.",
                    "The selected movie file is not supported. Mp4 is the only supported video format.",
                    Alert.AlertType.ERROR);
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

    public void setRatingId(int id) {
        this.ratingId.set(id);
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
     * Gets the artist property
     *
     * @return artist
     */
    public StringProperty ratingProperty() {
        return this.rating;
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
     * Get the last view property.
     *
     * @return
     */
    public StringProperty lastViewProperty() {
        return this.lastView;
    }

    /**
     * Get the last view in epoch.
     *
     * @return
     */
    public String getLastView() {
        return this.lastView.get();
    }

    /**
     * Get the last view epoch as a Date object.
     *
     * @return
     */
    public Date getLastViewAsDate() {
        return new Date(getLastView());
    }

    /**
     * Set the last view in epoch.
     *
     * @param epoch
     */
    public void setLastView(String epoch) {
        lastView.set(epoch);
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

    public String getRating() {
        return rating.get();
    }

    public void setRating(String rating) {
        this.rating.set(rating);
    }

    @Override
    public String toString() {
        return "Movie{" +
                "rating=" + rating +
                '}';
    }


}
