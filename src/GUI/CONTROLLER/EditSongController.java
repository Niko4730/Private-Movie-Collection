package GUI.CONTROLLER;

import BE.Song;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class EditSongController extends Component implements Initializable {
    @FXML
    TextField filePathTextField;
    @FXML
    TextField titleTextField;
    @FXML
    TextField artistTextField;
    @FXML
    ComboBox genreComboBox;
    @FXML
    Button saveButton;

    private MainViewController mainViewController;
    private Song selectedSong;
    private Song modifiedSong;
    private Map<Integer, String> genres;
    private String selectedCategory;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectedCategory();
    }

    /**
     * Initialize the combo box to listen to when a new item is selected.
     */
    private void selectedCategory() {
        genreComboBox.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            selectedCategory = (String) newValue;
            System.out.println("Selected category: " + selectedCategory);
        }));
    }

    /**
     * Assign the genre combo box to have the specified hash map genres.
     *
     * @param genres The genres to add.
     */
    public void setGenreComboBox(Map<Integer, String> genres) {
        this.genres = new HashMap<>(genres);
        genreComboBox.getItems().clear();
        genreComboBox.getItems().addAll(genres.values());
    }

    /**
     * Sets the main view controller
     *
     * @param mainViewController the main view controller
     */
    public void setMainController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    /**
     * Find the specified category name and return its id.
     *
     * @param categoryName The category name to find.
     * @return the category id
     */
    private int getCategoryIdFromName(String categoryName) {
        for (var category : genres.entrySet()) {
            if (category.getValue() == categoryName) {
                return category.getKey();
            }
        }
        return -1;
    }

    /**
     * Sets the selected song
     *
     * @param song the selected song
     */
    public void setSelectedSong(Song song) {
        if (song != null) {
            selectedSong = song;
            titleTextField.setText(selectedSong.getTitle());
            filePathTextField.setText(selectedSong.getFilePath());
            artistTextField.setText(selectedSong.getArtist());
            genreComboBox.getSelectionModel().select(selectedSong.getCategoryName());
        }
    }

    /**
     * Opens a window in which you can select files
     */
    public void browse() {
        try {
            //To allow variable usage in an anonymous lambda, we must create a new Object with the required variables.
            var ref = new Object() {
                int current_try = 0;
                int max_tries = 3;
            };

            Thread songEditorThread = null;
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();

                var fileName = selectedFile.getName();
                var fileNameNoExt = fileName.lastIndexOf('.') > 0 ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
                var filePath = selectedFile.getAbsolutePath();
                var artist = selectedSong.getArtist();

                // Some disclaimer.
                if (fileName.endsWith(".m4a"))
                    System.out.println("Note: M4a meta tags for some reason can't seem to read properly. Fields may be empty.");

                modifiedSong = new Song();
                modifiedSong.setFilePath(filePath);

                try {
                    songEditorThread = new Thread(() -> {
                        while (!selectedSong.getIsInitialized()) {
                            try {
                                if (ref.current_try < ref.max_tries) {
                                    ref.current_try++;
                                    System.out.println("Waiting for media to initialize.");
                                    Thread.sleep(500);
                                } else break;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        // https://stackoverflow.com/questions/49343256/threads-in-javafx-not-on-fx-application-thread.
                        // Required for updating GUI stuff from another thread.
                        Platform.runLater(() -> {
                            try {
                                var title = !modifiedSong.getTitle().isBlank() ? modifiedSong.getTitle() : fileNameNoExt;
                                titleTextField.setText(title);
                                filePathTextField.setText(filePath);
                                artistTextField.setText(artist);
                                System.out.println("Media initialized.");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    });
                    songEditorThread.start();
                } catch (
                        Exception e) {
                    if (songEditorThread != null)
                        songEditorThread.interrupt();
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the changes made to the selected song.
     */
    public void save() {
        try {
            if (selectedSong != null) {
                selectedSong.setTitle(titleTextField.getText());
                selectedSong.setFilePath(filePathTextField.getText());
                selectedSong.setArtist(artistTextField.getText());
                selectedSong.setCategoryId(getCategoryIdFromName(selectedCategory));

                mainViewController.getSongManager().updateSong(selectedSong);
                mainViewController.reloadSongTable();
                close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Close the window.
     */
    public void close() {
        mainViewController.getWindowStage().close();
    }
}
