package DAL.DAO;

import BE.Playlist;
import BE.Song;
import BLL.PlaylistManager;
import java.util.List;

public interface PlaylistDAOInterface {

    /**
     * Sets the playlistManager
     *
     * @param playlistManager the playlistManager
     */
    void setPlaylistManager(PlaylistManager playlistManager);

    /**
     * Loads the playlists
     *
     * @return  A list of playlists
     * @throws  Exception if something went wrong.
     */
    List<Playlist> loadPlaylist() throws Exception;

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

    Playlist getPlaylist(String name) throws Exception;

    /**
     * Deletes a playlist
     *
     * @param   playlist the playlist
     * @throws  Exception if something went wrong.
     */

    void deletePlaylist(Playlist playlist) throws Exception;

    /**
     * Loads the songs on the playlist
     *
     * @param   playlist_id the id of the playlist
     * @return  A list of songs on the playlist
     * @throws  Exception if something went wrong.
     */
    List<Song> loadSongsFromPlaylist(int playlist_id) throws Exception;

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
     * @param   playlist    the modified playlist
     * @throws  Exception   if something went wrong.
     */
    void updatePlaylist(Playlist playlist) throws Exception;
}
