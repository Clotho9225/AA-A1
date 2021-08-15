package nearestNeigh;

import java.util.Objects;
import static nearestNeigh.Category.*;

/**
 * Class representing a point in the assignment.
 *
 * 
 */
public class Point {

    // identifier
    public String id = null;
    // category
    public Category cat;
    // latitude or x-coordinate
    public double lat = 0;
    // longitude or y-coordinate
    public double lon = 0;


    /**
     * Empty constructor
     */
    public Point() {
        this.cat = null;
    }

    /**
     * constructor with given point information
     */
    public Point(String id, Category cat, double lat, double lon) {
        this.id = id;
        this.cat = cat;
        this.lat = lat;
        this.lon = lon;
    }


    /**
     * Cast String to Category
     * @param catStr String of a Category
     * @return Category.
     */
    public static Category parseCat(String catStr) {
        Category cat = null;
        switch (catStr) {
            case "restaurant":
            case "Restaurant":
            case "RESTAURANT":
                cat = RESTAURANT;
                break;
            case "education":
            case "Education":
            case "EDUCATION":
                cat = EDUCATION;
                break;
            case "hospital":
            case "Hospital":
            case "HOSPITAL":
                cat = HOSPITAL;
                break;
            default:
                System.err.println("Unknown Category.");
                System.err.println(catStr);
        }
        return cat;
    }

    /**
     * toSring for Point.
     */
    @Override
    public String toString() {
        return "Point{" + "id=" + id + ", cat=" + cat + ", lat=" + lat + ", lon=" + lon + '}';
    }


    /**
     * cast string to Point, assuming format of toString().
     * @param str to cast
     * @return Point object
     */
    public static Point parsePoint(String str) {
        String info = str.substring(str.indexOf("{") + 1, str.indexOf("}"));
        String[] fields;
        fields = info.split(",");
        String field = fields[0];
        String id = field.substring(field.indexOf("=") + 1);
        field = fields[1];
        Category cat = parseCat(field.substring(field.indexOf("=") + 1));
        field = fields[2];
        double lat = Double.parseDouble(field.substring(field.indexOf("=") + 1));
        field = fields[3];
        double lon = Double.parseDouble(field.substring(field.indexOf("=") + 1));

        Point point = new Point(id, cat, lat, lon);
        return point;
    }

    /**
     * Hashing for point.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.id);
        hash = 53 * hash + Objects.hashCode(this.cat);
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.lat) ^ (Double.doubleToLongBits(this.lat) >>> 32));
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.lon) ^ (Double.doubleToLongBits(this.lon) >>> 32));
        return hash;
    }

    /**
     * Equality for a point.
     * @param obj Object (Point) to compare.
     * @return True if equal, otherwise false.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Point other = (Point) obj;
        if (Double.doubleToLongBits(this.lat) != Double.doubleToLongBits(other.lat)) {
            return false;
        }
        if (Double.doubleToLongBits(this.lon) != Double.doubleToLongBits(other.lon)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (this.cat != other.cat) {
            return false;
        }
        return true;
    }

    /**
     * Computes the distance between two Points, for nearest neighbour searches.
     * Use this to compute your distances.
     * @param point Point to compute distance to, from this point object.
     * @return Distance.
     */
    public double distTo(Point point) {
        double theta = lon - point.lon;
        double dist = Math.sin(deg2rad(lat)) * Math.sin(deg2rad(point.lat)) + Math.cos(deg2rad(lat)) * Math.cos(deg2rad(point.lat)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return (dist);
    }


    /**
     * Convert degrees to radians.
     * @param deg Degrees to convert to radians.
     * @return Radians.
     */
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }


    /**
     * Convert randian to degrees.
     * @param deg Radians to convert to degrees.
     * @return Degrees.
     */
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

} // end of class Point
