package DAL.DAO.DB;

import BE.Movie;
import BLL.MovieManager;
import DAL.DAO.MovieDAOInterface;
import DAL.DB.DbConnectionHandler;

import java.io.File;
import java.io.RandomAccessFile;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieDBDAO implements MovieDAOInterface {
    protected DbConnectionHandler database;
    protected MovieManager movieManager;

    /**
     * Sets the manager.
     *
     * @param movieManager the manager
     */
    @Override
    public void setMovieManager(MovieManager movieManager) {
        this.movieManager = movieManager;
    }

    /**
     * Tries to connect to the database.
     *
     * @throws SQLException if it cant get connection to the database or something went wrong.
     */
    public MovieDBDAO() throws SQLException {
        database = DbConnectionHandler.getInstance();
        if (database.getConnection().isClosed()) {
            throw new SQLException("no connection to database");
        }
    }

    /**
     * Tries to load all movies.
     *
     * @return all movies on the database or if there are no movies on the database an empty list.
     * @throws SQLException if it cant get connection to the database or something went wrong.
     */
    @Override
    public List<Movie> loadMovies() throws SQLException {
        List<Movie> temp = new ArrayList<>();
        try (var con = database.getConnection();
             Statement statement = con.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT movie.*, category.category_name FROM movie LEFT JOIN category ON movie.category_id = category.category_id;");

            while (rs.next()) {
                int movie_id = rs.getInt("movie_id");
                String movie_title = rs.getString("movie_title");
                String movie_filepath = rs.getString("movie_filepath");
                String movie_lastview = rs.getString("movie_lastview");
                String movie_rating = rs.getString("movie_rating");
                int category_id = rs.getInt("category_id");
                String category_name = rs.getString("category_name");
                double movie_length = rs.getDouble("movie_length");
                temp.add(new Movie(movie_id, movie_title, movie_rating, movie_filepath, movie_lastview, category_id, category_name, movie_length));
            }
            return temp;
        } catch (SQLNonTransientConnectionException | NullPointerException e) {
            movieManager.goLocal();
            return temp;
        }
    }


    /**
     * tries to create a movie.
     *
     * @param movie the movie.
     * @throws SQLException
     */
    @Override
    public int createMovie(Movie movie) throws SQLException {
        var sql = "";
        switch (database.getConnectionType()) {
            case (0) -> sql = "INSERT INTO [dbo].[movie] ([movie_title], [movie_filepath], [movie_length], [movie_rating], [category_id]) VALUES (?,?,?,?,?)";
            case (1) -> sql = "INSERT INTO movie (movie_title, movie_filepath, movie_length, movie_rating, category_id) VALUES(?,?,?,?,?);";
        }
        try (var con = database.getConnection();
             PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, movie.getTitle());
            st.setString(2, movie.getFilePath());
            st.setDouble(3, movie.getDuration());
            st.setDouble(4, Double.parseDouble(movie.getRating()));
            st.setInt(5, movie.getCategoryId());
            st.executeUpdate();
            var keys = st.getGeneratedKeys();
            keys.next();
            return keys.getInt(1);
        } catch (SQLNonTransientConnectionException | NullPointerException e) {
            movieManager.goLocal();
            return -1;
        }
    }

    /**
     * Tries to find a movie with the given name.
     *
     * @param name the name of the movie.
     * @return A movie with the name.
     * @throws SQLException if it cant get connection to the database or something went wrong.
     */
    @Override
    public Movie getMovie(String name) throws SQLException {
        var sql = "SELECT FROM movie WHERE movie_name = ?;";
        try (var con = database.getConnection();
             PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, name);
            st.executeUpdate();
            var resultSet = st.getResultSet();
            var id = resultSet.getInt("movie_id");
            var title = resultSet.getString("movie_title");
            var path = resultSet.getString("movie_filepath");
            var lastview = resultSet.getString("movie_lastview");

            var rating = resultSet.getString("movie_rating");
            var category_id = resultSet.getInt("category_id");
            var duration = resultSet.getDouble("movie_length");
            var movie = new Movie(id, title, path, lastview, rating, category_id, duration);
            return movie;
        } catch (SQLNonTransientConnectionException | NullPointerException e) {
            movieManager.goLocal();
            return null;
        }
    }

    /**
     * Tries to delete a movie with the given id.
     *
     * @param id the id of the movie.
     * @throws SQLException if it cant get connection to the database or something went wrong.
     */
    @Override
    public void deleteMovie(int id) throws SQLException {
        var sql = "DELETE FROM movie WHERE movie_id = ?;";
        try (var con = database.getConnection();
             PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLNonTransientConnectionException | NullPointerException e) {
            movieManager.goLocal();
        }
    }

    /**
     * Tries to update a movie.
     *
     * @param modified the modified version of the movie.
     * @throws SQLException if it cant get connection to the database or something went wrong.
     */
    @Override
    public void updateMovie(Movie modified) throws SQLException {
        var sql = "UPDATE movie SET movie_title = ?, movie_filepath = ?, movie_lastview = ?, movie_rating = ?, category_id=?, movie_length=? WHERE movie_id = ?;";
        try (var con = database.getConnection();
             PreparedStatement st = con.prepareStatement(sql)) {
            st.setString(1, modified.getTitle());
            st.setString(2, modified.getFilePath());
            st.setString(3, modified.getLastView());
            st.setString(4, modified.getRating());
            st.setInt(5, modified.getCategoryId());
            st.setDouble(6, modified.getDuration());
            st.setInt(7, modified.getId());
            st.executeUpdate();
        } catch (SQLNonTransientConnectionException | NullPointerException e) {
            movieManager.goLocal();
        }
    }

    /**
     * Tries to find a list of movies that contain search
     *
     * @param searchQuery the search string
     * @return A list containing movies that match, or an empty list if no movie matches.
     */
    @Override
    public List<Movie> searchMovie(String searchQuery) {
        List<Movie> resultMovies = new ArrayList<>();
        try (var connection = database.getConnection()) {
            String sql = "SELECT movie.movie_id, movie.movie_title, movie.movie_filepath, movie.movie_rating, category.category_name FROM movie LEFT JOIN category ON movie.category_id = category.category_id WHERE LOWER(movie_title) LIKE LOWER(?) OR movie.movie_id LIKE LOWER(?) OR LOWER(movie_filepath) LIKE LOWER(?) OR LOWER(movie_rating) LIKE LOWER(?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "%" + searchQuery + "%");
            preparedStatement.setString(2, "%" + searchQuery + "%");
            preparedStatement.setString(3, "%" + searchQuery + "%");
            preparedStatement.setString(4, "%" + searchQuery + "%");
            if (preparedStatement.execute()) {
                ResultSet resultSet = preparedStatement.getResultSet();
                while (resultSet.next()) {
                    var id = resultSet.getInt("movie_id");
                    var name = resultSet.getString("movie_title");
                    var rating = resultSet.getString("movie_rating");
                    var category = resultSet.getString("category_name");
                    var path = resultSet.getString("movie_filepath");
                    var movie = new Movie(id, name, rating, path, category);
                    resultMovies.add(movie);
                }
                return resultMovies;
            } else {
                System.out.println(String.format("Couldn't find the movie: %s", searchQuery));
            }
        } catch (SQLNonTransientConnectionException | NullPointerException e) {
            movieManager.goLocal();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return resultMovies;
    }

    /**
     * Gets the genreMap
     *
     * @return A map of genres
     * @throws Exception if something went wrong
     */
    @Override
    public Map<Integer, String> getCategories() throws Exception {
        HashMap<Integer, String> temp = new HashMap<>();
        try (var con = database.getConnection();
             Statement statement = con.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT * FROM category;");
            while (rs.next()) {
                int category_id = rs.getInt("category_id");
                String category_name = rs.getString("category_name");
                if (!temp.containsKey(category_id)) {
                    temp.put(category_id, category_name);
                }
                try (RandomAccessFile raf = new RandomAccessFile(new File("Data/Category.data"), "rw")) {
                    category_name = String.format("%-50s", category_name).substring(0, 50);
                    while (raf.getFilePointer() < raf.length()) {
                        int categoryId = raf.readInt();
                        String categoryName = "";
                        for (int i = 0; i < 50; i++)
                            categoryName += raf.readChar();
                        if (categoryId == category_id && !category_name.equals(categoryName)) {
                            raf.seek(raf.getFilePointer() - categoryName.length() * 2);
                            raf.writeChars(category_name);
                            break;
                        } else if (categoryId == category_id)
                            break;
                    }
                    if (raf.getFilePointer() == raf.length()) {
                        raf.writeInt(category_id);
                        raf.writeChars(category_name);
                    }
                }
            }
            return temp;
        } catch (SQLNonTransientConnectionException | NullPointerException e) {
            movieManager.goLocal();
            return temp;
        }
    }

    public List<Movie> getOldMovies() {
        List<Movie> resultMovies = new ArrayList<>();
        try {
            var temp = loadMovies();
            if (temp.size() > 0) {
                String pattern = "dd/MM/yyyy  HH:mm:ss";
                var dateFormatter = new SimpleDateFormat(pattern);
                var currentDate = new Date();
                var currentYear = currentDate.getYear();

                for (int i = 0; i < temp.size(); i++) {
                    var movie = temp.get(i);
                    var lastView = movie.getLastView();

                    // Don't do anything if no date is specified.
                    if (!lastView.isBlank()) {
                        var lastViewDate = dateFormatter.parse(lastView);
                        var lastViewYear = lastViewDate.getYear();
                        var rating = movie.getRating();
                        var properRating = rating != null && !rating.isBlank() ? Double.parseDouble(rating) : 0;
                        if (properRating < 6 && lastViewYear + 2 < currentYear) {
                            if (!resultMovies.contains(movie)) resultMovies.add(movie);
                            System.out.println(String.format("Movie: %s is over two years old!", movie.getTitle()));
                        }
                    }
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resultMovies;
    }
}
