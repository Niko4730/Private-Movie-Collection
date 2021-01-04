package GUI.CONTROLLER;

import BE.InputAlert;
import BE.MusicPlayer;
import BE.Playlist;
import BE.Song;
import BLL.PlaylistManager;
import BLL.SongManager;
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
    private TableColumn<Song, String> playlistSongsColumn;
    @FXML
    private TableColumn<Playlist, String> playlistTimeColumn;
    @FXML
    private TableView songsTable;
    @FXML
    private TableColumn<Song, String> songTableTitleColumn;
    @FXML
    private TableColumn<Song, String> songTableArtistColumn;
    @FXML
    private TableColumn<Song, String> songTableCategoryColumn;
    @FXML
    private TableColumn<Song, String> songTableTimeColumn;
    @FXML
    private Label currentSong;
    @FXML
    private TextField volumeSliderField;
    private Song songPlaying;
    private Song selectedSong;
    private Song selectedSongOnPlayList;
    private Playlist selectedPlaylist;
    private Main main;
    private Stage windowStage = new Stage();
    private ObservableList<Song> songs;
    private ObservableList<Playlist> playlists;
    private ObservableList<Song> playlistSongs;
    private boolean playing = false;
    private boolean isMaximized = false;
    private boolean autoPlay = false;
    private double volumePercentage;
    private static final PlaylistManager playlistManager = new PlaylistManager();
    private static final SongManager songManager = new SongManager();
    private static final MusicPlayer musicPlayer = new MusicPlayer();

    /**
     * Constructor
     */
    public MainViewController() {
        playlistManager.setMainController(this);
        songManager.setMainController(this);
    }

    /**
     * Gets the song manager
     *
     * @return the songManager
     */
    public SongManager getSongManager() {
        return songManager;
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
        volumeFieldControl();
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
            this.playlists = FXCollections.observableArrayList(playlistManager.loadPlaylists());
            reloadPlaylistTable();
            this.songs = FXCollections.observableArrayList(songManager.loadSongs());
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
        songTableCategoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryNameProperty());
        songTableTimeColumn.setCellValueFactory(cellData -> cellData.getValue().durationStringProperty());

        playlistSongsColumn.setCellValueFactory(cellData -> cellData.getValue() == null ? new SimpleStringProperty("") : cellData.getValue().titleProperty());

        playlistNameColumn.setCellValueFactory(cellData -> cellData.getValue().getPlayListNameProperty());
        playlistAmountOfSongsColumn.setCellValueFactory(cellData -> cellData.getValue().getPlaylistSize());
        playlistTimeColumn.setCellValueFactory(cellData -> cellData.getValue().playlistDurationPropertyString());
    }

    /**
     * Changes selected playlist to the playlist clicked in the playlistTable
     */
    private void selectedPlaylist() {
        this.playlistTable.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            this.selectedPlaylist = (Playlist) newValue;
            if (selectedPlaylist != null) {
                try {
                    if (playlistManager.loadSongsOnPlaylist(selectedPlaylist.getPlaylistId()) != null)
                        this.playlistSongs = FXCollections.observableArrayList(playlistManager.loadSongsOnPlaylist(selectedPlaylist.getPlaylistId()));
                    songsOnPlaylistTable.setItems(playlistSongs);
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
            this.selectedSongOnPlayList = (Song) newValue;
            if (selectedSongOnPlayList != null) {
                currentSong.setText(selectedSongOnPlayList.getTitle());
                songPlaying = selectedSongOnPlayList;
                this.songsTable.getSelectionModel().clearSelection();
                if (playing) {
                    changeSong();
                }
            }
        }));
    }

    /**
     * Changes selected song to the song clicked in the songsTable
     */
    private void selectedSong() {
        this.songsTable.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            this.selectedSong = (Song) newValue;
            if (selectedSong != null) {
                currentSong.setText(selectedSong.getTitle());
                songPlaying = selectedSong;
                this.songsOnPlaylistTable.getSelectionModel().clearSelection();
                if (playing) {
                    changeSong();
                }
            }
        }));
    }

    /**
     * Reloads the song table
     */
    public void reloadSongTable() {
        try {
            int index = songsTable.getSelectionModel().getFocusedIndex();
            this.songsTable.setItems(FXCollections.observableList(songManager.loadSongs()));
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
            this.songsOnPlaylistTable.setItems(FXCollections.observableList(playlistManager.loadSongsOnPlaylist(selectedPlaylist.getPlaylistId())));
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
            this.playlistTable.setItems(FXCollections.observableList(playlistManager.loadPlaylists()));
            playlistTable.getSelectionModel().select(index);
        } catch (Exception exception) {
            System.out.println("could not load playlist table");
        }
    }

    /**
     * Makes the volume slider change when the volume field is changed to a valid value.
     * and the field change when the volume slider changes.
     */
    private void volumeFieldControl() {
        // Default value.
        volumeSlider.setValue(25);
        volumeSliderField.setText(String.format("%.0f", volumeSlider.getValue()));

        volumeSliderField.textProperty().addListener(
                (observableValue, oldValue, newValue) -> {
                    try {
                        if (newValue.contains(","))
                            newValue = newValue.replaceAll(",", ".");
                        volumeSlider.setValue(Integer.parseInt(newValue));
                        musicPlayer.setVolume(volumePercentage / 100);
                    } catch (IllegalArgumentException e) {
                        //Does nothing when the input is invalid.
                    }
                }
        );

        // Makes the volume field change when the volume slider is changed.
        volumeSlider.valueProperty().addListener(
                (observableValue, oldValue, newValue) -> {
                    volumePercentage = newValue.doubleValue();
                    volumeSliderField.setText(String.format("%.0f", volumePercentage));
                    musicPlayer.setVolume(volumePercentage / 100);
                }
        );
    }

    /**
     * gets the value of the volume slider
     *
     * @return the volume
     */
    public double getVolumePercentage() {
        return volumeSlider.getValue() / 100;
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
            this.songsTable.setItems(FXCollections.observableList(songManager.searchSong(searchField.getText())));
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
            playlistManager.createPlaylist(playlist.getPlayListName());
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
            playlistManager.updatePlaylist(selectedPlaylist);
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
        AddPlaylistController controller = loader.getController();
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
                playlistManager.deletePlaylist(selectedPlaylist);
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
                for (Song song : Collections.unmodifiableList(playlistManager.loadSongsOnPlaylist(selectedPlaylist.getPlaylistId()))) {
                    if (song.getId() == selectedSong.getId()) {
                        if (songsTable.getSelectionModel().getFocusedIndex() == songsTable.getItems().size() - 1) {
                            songsTable.getSelectionModel().select(0);
                        } else
                            songsTable.getSelectionModel().select(songsTable.getSelectionModel().getFocusedIndex() + 1);
                        return;
                    }
                }
                playlistManager.addSongsToPlaylist(selectedPlaylist.getPlaylistId(), selectedSong.getId());
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
        this.playlistSongs = FXCollections.observableArrayList(moveOnPlaylist(songsOnPlaylistTable.getItems(), selectedSongOnPlayList, -1));
        this.songsOnPlaylistTable.setItems(playlistSongs);
    }

    /**
     * Moves a song down on the current playlist
     */
    public void moveSongDownOnPlaylistButton() {
        this.playlistSongs = FXCollections.observableArrayList(moveOnPlaylist(songsOnPlaylistTable.getItems(), selectedSongOnPlayList, 1));
        this.songsOnPlaylistTable.setItems(playlistSongs);
    }

    /**
     * Changes the position on the playlist
     *
     * @param listOfSongs the list of song you want to change
     * @param song        the song
     * @param pos         the position
     * @return A playlist with the new order
     */
    public List<Song> moveOnPlaylist(List<Song> listOfSongs, Song song, int pos) {
        LinkedList<Song> linkedSongs = new LinkedList<>(listOfSongs);
        int index = linkedSongs.indexOf(song) + pos;
        if (linkedSongs.size() == 2) {
            linkedSongs.addLast(linkedSongs.get(0));
            linkedSongs.remove(linkedSongs.getFirst());
        }
        if (linkedSongs.size() > 2) {
            if (index < 0) {
                linkedSongs.removeFirstOccurrence(song);
                linkedSongs.addLast(song);
            } else if (index >= linkedSongs.size()) {
                linkedSongs.removeLastOccurrence(song);
                linkedSongs.addFirst(song);
            }
            if (index >= 0 && index < linkedSongs.size()) {
                linkedSongs.remove(song);
                linkedSongs.add(index, song);
            }
        }
        return linkedSongs;
    }

    /**
     * Saves the playlists
     */
    public void savePlaylist() {
        if (songsOnPlaylistTable.getItems() != null) {
            for (Song song : playlistSongs) {
                try {
                    playlistManager.deleteSongFromPlaylist(selectedPlaylist.getPlaylistId(), song.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (Song song : playlistSongs) {
                try {
                    playlistManager.addSongsToPlaylist(selectedPlaylist.getPlaylistId(), song.getId());
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
        Collections.shuffle(playlistSongs);
    }

    /**
     * Removes the selected song from the current playlist.
     */
    public void removeFromPlaylistButton() {
        if (selectedPlaylist != null && selectedSongOnPlayList != null) {
            try {
                int index = songsOnPlaylistTable.getSelectionModel().getFocusedIndex();
                playlistManager.deleteSongFromPlaylist(selectedPlaylist.getPlaylistId(), selectedSongOnPlayList.getId());
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
            AddSongController controller = loader.getController();
            controller.setMainController(this);
            controller.setGenreComboBox(songManager.getGenres());
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
     * @param song the song
     */
    public void createSong(Song song) {
        try {
            songManager.createSong(song);
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
                        songManager.createSong(new Song(selectedFile.getName().substring(0, selectedFile.getName().indexOf('.')), selectedFile.getPath()));
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
        if (selectedSong != null) {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("DIALOGUE/EditSong.fxml"));
            AnchorPane dialog = null;
            try {
                dialog = loader.load();
                EditSongController controller = loader.getController();
                controller.setMainController(this);
                controller.setGenreComboBox(songManager.getGenres());
                controller.setSelectedSong(selectedSong);
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
        var result = InputAlert.showMessageBox("Do you want to delete this song?", String.format("Deleting %s", selectedSong.getTitle()),
                "You cannot undo this action once it's done!", Alert.AlertType.CONFIRMATION);
        if (result.get() == ButtonType.OK) {
            try {
                songManager.deleteSong(selectedSong.getId());
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
     * Plays from the playlist
     */
    public void playButton() {
        if (selectedSongOnPlayList != null && selectedSongOnPlayList.getFilePath()!=null && !playing) {
            songPlaying = selectedSongOnPlayList;
            musicPlayer.setSong(songPlaying);
            musicPlayer.setVolume(getVolumePercentage());
            musicPlayer.play();
            playPauseImg.setImage(new Image("GUI/IMG/PauseButton.png"));
            playing = !playing;
        } else if (selectedSong != null && !playing) {
            songPlaying = selectedSong;
            musicPlayer.setSong(songPlaying);
            musicPlayer.setVolume(getVolumePercentage());
            musicPlayer.play();
            playPauseImg.setImage(new Image("GUI/IMG/PauseButton.png"));
            playing = !playing;
        } else if (songPlaying != null) {
            musicPlayer.pause();
            playPauseImg.setImage(new Image("GUI/IMG/PlayButton.png"));
            playing = !playing;
        }

        if (songPlaying != null && musicPlayer.getMediaPlayer()!=null) {
            musicPlayer.getMediaPlayer().setOnEndOfMedia(() -> {
                if (songPlaying != null && playing) {
                    if (!autoPlay)
                        nextButton();
                    playing = !playing;
                    musicPlayer.stop();
                    playButton();
                    return;
                }
            });
        }
    }

    public void toggleAutoplay() {
        autoPlay = !autoPlay;
        if (autoPlay)
            repeatPic.setImage(new Image("GUI/IMG/repeatPressed.png"));
        else
            repeatPic.setImage(new Image("GUI/IMG/repeatNonPressed.png"));
    }

    private void changeSong() {
        playButton();
        playButton();
    }

    /**
     * Goes to the next song on the playlist
     */
    public void nextButton() {
        if (selectedSongOnPlayList != null) {
            if (this.songsOnPlaylistTable.getSelectionModel().getFocusedIndex() != this.songsOnPlaylistTable.getItems().size() - 1)
                this.songsOnPlaylistTable.getSelectionModel().selectBelowCell();
            else
                this.songsOnPlaylistTable.getSelectionModel().selectFirst();
            changeSong();
        }
        if (selectedSong != null) {
            if (this.songsTable.getSelectionModel().getFocusedIndex() != this.songsTable.getItems().size() - 1)
                this.songsTable.getSelectionModel().selectBelowCell();
            else
                this.songsTable.getSelectionModel().selectFirst();
            changeSong();
        }
    }

    /**
     * Goes to the last song on the playlist
     */
    public void previousButton() {
        if (selectedSongOnPlayList != null) {
            if (this.songsOnPlaylistTable.getSelectionModel().getFocusedIndex() != 0)
                this.songsOnPlaylistTable.getSelectionModel().selectAboveCell();
            else this.songsOnPlaylistTable.getSelectionModel().selectLast();
            changeSong();
        }
        if (selectedSong != null) {
            if (this.songsTable.getSelectionModel().getFocusedIndex() != 0)
                this.songsTable.getSelectionModel().selectAboveCell();
            else this.songsTable.getSelectionModel().selectLast();
            changeSong();
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
