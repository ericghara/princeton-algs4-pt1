import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;

public class BruteCollinearPoints {
    private List<LineSegment> collinears;
    private final int N;

    // Generates all unique 4 point combinations and adds
    // to lineSegs if all 4  are on the same line
    public BruteCollinearPoints(Point[] points) {
        errorOnNull(points);
        N = points.length;
        Point[] ptSorted = points.clone();
        Arrays.sort(ptSorted);
        errorOnDuplicate(ptSorted);
        collinears = new LinkedList<>();
        for (int p = 0; p < N - 3; p++) {
            Point ptP = ptSorted[p];
            for (int q = p + 1; q < N - 2; q++ ) {
                Point ptQ = ptSorted[q];
                double PQ = ptP.slopeTo(ptQ);
                for (int r = q + 1; r < N -1; r++) {
                    Point ptR = ptSorted[r];
                    double QR = ptQ.slopeTo(ptR);
                    if (PQ == QR)  {
                        for (int s = r + 1; s < N; s++) {
                            Point ptS = ptSorted[s];
                            double RS = ptR.slopeTo(ptS);
                            if (QR == RS) {
                                collinears.add(new LineSegment(ptP, ptS));
                            }
                        }
                    }
                }
            }
        }
    }

    private void errorOnNull(Point[] points) {
        if (points == null)
            throw new IllegalArgumentException("Received null input.");
        for (Point p : points) {
            if (p == null) { throw new IllegalArgumentException("Found a null point value."); }
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
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
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