package BLL;

import BE.InputAlert;
import BE.Category;
import BE.Movie;
import DAL.DAO.DB.CategoryDBDAO;
import DAL.DAO.FILE.CategoryLocalDAO;
import DAL.DAO.CategoryDAOInterface;
import GUI.CONTROLLER.MainViewController;

import java.util.List;

public class CategoryManager {
    protected static CategoryDAOInterface playlistDAO;
    private static InputAlert inputAlert = new InputAlert();
    protected MainViewController mainController;


    //initializes the interface, if no connection to the database, it will try to use the local option.
    static {
        try {
            playlistDAO = new CategoryDBDAO();
        } catch (Exception e) {
            playlistDAO = new CategoryLocalDAO();
            inputAlert.showAlert("Couldn't establish connection to the database. Your changes will be done locally.");
        }
    }

    /**
     * Save playlists locally instead of to database.
     */
    public void goLocal() {
        inputAlert.showAlert("Couldn't establish connection to the database. Your changes will be done locally.");
        playlistDAO = new CategoryLocalDAO();
    }

    /**
     * Set the value of PlaylistDAO
     *
     * @param playlistDAO new value of PlaylistDAO
     */
    public void setPlaylistDAO(CategoryDAOInterface playlistDAO) {
        CategoryManager.playlistDAO = playlistDAO;
    }

    /**
     *
     */
    public CategoryManager() {
        playlistDAO.setPlaylistManager(this);
    }

    /**
     * Set the value of mainController
     *
     * @param mainController new value of mainController
     */
    public void setMainController(MainViewController mainController) {
        this.mainController = mainController;
    }

    /**
     * loads the playlists, if it cannot connect to the database, it saves locally
     *
     * @return Playlists
     */
    public List<Category> loadCategories() throws Exception {
        return playlistDAO.loadPlaylist();
    }

    /**
     * Sends information to create playlist
     *
     * @param name          the playlist name
     * @throws Exception    if something went wrong
     */
    public void createCategory(String name) throws Exception {
        playlistDAO.createPlaylist(name);
    }

    /**
     * Get the value of playlist name
     *
     * @param name          new value of name
     * @return              the value of name
     * @throws Exception    if something went wrong
     */
    public Category getPlaylist(String name) throws Exception {
        return playlistDAO.getPlaylist(name);
    }

    /**
     * Sends information to delete playlist
     *
     * @param category      the playlist
     * @throws Exception    if something went wrong.
     */
    public void deleteCategory(Category category) throws Exception {
        playlistDAO.deletePlaylist(category);
    }

    /**
     * @param playlist_id   the id of the playlist
     * @return              a list of songs
     * @throws Exception    if something went wrong
     */
    public List<Movie> loadMoviesInCategory(int playlist_id) throws Exception {
        return playlistDAO.loadSongsFromPlaylist(playlist_id);
    }

    /**
     * Sends information to add a song to playlist
     *
     * @param playlist_id   the id of the playlist
     * @param song_id       the id of the song
     * @throws Exception    if something went wrong.
     */
    public void addMoviesToCategory(int playlist_id, int song_id) throws Exception {
        playlistDAO.AddSongToPlaylist(playlist_id, song_id);
    }

    /**
     * Sends information to delete a song from playlist
     *
     * @param playlist_id   the id of the playlist
     * @param song_id       the id of the song
     * @throws Exception    if something went wrong.
     */
    public void deleteMovieFromCategory(int playlist_id, int song_id) throws Exception {
        playlistDAO.deleteFromPlaylist(playlist_id, song_id);
    }

    /**
     * Sends information to update playlist
     *
     * @param category      the playlist
     * @throws Exception    if something went wrong.
     */
    public void updateCategory(Category category) throws Exception {
        playlistDAO.updatePlaylist(category);
    }
}
