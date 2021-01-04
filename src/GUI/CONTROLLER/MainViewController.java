package GUI.CONTROLLER;

import BE.InputAlert;
import BE.MusicPlayer;
import BE.Category;
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
    private ImageView maximizeBtn;
    @FXML
    private GridPane borderGridPane;
    @FXML
    private GridPane menuBar;
    @FXML
    private TextField searchField;
    @FXML
    private TableView categoryTable;
    @FXML
    private TableColumn<Category, String> categoryNameColumn;
    @FXML
    private TableColumn<Category, Integer> categoryAmountOfMoviesColumn;
    @FXML
    private TableView moviesInCategoryTable;
    @FXML
    private TableColumn<Movie, String> CategoryMoviesColumn;
    @FXML
    private TableView movieTable;
    @FXML
    private TableColumn<Movie, String> movieTableTitleColumn;
    @FXML
    private TableColumn<Movie, String> movieTableRatingColumn;
    @FXML
    private TableColumn<Movie, String> movieTableTimeColumn;
    private Movie selectedMovie;
    private Movie selectedMovieInCategory;
    private Category selectedCategory;
    private Main main;
    private Stage windowStage = new Stage();
    private ObservableList<Movie> movies;
    private ObservableList<Category> categories;
    private ObservableList<Movie> categoryMovies;
    private boolean isMaximized = false;
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
     * Gets the movie manager
     *
     * @return the movieManager
     */
    public MovieManager getMovieManager() {
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
        selectedMovie();
        selectedMovieInCategory();
        selectedCategory();
        setMainViewSize();
        moveMainView();
    }

    /**
     * should load the lists from the db, if it cannot load from db, it will load from local storage.
     */
    public void load() {
        try {
            this.categories = FXCollections.observableArrayList(CATEGORY_MANAGER.loadCategories());
            reloadCategoryTable();
            this.movies = FXCollections.observableArrayList(MOVIE_MANAGER.loadMovies());
            reloadMovieTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Puts the proper values into the tables
     */
    private void initTables() {
        movieTableTitleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        movieTableRatingColumn.setCellValueFactory(cellData -> cellData.getValue().ratingProperty());
        movieTableTimeColumn.setCellValueFactory(cellData -> cellData.getValue().durationStringProperty());

        CategoryMoviesColumn.setCellValueFactory(cellData -> cellData.getValue() == null ? new SimpleStringProperty("") : cellData.getValue().titleProperty());

        categoryNameColumn.setCellValueFactory(cellData -> cellData.getValue().getcategoryNameProperty());
        categoryAmountOfMoviesColumn.setCellValueFactory(cellData -> cellData.getValue().getCategorySize());
    }

    /**
     * Changes selected category to the category clicked in the categoryTable
     */
    private void selectedCategory() {
        this.categoryTable.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            this.selectedCategory = (Category) newValue;
            if (selectedCategory != null) {
                try {
                    if (CATEGORY_MANAGER.loadMoviesInCategory(selectedCategory.getCategoryId()) != null)
                        this.categoryMovies = FXCollections.observableArrayList(CATEGORY_MANAGER.loadMoviesInCategory(selectedCategory.getCategoryId()));
                    moviesInCategoryTable.setItems(categoryMovies);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
    }


    /**
     * Changes selected movie in the category to the movie clicked on the moviesInCategoryTable
     */
    private void selectedMovieInCategory() {
        this.moviesInCategoryTable.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            this.selectedMovieInCategory = (Movie) newValue;
            if (selectedMovieInCategory != null) {
                this.movieTable.getSelectionModel().clearSelection();
            }
        }));
    }

    /**
     * Changes selected movie to the movie clicked in the movieTable
     */
    private void selectedMovie() {
        this.movieTable.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            this.selectedMovie = (Movie) newValue;
            if (selectedMovie != null) {
                this.moviesInCategoryTable.getSelectionModel().clearSelection();
            }
        }));
    }

    /**
     * Reloads the movie table
     */
    public void reloadMovieTable() {
        try {
            int index = movieTable.getSelectionModel().getFocusedIndex();
            this.movieTable.setItems(FXCollections.observableList(MOVIE_MANAGER.loadMovies()));
            movieTable.getSelectionModel().select(index);
        } catch (Exception exception) {
            System.out.println("could not load movie table");
        }
    }

    /**
     * Reloads the movies in the selected category
     */
    private void reloadMoviesInCategory() {
        try {
            int index = moviesInCategoryTable.getSelectionModel().getFocusedIndex();
            this.moviesInCategoryTable.setItems(FXCollections.observableList(CATEGORY_MANAGER.loadMoviesInCategory(selectedCategory.getCategoryId())));
            moviesInCategoryTable.getSelectionModel().select(index);
        } catch (Exception exception) {
            System.out.println("could not load movie in the category table");
        }
    }

    /**
     * Reloads The category table
     */
    private void reloadCategoryTable() {
        try {
            int index = categoryTable.getSelectionModel().getFocusedIndex();
            this.categoryTable.setItems(FXCollections.observableList(CATEGORY_MANAGER.loadCategories()));
            categoryTable.getSelectionModel().select(index);
        } catch (Exception exception) {
            System.out.println("could not load category table");
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
     * Changes movieTable, whenever the searchField changes.
     */
    public void search() {
        try {
            this.movieTable.setItems(FXCollections.observableList(MOVIE_MANAGER.searchMovie(searchField.getText())));
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
     * Adds a new category.
     */
    public void addCategoryButton() throws IOException {
        dialog("category name:", "Add category", "", 1);
    }

    /**
     * Creates a new category
     *
     * @param category the new category
     */
    public void addCategory(Category category) {
        try {
            CATEGORY_MANAGER.createCategory(category.getCategoryName());
            reloadCategoryTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Edits the selected category.
     */
    public void editCategoryButton() throws IOException {
        if (selectedCategory != null) {
            dialog("category name:", "Edit category", selectedCategory.getCategoryName(), 2);
        }
    }

    /**
     * Edits the selected category.
     *
     * @param newTitle new title
     */
    public void editCategory(String newTitle) {
        try {
            selectedCategory.setCategoryName(newTitle);
            CATEGORY_MANAGER.updateCategory(selectedCategory);
            reloadCategoryTable();
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
     * Deletes the selected category.
     */
    public void deleteCategoryButton() {
        var result = InputAlert.showMessageBox("Are you sure?", String.format("Deleting %s", selectedCategory.getCategoryName()),
                "You cannot undo this action once it's done!", Alert.AlertType.CONFIRMATION);
        if (result.get() == ButtonType.OK) {
            try {
                CATEGORY_MANAGER.deleteCategory(selectedCategory);
                reloadMoviesInCategory();
                load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds movie to the current category.
     */
    public void addToCategoryButton() {
        if (selectedCategory != null) {
            try {
                for (Movie movie : Collections.unmodifiableList(CATEGORY_MANAGER.loadMoviesInCategory(selectedCategory.getCategoryId()))) {
                    if (movie.getId() == selectedMovie.getId()) {
                        if (movieTable.getSelectionModel().getFocusedIndex() == movieTable.getItems().size() - 1) {
                            movieTable.getSelectionModel().select(0);
                        } else
                            movieTable.getSelectionModel().select(movieTable.getSelectionModel().getFocusedIndex() + 1);
                        return;
                    }
                }
                CATEGORY_MANAGER.addMoviesToCategory(selectedCategory.getCategoryId(), selectedMovie.getId());
                reloadMoviesInCategory();
                reloadCategoryTable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Moves a movie up on the current category.
     */
    public void moveMovieUpInCategoryButton() {
        this.categoryMovies = FXCollections.observableArrayList(moveInCategory(moviesInCategoryTable.getItems(), selectedMovieInCategory, -1));
        this.moviesInCategoryTable.setItems(categoryMovies);
    }

    /**
     * Moves a movie down on the current category
     */
    public void moveMovieDownInCategoryButton() {
        this.categoryMovies = FXCollections.observableArrayList(moveInCategory(moviesInCategoryTable.getItems(), selectedMovieInCategory, 1));
        this.moviesInCategoryTable.setItems(categoryMovies);
    }

    /**
     * Changes the position on the category
     *
     * @param listOfMovies the list of movies you want to change
     * @param movie        the movie
     * @param pos         the position
     * @return A list of movies with the new order
     */
    public List<Movie> moveInCategory(List<Movie> listOfMovies, Movie movie, int pos) {
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
     * Saves the category
     */
    public void saveCategory() {
        if (moviesInCategoryTable.getItems() != null) {
            for (Movie movie : categoryMovies) {
                try {
                    CATEGORY_MANAGER.deleteMovieFromCategory(selectedCategory.getCategoryId(), movie.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (Movie movie : categoryMovies) {
                try {
                    CATEGORY_MANAGER.addMoviesToCategory(selectedCategory.getCategoryId(), movie.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Shuffles the categories
     */
    @FXML
    private void shuffleCategory() {
        Collections.shuffle(categoryMovies);
    }

    /**
     * Removes the selected movie from the current category.
     */
    public void removeFromCategoryButton() {
        if (selectedCategory != null && selectedMovieInCategory != null) {
            try {
                int index = moviesInCategoryTable.getSelectionModel().getFocusedIndex();
                CATEGORY_MANAGER.deleteMovieFromCategory(selectedCategory.getCategoryId(), selectedMovieInCategory.getId());
                reloadMoviesInCategory();
                reloadCategoryTable();
                moviesInCategoryTable.getSelectionModel().select(index > 0 ? index - 1 : index);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds a new movie
     */
    public void newMovieButton() {
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
     * Creates a movie
     *
     * @param movie the movie
     */
    public void createMovie(Movie movie) {
        try {
            MOVIE_MANAGER.createMovie(movie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds multiple movies at once.
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
                        MOVIE_MANAGER.createMovie(new Movie(selectedFile.getName().substring(0, selectedFile.getName().indexOf('.')), selectedFile.getPath()));
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
     * Edits the selected movie
     */
    public void editMovieButton() {
        if (selectedMovie != null) {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("DIALOGUE/EditSong.fxml"));
            AnchorPane dialog = null;
            try {
                dialog = loader.load();
                EditMovieController controller = loader.getController();
                controller.setMainController(this);
                controller.setGenreComboBox(MOVIE_MANAGER.getGenres());
                controller.setSelectedMovie(selectedMovie);
                windowStage = new Stage();
                windowStage.setScene(new Scene(dialog));
                windowStage.initModality(Modality.APPLICATION_MODAL);
                windowStage.alwaysOnTopProperty();
                windowStage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            InputAlert.showMessageBox("No movie selected", "Cannot modify something that doesn't exist!", "Please select a movie.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Deletes the selected movie
     */
    public void deleteMovieButton() {
        var result = InputAlert.showMessageBox("Do you want to delete this movie?", String.format("Deleting %s", selectedMovie.getTitle()),
                "You cannot undo this action once it's done!", Alert.AlertType.CONFIRMATION);
        if (result.get() == ButtonType.OK) {
            try {
                MOVIE_MANAGER.deleteMovie(selectedMovie.getId());
                reloadMoviesInCategory();
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
