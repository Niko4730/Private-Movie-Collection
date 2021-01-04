package DAL.DAO;

import BE.Category;
import BE.Movie;
import BLL.CategoryManager;
import java.util.List;

public interface CategoryDAOInterface {

    /**
     * Sets the playlistManager
     *
     * @param categoryManager the playlistManager
     */
    void setPlaylistManager(CategoryManager categoryManager);

    /**
     * Loads the playlists
     *
     * @return  A list of playlists
     * @throws  Exception if something went wrong.
     */
    List<Category> loadPlaylist() throws Exception;

    /**
     * Creates a playlist
     *
     * @param   name the name of the playlist
     * @throws  Exception if something went wrong.
     */
    void createPlaylist(String name) throws Exception;

    /**
     * Gets a playlist
     *
     * @param   name the name of the playlist
     * @return  the requested playlist.
     * @throws  Exception if something went wrong.
     */

    Category getPlaylist(String name) throws Exception;

    /**
     * Deletes a playlist
     *
     * @param   category the playlist
     * @throws  Exception if something went wrong.
     */

    void deletePlaylist(Category category) throws Exception;

    /**
     * Loads the songs on the playlist
     *
     * @param   playlist_id the id of the playlist
     * @return  A list of songs on the playlist
     * @throws  Exception if something went wrong.
     */
    List<Movie> loadSongsFromPlaylist(int playlist_id) throws Exception;

    /**
     * Adds a song to the playlist
     *
     * @param   playlist_id the playlist you want to add a song to
     * @param   song_id the song you want to add
     * @throws  Exception if something went wrong.
     */

    void AddSongToPlaylist(int playlist_id,int song_id) throws Exception;

    /**
     * Removes a song from the playlist
     *
     * @param   playlist_id the id of the playlist you want to remove the song from
     * @param   song_id the id of the song you want to remove
     * @throws  Exception if something went wrong.
     */

    void deleteFromPlaylist(int playlist_id,int song_id) throws Exception;

    /**
     * Updates a playlist to with new parameters
     *
     * @param   category    the modified playlist
     * @throws  Exception   if something went wrong.
     */
    void updatePlaylist(Category category) throws Exception;
}
