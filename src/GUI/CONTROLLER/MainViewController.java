package GUI.CONTROLLER;

import BE.InputAlert;
import BE.MusicPlayer;
import BE.Playlist;
import BE.Movie;
import BLL.CategoryManager;
import BLL.MovieManager;
import GUI.Main;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class MainViewController implements Initializable {
    @FXML
    private ImageView playPauseImg;
    @FXML
    private ImageView repeatPic;
    @FXML
    private ImageView maximizeBtn;
    @FXML
    private GridPane borderGridPane;
    @FXML
    private GridPane menuBar;
    @FXML
    private Slider volumeSlider;
    @FXML
    private TextField searchField;
    @FXML
    private TableView playlistTable;
    @FXML
    private TableColumn<Playlist, String> playlistNameColumn;
    @FXML
    private TableColumn<Playlist, Integer> playlistAmountOfSongsColumn;
    @FXML
    private TableView songsOnPlaylistTable;
    @FXML
    private TableColumn<Movie, String> playlistSongsColumn;
    @FXML
    private TableColumn<Playlist, String> playlistTimeColumn;
    @FXML
    private TableView songsTable;
    @FXML
    private TableColumn<Movie, String> songTableTitleColumn;
    @FXML
    private TableColumn<Movie, String> songTableArtistColumn;
    @FXML
    private TableColumn<Movie, String> songTableCategoryColumn;
    @FXML
    private TableColumn<Movie, String> songTableTimeColumn;
    @FXML
    private Label currentSong;
    @FXML
    private TextField volumeSliderField;
    private Movie moviePlaying;
    private Movie selectedMovie;
    private Movie selectedMovieOnPlayList;
    private Playlist selectedPlaylist;
    private Main main;
    private Stage windowStage = new Stage();
    private ObservableList<Movie> movies;
    private ObservableList<Playlist> playlists;
    private ObservableList<Movie> playlistMovies;
    private boolean playing = false;
    private boolean isMaximized = false;
    private boolean autoPlay = false;
    private double volumePercentage;
    private static final CategoryManager CATEGORY_MANAGER = new CategoryManager();
    private static final MovieManager MOVIE_MANAGER = new MovieManager();
    private static final MusicPlayer musicPlayer = new MusicPlayer();

    /**
     * Constructor
     */
    public MainViewController() {
        CATEGORY_MANAGER.setMainController(this);
        MOVIE_MANAGER.setMainController(this);
    }

    /**
     * Gets the song manager
     *
     * @return the songManager
     */
    public MovieManager getSongManager() {
        return MOVIE_MANAGER;
    }


    /**
     * Sets the main
     *
     * @param main the main class
     */
    public void setMain(Main main) {
        this.main = main;
    }

    /**
     * Listens to what happens in the window and acts accordingly.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        load();
        initTables();
        selectedSong();
        selectedSongOnPlayList();
        selectedPlaylist();
        setMainViewSize();
        moveMainView();
    }

    /**
     * should load the lists from the db, if it cannot load from db, it will load from local storage.
     */
    public void load() {
        try {
            this.playlists = FXCollections.observableArrayList(CATEGORY_MANAGER.loadPlaylists());
            reloadPlaylistTable();
            this.movies = FXCollections.observableArrayList(MOVIE_MANAGER.loadSongs());
            reloadSongTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Puts the proper values into the tables
     */
    private void initTables() {
        songTableTitleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        songTableArtistColumn.setCellValueFactory(cellData -> cellData.getValue().artistProperty());
        songTableTimeColumn.setCellValueFactory(cellData -> cellData.getValue().durationStringProperty());

        playlistSongsColumn.setCellValueFactory(cellData -> cellData.getValue() == null ? new SimpleStringProperty("") : cellData.getValue().titleProperty());

        playlistNameColumn.setCellValueFactory(cellData -> cellData.getValue().getPlayListNameProperty());
        playlistAmountOfSongsColumn.setCellValueFactory(cellData -> cellData.getValue().getPlaylistSize());
    }

    /**
     * Changes selected playlist to the playlist clicked in the playlistTable
     */
    private void selectedPlaylist() {
        this.playlistTable.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            this.selectedPlaylist = (Playlist) newValue;
            if (selectedPlaylist != null) {
                try {
                    if (CATEGORY_MANAGER.loadSongsOnPlaylist(selectedPlaylist.getPlaylistId()) != null)
                        this.playlistMovies = FXCollections.observableArrayList(CATEGORY_MANAGER.loadSongsOnPlaylist(selectedPlaylist.getPlaylistId()));
                    songsOnPlaylistTable.setItems(playlistMovies);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
    }


    /**
     * Changes selected song  on playlist to the song clicked in the songsOnPlaylistTable
     */
    private void selectedSongOnPlayList() {
        this.songsOnPlaylistTable.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            this.selectedMovieOnPlayList = (Movie) newValue;
            if (selectedMovieOnPlayList != null) {
                currentSong.setText(selectedMovieOnPlayList.getTitle());
                moviePlaying = selectedMovieOnPlayList;
                this.songsTable.getSelectionModel().clearSelection();
            }
        }));
    }

    /**
     * Changes selected song to the song clicked in the songsTable
     */
    private void selectedSong() {
        this.songsTable.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            this.selectedMovie = (Movie) newValue;
            if (selectedMovie != null) {
                currentSong.setText(selectedMovie.getTitle());
                moviePlaying = selectedMovie;
                this.songsOnPlaylistTable.getSelectionModel().clearSelection();
            }
        }));
    }

    /**
     * Reloads the song table
     */
    public void reloadSongTable() {
        try {
            int index = songsTable.getSelectionModel().getFocusedIndex();
            this.songsTable.setItems(FXCollections.observableList(MOVIE_MANAGER.loadSongs()));
            songsTable.getSelectionModel().select(index);
        } catch (Exception exception) {
            System.out.println("could not load song table");
        }
    }

    /**
     * Reloads the songs on the selected playlist
     */
    private void reloadSongsOnPlaylist() {
        try {
            int index = songsOnPlaylistTable.getSelectionModel().getFocusedIndex();
            this.songsOnPlaylistTable.setItems(FXCollections.observableList(CATEGORY_MANAGER.loadSongsOnPlaylist(selectedPlaylist.getPlaylistId())));
            songsOnPlaylistTable.getSelectionModel().select(index);
        } catch (Exception exception) {
            System.out.println("could not load songs on playlist table");
        }
    }

    /**
     * Reloads The playlist table
     */
    private void reloadPlaylistTable() {
        try {
            int index = playlistTable.getSelectionModel().getFocusedIndex();
            this.playlistTable.setItems(FXCollections.observableList(CATEGORY_MANAGER.loadPlaylists()));
            playlistTable.getSelectionModel().select(index);
        } catch (Exception exception) {
            System.out.println("could not load playlist table");
        }
    }

    /**
     * Gets the windowStage
     *
     * @return the windowStage
     */
    public Stage getWindowStage() {
        return windowStage;
    }

    /**
     * Changes songsTable, whenever the searchField changes.
     */
    public void search() {
        try {
            this.songsTable.setItems(FXCollections.observableList(MOVIE_MANAGER.searchSong(searchField.getText())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the searchField.
     */
    public void clearSearchButton() {
        searchField.setText("");
        search();
    }

    /**
     * Adds a new playlist.
     */
    public void addPlayListButton() throws IOException {
        dialog("playlist name:", "Add playlist", "", 1);
    }

    /**
     * Creates a new playlist
     *
     * @param playlist the new playlist
     */
    public void addPlaylist(Playlist playlist) {
        try {
            CATEGORY_MANAGER.createPlaylist(playlist.getPlayListName());
            reloadPlaylistTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Edits the selected playlist.
     */
    public void editPlaylistButton() throws IOException {
        if (selectedPlaylist != null) {
            dialog("playlist name:", "Edit playlist", selectedPlaylist.getPlayListName(), 2);
        }
    }

    /**
     * Edits the selected playlist.
     *
     * @param newTitle new title
     */
    public void editPlaylist(String newTitle) {
        try {
            selectedPlaylist.setPlayListName(newTitle);
            CATEGORY_MANAGER.updatePlaylist(selectedPlaylist);
            reloadPlaylistTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a dialog window
     *
     * @param labelFieldText  The new label field text
     * @param dialogTitleText The dialog title text
     * @param titleFieldText  The title field text
     * @param mode            The mode
     * @throws IOException    If something went wrong
     */
    private void dialog(String labelFieldText, String dialogTitleText, String titleFieldText, int mode) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("DIALOGUE/AddPlaylist.fxml"));
        AnchorPane dialog = loader.load();
        AddCategoryController controller = loader.getController();
        controller.setMainController(this);
        controller.setLabelField(labelFieldText);
        controller.setTitleField(titleFieldText);
        controller.setDialogTitle(dialogTitleText);
        controller.setMode(mode);
        windowStage = new Stage();
        windowStage.setScene(new Scene(dialog));
        windowStage.initOwner(main.getPrimaryStage());
        windowStage.initModality(Modality.APPLICATION_MODAL);
        windowStage.alwaysOnTopProperty();
        windowStage.show();
    }

    /**
     * Deletes the selected playlist.
     */
    public void deletePlaylistButton() {
        var result = InputAlert.showMessageBox("Are you sure?", String.format("Deleting %s", selectedPlaylist.getPlayListName()),
                "You cannot undo this action once it's done!", Alert.AlertType.CONFIRMATION);
        if (result.get() == ButtonType.OK) {
            try {
                CATEGORY_MANAGER.deletePlaylist(selectedPlaylist);
                reloadSongsOnPlaylist();
                load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds Song to the current playlist.
     */
    public void addToPlaylistButton() {
        if (selectedPlaylist != null) {
            try {
                for (Movie movie : Collections.unmodifiableList(CATEGORY_MANAGER.loadSongsOnPlaylist(selectedPlaylist.getPlaylistId()))) {
                    if (movie.getId() == selectedMovie.getId()) {
                        if (songsTable.getSelectionModel().getFocusedIndex() == songsTable.getItems().size() - 1) {
                            songsTable.getSelectionModel().select(0);
                        } else
                            songsTable.getSelectionModel().select(songsTable.getSelectionModel().getFocusedIndex() + 1);
                        return;
                    }
                }
                CATEGORY_MANAGER.addSongsToPlaylist(selectedPlaylist.getPlaylistId(), selectedMovie.getId());
                reloadSongsOnPlaylist();
                reloadPlaylistTable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Moves a song up on the current playlist.
     */
    public void moveSongUpOnPlaylistButton() {
        this.playlistMovies = FXCollections.observableArrayList(moveOnPlaylist(songsOnPlaylistTable.getItems(), selectedMovieOnPlayList, -1));
        this.songsOnPlaylistTable.setItems(playlistMovies);
    }

    /**
     * Moves a song down on the current playlist
     */
    public void moveSongDownOnPlaylistButton() {
        this.playlistMovies = FXCollections.observableArrayList(moveOnPlaylist(songsOnPlaylistTable.getItems(), selectedMovieOnPlayList, 1));
        this.songsOnPlaylistTable.setItems(playlistMovies);
    }

    /**
     * Changes the position on the playlist
     *
     * @param listOfMovies the list of song you want to change
     * @param movie        the song
     * @param pos         the position
     * @return A playlist with the new order
     */
    public List<Movie> moveOnPlaylist(List<Movie> listOfMovies, Movie movie, int pos) {
        LinkedList<Movie> linkedMovies = new LinkedList<>(listOfMovies);
        int index = linkedMovies.indexOf(movie) + pos;
        if (linkedMovies.size() == 2) {
            linkedMovies.addLast(linkedMovies.get(0));
            linkedMovies.remove(linkedMovies.getFirst());
        }
        if (linkedMovies.size() > 2) {
            if (index < 0) {
                linkedMovies.removeFirstOccurrence(movie);
                linkedMovies.addLast(movie);
            } else if (index >= linkedMovies.size()) {
                linkedMovies.removeLastOccurrence(movie);
                linkedMovies.addFirst(movie);
            }
            if (index >= 0 && index < linkedMovies.size()) {
                linkedMovies.remove(movie);
                linkedMovies.add(index, movie);
            }
        }
        return linkedMovies;
    }

    /**
     * Saves the playlists
     */
    public void savePlaylist() {
        if (songsOnPlaylistTable.getItems() != null) {
            for (Movie movie : playlistMovies) {
                try {
                    CATEGORY_MANAGER.deleteSongFromPlaylist(selectedPlaylist.getPlaylistId(), movie.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (Movie movie : playlistMovies) {
                try {
                    CATEGORY_MANAGER.addSongsToPlaylist(selectedPlaylist.getPlaylistId(), movie.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Shuffles the playlists
     */
    @FXML
    private void shufflePlaylist() {
        Collections.shuffle(playlistMovies);
    }

    /**
     * Removes the selected song from the current playlist.
     */
    public void removeFromPlaylistButton() {
        if (selectedPlaylist != null && selectedMovieOnPlayList != null) {
            try {
                int index = songsOnPlaylistTable.getSelectionModel().getFocusedIndex();
                CATEGORY_MANAGER.deleteSongFromPlaylist(selectedPlaylist.getPlaylistId(), selectedMovieOnPlayList.getId());
                reloadSongsOnPlaylist();
                reloadPlaylistTable();
                songsOnPlaylistTable.getSelectionModel().select(index > 0 ? index - 1 : index);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds a new song
     */
    public void newSongButton() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("DIALOGUE/AddSong.fxml"));
        AnchorPane dialog = null;
        try {
            dialog = loader.load();
            AddMovieController controller = loader.getController();
            controller.setMainController(this);
            controller.setGenreComboBox(MOVIE_MANAGER.getGenres());
            windowStage = new Stage();
            windowStage.setScene(new Scene(dialog));
            windowStage.initModality(Modality.APPLICATION_MODAL);
            windowStage.alwaysOnTopProperty();
            windowStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a song
     *
     * @param movie the song
     */
    public void createSong(Movie movie) {
        try {
            MOVIE_MANAGER.createSong(movie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds multiple songs at once.
     */
    public void bulkAddButton() {
        FileChooser fileChooser = new FileChooser();
        windowStage = new Stage();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3-Files", "*.mp3"));
        try {
            List<File> selectedFiles = new ArrayList<>(fileChooser.showOpenMultipleDialog(windowStage));
            try {
                if (!selectedFiles.isEmpty()) {
                    for (File selectedFile : selectedFiles)
                        MOVIE_MANAGER.createSong(new Movie(selectedFile.getName().substring(0, selectedFile.getName().indexOf('.')), selectedFile.getPath()));
                    load();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (RuntimeException e) {
            System.out.println("No file selected");
        }
    }

    /**
     * Edits the selected song
     */
    public void editSongButton() {
        if (selectedMovie != null) {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("DIALOGUE/EditSong.fxml"));
            AnchorPane dialog = null;
            try {
                dialog = loader.load();
                EditMovieController controller = loader.getController();
                controller.setMainController(this);
                controller.setGenreComboBox(MOVIE_MANAGER.getGenres());
                controller.setSelectedSong(selectedMovie);
                windowStage = new Stage();
                windowStage.setScene(new Scene(dialog));
                windowStage.initModality(Modality.APPLICATION_MODAL);
                windowStage.alwaysOnTopProperty();
                windowStage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            InputAlert.showMessageBox("No song selected", "Cannot modify something that doesn't exist!", "Please select a song.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Deletes the selected song
     */
    public void deleteSongButton() {
        var result = InputAlert.showMessageBox("Do you want to delete this song?", String.format("Deleting %s", selectedMovie.getTitle()),
                "You cannot undo this action once it's done!", Alert.AlertType.CONFIRMATION);
        if (result.get() == ButtonType.OK) {
            try {
                MOVIE_MANAGER.deleteSong(selectedMovie.getId());
                reloadSongsOnPlaylist();
            } catch (Exception e) {
                e.printStackTrace();
            }
            load();
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }

    /**
     * Closes the stage
     */
    public void closeButton() {
        main.getPrimaryStage().close();
    }

    /**
     * Maximizes the stage
     */
    public void maximizeButton() {
        if (!isMaximized) {
            main.getPrimaryStage().setFullScreen(true);
            maximizeBtn.setImage(new Image("GUI/IMG/RestoreWindowButton.png"));
            isMaximized = true;
        } else {
            main.getPrimaryStage().setFullScreen(false);
            maximizeBtn.setImage(new Image("GUI/IMG/MaximizeButton.png"));
            isMaximized = false;
        }
    }

    /**
     * Minimizes the stage
     */
    public void minimizeButton() {
        main.getPrimaryStage().setIconified(true);
    }

    /**
     * Changes window size when pulled from the bottom and the right
     */
    public void setMainViewSize() {
        AtomicReference<Double> x = new AtomicReference<>((double) 0);
        AtomicReference<Double> y = new AtomicReference<>((double) 0);
        borderGridPane.setOnMousePressed(mouseEvent1 -> {
            x.set(mouseEvent1.getSceneX());
            y.set(mouseEvent1.getSceneY());
            int offset = 5;
            if (y.get() > borderGridPane.getHeight() - offset || x.get() > borderGridPane.getWidth() - offset) {
                borderGridPane.setOnMouseReleased(mouseEvent2 -> {
                    main.getPrimaryStage().setHeight(borderGridPane.getHeight() + (mouseEvent2.getSceneY() - y.get()));
                    main.getPrimaryStage().setWidth(borderGridPane.getWidth() + (mouseEvent2.getSceneX() - x.get()));
                    mouseEvent2.consume();
                });
            }
        });
    }

    /**
     * Moves the main view when pulling the top bar
     */
    public void moveMainView() {
        AtomicReference<Double> x = new AtomicReference<>((double) 0);
        AtomicReference<Double> y = new AtomicReference<>((double) 0);
        menuBar.setOnMousePressed(mouseEvent -> {
            x.set(mouseEvent.getSceneX());
            y.set(mouseEvent.getSceneY());
        });
        menuBar.setOnMouseDragged(mouseEvent -> {
                    main.getPrimaryStage().setX(mouseEvent.getScreenX() - x.get());
                    main.getPrimaryStage().setY(mouseEvent.getScreenY() - y.get());
                }
        );
    }
}
