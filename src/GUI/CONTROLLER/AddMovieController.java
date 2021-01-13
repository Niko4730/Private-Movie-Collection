package GUI.CONTROLLER;

import BE.Category;
import BE.Movie;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class AddMovieController extends Component implements Initializable {
    @FXML
    TextField filePathTextField;
    @FXML
    TextField titleTextField;
    @FXML
    ComboBox categoryComboBox;
    @FXML
    TextField ratingTextField;
    @FXML
    Button addButton;

    private MainViewController mainViewController;
    private Movie movieToAdd;
    private ObservableList<Category> categories;
    private String selectedCategory;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectedCategory();
    }

    /**
     * Initialize the combo box to listen to when a new item is selected.
     */
    private void selectedCategory() {
        categoryComboBox.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            selectedCategory = (String) newValue;
            System.out.println("Selected category: " + selectedCategory);
        }));
    }

    /**
     * Assign the category combo box to have the specified categories.
     *
     * @param categories The genres to add.
     */
    public void setCategoryComboBox(ObservableList<Category> categories) {
        this.categories = categories;
        categoryComboBox.getItems().clear();
        for (Category cat : categories)
            categoryComboBox.getItems().add(cat.getCategoryName());
    }

    /**
     * Sets the main view controller
     *
     * @param mainViewController
     */
    public void setMainController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    /**
     * Lets you select a file in your system
     */
    public void browse() {
        try {
            //To allow variable usage in an anonymous lambda, we must create a new Object with the required variables.
            var ref = new Object() {
                int current_try = 0;
                int max_tries = 3;
            };

            Thread songAdderThread = null;
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Mp4 file", "mp4"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile().getAbsoluteFile();

                var fileName = selectedFile.getName();
                var fileNameNoExt = fileName.lastIndexOf('.') > 0 ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
                var filePath = selectedFile.getAbsolutePath().contains("\\Data\\") ? selectedFile.getAbsolutePath().substring(selectedFile.getAbsolutePath().indexOf("Data\\")) : selectedFile.getAbsolutePath();

                movieToAdd = new Movie();
                movieToAdd.setFilePath(filePath);
                movieToAdd.getMeta();

                try {
                    songAdderThread = new Thread(() -> {
                        while (!movieToAdd.getIsInitialized()) {
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
                                var title = !movieToAdd.getTitle().isBlank() ? movieToAdd.getTitle() : fileNameNoExt;
                                titleTextField.setText(title);
                                filePathTextField.setText(filePath);
                                System.out.println("Media initialized.");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    });
                    songAdderThread.start();
                } catch (
                        Exception e) {
                    if (songAdderThread != null)
                        songAdderThread.interrupt();
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Find the specified category name and return its id.
     *
     * @param categoryName The category name to find.
     * @return
     */
    private int getCategoryIdFromName(String categoryName) {
        for (var category : categories) {
            if (category.getCategoryName().equals(categoryName)) {
                return category.getCategoryId();
            }
        }
        return -1;
    }

    /**
     * Add the new movie to database.
     */
    public void addMovie() {
        try {
            if (movieToAdd != null) {
                movieToAdd.setTitle(titleTextField.getText());
                movieToAdd.setCategoryId(getCategoryIdFromName(selectedCategory));

                var rating = Double.parseDouble(ratingTextField.getText().isEmpty() ? "0" : ratingTextField.getText().replaceAll(",", "."));
                movieToAdd.setRating(Double.toString(rating));
                int id = mainViewController.createMovie(movieToAdd);
                if (id != -1) movieToAdd.setId(id);
                mainViewController.reloadMovieTable();
                mainViewController.addMovieToCategory(movieToAdd.getCategoryId(), movieToAdd.getId());
                close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the window
     */
    public void close() {
        mainViewController.getWindowStage().close();
    }
}
