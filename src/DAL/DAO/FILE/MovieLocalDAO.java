package DAL.DAO.FILE;

import BE.Movie;
import BLL.MovieManager;
import DAL.DAO.MovieDAOInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MovieLocalDAO implements MovieDAOInterface {
    private MovieManager movieManager;
    private static final String LOCAL_MOVIE_PATH = "Data/localMovies.data";
    private static final String LOCAL_CATEGORY_MOVIE = "Data/localCategory_movie.data";
    private static final int MOVIE_NAME_SIZE = 100;
    private static final int MOVIE_PATH_SIZE = 150;
    private static final int MOVIE_RATING_SIZE = 100;
    private static final int emptyIntValue = -1;
    private static final String emptyNameValue = String.format("%-" + MOVIE_NAME_SIZE + "s", emptyIntValue);
    private static final String emptyPathValue = String.format("%-" + MOVIE_PATH_SIZE + "s", emptyIntValue);
    private static final String emptyRatingValue = String.format("%-" + MOVIE_RATING_SIZE + "s", emptyIntValue);

    /**
     * sets the movie manager.
     *
     * @param movieManager
     */
    @Override
    public void setMovieManager(MovieManager movieManager) {
        this.movieManager = movieManager;
    }

    /**
     * Loads all movies in the files, makes sure the movies are not equal to the emptyValue
     *
     * @return A list of movies if there are any in the file or a empty list if there are no movies in the file.
     * @throws IOException if something went wrong.
     */
    @Override
    public List<Movie> loadMovies() throws Exception {
        File file = new File(LOCAL_MOVIE_PATH);
        List<Movie> tmp = new ArrayList<>();
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            while (raf.getFilePointer() < raf.length()) {
                int movie_id = raf.readInt();
                StringBuilder movieName = new StringBuilder();
                StringBuilder path = new StringBuilder();
                StringBuilder rating = new StringBuilder();
                for (int i = 0; i < MOVIE_NAME_SIZE; i++)
                    movieName.append(raf.readChar());
                for (int i = 0; i < MOVIE_PATH_SIZE; i++)
                    path.append(raf.readChar());
                for (int i = 0; i < MOVIE_RATING_SIZE; i++)
                    rating.append(raf.readChar());
                int category_id = raf.readInt();
                if (!movieName.toString().equals(emptyNameValue) && !path.toString().equals(emptyPathValue))
                    tmp.add(new Movie(movie_id, movieName.toString().trim(), rating.toString().trim(), path.toString().trim(), category_id, getCategories().get(category_id)));
            }
            return tmp;
        } catch (FileNotFoundException e) {
            file.createNewFile();
            return tmp;
        }
    }

    /**
     * Tries to create a movie, overwrites empty values if such exist. Auto increments and adds movie if no emptyValues found.
     *
     * @param movie the movie.
     * @throws IOException if something went wrong.
     */
    @Override
    public int createMovie(Movie movie) throws IOException {
        String formattedName = String.format("%-" + MOVIE_NAME_SIZE + "s", movie.getTitle()).substring(0, MOVIE_NAME_SIZE);
        String formattedPath = String.format("%-" + MOVIE_PATH_SIZE + "s", movie.getFilePath()).substring(0, MOVIE_PATH_SIZE);
        String formattedRating = String.format("%-" + MOVIE_RATING_SIZE + "s", movie.getRating() == null ? "" : movie.getRating()).substring(0, MOVIE_RATING_SIZE);
        try (RandomAccessFile raf = new RandomAccessFile(new File(LOCAL_MOVIE_PATH), "rw")) {
            if (raf.length() == 0) {
                raf.writeInt(1);
                raf.writeChars(emptyNameValue);
                raf.writeChars(emptyPathValue);
                raf.writeChars(emptyRatingValue);
                raf.writeInt(emptyIntValue);
                raf.seek(0);
            }
            while (raf.getFilePointer() < raf.length()) {
                StringBuilder movieName = new StringBuilder();
                raf.skipBytes(4);
                for (int i = 0; i < MOVIE_NAME_SIZE; i++)
                    movieName.append(raf.readChar());
                if (movieName.toString().equals(emptyNameValue)) {
                    raf.seek(raf.getFilePointer() - MOVIE_NAME_SIZE * 2 - 4);
                    var movie_id = raf.readInt();
                    raf.writeChars(formattedName);
                    raf.writeChars(formattedPath);
                    raf.writeChars(formattedRating);
                    raf.writeInt(movie.getCategoryId());
                    return movie_id;
                } else raf.skipBytes(MOVIE_PATH_SIZE * 2 + MOVIE_RATING_SIZE * 2 + 4);
            }
            raf.seek(raf.length() - (MOVIE_NAME_SIZE * 2) - (MOVIE_PATH_SIZE * 2) - (MOVIE_RATING_SIZE * 2) - 8);
            int index = raf.readInt() + 1;
            raf.seek(raf.length());
            raf.writeInt(index);
            raf.writeChars(formattedName);
            raf.writeChars(formattedPath);
            raf.writeChars(formattedRating);
            raf.writeInt(movie.getCategoryId());
            return index;
        }
    }

    /**
     * Finds a movie in the file.
     *
     * @param name the name of the movie you want to get
     * @return A movie that has the given name.
     * @throws IOException if something went wrong.
     */
    @Override
    public Movie getMovie(String name) throws Exception {
        try (RandomAccessFile raf = new RandomAccessFile(new File(LOCAL_MOVIE_PATH), "r")) {
            while (raf.getFilePointer() < raf.length()) {
                int movie_id = raf.readInt();
                StringBuilder movieName = new StringBuilder();
                StringBuilder path = new StringBuilder();
                StringBuilder rating = new StringBuilder();
                for (int i = 0; i < MOVIE_NAME_SIZE; i++)
                    movieName.append(raf.readChar());
                for (int i = 0; i < MOVIE_PATH_SIZE; i++)
                    path.append(raf.readChar());
                for (int i = 0; i < MOVIE_RATING_SIZE; i++)
                    rating.append(raf.readChar());
                int category_id = raf.readInt();
                if (movieName.toString().trim().equals(name))
                    return new Movie(movie_id, movieName.toString().trim(), rating.toString().trim(), path.toString().trim(), category_id, getCategories().get(category_id));
            }
            return null;
        }
    }

    /**
     * Finds a movie in the file.
     *
     * @param id the id of the movie you want to get
     * @return A movie that has the given name.
     * @throws IOException if something went wrong.
     */
    public Movie getMovie(int id) throws Exception {
        try (RandomAccessFile raf = new RandomAccessFile(new File(LOCAL_MOVIE_PATH), "r")) {
            while (raf.getFilePointer() < raf.length()) {
                int movie_id = raf.readInt();
                StringBuilder movieName = new StringBuilder();
                StringBuilder path = new StringBuilder();
                StringBuilder rating = new StringBuilder();
                for (int i = 0; i < MOVIE_NAME_SIZE; i++)
                    movieName.append(raf.readChar());
                for (int i = 0; i < MOVIE_PATH_SIZE; i++)
                    path.append(raf.readChar());
                for (int i = 0; i < MOVIE_RATING_SIZE; i++)
                    rating.append(raf.readChar());
                int category_id = raf.readInt();
                if (movie_id == id)
                    return new Movie(movie_id, movieName.toString().trim(), rating.toString().trim(), path.toString().trim(), category_id, getCategories().get(category_id));
            }
            return null;
        }
    }

    /**
     * Overwrites a movie with matching id with emptyValues. Also overwrites the movie matches from categories with emptyIntValue
     *
     * @param id the id of the movie you want to delete.
     * @throws IOException if something went wrong.
     */
    @Override
    public void deleteMovie(int id) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(new File(LOCAL_MOVIE_PATH), "rw")) {
            while (raf.getFilePointer() < raf.length()) {
                if (raf.readInt() == id) {
                    raf.writeChars(emptyNameValue);
                    raf.writeChars(emptyPathValue);
                    raf.writeChars(emptyRatingValue);
                    raf.writeInt(emptyIntValue);
                } else raf.skipBytes(MOVIE_NAME_SIZE * 2 + MOVIE_PATH_SIZE * 2 + MOVIE_RATING_SIZE * 2 + 4);
            }
        }

        try (RandomAccessFile raf = new RandomAccessFile(new File(LOCAL_CATEGORY_MOVIE), "rw")) {
            while (raf.getFilePointer() < raf.length()) {
                raf.skipBytes(4);
                if (raf.readInt() == id) {
                    raf.seek(raf.getFilePointer() - 8);
                    raf.writeInt(emptyIntValue);
                    raf.writeInt(emptyIntValue);
                }
            }
        }
    }

    /**
     * Overwrites the movie with the new values in modified.
     *
     * @param modified the modified movie
     * @throws IOException if something went wrong.
     */
    @Override
    public void updateMovie(Movie modified) throws IOException {
        String formattedName = String.format("%-" + MOVIE_NAME_SIZE + "s", modified.getTitle()).substring(0, MOVIE_NAME_SIZE);
        String formattedPath = String.format("%-" + MOVIE_PATH_SIZE + "s", modified.getFilePath()).substring(0, MOVIE_PATH_SIZE);
        String formattedArtist = String.format("%-" + MOVIE_RATING_SIZE + "s", modified.getRating()).substring(0, MOVIE_RATING_SIZE);
        try (RandomAccessFile raf = new RandomAccessFile(new File(LOCAL_MOVIE_PATH), "rw")) {
            while (raf.getFilePointer() < raf.length()) {
                if (raf.readInt() == modified.getId()) {
                    raf.writeChars(formattedName);
                    raf.writeChars(formattedPath);
                    raf.writeChars(formattedArtist);
                    raf.writeInt(modified.getId());
                } else raf.skipBytes(MOVIE_NAME_SIZE * 2 + MOVIE_PATH_SIZE * 2 + MOVIE_RATING_SIZE * 2 + 4);
            }
        }
    }

    /**
     * Searches for a movie in the file
     *
     * @param searchQuery the string you are searching for
     * @return A list of movies containing all matches, a empty list if no matches.
     * @throws IOException if something went wrong.
     */
    @Override
    public List<Movie> searchMovie(String searchQuery) throws Exception {
        if (searchQuery.isEmpty())
            return loadMovies();
        List<Movie> tmp = new ArrayList<>();
        try (RandomAccessFile raf = new RandomAccessFile(new File(LOCAL_MOVIE_PATH), "rw")) {
            while (raf.getFilePointer() < raf.length()) {
                int movie_id = raf.readInt();
                StringBuilder movieName = new StringBuilder();
                StringBuilder path = new StringBuilder();
                StringBuilder rating = new StringBuilder();
                for (int i = 0; i < MOVIE_NAME_SIZE; i++)
                    movieName.append(raf.readChar());
                for (int i = 0; i < MOVIE_PATH_SIZE; i++)
                    path.append(raf.readChar());
                for (int i = 0; i < MOVIE_RATING_SIZE; i++)
                    rating.append(raf.readChar());
                int category_id = raf.readInt();
                if (movieName.toString().trim().toLowerCase().contains(searchQuery.trim().toLowerCase()) || path.toString().trim().toLowerCase().contains(searchQuery.trim().toLowerCase()))
                    tmp.add(new Movie(movie_id, movieName.toString().trim(), rating.toString().trim(), path.toString().trim(), category_id, getCategories().get(category_id)));
            }
            return tmp;
        }
    }

    /**
     * gets the map of genres
     *
     * @return a map of genres
     * @throws Exception if something went wrong
     */
    @Override
    public Map<Integer, String> getCategories() throws Exception {
        File file = new File("Data/localCategory.data");
        Map<Integer, String> tmp = new HashMap<Integer, String>();
        tmp.put(-1, "");
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            while(raf.getFilePointer()<raf.length()){
            StringBuilder categoryName = new StringBuilder();
            for(int i = 0 ; i<100 ; i++)
            categoryName.append(raf.readChar());
            tmp.put(raf.readInt(), categoryName.toString().trim());
        }
        }catch (FileNotFoundException e){
            file.createNewFile();
        }
        return tmp;
    }

    /**
     * Gets all movies that's older than 2 years with a rating below 6.
     *
     * @return The old movies.
     */
    public List<Movie> getOldMovies() {
        List<Movie> resultMovies = new ArrayList<>();
        try {
            List<Movie> temp = new ArrayList<>();
            if (temp.size() > 0) {
                String pattern = "dd/MM/yyyy  HH:mm:ss";
                var dateFormatter = new SimpleDateFormat(pattern);
                var currentDate = new Date();
                var currentYear = currentDate.getYear();

                for (int i = 0; i < temp.size(); i++) {
                    var movie = temp.get(i);
                    var lastView = movie.getLastView();
                    var lastViewDate = dateFormatter.parse(lastView);
                    var lastViewYear = lastViewDate.getYear();
                    var rating = Double.parseDouble(movie.getRating());
                    if (rating < 6 && lastViewYear + 2 < currentYear) {
                        if (!resultMovies.contains(movie)) resultMovies.add(movie);
                        System.out.println(String.format("Movie: %s is over two years old!", movie.getTitle()));
                    }
                }
            }
            return resultMovies;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}