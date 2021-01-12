package BLL;

import BE.Movie;
import DAL.DAO.DB.MovieDBDAO;
import DAL.DAO.FILE.MovieLocalDAO;
import DAL.DAO.MovieDAOInterface;
import GUI.CONTROLLER.MainViewController;
import java.util.List;
import java.util.Map;

public class MovieManager {
    protected static MovieDAOInterface movieDAO;
    protected MainViewController mainController;

    //initializes the interface, if no connection to the database, it will try to use the local option.
    static {
        try {
            movieDAO = new MovieDBDAO();
        } catch (Exception e) {
            movieDAO = new MovieLocalDAO();
        }
    }

    /**
     * Makes it use the local class.
     */
    public void goLocal() {
        movieDAO = new MovieLocalDAO();
    }


    /**
     * Constructor
     */
    public MovieManager() {
        movieDAO.setMovieManager(this);
    }

    /**
     * Sets the value of songDAO
     *
     * @param movieDAO new value of songDAO
     */
    public void setMovieDAO(MovieDAOInterface movieDAO) {
        MovieManager.movieDAO = movieDAO;
    }

    /**
     * Sets he value of MainController
     *
     * @param mainController new value of MainController
     */
    public void setMainController(MainViewController mainController) {
        this.mainController = mainController;
    }

    /**
     * Loads songs
     *
     * @return              a list of the songs
     * @throws Exception    if something went wrong
     */
    public List<Movie> loadMovies() throws Exception {
        return movieDAO.loadMovies();
    }

    /**
     * Sends information to create a song
     *
     * @param movie          the new song
     * @throws Exception    if something went wrong
     */
    public int createMovie(Movie movie) throws Exception {
        return movieDAO.createMovie(movie);
    }

    /**
     * Gets the value of song name
     *
     * @param   name new value of song name
     * @return  the value of song name
     * @throws Exception if something went wrong
     */
    public Movie getMovie(String name) throws Exception {
        return movieDAO.getMovie(name);
    }

    /**
     * Sends information to update song
     *
     * @param modified      the modified song
     * @throws Exception    if something went wrong
     */
    public void updateMovie(Movie modified) throws Exception {
        movieDAO.updateMovie(modified);
    }

    /**
     * Sends information to delete song
     *
     * @param id            the song id
     * @throws Exception    if something went wrong
     */
    public void deleteMovie(int id) throws Exception {
        movieDAO.deleteMovie(id);
    }

    /**
     * Searches trough the songs
     *
     * @param search        the searchQuery
     * @return              the list that contains the search query
     * @throws Exception    if something went wrong
     */
    public List<Movie> searchMovie(String search) throws Exception {
        return movieDAO.searchMovie(search);
    }

    /**
     * Gets the map of genres
     * @return              the map
     * @throws Exception    if something went wrong
     */
    public Map<Integer, String> getCategories() throws Exception {
        return movieDAO.getCategories();
    }
}
