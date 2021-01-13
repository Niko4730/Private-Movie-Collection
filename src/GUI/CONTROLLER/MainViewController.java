package GUI.CONTROLLER;

import BE.*;
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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MainViewController implements Initializable {
    @FXML
    private ImageView movieImg;
    @FXML
    private BorderPane lowerPane;
    @FXML
    private AnchorPane allMovies;
    @FXML
    private AnchorPane viewAnchorPane;
    @FXML
    private BorderPane topBar;
    @FXML
    private ImageView maximizeBtn;
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
    private TableColumn<Movie, String> ratingMovieInCategoryColumn;
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
    @FXML
    private TableColumn<Movie, String> movieTablelastViewColumn;

    private Movie selectedMovie;
    private Movie selectedMovieInCategory;
    private Category selectedCategory;
    private Main main;
    private Stage windowStage = new Stage();
    private ObservableList<Movie> movies;
    private ObservableList<Category> categories;
    private ObservableList<Movie> categoryMovies;
    private boolean allMoviesShown = true;
    private boolean isMaximized = false;
    private static final CategoryManager CATEGORY_MANAGER = new CategoryManager();
    private static final MovieManager MOVIE_MANAGER = new MovieManager();
    private static final MoviePlayer MOVIE_PLAYER = new MoviePlayer();
    private static final ConvenientUtils CONVENIENT_UTILS = new ConvenientUtils();
    private static final InputAlert INPUT_ALERT = new InputAlert();
    private static final int CHAR_SIMILARITY_LIM = 5;

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
        hideLowerPane();
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
            getOldVideos();
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
        movieTablelastViewColumn.setCellValueFactory(cellData -> cellData.getValue().lastViewProperty());

        CategoryMoviesColumn.setCellValueFactory(cellData -> cellData.getValue() == null ? new SimpleStringProperty("") : cellData.getValue().titleProperty());
        ratingMovieInCategoryColumn.setCellValueFactory(cellData -> cellData.getValue() == null ? new SimpleStringProperty("") : cellData.getValue().ratingProperty());

        categoryNameColumn.setCellValueFactory(cellData -> cellData.getValue().getcategoryNameProperty());
        categoryAmountOfMoviesColumn.setCellValueFactory(cellData -> cellData.getValue().getCategorySize());
    }

    /**
     * Gets all movies that's older than 2 years with a rating below 6.
     *
     * @return The old movies.
     */
    private void getOldVideos() {
        var oldVideos = getMovieManager().getOldMovies();
        var videCount = oldVideos.size();
        if (videCount > 0) {
            var properTitle = videCount < 2 ? String.format("%s old movie found", videCount) : String.format("%s old movies found", videCount);
            var properContent = videCount < 2 ? String.format("%s old movie was found. Check it and see if it's worth having around?", videCount) : String.format("%s old movies were found. You should really clean up your collection.", videCount);
            InputAlert.showMessageBox(properTitle, properTitle, properContent, videCount < 2 ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING);
        }
    }

    /**
     * Changes selected category to the category clicked in the categoryTable
     */
    private void selectedCategory() {
        this.categoryTable.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            this.selectedCategory = (Category) newValue;
            if (selectedCategory != null) {
                try {
                    var moviesInCat = CATEGORY_MANAGER.loadMoviesInCategory(selectedCategory.getCategoryId());
                    if (!moviesInCat.isEmpty())
                        this.categoryMovies = FXCollections.observableArrayList(moviesInCat);
                    else {
                        if (categoryMovies != null)
                            categoryMovies.clear();
                    }
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
                //showLowerPane();
                this.movieTable.getSelectionModel().clearSelection();
            }
        }));

        // Play the selected video in the default media player.
        /** References:
         /* https://stackoverflow.com/questions/16437951/playing-streaming-video-using-default-media-player
         /* https://stackoverflow.com/questions/26563390/detect-doubleclick-on-row-of-tableview-javafx
         */
        this.moviesInCategoryTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && selectedMovieInCategory != null) {
                try {
                    var lastView_date = new Date(System.currentTimeMillis());
                    String pattern = "dd/MM/yyyy  HH:mm:ss";
                    var simpleDateFormat = new SimpleDateFormat(pattern);
                    String date = simpleDateFormat.format(lastView_date);

                    selectedMovieInCategory.setLastView(date);
                    getMovieManager().updateMovie(selectedMovieInCategory);
                    Desktop.getDesktop().open(new File(selectedMovieInCategory.getFilePath()));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Changes selected movie to the movie clicked in the movieTable
     */
    private void selectedMovie() {
        this.movieTable.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            this.selectedMovie = (Movie) newValue;
            if (selectedMovie != null) {
                //showLowerPane();
                this.moviesInCategoryTable.getSelectionModel().clearSelection();
            }
        }));


        // Play the selected video in the default media player.
        /** References:
         /* https://stackoverflow.com/questions/16437951/playing-streaming-video-using-default-media-player
         /* https://stackoverflow.com/questions/26563390/detect-doubleclick-on-row-of-tableview-javafx
         */
        this.movieTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && selectedMovie != null) {
                try {
                    var lastView_date = new Date(System.currentTimeMillis());
                    String pattern = "dd/MM/yyyy  HH:mm:ss";
                    var simpleDateFormat = new SimpleDateFormat(pattern);
                    String date = simpleDateFormat.format(lastView_date);

                    selectedMovie.setLastView(date);
                    getMovieManager().updateMovie(selectedMovie);
                    Desktop.getDesktop().open(new File(selectedMovie.getFilePath()));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Reloads the movie table
     */
    public void reloadMovieTable() {
        try {
            int index = movieTable.getSelectionModel().getFocusedIndex();
            this.movies = FXCollections.observableList(MOVIE_MANAGER.loadMovies());
            this.movieTable.setItems(movies);
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
            this.categoryMovies = FXCollections.observableList(CATEGORY_MANAGER.loadMoviesInCategory(selectedCategory.getCategoryId()));
            this.moviesInCategoryTable.setItems(categoryMovies);
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
            this.categories = FXCollections.observableList(CATEGORY_MANAGER.loadCategories());
            this.categoryTable.setItems(categories);
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
        this.movieTable.setItems(movies);
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
            category.setCategoryId(CATEGORY_MANAGER.createCategory(category.getCategoryName()));
            categories.add(category);
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
     * @throws IOException If something went wrong
     */
    private void dialog(String labelFieldText, String dialogTitleText, String titleFieldText, int mode) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("DIALOGUE/AddCategory.fxml"));
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
                categories.remove(selectedCategory);
                reloadMoviesInCategory();
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
                selectedCategory.setCategorySize(selectedCategory.getCategorySize().getValue() + 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Moves a movie up on the current category.
     */
    public void moveMovieUpInCategoryButton() {
        this.categoryMovies = FXCollections.observableArrayList(CONVENIENT_UTILS.moveInCategory(moviesInCategoryTable.getItems(), selectedMovieInCategory, -1));
        this.moviesInCategoryTable.setItems(categoryMovies);
    }

    /**
     * Moves a movie down on the current category
     */
    public void moveMovieDownInCategoryButton() {
        this.categoryMovies = FXCollections.observableArrayList(CONVENIENT_UTILS.moveInCategory(moviesInCategoryTable.getItems(), selectedMovieInCategory, 1));
        this.moviesInCategoryTable.setItems(categoryMovies);
    }

    /**
     * Removes the selected movie from the current category.
     */
    public void removeFromCategoryButton() {
        if (selectedCategory != null && selectedMovieInCategory != null) {
            try {
                int index = moviesInCategoryTable.getSelectionModel().getFocusedIndex();
                CATEGORY_MANAGER.deleteMovieFromCategory(selectedCategory.getCategoryId(), selectedMovieInCategory.getId());
                categoryMovies.remove(selectedMovie);
                selectedCategory.setCategorySize(selectedCategory.getCategorySize().getValue() - 1);
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
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("DIALOGUE/AddMovie.fxml"));
        AnchorPane dialog = null;
        try {
            dialog = loader.load();
            AddMovieController controller = loader.getController();
            controller.setMainController(this);
            controller.setGenreComboBox(MOVIE_MANAGER.getCategories());
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
     * Also checks if the movies looks similar to other movies contained in the movies list
     *
     * @param movie the movie
     */
    public int createMovie(Movie movie) {
        try {
            for (Movie movie1 : movies) {
                if (CONVENIENT_UTILS.titleDiff(movie.getTitle(), movie1.getTitle()) < CHAR_SIMILARITY_LIM) {
                    var result = InputAlert.showMessageBox("Are you sure?", "There is a movie, that looks similar", movie.getTitle() + " looks an awful lot like " + movie1.getTitle(), Alert.AlertType.CONFIRMATION);
                    if (result.get() == ButtonType.OK) {
                        movies.add(movie);
                        return MOVIE_MANAGER.createMovie(movie);
                    }
                }
            }
            // If no similar movie found, just create it.
            return MOVIE_MANAGER.createMovie(movie);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        //return -1;
    }

    /**
     * Adds multiple movies at once.
     */
    public void bulkAddButton() {
        FileChooser fileChooser = new FileChooser();
        windowStage = new Stage();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP4-files", "*.mp4"));
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
            System.out.println("No file selected.");
        }
    }

    /**
     * Edits the selected movie
     */
    public void editMovieButton() {
        if (selectedMovie != null) {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("DIALOGUE/EditMovie.fxml"));
            AnchorPane dialog = null;
            try {
                dialog = loader.load();
                EditMovieController controller = loader.getController();
                controller.setMainController(this);
                controller.setGenreComboBox(MOVIE_MANAGER.getCategories());
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
                movies.remove(selectedMovie);
            } catch (Exception e) {
                InputAlert.showMessageBox("No movie selected", "Cannot delete something that doesn't exist!", "Please select a movie.", Alert.AlertType.ERROR);

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
        viewAnchorPane.setOnMousePressed(mouseEvent1 -> {
            x.set(mouseEvent1.getSceneX());
            y.set(mouseEvent1.getSceneY());
            mouseEvent1.consume();
            int offset = 5;
            viewAnchorPane.setOnMouseReleased(mouseEvent2 -> {
                if (y.get() > viewAnchorPane.getHeight() - offset || x.get() > viewAnchorPane.getWidth() - offset) {
                    main.getPrimaryStage().setHeight(viewAnchorPane.getHeight() + (mouseEvent2.getSceneY() - y.get()));
                    main.getPrimaryStage().setWidth(viewAnchorPane.getWidth() + (mouseEvent2.getSceneX() - x.get()));
                    mouseEvent2.consume();
                }
            });
        });
    }

    /**
     * Moves the main view when pulling the top bar
     */
    public void moveMainView() {
        AtomicReference<Double> x = new AtomicReference<>((double) 0);
        AtomicReference<Double> y = new AtomicReference<>((double) 0);
        topBar.setOnMousePressed(mouseEvent -> {
            x.set(mouseEvent.getSceneX());
            y.set(mouseEvent.getSceneY());
        });
        topBar.setOnMouseDragged(mouseEvent -> {
                    main.getPrimaryStage().setX(mouseEvent.getScreenX() - x.get());
                    main.getPrimaryStage().setY(mouseEvent.getScreenY() - y.get());
                }
        );
    }

    public void hideAllMoviesTable() {
        if (allMoviesShown) {
            allMovies.setVisible(false);
            allMovies.setMinWidth(0);
            allMovies.setPrefWidth(0);
            allMovies.setMaxWidth(0);
        } else {
            allMovies.setVisible(true);

            allMovies.setMinWidth(205);
            allMovies.setPrefWidth(400);
            allMovies.setMaxWidth(5000);
        }
        allMoviesShown = !allMoviesShown;
    }

    private void hideLowerPane() {
        lowerPane.setVisible(false);
        lowerPane.setMaxHeight(0);
        lowerPane.setMinHeight(0);
        lowerPane.setPrefHeight(0);
    }

    private void showLowerPane() {
        lowerPane.setVisible(true);
        lowerPane.setMaxHeight(215);
        lowerPane.setMinHeight(215);
        lowerPane.setPrefHeight(215);
    }

    public void viewMovieOrWhatever() {

    }


    public void imgChange() {
        movieImg.setImage(new Image(""));
        System.out.println("WOW THE IMAGE CHANGED... or is yet to be implemented");
    }

    public void searchOnIMDB() {
        String query = selectedMovie == null ? selectedMovieInCategory.getTitle() : selectedMovie.getTitle();
        query = query.replaceAll(" ", "_");
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI("https://www.imdb.com/find?q=" + query + "&ref_=nv_sr_sm"));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}
