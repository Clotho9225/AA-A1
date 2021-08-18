package nearestNeigh;

import java.util.*;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is required to be implemented.  Naive approach implementation.
 *
 * 
 */
public class NaiveNN implements NearestNeigh{

    // initialize an empty hashMap, the key is the number of the id, value is the point
    // easy for searching
    HashMap<String, Point> pointsMap;

    public NaiveNN() {
        this.pointsMap =  new HashMap<>();
    }

    @Override
    public void buildIndex(List<Point> points) {
        // To be implemented.

        // add elements into the map
        for ( Point point : points ) {
            pointsMap.put(point.id, point);
        }
    }

    @Override
    public List<Point> search(Point searchTerm, int k) {
        // To be implemented.

        // initialize empty data structure
        LinkedList<Point> resultList = new LinkedList<>();

        // calculate the distance for all points
        int j = 0;
        Point v = new Point();
        for (int index = 0; index < pointsMap.size(); index++) {
            if (pointsMap.get(index).cat.equals(searchTerm.cat)){
                if (resultList.isEmpty()){
                    resultList.add(pointsMap.get(index));
                }else{
                    Double distance = searchTerm.distTo(pointsMap.get(index));
                    v = pointsMap.get(index);
                    if (resultList.size() < k){
                        j = resultList.size() - 1;
                    }else{
                        j = k - 1;
                    }
                    Double tempDis = searchTerm.distTo(resultList.get(j));
                    while (j >= 0 && tempDis > distance){
                        resultList.set(j + 1, resultList.get(j));
                        j = j - 1;
                    }
                    resultList.set(j + 1, v);
                }
            }else{
                break;
            }
        }

        return resultList;
    }

    @Override
    public boolean addPoint(Point point) {
        // To be implemented.
        if (!pointsMap.containsValue(point)){
            pointsMap.put(point.id, point);
            return true;
        }
        return false;
    }

    @Override
    public boolean deletePoint(Point point) {
        // To be implemented.
        if (pointsMap.containsValue(point)){
            pointsMap.remove(point.id, point);
            return true;
        }
        return false;
    }

    @Override
    public boolean isPointIn(Point point) {
        // To be implemented.
        if (pointsMap.containsValue(point)){
            return true;
        }
        return false;
    }

}
