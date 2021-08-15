package nearestNeigh;

import java.util.List;

/**
 * Interface for nearest neighbour approaches.
 *
 * 
 */
public interface NearestNeigh {

    /**
     * construct the data structure to store the nodes
     * @param nodes to be stored
     */
    void buildIndex(List<Point> points);

    /**
     * search for nearby points
     * @param searchTerm The query point which passes information such as category and location
     * @return a list of point as search result
     */
    List<Point> search(Point searchTerm, int k);

    /**
     * add a point to the data structure
     * @param point to be added
     * @return whether succeeded, e.g. return false when point is already in the data structure
     */
    boolean addPoint(Point point);

    /**
     * delete a point from data structure. Be aware that even when the object are not in the data structure, the identical point in th data structure should be deleted
     * @param point to be deleted
     * @return whether succeeded, e.g. return false when point not found
     */
    boolean deletePoint(Point point);

    /**
     * Check whether the point is in the index
     * @param point to be checked
     * @return true if point is in
     */
    boolean isPointIn(Point point);

}
