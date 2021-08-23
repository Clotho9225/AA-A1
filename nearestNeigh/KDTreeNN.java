package nearestNeigh;

import javax.swing.*;
import javax.xml.soap.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class is required to be implemented.  Kd-tree implementation.
 *
 * 
 */
class KDNode{
    String id;
    String leftChildId;
    String rightChildId;

    public KDNode() {
        String id = null;
        leftChildId = null;
        rightChildId = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLeftChildId() {
        return leftChildId;
    }

    public void setLeftChildId(String leftChildId) {
        this.leftChildId = leftChildId;
    }

    public String getRightChildId() {
        return rightChildId;
    }

    public void setRightChildId(String rightChildId) {
        this.rightChildId = rightChildId;
    }
}

public class KDTreeNN implements NearestNeigh{
    // initialize an empty hashMap, the key is the number of the id, value is the point
    // easy for searching
    HashMap<String, Point> pointsMap;
    ArrayList<KDNode> kdTree;

    public KDTreeNN() {
        this.pointsMap =  new HashMap<>();
        this.kdTree = new ArrayList<>();
    }

    private ArrayList<KDNode> buildKdTree(ArrayList<KDNode> kdTree, HashMap<String, Point> pointsMap){
        // to be
        // 先看x纬度，找到x的中位数所在的点作为root点 （点两边的数量平均）
        // 再看y 循环
        // bool 控制纬度，0 - x， 1 - y

        return kdTree;
    }

    @Override
    public void buildIndex(List<Point> points) {
        // To be implemented.

        // add elements into the map
        for ( Point point : points ) {
            pointsMap.put(point.id, point);
        }



        kdTree = buildKdTree(kdTree, pointsMap);
    }

    @Override
    public List<Point> search(Point searchTerm, int k) {
        // To be implemented.

        return new ArrayList<Point>();
    }

    @Override
    public boolean addPoint(Point point) {
        // To be implemented.
        return false;
    }

    @Override
    public boolean deletePoint(Point point) {
        // To be implemented.
        return false;
    }

    @Override
    public boolean isPointIn(Point point) {
        // To be implemented.
        return false;
    }

}
