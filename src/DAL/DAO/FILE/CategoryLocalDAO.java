package DAL.DAO.FILE;

import BE.Category;
import BE.Movie;
import BLL.CategoryManager;
import DAL.DAO.CategoryDAOInterface;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryLocalDAO implements CategoryDAOInterface {
    private CategoryManager categoryManager;
    private static final int CATEGORYNAMESIZE = 100;
    private static final String emptyValue = String.format("%-" + CATEGORYNAMESIZE + "s", -1);
    private static final int emptyIntValue = -1;
    private static final String LOCAL_CATEGORY_PATH = "Data/localPlaylists.data";
    private static final String LOCAL_CATEGORY_MOVIE = "Data/localPlaylist_song.data";

    @Override
    public void setCategoryManager(CategoryManager categoryManager) {
        this.categoryManager = categoryManager;
    }

    /**
     * Tries to make a playlist with the given name.
     *
     * @param name the name of the playlist.
     * @throws IOException if something went wrong.
     */
    @Override
    public void createCategory(String name) throws IOException {
        String formattedName = String.format("%-" + CATEGORYNAMESIZE + "s", name).substring(0, CATEGORYNAMESIZE);
        try (RandomAccessFile raf = new RandomAccessFile(new File(LOCAL_CATEGORY_PATH), "rw")) {
            while (raf.getFilePointer() < raf.length()) {
                StringBuilder categoryName = new StringBuilder();
                raf.skipBytes(4);
                for (int i = 0; i < CATEGORYNAMESIZE; i++) {
                    categoryName.append(raf.readChar());
                    if (categoryName.toString().equals(emptyValue)) {
                        raf.seek(raf.getFilePointer() - CATEGORYNAMESIZE * 2);
                        raf.writeChars(formattedName);
                        return;
                    }
                }
            }
            raf.seek(raf.getFilePointer() - (CATEGORYNAMESIZE * 2) - 4);
            int index = raf.readInt() + 1;
            raf.seek(raf.length());
            raf.writeInt(index);
            raf.writeChars(formattedName);
        }
    }

    /**
     * Tries to load categories, ignores playlists with emptyValue, creates file if the file does not exist.
     * makes sure there is a file to overwrite, when creating a playlist.
     *
     * @return A list of categories, an empty list if no categories exist.
     * @throws IOException if something went wrong.
     */
    @Override
    public List<Category> loadCategories() throws IOException {
        List<Category> tmp = new ArrayList<>();
        try (RandomAccessFile raf = new RandomAccessFile(new File(LOCAL_CATEGORY_PATH), "rw")) {
            if (raf.length() == 0) {
                raf.writeInt(1);
                raf.writeChars(emptyValue);
                raf.seek(0);
            }
            while (raf.getFilePointer() < raf.length()) {
                int categoryId = raf.readInt();
                StringBuilder categoryName = new StringBuilder();
                for (int i = 0; i < CATEGORYNAMESIZE; i++) {
                    categoryName.append(raf.readChar());
                }
                if (!categoryName.toString().equals(emptyValue))
                    tmp.add(new Category(categoryId, categoryName.toString().trim()));
            }
            return tmp;
        }
    }

    /**
     * Tries to get a category.
     *
     * @param name The name of the category.
     * @return a category or null if none found.
     * @throws IOException if something went wrong.
     */
    @Override
    public Category getCategory(String name) throws IOException {
        String formattedName = String.format("%-" + CATEGORYNAMESIZE + "s", name);
        try (RandomAccessFile raf = new RandomAccessFile(new File(LOCAL_CATEGORY_PATH), "r")) {
            while (raf.getFilePointer() < raf.length()) {
                int categoryId = raf.readInt();
                StringBuilder categoryName = new StringBuilder();
                for (int i = 0; i < CATEGORYNAMESIZE; i++) {
                    categoryName.append(raf.readChar());
                }
                if (categoryName.toString().equals(formattedName))
                    return new Category(categoryId, categoryName.toString());
            }
            return null;
        }
    }

    /**
     * Tries to overwrite a category with emptyValue, and deletes songs all songs from the category.
     *
     * @param category the playlist.
     * @throws IOException if something went wrong.
     */
    @Override
    public void deleteCategory(Category category) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(new File(LOCAL_CATEGORY_PATH), "rw")) {
            while (raf.getFilePointer() < raf.length()) {
                if (raf.readInt() == category.getCategoryId()) {
                    raf.writeChars(emptyValue);
                    break;
                }
            }
        }
        deleteAllFromCategory(category.getCategoryId());
    }

    /**
     * Overwrites the old name, with the new modified name from the category.
     *
     * @param modified the modified category.
     * @throws IOException if something went wrong.
     */
    @Override
    public void updateCategory(Category modified) throws IOException {
        String formattedName = String.format("%-" + CATEGORYNAMESIZE + "s", modified.getCategoryName()).substring(0, CATEGORYNAMESIZE);
        try (RandomAccessFile raf = new RandomAccessFile(new File(LOCAL_CATEGORY_PATH), "rw")) {
            while (raf.getFilePointer() < raf.length()) {
                if (raf.readInt() == modified.getCategoryId()) {
                    raf.writeChars(formattedName);
                    break;
                }
            }
        }
    }

    /**
     * Tries to load songs from the playlist with category id.
     *
     * @param category_id the id of the category you want to load.
     * @return A list of Movies in the Category, a empty list if there and no songs in the category.
     * @throws IOException if something when wrong.
     */
    @Override
    public List<Movie> loadMoviesFromCategory(int category_id) throws Exception {
        File file = new File(LOCAL_CATEGORY_MOVIE);
        MovieLocalDAO movieLocalDAO = new MovieLocalDAO();
        List<Movie> tmp = new ArrayList<>();
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            while (raf.getFilePointer() < raf.length()) {
                int categoryId = raf.readInt();
                if (categoryId == category_id)
                    tmp.add(movieLocalDAO.getMovie(raf.readInt()));
                else
                    raf.skipBytes(4);
            }
            return tmp;
        } catch (FileNotFoundException e) {
            file.createNewFile();
            return tmp;
        }
    }

    /**
     * Tries to add a song to a category, if it finds an emptyIntValue, it overwrites instead of writing at the file end.
     *
     * @param category_id the id of the category
     * @param movie_id    the id of the movie
     * @throws IOException if something went wrong.
     */
    @Override
    public void AddMovieToCategory(int category_id, int movie_id) throws IOException {
        File file = new File(LOCAL_CATEGORY_MOVIE);
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            while (raf.getFilePointer() < raf.length()) {
                if (raf.readInt() == emptyIntValue) {
                    raf.seek(raf.getFilePointer() - 4);
                    raf.writeInt(category_id);
                    raf.writeInt(movie_id);
                    return;
                } else raf.skipBytes(4);
            }
            raf.seek(raf.length());
            raf.writeInt(category_id);
            raf.writeInt(movie_id);
        }
    }

    /**
     * Overwrites a song and category id with emptyIntValue.
     *
     * @param category_id the id of the category
     * @param movie_id    the id of the movie
     * @throws IOException if something went wrong.
     */
    @Override
    public void deleteFromCategory(int category_id, int movie_id) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(new File(LOCAL_CATEGORY_MOVIE), "rw")) {
            while (raf.getFilePointer() < raf.length()) {
                int categoryId = raf.readInt();
                int movieId = raf.readInt();
                if (categoryId == category_id && movieId == movie_id) {
                    raf.seek(raf.getFilePointer() - 8);
                    raf.writeInt(emptyIntValue);
                    raf.writeInt(emptyIntValue);
                    break;
                }
            }
        }
    }

    /**
     * Tries to overwrite all matches of category_id with emptyIntValue
     *
     * @param category_id the id of the category you want to clear of movies.
     * @throws IOException if something went wrong.
     */
    private void deleteAllFromCategory(int category_id) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(new File(LOCAL_CATEGORY_MOVIE), "rw")) {
            while (raf.getFilePointer() < raf.length()) {
                if (raf.readInt() == category_id) {
                    raf.seek(raf.getFilePointer() - 4);
                    raf.writeInt(emptyIntValue);
                    raf.writeInt(emptyIntValue);
                } else raf.skipBytes(4);
            }
        }
    }
}