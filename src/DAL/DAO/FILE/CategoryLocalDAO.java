package DAL.DAO.FILE;

import BE.Playlist;
import BE.Movie;
import BLL.CategoryManager;
import DAL.DAO.CategoryDAOInterface;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryLocalDAO implements CategoryDAOInterface {
    private CategoryManager categoryManager;
    private static final int PLAYLISTNAMESIZE=100;
    private static final String emptyValue=String.format("%-" + PLAYLISTNAMESIZE + "s",-1);
    private static final int emptyIntValue=-1;
    private static final String LOCAL_PLAYLIST_PATH = "Data/localPlaylists.data";
    private static final String LOCAL_PLAYLIST_SONG = "Data/localPlaylist_song.data";

    @Override
    public void setPlaylistManager(CategoryManager categoryManager) {
        this.categoryManager = categoryManager;
    }

    /**
     * Tries to make a playlist with the given name.
     *
     * @param   name the name of the playlist.
     * @throws  IOException if something went wrong.
     */
    @Override
    public void createPlaylist(String name) throws IOException {
        String formattedName = String.format("%-" + PLAYLISTNAMESIZE + "s",name).substring(0,PLAYLISTNAMESIZE);
        try(RandomAccessFile raf = new RandomAccessFile(new File(LOCAL_PLAYLIST_PATH),"rw")){
            while(raf.getFilePointer()<raf.length()){
                StringBuilder playlistName = new StringBuilder();
                raf.skipBytes(4);
                for(int i=0;i<PLAYLISTNAMESIZE;i++){
                playlistName.append(raf.readChar());
                if(playlistName.toString().equals(emptyValue)){
                    raf.seek(raf.getFilePointer()-PLAYLISTNAMESIZE*2);
                    raf.writeChars(formattedName);
                    return;
                }
                }
            }
            raf.seek(raf.getFilePointer()-(PLAYLISTNAMESIZE*2)-4);
            int index = raf.readInt()+1;
            raf.seek(raf.length());
            raf.writeInt(index);
            raf.writeChars(formattedName);
        }
    }

    /**
     * Tries to load playlists, ignores playlists with emptyValue, creates file if the file does not exist.
     * makes sure there is a file to overwrite, when creating a playlist.
     *
     * @return  A list of playlists, an empty list if no playlists exist.
     * @throws  IOException if something went wrong.
     */
    @Override
    public List<Playlist> loadPlaylist() throws IOException {
        List<Playlist> tmp = new ArrayList<>();
        try(RandomAccessFile raf = new RandomAccessFile(new File(LOCAL_PLAYLIST_PATH),"rw")){
            if(raf.length()==0) {
            raf.writeInt(1);
            raf.writeChars(emptyValue);
            raf.seek(0);}
            while(raf.getFilePointer()<raf.length()){
                int playlistId=raf.readInt();
                StringBuilder playlistName= new StringBuilder();
                for(int i=0;i<PLAYLISTNAMESIZE;i++){
                    playlistName.append(raf.readChar());
                }
                if(!playlistName.toString().equals(emptyValue))
                tmp.add(new Playlist(playlistId, playlistName.toString().trim()));
            }
            return tmp;
        }
    }

    /**
     * Tries to get a playlist.
     *
     * @param   name The name of the playlist.
     * @return  a playlist or null if none found.
     * @throws  IOException if something went wrong.
     */
    @Override
    public Playlist getPlaylist(String name) throws IOException {
        String formattedName = String.format("%-" + PLAYLISTNAMESIZE + "s",name);
        try(RandomAccessFile raf = new RandomAccessFile(new File(LOCAL_PLAYLIST_PATH),"r")){
            while(raf.getFilePointer()<raf.length()){
                int playlistId=raf.readInt();
                StringBuilder playlistName= new StringBuilder();
                for(int i=0;i<PLAYLISTNAMESIZE;i++){
                    playlistName.append(raf.readChar());
                }
                if(playlistName.toString().equals(formattedName))
                    return new Playlist(playlistId, playlistName.toString());
            }
            return null;
        }
    }

    /**
     * Tries to overwrite a playlist with emptyValue, and deletes songs all songs from the playlist.
     *
     * @param   playlist the playlist.
     * @throws  IOException if something went wrong.
     */
    @Override
    public void deletePlaylist(Playlist playlist) throws IOException {
        try(RandomAccessFile raf = new RandomAccessFile(new File(LOCAL_PLAYLIST_PATH),"rw")){
            while(raf.getFilePointer()<raf.length()){
                if(raf.readInt()==playlist.getPlaylistId()){
                    raf.writeChars(emptyValue);
                    break;
                }
            }
        }
        deleteAllFromPlaylist(playlist.getPlaylistId());
    }

    /**
     * Overwrites the old name, with the new modified name from the Playlist.
     *
     * @param   modified the modified playlist.
     * @throws  IOException if something went wrong.
     */
    @Override
    public void updatePlaylist(Playlist modified) throws IOException {
        String formattedName = String.format("%-" + PLAYLISTNAMESIZE + "s",modified.getPlayListName()).substring(0,PLAYLISTNAMESIZE);
        try(RandomAccessFile raf = new RandomAccessFile(new File(LOCAL_PLAYLIST_PATH),"rw")){
            while(raf.getFilePointer()<raf.length()){
                if(raf.readInt()==modified.getPlaylistId()){
                    raf.writeChars(formattedName);
                    break;
                }
            }
        }
    }

    /**
     * Tries to load songs from the playlist with playlist id.
     *
     * @param   playlist_id the id of the playlist you want to load.
     * @return  A list of Songs in the Playlist, a empty list if there and no songs in the playlist.
     * @throws  IOException if something when wrong.
     */
    @Override
    public List<Movie> loadSongsFromPlaylist(int playlist_id) throws Exception {
        File file = new File(LOCAL_PLAYLIST_SONG);
        MovieLocalDAO songLocalDAO = new MovieLocalDAO();
        List<Movie> tmp = new ArrayList<>();
        try(RandomAccessFile raf = new RandomAccessFile(file,"r")){
            while (raf.getFilePointer()<raf.length()) {
                int playlistId=raf.readInt();
                if(playlistId==playlist_id)
                    tmp.add(songLocalDAO.getSong(raf.readInt()));
                else
                    raf.skipBytes(4);
            }
            return tmp;
        }
        catch (FileNotFoundException e){
            file.createNewFile();
            return tmp;
        }
    }

    /**
     * Tries to add a song to a playlist, if it finds an emptyIntValue, it overwrites instead of writing at the file end.
     *
     * @param   playlist_id the id of the playlist
     * @param   song_id the id of the song
     * @throws  IOException if something went wrong.
     */
    @Override
    public void AddSongToPlaylist(int playlist_id, int song_id) throws IOException {
        File file = new File(LOCAL_PLAYLIST_SONG);
        try(RandomAccessFile raf = new RandomAccessFile(file,"rw")){
            while(raf.getFilePointer()<raf.length()){
                if(raf.readInt()==emptyIntValue){
                    raf.seek(raf.getFilePointer()-4);
                    raf.writeInt(playlist_id);
                    raf.writeInt(song_id);
                    return;
                }
                else raf.skipBytes(4);
            }
            raf.seek(raf.length());
            raf.writeInt(playlist_id);
            raf.writeInt(song_id);
        }
    }

    /**
     * Overwrites a song and playlist id with emptyIntValue.
     *
     * @param   playlist_id the id of the playlist
     * @param   song_id the id of the song
     * @throws  IOException if something went wrong.
     */
    @Override
    public void deleteFromPlaylist(int playlist_id, int song_id) throws IOException {
        try(RandomAccessFile raf = new RandomAccessFile(new File(LOCAL_PLAYLIST_SONG),"rw")){
            while (raf.getFilePointer()<raf.length()){
                int playlistId = raf.readInt();
                int songId=raf.readInt();
                if(playlistId==playlist_id && songId==song_id){
                    raf.seek(raf.getFilePointer()-8);
                    raf.writeInt(emptyIntValue);
                    raf.writeInt(emptyIntValue);
                    break;
                }
        }
    }
    }

    /**
     * Tries to overwrite all matches of playlist_id with emptyIntValue
     *
     * @param   playlist_id the id of the playlist you want to clear of songs.
     * @throws  IOException if something went wrong.
     */
    private void deleteAllFromPlaylist(int playlist_id) throws IOException {
        try(RandomAccessFile raf = new RandomAccessFile(new File(LOCAL_PLAYLIST_SONG),"rw")){
            while (raf.getFilePointer()<raf.length()){
                if(raf.readInt()==playlist_id){
                    raf.seek(raf.getFilePointer()-4);
                    raf.writeInt(emptyIntValue);
                    raf.writeInt(emptyIntValue);
                }
                else raf.skipBytes(4);
            }
        }
    }
}