package BE;

import java.util.*;

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

    /**
     * Returns the character difference from one string to the other.
     * @param str1 one of the strings
     * @param str2 the other string
     * @return the difference between the strings
     */
    public int titleDiff(String str1, String str2){
        int result = 0;
        if(str1.equals(str2))
            return result;
        if(str1.contains(str2)||str2.contains(str1))
            return Math.abs(str1.length()-str2.length());
        int j=0;
            for (int i = 0; i < str1.length(); i++) {
                while (j<str1.length() && str1.toCharArray()[j] != str2.toCharArray()[i]) {
                    j++;
                    result += 1;
                }
            }
        return result;
    }
}
