package nearestNeigh;

import java.util.*;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;

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

    private void insertIntoList(ArrayList<Point> list, Point point, HashMap<String, Double> distance, int lengthLimit){
        list.add(point);
        for (int i = list.size() - 1; i > 0; i--){
            if (distance.get(list.get(i - 1).id) > distance.get(point.id)){
                list.set(i, list.get(i - 1));

                if (i == 1){
                    list.set(0, point);
                }
            }else{
                list.set(i, point);
                break;
            }
        }
        if (list.size() > lengthLimit){
            list.remove(list.size() - 1);
        }
    }


    @Override
    public List<Point> search(Point searchTerm, int k) {
        // initialize result list and distances hashmap
        HashMap<String, Double> distances = new HashMap<>();
        ArrayList<Point> resultList = new ArrayList<>();

        // go through the pointsMap
        for (Point point : pointsMap.values()) {
            if (point.cat == searchTerm.cat){
                distances.put(point.id, searchTerm.distTo(point));
                insertIntoList(resultList, point, distances, k);
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
            pointsMap.remove(point.id);
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
