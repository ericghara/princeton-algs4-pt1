import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class FastCollinearPoints {
    // finds all line segments containing 4 or more points
    private Point[] aux, sortAux;
    private List<LineSegment> collinears;  // where all found collinear segs are stored
    private final int N;  // points list length
    public FastCollinearPoints(Point[] points) {
            // initialize and check edge cases
            errorOnNull(points);
            N = points.length;
            sortAux = points.clone();
            Arrays.sort(sortAux);
            errorOnDuplicate(sortAux);
            aux = points.clone();
            collinears = new LinkedList<>();
            find(); // heart of algorithm
        }

    // Finds collinear points
    private void find() {
        for (Point keyVal: sortAux) {
            Arrays.sort(aux, keyVal.slopeOrder());
            // lastSlope not a sentinel.  Comparison of keyVal to itself will always be -inf; see Points.slopeTo()
            int L = 0; double lastSlope = Double.NEGATIVE_INFINITY;  //  sliding window to identify portions of list with equal slopes to keyVal
            for (int R = 1; R < N; R++) {
                double thisSlope = keyVal.slopeTo(aux[R]);
                if (thisSlope != lastSlope) {
                    lastSlope = thisSlope;
                    if (R - L >= 3) { addCollinearSeg(keyVal, L, R); } // ie we have >=4 collinear points (these >=3 + 1 (keyVal))
                    L = R;
                }
            }
            if (N - L >= 3) { addCollinearSeg(keyVal, L, N); } // if array ends with collinear block need to process
        }
        aux = sortAux = null;  // clean-up
    }

    private void addCollinearSeg(Point keyVal, int L, int R) {
        // Re-sorts collinear block of array based on coordinates *Not* slope
        Arrays.sort(aux, L,R);
        // if (true): unique combination based on this list block being sorted by coordinate
        // and keyVal being a coordinate sorted array ; duplicates will be >=0
        if (keyVal.compareTo(aux[L]) < 0)
            collinears.add(new LineSegment(keyVal, aux[R - 1]));
    }

    private void errorOnNull(Point[] points) {
        if (points == null)  // null array
            throw new IllegalArgumentException("Received null input.");
        for (Point p : points) { // null element in array
            if (p == null) { throw new IllegalArgumentException("Found a null value point."); }
        }
    }

    // returns sorted list with no duplicates
    private void errorOnDuplicate(Point[] ptSorted) {
        for (int i = 0; i < N-1; i++) {
            if ( ptSorted[i].compareTo(ptSorted[i+1] ) == 0 )
                throw new IllegalArgumentException("Found a duplicate point");
        }
    }

    // the number of line segments
    public int numberOfSegments() {
        return this.collinears.size();
    }

    // the line segments
    public LineSegment[] segments() {
        return collinears.toArray(new LineSegment[0]);
    }

    // Client provided by instructor
    public static void main(String[] args) {
        if (args.length == 0) { throw new IllegalArgumentException("No points input file was given."); }
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        int cnt = 0;
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
            cnt ++;
        }
        StdOut.println("Found segs:" + cnt);
        StdDraw.show();
    }

}


