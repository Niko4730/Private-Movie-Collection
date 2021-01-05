package BE;

import java.util.LinkedList;
import java.util.List;

public class ConvenientUtils {
    /**
     * Changes the position on the category
     *
     * @param listOfMovies the list of movies you want to change
     * @param movie        the movie
     * @param pos          the position
     * @return A list of movies with the new order
     */
    public List<Movie> moveInCategory(List<Movie> listOfMovies, Movie movie, int pos) {
        LinkedList<Movie> linkedMovies = new LinkedList<>(listOfMovies);
        int index = linkedMovies.indexOf(movie) + pos;
        if (linkedMovies.size() == 2) {
            linkedMovies.addLast(linkedMovies.get(0));
            linkedMovies.remove(linkedMovies.getFirst());
        }
        if (linkedMovies.size() > 2) {
            if (index < 0) {
                linkedMovies.removeFirstOccurrence(movie);
                linkedMovies.addLast(movie);
            } else if (index >= linkedMovies.size()) {
                linkedMovies.removeLastOccurrence(movie);
                linkedMovies.addFirst(movie);
            }
            if (index >= 0 && index < linkedMovies.size()) {
                linkedMovies.remove(movie);
                linkedMovies.add(index, movie);
            }
        }
        return linkedMovies;
    }
}
