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
     * Sets the value of movieDAO
     *
     * @param movieDAO new value of movieDAO
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
     * Loads movies
     *
     * @return a list of the movies
     * @throws Exception if something went wrong
     */
    public List<Movie> loadMovies() throws Exception {
        return movieDAO.loadMovies();
    }

    /**
     * Sends information to create a movies
     *
     * @param movie the new movies
     * @throws Exception if something went wrong
     */
    public int createMovie(Movie movie) throws Exception {
        return movieDAO.createMovie(movie);
    }

    /**
     * Gets the value of movies name
     *
     * @param name new value of movies name
     * @return the value of movies name
     * @throws Exception if something went wrong
     */
    public Movie getMovie(String name) throws Exception {
        return movieDAO.getMovie(name);
    }

    /**
     * Sends information to update movies
     *
     * @param modified the modified movies
     * @throws Exception if something went wrong
     */
    public void updateMovie(Movie modified) throws Exception {
        movieDAO.updateMovie(modified);
    }

    /**
     * Sends information to delete movies
     *
     * @param id the movies id
     * @throws Exception if something went wrong
     */
    public void deleteMovie(int id) throws Exception {
        movieDAO.deleteMovie(id);
    }

    /**
     * Searches trough the movies
     *
     * @param search the searchQuery
     * @return the list that contains the search query
     * @throws Exception if something went wrong
     */
    public List<Movie> searchMovie(String search) throws Exception {
        return movieDAO.searchMovie(search);
    }

    /**
     * Gets the map of genres
     *
     * @return the map
     * @throws Exception if something went wrong
     */
    public Map<Integer, String> getCategories() throws Exception {
        return movieDAO.getCategories();
    }

    /**
     * Gets all movies that's older than 2 years with a rating below 6.
     *
     * @return The old movies.
     */
    public List<Movie> getOldMovies() {
        return movieDAO.getOldMovies();
    }
}
