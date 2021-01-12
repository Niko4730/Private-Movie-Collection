package DAL.DAO;

import BE.Category;
import BE.Movie;
import BLL.CategoryManager;
import java.util.List;

public interface CategoryDAOInterface {

    /**
     * Sets the categoryManager
     *
     * @param categoryManager the playlistManager
     */
    void setCategoryManager(CategoryManager categoryManager);

    /**
     * Loads the category
     *
     * @return  A list of categories
     * @throws  Exception if something went wrong.
     */
    List<Category> loadCategories() throws Exception;

    /**
     * Creates a category
     *
     * @param   name the name of the category
     * @throws  Exception if something went wrong.
     */
    int createCategory(String name) throws Exception;

    /**
     * Gets a category
     *
     * @param   name the name of the category
     * @return  the requested category.
     * @throws  Exception if something went wrong.
     */

    Category getCategory(String name) throws Exception;

    /**
     * Deletes a category
     *
     * @param   category the category
     * @throws  Exception if something went wrong.
     */

    void deleteCategory(Category category) throws Exception;

    /**
     * Loads the movies on the category
     *
     * @param   category_id the id of the category
     * @return  A list of movies on the category
     * @throws  Exception if something went wrong.
     */
    List<Movie> loadMoviesFromCategory(int category_id) throws Exception;

    /**
     * Adds a movie to the category
     *
     * @param   category_id the category you want to add a movie to
     * @param   movie_id the movie you want to add
     * @throws  Exception if something went wrong.
     */

    void AddMovieToCategory(int category_id,int movie_id) throws Exception;

    /**
     * Removes a movie from the category
     *
     * @param   category_id the id of the category you want to remove the movie from
     * @param   movie_id the id of the movie you want to remove
     * @throws  Exception if something went wrong.
     */

    void deleteFromCategory(int category_id,int movie_id) throws Exception;

    /**
     * Updates a category to with new parameters
     *
     * @param   category    the modified category
     * @throws  Exception   if something went wrong.
     */
    void updateCategory(Category category) throws Exception;
}
