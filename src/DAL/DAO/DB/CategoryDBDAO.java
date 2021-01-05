package DAL.DAO.DB;

import BE.Category;
import BE.Movie;
import BLL.CategoryManager;
import DAL.DAO.CategoryDAOInterface;
import DAL.DB.DbConnectionHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDBDAO implements CategoryDAOInterface {
    protected DbConnectionHandler database;
    protected CategoryManager categoryManager;

    /**
     * Sets the manager.
     *
     * @param categoryManager the current instance of the manager.
     */
    @Override
    public void setCategoryManager(CategoryManager categoryManager) {
        this.categoryManager = categoryManager;
    }

    /**
     * Tries to connect to the database.
     */
    public CategoryDBDAO() throws SQLException {
        database = DbConnectionHandler.getInstance();
        if (database.getConnection().isClosed()) {
            throw new SQLException("no connection to database");
        }
    }

    /**
     * Tries to load the movies from the database.
     *
     * @return A list of the movies in the database or a empty list if the database has no songs.
     * @throws SQLException if it cant get connection to the database or something went wrong.
     */
    @Override
    public List<Category> loadCategories() throws SQLException {
        var temp = new ArrayList<Category>();

        try (var con = database.getConnection();
             Statement statement = con.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT * FROM category;");
            while (rs.next()) {
                int id = rs.getInt("category_id");
                String name = rs.getString("category_name");
                temp.add(new Category(id, name));
            }

            for (int i = 0; i < temp.size(); i++) {
                var category = temp.get(i);
                if (category != null) {
                    var totalLength = getTotalDurationOfCategory(category.getCategoryId());
                    category.setCategoryDurationProperty(totalLength);
                    category.setCategoryDurationStringProperty(totalLength);
                }
            }
            return temp;
        } catch (SQLNonTransientConnectionException e) {
            categoryManager.goLocal();
            return temp;
        }
    }

    /**
     * Tries to create a category on the database
     *
     * @param name the name of the category
     * @throws SQLException if it cant get connection to the database or something went wrong.
     */
    @Override
    public void createCategory(String name) throws SQLException {
        var sql = "";
        switch (database.getConnectionType()) {
            case 0 -> sql = "INSERT INTO [dbo].[category] ([category_name]) VALUES(?);";
            case 1 -> sql = "INSERT INTO category (cateogory_name) VALUES(?);";
        }
        try (var con = database.getConnection();
             PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, name);
            st.executeUpdate();
        } catch (SQLNonTransientConnectionException e) {
            categoryManager.goLocal();
        }
    }

    /**
     * Searches for a category on the database
     *
     * @param name the name of the category you are looking for
     * @return a category with the name
     * @throws SQLException if it cannot connect to the database or something went wrong.
     */
    @Override
    public Category getCategory(String name) throws SQLException {
        var sql = "SELECT FROM category WHERE category_name = ?;";
        try (var con = database.getConnection();
             PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, name);
            st.executeUpdate();
            var resultSet = st.getResultSet();
            var id = resultSet.getInt("category_id");
            var name1 = resultSet.getString("category_name");
            var category = new Category(id, name1);
            return category;
        } catch (SQLNonTransientConnectionException e) {
            categoryManager.goLocal();
            return null;
        }
    }

    /**
     * Tries to delete a category from the database, does nothing if a category with name doesnt exist.
     *
     * @param category the category
     * @throws SQLException if it cannot connect to the database or something went wrong.
     */
    @Override
    public void deleteCategory(Category category) throws SQLException {
        var sql = "DELETE FROM category WHERE category_name = ?;";
        try (var con = database.getConnection(); PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, category.getCategoryName());
            st.executeUpdate();
            return;
        } catch (SQLNonTransientConnectionException e) {
            categoryManager.goLocal();
        }
    }

    /**
     * Tries to load movies from a category, by looking for id matches
     *
     * @param category_id the id of the category whose songs you are looking for.
     * @return a list of songs if theres a positive match for the category, an empty category otherwise.
     * @throws SQLException if it cannot connect to the database or something went wrong.
     */
    @Override
    public List<Movie> loadMoviesFromCategory(int category_id) throws SQLException {
        var temp = new ArrayList<Movie>();
        var sql = "SELECT movie.*, category.category_name FROM category LEFT OUTER JOIN category_movie ON  category.category_id = category_movie.category_id LEFT OUTER JOIN movie ON category_movie.movie_id = movie.movie_id LEFT OUTER JOIN category ON  movie.category_id = category.category_id WHERE category.category_id = ?;";
        try (var con = database.getConnection();
             PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setInt(1, category_id);
            st.execute();
            ResultSet rs = st.getResultSet();
            while (rs.next()) {
                int movie_id = rs.getInt("movie_id");
                String movie_title = rs.getString("movie_title");
                String movie_artist = rs.getString("movie_artist");
                String movie_filepath = rs.getString("movie_filepath");
                category_id = rs.getInt("category_id");
                String category_name = rs.getString("category_name");
                if (movie_filepath != null)
                    temp.add(new Movie(movie_id, movie_title, movie_artist, movie_filepath, category_id, category_name));
            }
            return temp;
        } catch (SQLNonTransientConnectionException | NullPointerException e) {
            categoryManager.goLocal();
            return temp;
        }
    }


    /**
     * Tries to add a song to a playlist
     *
     * @param category_id the id of the playlist you want to add a song to.
     * @param movie_id     the id of the song you want to add to the playlist.
     * @throws SQLException if it cannot connect to the database or something went wrong.
     */
    @Override
    public void AddMovieToCategory(int category_id, int movie_id) throws SQLException {
        var sql = "";
        switch (database.getConnectionType()) {
            case 0 -> sql = "INSERT INTO [dbo].[category_movie] ([category_id],[movie_id]) VALUES (?,?);";
            case 1 -> sql = "INSERT INTO category_movie (category_id,movie_id) VALUES (?,?);";
        }
        try (var con = database.getConnection();
             PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setInt(1, category_id);
            st.setInt(2, movie_id);
            st.executeUpdate();
        } catch (SQLNonTransientConnectionException e) {
            categoryManager.goLocal();
        }
    }

    /**
     * Tries to delete a movie with movie_id from a category in the database, does nothing if no match found.
     *
     * @param category_id the id of the category you want to remove a movie from.
     * @param movie_id     the id of the movie you want to remove from the category.
     * @throws SQLException if it cannot connect to the database or something went wrong.
     */
    @Override
    public void deleteFromCategory(int category_id, int movie_id) throws SQLException {
        var sql = "DELETE FROM category_movie WHERE category_id=? AND movie_id=?;";
        try (var con = database.getConnection();
             PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setInt(1, category_id);
            st.setInt(2, movie_id);
            st.executeUpdate();
        } catch (SQLNonTransientConnectionException | NullPointerException e) {
            categoryManager.goLocal();
        }
    }


    /**
     * Changes the name of the category if a match is found.
     *
     * @param category a Category with the new name, and the original id.
     * @throws SQLException if it cannot connect to the database or something went wrong.
     */
    @Override
    public void updateCategory(Category category) throws SQLException {
        String sql = "UPDATE playlist SET playlist_name=? WHERE playlist_id=?;";
        try (var con = database.getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(sql)) {
            preparedStatement.setString(1, category.getCategoryName());
            preparedStatement.setInt(2, category.getCategoryId());
            if (preparedStatement.executeUpdate() != 1) {
                System.out.println("Could not update playlist: " + category.toString());
            }
        } catch (SQLNonTransientConnectionException | NullPointerException e) {
            categoryManager.goLocal();
        }
    }

    /**
     * Get the total duration of a given category.
     *
     * @param category the category
     * @return the total duration
     * @throws SQLException if something went wrong.
     */
    public double getTotalDurationOfCategory(Category category) throws SQLException {
        String sql = "SELECT movie.*, category.category_name FROM category LEFT OUTER JOIN category_movie ON  category.category_id = category_movie.categoru_id LEFT OUTER JOIN movie ON category_movie.movie_id = movie.movie_id LEFT OUTER JOIN category ON  movie.category_id = category.category_id WHERE category.category_id = ?;";
        double totalDuration = 0;
        try (var con = database.getConnection();
             PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setInt(1, category.getCategoryId());
            st.execute();

            ResultSet rs = st.getResultSet();
            while (rs.next()) {
                double movie_length = rs.getDouble("movie_length");
                totalDuration += movie_length;
            }

            return totalDuration;
        } catch (SQLNonTransientConnectionException | NullPointerException e) {
            categoryManager.goLocal();
            return 0;
        }
    }


    /**
     * Get the total duration of a given category id.
     *
     * @param category_id the id of the category
     * @return the total duration
     * @throws SQLException if something went wrong.
     */
    public double getTotalDurationOfCategory(int category_id) throws SQLException {
        String sql = "SELECT movie.*, category.category_name FROM category LEFT OUTER JOIN category_movie ON  category.category_id = category_movie.category_id LEFT OUTER JOIN movie ON category_movie.movie_id = movie.movie_id LEFT OUTER JOIN category ON  movie.category_id = category.category_id WHERE category.category_id = ?;";
        double totalDuration = 0;
        try (var con = database.getConnection();
             PreparedStatement st = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setInt(1, category_id);
            st.execute();

            ResultSet rs = st.getResultSet();
            while (rs.next()) {
                double movie_length = rs.getDouble("movie_length");
                totalDuration += movie_length;
            }

            return totalDuration;
        } catch (SQLNonTransientConnectionException | NullPointerException e) {
            categoryManager.goLocal();
            return 0;
        }
    }
}