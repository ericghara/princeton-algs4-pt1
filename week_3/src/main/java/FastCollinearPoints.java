import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;

public class FastCollinearPoints {
    // finds all line segments containing 4 or more points
    private Point[] aux, sortAux;
    private List<LineSegment> collinearsList;  // temporarily holds collinear segs while they are being found
    final LineSegment[] collinearsArray;       // collinearsList vals are copied here after all are found
    private final int N;  // points list length

    public FastCollinearPoints(Point[] points) {
            // initialize and check edge cases
            errorOnNull(points);
            N = points.length;
            sortAux = points.clone();
            Arrays.sort(sortAux);
            errorOnDuplicate(sortAux);
            aux = points.clone();
            collinearsList = new LinkedList<>();
            find(); // heart of algorithm
            // finish up
            collinearsArray = collinearsList.toArray(new LineSegment[0]);
            collinearsList  = null;
        }

    // Finds collinear points
    private void find() {
        for (Point keyVal: sortAux) {
            Arrays.sort(aux, keyVal.slopeOrder());
            // curSlope not a sentinel.  Comparison of keyVal to itself will always be -inf; see Points.slopeTo()
            int L = 0; double curSlope = Double.NEGATIVE_INFINITY;  //  sliding window to identify portions of list with equal slopes to keyVal
            for (int R = 1; R < N; R++) {
                double thisSlope = keyVal.slopeTo(aux[R]);
                if (thisSlope != curSlope) {
                    curSlope = thisSlope;
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
            collinearsList.add(new LineSegment(keyVal, aux[R - 1]));
    }

    private void errorOnNull(Point[] points) {
        if (points == null)  // null array
            throw new IllegalArgumentException("Received null input.");
        for (Point p : points) { // null element in array
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
        return collinearsArray.length;
    }

    // the line segments
    public LineSegment[] segments() {
        return collinearsArray;
    }
}

