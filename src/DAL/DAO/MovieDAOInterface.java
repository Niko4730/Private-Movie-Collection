package DAL.DAO;

import BE.Movie;
import BLL.MovieManager;

import java.util.List;
import java.util.Map;

public interface MovieDAOInterface {
    /**
     * Sets the movie manager
     *
     * @param movieManager the movie manager
     */
    void setMovieManager(MovieManager movieManager);

    /**
     * Loads all movies
     *
     * @return A list of movies
     * @throws Exception If something went wrong
     */
    List<Movie> loadMovies() throws Exception;

    /**
     * Creates a new movie
     *
     * @param movie the new movie
     * @throws Exception If something went wrong
     */
    int createMovie(Movie movie) throws Exception;

    /**
     * Gets a movie
     *
     * @param name the name of teh movie you wanted
     * @return the movie
     * @throws Exception If something went wrong
     */
    Movie getMovie(String name) throws Exception;

    /**
     * Deletes a movie
     *
     * @param id the id of the movie you want to delete
     * @throws Exception If something went wrong
     */
    void deleteMovie(int id) throws Exception;

    /**
     * modifies a movie
     *
     * @param modified the modified movie
     * @throws Exception if something went wrong
     */
    void updateMovie(Movie modified) throws Exception;

    /**
     * Searches for a string
     *
     * @param searchQuery the string you are searching
     * @return a list of movies, that matches the searchQuery
     * @throws Exception if something went wrong
     */
    List<Movie> searchMovie(String searchQuery) throws Exception;

    /**
     * Gets the map of genres
     *
     * @return a map of the genres
     * @throws Exception if something went wrong
     */
    Map<Integer, String> getCategories() throws Exception;

    /**
     * Gets all movies that's older than 2 years with a rating below 6.
     *
     * @return The old movies.
     */
    List<Movie> getOldMovies();
}