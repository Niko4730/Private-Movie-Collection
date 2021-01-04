package BLL;

import BE.Movie;
import DAL.DAO.DB.MovieDBDAO;
import DAL.DAO.FILE.MovieLocalDAO;
import DAL.DAO.MovieDAOInterface;
import GUI.CONTROLLER.MainViewController;
import java.util.List;
import java.util.Map;

public class MovieManager {
    protected static MovieDAOInterface songDAO;
    protected MainViewController mainController;

    //initializes the interface, if no connection to the database, it will try to use the local option.
    static {
        try {
            songDAO = new MovieDBDAO();
        } catch (Exception e) {
            songDAO = new MovieLocalDAO();
        }
    }

    /**
     * Makes it use the local class.
     */
    public void goLocal() {
        songDAO = new MovieLocalDAO();
    }


    /**
     * Constructor
     */
    public MovieManager() {
        songDAO.setSongManager(this);
    }

    /**
     * Sets the value of songDAO
     *
     * @param songDAO new value of songDAO
     */
    public void setSongDAO(MovieDAOInterface songDAO) {
        MovieManager.songDAO = songDAO;
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
        return songDAO.loadSongs();
    }

    /**
     * Sends information to create a song
     *
     * @param movie          the new song
     * @throws Exception    if something went wrong
     */
    public void createMovie(Movie movie) throws Exception {
        songDAO.createSong(movie);
    }

    /**
     * Gets the value of song name
     *
     * @param   name new value of song name
     * @return  the value of song name
     * @throws Exception if something went wrong
     */
    public Movie getSong(String name) throws Exception {
        return songDAO.getSong(name);
    }

    /**
     * Sends information to update song
     *
     * @param modified      the modified song
     * @throws Exception    if something went wrong
     */
    public void updateSong(Movie modified) throws Exception {
        songDAO.updateSong(modified);
    }

    /**
     * Sends information to delete song
     *
     * @param id            the song id
     * @throws Exception    if something went wrong
     */
    public void deleteMovie(int id) throws Exception {
        songDAO.deleteSong(id);
    }

    /**
     * Searches trough the songs
     *
     * @param search        the searchQuery
     * @return              the list that contains the search query
     * @throws Exception    if something went wrong
     */
    public List<Movie> searchMovie(String search) throws Exception {
        return songDAO.searchSong(search);
    }

    /**
     * Gets the map of genres
     * @return              the map
     * @throws Exception    if something went wrong
     */
    public Map<Integer, String> getGenres() throws Exception {
        return songDAO.getGenres();
    }
}
