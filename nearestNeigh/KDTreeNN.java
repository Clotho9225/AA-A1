package nearestNeigh;

import java.util.*;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class is required to be implemented.  Kd-tree implementation.
 *
 * 
 */
public class KDTreeNN implements NearestNeigh{
    protected static final int X_AXIS = 0;
    protected static final int Y_AXIS = 1;

    // helper method
    private static final Comparator<Point> X_COMPARATOR = new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
            if (o1.lat < o2.lat){
                return -1;
            }
            if (o1.lat > o2.lat){
                return 1;
            }
            return 0;
        }
    };

    private static final Comparator<Point> Y_COMPARATOR = new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
            if (o1.lon < o2.lon){
                return -1;
            }
            if (o1.lon > o2.lon){
                return 1;
            }
            return 0;
        }
    };

    public static class KDNode {
        private Point point;
        int depth;

        private KDNode parent = null;
        private KDNode left = null;
        private KDNode right = null;

        public KDNode() {
            this.point = null;
            this.depth = 0;
        }

        public KDNode(Point point){
            this.point = point;
            this.depth = 0;
        }

        public KDNode(Point point, int depth){
            this.point = point;
            this.depth = depth;
        }

        static int compareTo(int depth, int dimension, Point o1, Point o2) {
            int axis = depth % dimension;
            if (axis == KDTreeNN.X_AXIS)
                return KDTreeNN.X_COMPARATOR.compare(o1, o2);
            return KDTreeNN.Y_COMPARATOR.compare(o1, o2);
        }

        public Point getPoint() {
            return point;
        }

        public void setPoint(Point point) {
            this.point = point;
        }

        public int getDepth() {
            return depth;
        }

        public void setDepth(int depth) {
            this.depth = depth;
        }
    }

    KDNode restaurantTree = null;
    KDNode educationTree = null;
    KDNode hospitalTree = null;

    // default constructor
    public KDTreeNN() { }

    // create node to build the tree from the input list
    private KDNode createTree(List<Point> points, int depth){
        // edge case, avoid null pointer exception
        try{
            if (points.size() == 0 || points == null){
                return null;
            }
        }catch (NullPointerException e){
            return null;
        }

        // sort the list according to the dimension
        int axis = depth % 2;
        if (axis == X_AXIS)
            Collections.sort(points, X_COMPARATOR);
        else if (axis == Y_AXIS)
            Collections.sort(points, Y_COMPARATOR);

        KDNode node = null;
        List<Point> less = new ArrayList<Point>(points.size());
        List<Point> more = new ArrayList<Point>(points.size());

        if (points.size() > 0 ){
            int medianIndex = points.size() / 2;
            node = new KDNode(points.get(medianIndex), depth);
            // Process list to see where each non-median point lies
            for (int i = 0; i < points.size(); i++) {
                if (i == medianIndex){
                    continue;
                }
                Point point = points.get(i);
                // Cannot assume points before the median are less since they could be equal
                if (KDNode.compareTo(depth, 2, point, node.point) <= 0) {
                    less.add(point);
                } else {
                    more.add(point);
                }
            }

            if ((medianIndex <= points.size()-1) && more.size() > 0) {
                node.right = createTree(more, depth + 1);
                node.right.parent = node;
            }

            if ((medianIndex-1 >= 0) && less.size() > 0) {
                node.left = createTree(less, depth + 1);
                node.left.parent = node;
            }
        }
        return node;
    }

    @Override
    public void buildIndex(List<Point> points) {
        List<Point> restaurantPoints = new ArrayList<>();
        List<Point> educationPoints = new ArrayList<>();
        List<Point> hospitalPoints = new ArrayList<>();

        // separate points by category
        for ( Point point : points ) {
            if (point.cat == Category.RESTAURANT){
                restaurantPoints.add(point);
            }
            if (point.cat == Category.EDUCATION){
                educationPoints.add(point);
            }
            if (point.cat == Category.HOSPITAL){
                hospitalPoints.add(point);
            }
        }

        // build the kd-tree for each category
        restaurantTree = createTree(restaurantPoints, 0);
        educationTree = createTree(educationPoints, 0);
        hospitalTree = createTree(hospitalPoints, 0);
    }

    private void searchNode(Point searchTerm, KDNode node, int k, TreeSet<KDNode> results, Set<KDNode> examined) {
        examined.add(node);

        // Search node
        KDNode lastNode = null;
        Double lastDistance = Double.MAX_VALUE;
        if (results.size() > 0) {
            lastNode = results.last();
            lastDistance = lastNode.point.distTo(searchTerm);
        }
        Double nodeDistance = node.point.distTo(searchTerm);
        if (nodeDistance < lastDistance) {
            if (results.size() == k && lastNode != null)
                results.remove(lastNode);
            results.add(node);
        } else if (nodeDistance == lastDistance) {
            results.add(node);
        } else if (results.size() < k) {
            results.add(node);
        }
        // update nearest node and the distance
        lastNode = results.last();
        lastDistance = lastNode.point.distTo(searchTerm);

        int axis = node.depth % 2;
        KDNode leftNode = node.left;
        KDNode rightNode = node.right;

        // if axis aligned distance is less than current distance, search children trees,
        if (leftNode != null && !examined.contains(leftNode)) {
            examined.add(leftNode);

            double nodePoint = Double.MIN_VALUE;
            double valuePlusDistance = Double.MIN_VALUE;
            if (axis == X_AXIS) {
                nodePoint = node.point.lat;
                valuePlusDistance = searchTerm.lat - lastDistance;
            } else {
                nodePoint = node.point.lon;
                valuePlusDistance = searchTerm.lon - lastDistance;
                boolean lineIntersectsCube = ((valuePlusDistance <= nodePoint) ? true : false);

                // Continue down left branch
                if (lineIntersectsCube)
                    searchNode(searchTerm, leftNode, k, results, examined);
            }
            if (rightNode != null && !examined.contains(rightNode)) {
                examined.add(rightNode);

                nodePoint = Double.MIN_VALUE;
                valuePlusDistance = Double.MIN_VALUE;
                if (axis == X_AXIS) {
                    nodePoint = node.point.lat;
                    valuePlusDistance = searchTerm.lat + lastDistance;
                } else if (axis == Y_AXIS) {
                    nodePoint = node.point.lon;
                    valuePlusDistance = searchTerm.lon + lastDistance;
                }
                boolean lineIntersectsCube = ((valuePlusDistance >= nodePoint) ? true : false);

                // Continue down greater branch
                if (lineIntersectsCube) {
                    searchNode(searchTerm, rightNode, k, results, examined);
                }
            }
        }
    }

    protected static class EuclideanComparator implements Comparator<KDNode> {

        private final Point point;

        public EuclideanComparator(Point point) {
            this.point = point;
        }

        @Override
        public int compare(KDNode o1, KDNode o2) {
            Double distance1 = point.distTo(o1.point);
            Double distance2 = point.distTo(o1.point);
            if (distance1.compareTo(distance2) < 0)
                return -1;
            else if (distance2.compareTo(distance1) < 0)
                return 1;
            int depth = o1.depth;
            return KDNode.compareTo(depth, 2, o1.point, o2.point);
        }
    }

    @Override
    public List<Point> search(Point searchTerm, int k) {
        // initialize the result set
        TreeSet<KDNode> results = new TreeSet<KDNode>(new EuclideanComparator(searchTerm));
        ArrayList<Point> resultList = new ArrayList<>();

        // find the closest leaf
        KDNode prev = null;
        KDNode node = null;
        if (searchTerm.cat == Category.RESTAURANT){
            node = restaurantTree;
        }
        if (searchTerm.cat == Category.EDUCATION){
            node = educationTree;
        }
        if (searchTerm.cat == Category.HOSPITAL){
            node = hospitalTree;
        }
        while (node != null) {
            if (KDNode.compareTo(node.depth, 2, searchTerm, node.point) <= 0) {
                // left branch
                prev = node;
                node = node.left;
            } else {
                // right branch
                prev = node;
                node = node.right;
            }
        }
        KDNode leaf = prev;

        if (leaf != null) {
            // Used to not re-examine nodes
            Set<KDNode> examined = new HashSet<KDNode>();

            // recurve the tree, check if the leaf node is the nearest one
            node = leaf;
            while (node != null) {
                // Search node
                searchNode(searchTerm, node, k, results, examined);
                node = node.parent;
            }
        }

        for (KDNode temNode : results) {
            System.out.println("tmpNode: " + temNode.point.id);
            resultList.add(temNode.point);
        }

        System.out.println("---");

        for (Point point : resultList) {
            System.out.println("distance: " + point.distTo(searchTerm));
        }

        return resultList;
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
