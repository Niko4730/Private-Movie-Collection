package DAL.DAO;

import BE.Movie;
import BLL.MovieManager;

import java.util.List;
import java.util.Map;

public interface MovieDAOInterface {
    /**
     * Sets the song manager
     *
     * @param movieManager the song manager
     */
    void setSongManager(MovieManager movieManager);

    /**
     * Loads all songs
     *
     * @return  A list of songs
     * @throws  Exception If something went wrong
     */
    List<Movie> loadSongs() throws Exception;

    /**
     * Creates a new song
     *
     * @param   movie the new song
     * @throws  Exception If something went wrong
     */
    void createSong(Movie movie) throws Exception;

    /**
     * Gets a song
     *
     * @param   name the name of teh song you wanted
     * @return  the song
     * @throws  Exception If something went wrong
     */
    Movie getSong(String name) throws Exception;

    /**
     * Deletes a song
     *
     * @param   id the id of the song you want to delete
     * @throws  Exception If something went wrong
     */
    void deleteSong(int id) throws Exception;

    /**
     * modifies a song
     *
     * @param   modified     the modified song
     * @throws  Exception    if something went wrong
     */
    void updateSong(Movie modified) throws Exception;

    /**
     * Searches for a string
     *
     * @param   searchQuery     the string you are searching
     * @return                  a list of songs, that matches the searchQuery
     * @throws  Exception       if something went wrong
     */
    List<Movie> searchSong(String searchQuery) throws Exception;

    /**
     * Gets the map of genres
     *
     * @return  a map of the genres
     * @throws  Exception if something went wrong
     */
    Map<Integer, String> getGenres() throws Exception;
}