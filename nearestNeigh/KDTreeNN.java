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
    private static final Comparator<Point> X_COMPARATOR = Comparator.comparingDouble(o -> o.lat);

    private static final Comparator<Point> Y_COMPARATOR = Comparator.comparingDouble(o -> o.lon);

    public static class KDNode {
        private final Point point;
        int depth;

        private KDNode parent = null;
        private KDNode left = null;
        private KDNode right = null;

        public KDNode() {
            this.point = null;
            this.depth = 0;
        }

        public KDNode(Point point, int depth){
            this.point = point;
            this.depth = depth;
        }

        static int compareTo(int depth, Point o1, Point o2) {
            int axis = depth % 2;
            if (axis == KDTreeNN.X_AXIS)
                return KDTreeNN.X_COMPARATOR.compare(o1, o2);
            return KDTreeNN.Y_COMPARATOR.compare(o1, o2);
        }
    }

    KDNode restaurantTree;
    KDNode educationTree;
    KDNode hospitalTree;

    List<Point> restaurantPoints;
    List<Point> educationPoints;
    List<Point> hospitalPoints;

    // default constructor
    public KDTreeNN() {
        this.restaurantTree = new KDNode();
        this.educationTree = new KDNode();
        this.hospitalTree = new KDNode();

        this.restaurantPoints  = new ArrayList<>();
        this.educationPoints  = new ArrayList<>();
        this.hospitalPoints  = new ArrayList<>();
    }

    @Override
    public void buildIndex(List<Point> points) {
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
        restaurantTree = buildTree(restaurantPoints, 0);
        educationTree = buildTree(educationPoints, 0);
        hospitalTree = buildTree(hospitalPoints, 0);
    }

    @Override
    public List<Point> search(Point searchTerm, int k) {
        // initialize the result list
        ArrayList<Point> resultList = new ArrayList<>();
        TreeSet<KDNode> results = new TreeSet<>(new EuclideanComparator(searchTerm));

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
            if (KDNode.compareTo(node.depth, searchTerm, node.point) <= 0) {
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
            Set<KDNode> examined = new HashSet<>();

            // backtrack
            node = leaf;
            while (node != null) {
                // backtrack
                searchNode(searchTerm, node, k, results, examined);
                node = node.parent;
            }
        }

        for (KDNode tmpNode : results) {
            resultList.add(tmpNode.point);
        }

        return resultList;
    }

    @Override
    public boolean addPoint(Point point) {
        if (point.cat == Category.RESTAURANT){
            if (!restaurantPoints.contains(point)){
                restaurantPoints.add(point);
                addPointToTree(point, restaurantTree);
                return true;
            }
        }
        if (point.cat == Category.EDUCATION){
            if (!educationPoints.contains(point)){
                educationPoints.add(point);
                addPointToTree(point, educationTree);
                return true;
            }
        }
        if (point.cat == Category.HOSPITAL){
            if (!hospitalPoints.contains(point)){
                hospitalPoints.add(point);
                addPointToTree(point, hospitalTree);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deletePoint(Point point) {
        if (point.cat == Category.RESTAURANT){
            if (restaurantPoints.contains(point)){
                restaurantPoints.remove(point);
                remove(point, restaurantTree);
                return true;
            }
        }
        if (point.cat == Category.EDUCATION){
            if (educationPoints.contains(point)){
                educationPoints.remove(point);
                remove(point, educationTree);
                return true;
            }
        }
        if (point.cat == Category.HOSPITAL){
            if (hospitalPoints.contains(point)){
                hospitalPoints.remove(point);
                remove(point, hospitalTree);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isPointIn(Point point) {
        if (point.cat == Category.RESTAURANT){
            if (restaurantPoints.contains(point)){
                return true;
            }
        }
        if (point.cat == Category.EDUCATION){
            if (educationPoints.contains(point)){
                return true;
            }
        }
        if (point.cat == Category.HOSPITAL){
            return hospitalPoints.contains(point);
        }
        return false;
    }

    // build the tree from the input list
    private KDNode buildTree(List<Point> points, int depth){
        // edge case, avoid null pointer exception
        try{
            if (points.size() == 0){
                return null;
            }
        }catch (NullPointerException e){
            return null;
        }

        // sort the list according to the dimension
        int axis = depth % 2;
        if (axis == X_AXIS)
            points.sort(X_COMPARATOR);
        else if (axis == Y_AXIS)
            points.sort(Y_COMPARATOR);

        KDNode node = null;
        List<Point> less = new ArrayList<>(points.size());
        List<Point> more = new ArrayList<>(points.size());

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
                if (KDNode.compareTo(depth, point, node.point) <= 0) {
                    less.add(point);
                } else {
                    more.add(point);
                }
            }

            if ((medianIndex <= points.size()-1) && more.size() > 0) {
                node.right = buildTree(more, depth + 1);
                assert node.right != null;
                node.right.parent = node;
            }

            if ((medianIndex-1 >= 0) && less.size() > 0) {
                node.left = buildTree(less, depth + 1);
                assert node.left != null;
                node.left.parent = node;
            }
        }
        return node;
    }

    private void searchNode(Point searchTerm, KDNode node, int k, TreeSet<KDNode> results, Set<KDNode> examined) {
        examined.add(node);

        // Search node
        KDNode lastNode = null;
        double lastDistance = Double.MAX_VALUE;
        int resultSize = results.size();
        if (resultSize != 0) {
            lastNode = results.last();
            assert lastNode.point != null;
            lastDistance = lastNode.point.distTo(searchTerm);
        }
        assert node.point != null;
        double nodeDistance = node.point.distTo(searchTerm);

        if (nodeDistance < lastDistance){
            if (resultSize == k){
                results.remove(lastNode);
            }
            results.add(node);
        }
        if (results.size() < k){
            results.add(node);
        }

        // update nearest K node and the distance
        lastNode = results.last();
        assert lastNode.point != null;
        lastDistance = lastNode.point.distTo(searchTerm);


        // recurse
        int axis = node.depth % 2;
        KDNode leftNode = node.left;
        KDNode rightNode = node.right;

        // if axis aligned distance is less than current distance, search children trees,
        if (leftNode != null && !examined.contains(leftNode)) {
            examined.add(leftNode);

            double nodeCoordinate= Double.MIN_VALUE;
            double valueMinusDistance = Double.MIN_VALUE;
            if (axis == X_AXIS) {
                nodeCoordinate = node.point.lat;
                valueMinusDistance = searchTerm.lat - lastDistance;
            }
            if (axis == Y_AXIS) {
                nodeCoordinate = node.point.lon;
                valueMinusDistance = searchTerm.lon - lastDistance;
            }

            boolean lineIntersectsCube = (valueMinusDistance <= nodeCoordinate);

            // Continue down left branch
            if (lineIntersectsCube){
                searchNode(searchTerm, leftNode, k, results, examined);
            }
        }

        if (rightNode != null && !examined.contains(rightNode)) {
            examined.add(rightNode);

            double nodePoint = Double.MIN_VALUE;
            double valuePlusDistance = Double.MIN_VALUE;
            if (axis == X_AXIS) {
                nodePoint = node.point.lat;
                valuePlusDistance = searchTerm.lat + lastDistance;
            }
            if (axis == Y_AXIS){
                nodePoint = node.point.lon;
                valuePlusDistance = searchTerm.lon + lastDistance;
            }

            boolean lineIntersectsCube = (valuePlusDistance >= nodePoint);

            // Continue down greater branch
            if (lineIntersectsCube) {
                searchNode(searchTerm, rightNode, k, results, examined);
            }
        }

    }

    protected static class EuclideanComparator implements Comparator<KDNode> {

        private final Point point;

        public EuclideanComparator(Point point) {
            this.point = point;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(KDNode o1, KDNode o2) {
            assert o1.point != null;
            double d1 = point.distTo(o1.point);
            assert o2.point != null;
            double d2;
            d2 = point.distTo(o2.point);

            return Double.compare(d1, d2);
        }
    }

    private void addPointToTree(Point point, KDNode tree){
        KDNode node = tree;
        while (true) {
            if (KDNode.compareTo(node.depth, point, node.point) <= 0) {
                // left
                if (node.left == null) {
                    KDNode newNode = new KDNode(point, node.depth + 1);
                    newNode.parent = node;
                    node.left = newNode;
                    break;
                }
                node = node.left;
            } else {
                // right
                if (node.right == null) {
                    KDNode newNode = new KDNode(point, node.depth + 1);
                    newNode.parent = node;
                    node.right = newNode;
                    break;
                }
                node = node.right;
            }
        }
    }

    private void remove(Point point, KDNode tree){
        KDNode node = getNode(tree, point);
        KDNode parent = node.parent;
        if (parent != null) {
            List<Point> nodes = getTree(node);
            if (parent.left != null && node.equals(parent.left)) {
                if (nodes.size() > 0) {
                    parent.left = buildTree(nodes, node.depth);
                    if (parent.left != null) {
                        parent.left.parent = parent;
                    }
                } else {
                    parent.left = null;
                }
            } else {
                if (nodes.size() > 0) {
                    parent.right = buildTree(nodes, node.depth);
                    if (parent.right != null) {
                        parent.right.parent = parent;
                    }
                } else {
                    parent.right = null;
                }
            }
        } else {
            // root
            List<Point> nodes = getTree(node);
            if (nodes.size() > 0){
                tree = buildTree(nodes, node.depth);
            }else{
                tree = null;
            }
        }

    }

    private static KDNode getNode(KDNode tree, Point point) {
        if (tree == null || point == null)
            return null;

        KDNode node = tree;
        while (true) {
            if (node.point.equals(point)) {
                return node;
            } else if (KDNode.compareTo(node.depth, point, node.point) <= 0) {
                // left
                if (node.left == null) {
                    return null;
                }
                node = node.left;
            } else {
                // right
                if (node.right == null) {
                    return null;
                }
                node = node.right;
            }
        }
    }

    private static ArrayList<Point> getTree(KDNode root) {
        ArrayList<Point> list = new ArrayList<>();
        if (root == null)
            return list;

        if (root.left != null) {
            list.add(root.left.point);
            list.addAll(getTree(root.left));
        }
        if (root.right != null) {
            list.add(root.right.point);
            list.addAll(getTree(root.right));
        }

        return list;
    }
}
