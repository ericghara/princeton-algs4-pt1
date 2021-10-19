import java.util.TreeSet;
import java.util.ArrayList;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.In;

public class PointSET {
    private static TreeSet<Point2D> points;

    // construct an empty set of points
    public PointSET() {
        points = new TreeSet<>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return size() == 0;
    }

    // number of points in the set
    public int size() {
        return points.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        errorOnNull(p);
        points.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        errorOnNull(p);
        return points.contains(p);
    }

    // draw all points to standard draw
    public void draw() { points.forEach(Point2D::draw); }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        errorOnNull(rect);
        ArrayList<Point2D> insidePoints = new ArrayList<>();
        for (Point2D p : points) {
            if (rect.contains(p)) {
                insidePoints.add(p);
            }
        }
        return insidePoints;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        errorOnNull(p);
        if (points.contains(p)) {
            return p;
        }  // optimization; constant time if in p in set
        double minDist = Double.POSITIVE_INFINITY;
        Point2D minPoint = null;
        for (Point2D thisP : points) {
            double thisDist = Math.abs(p.distanceSquaredTo(thisP));
            if (thisDist < minDist) {
                minDist = thisDist;
                minPoint = thisP;
            }
        }
        return minPoint;
    }

    // unit testing of the methods (optional)
    private void errorOnNull(Object obj) {
        if (obj == null) throw new IllegalArgumentException("Recieved a null input.");
    }

    public static void main(String[] args) {
        String filename = args[0];
        In in = new In(filename);
        PointSET brute = new PointSET();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            brute.insert(p);
        }
        in = new In(filename);
        int cnt = 0; int found = 0;
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            if (!brute.contains(p)) {
                System.out.printf("Couldn't find %1$s in set!%n", p);
            }
            else { found++; }
            cnt++;
        }
        System.out.printf("Set contained %1$d items; %2$d were found.%n", cnt,found);
        StdDraw.enableDoubleBuffering();
        while (true) {

            // the location (x, y) of the mouse
            double x = StdDraw.mouseX();
            double y = StdDraw.mouseY();
            Point2D query = new Point2D(x, y);

            // draw all of the points
            StdDraw.clear();
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.01);
            brute.draw();

            // draw in red the nearest neighbor
            StdDraw.setPenRadius(0.03);
            StdDraw.setPenColor(StdDraw.RED);
            brute.nearest(query).draw();
            StdDraw.setPenRadius(0.02);
            StdDraw.show();
            StdDraw.pause(40);
        }
    }
}

