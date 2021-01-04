package BLL;

import BE.Song;
import DAL.DAO.DB.SongDBDAO;
import DAL.DAO.FILE.SongLocalDAO;
import DAL.DAO.SongDAOInterface;
import GUI.CONTROLLER.MainViewController;
import java.util.List;
import java.util.Map;

public class SongManager {
    protected static SongDAOInterface songDAO;
    protected MainViewController mainController;

    //initializes the interface, if no connection to the database, it will try to use the local option.
    static {
        try {
            songDAO = new SongDBDAO();
        } catch (Exception e) {
            songDAO = new SongLocalDAO();
        }
    }

    /**
     * Makes it use the local class.
     */
    public void goLocal() {
        songDAO = new SongLocalDAO();
    }


    /**
     * Constructor
     */
    public SongManager() {
        songDAO.setSongManager(this);
    }

    /**
     * Sets the value of songDAO
     *
     * @param songDAO new value of songDAO
     */
    public void setSongDAO(SongDAOInterface songDAO) {
        SongManager.songDAO = songDAO;
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
    public List<Song> loadSongs() throws Exception {
        return songDAO.loadSongs();
    }

    /**
     * Sends information to create a song
     *
     * @param song          the new song
     * @throws Exception    if something went wrong
     */
    public void createSong(Song song) throws Exception {
        songDAO.createSong(song);
    }

    /**
     * Gets the value of song name
     *
     * @param   name new value of song name
     * @return  the value of song name
     * @throws Exception if something went wrong
     */
    public Song getSong(String name) throws Exception {
        return songDAO.getSong(name);
    }

    /**
     * Sends information to update song
     *
     * @param modified      the modified song
     * @throws Exception    if something went wrong
     */
    public void updateSong(Song modified) throws Exception {
        songDAO.updateSong(modified);
    }

    /**
     * Sends information to delete song
     *
     * @param id            the song id
     * @throws Exception    if something went wrong
     */
    public void deleteSong(int id) throws Exception {
        songDAO.deleteSong(id);
    }

    /**
     * Searches trough the songs
     *
     * @param search        the searchQuery
     * @return              the list that contains the search query
     * @throws Exception    if something went wrong
     */
    public List<Song> searchSong(String search) throws Exception {
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
