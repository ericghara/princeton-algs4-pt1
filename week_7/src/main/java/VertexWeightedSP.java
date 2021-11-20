import java.util.Arrays;

// Vertex Weighted Shortest Path
// Finds the lowest weight horizontal path across a matrix
public class VertexWeightedSP {

    private final double[] pathWeight;  // cumulative weight to reach vertex
    private final int[][] lastVert;  // y coordinate of vertex used to reach current vertex
    private int W, H;
    private final MatrixInterface wMatrix; // Interface VWSP uses to access external weight matrix

    public interface MatrixInterface {

        double get(int x, int y);

        int height();

        int width();
    }

    public VertexWeightedSP(MatrixInterface wMatrix) {
        this.wMatrix = wMatrix;
        W = wMatrix.width();
        H = wMatrix.height();
        lastVert = createLastVert();
        pathWeight = calcPath();
    }

    // finds tail of min weight path and returns its y coordinate;
    // x coordinate is implicitly last column.
    private int findTail() {
        double minWt = Double.POSITIVE_INFINITY;
        int minY = -1;
        for (int y = 0; y < H; y++) {
            if (minWt > pathWeight[y]) {
                minWt = pathWeight[y];
                minY = y;
            }
        }
        // this is last y pos lastVert[X][minY] is penultimate y pos
        return minY;
    }

    // Traces lowest weight path using lastVert matrix
    public int[] shortestPath() {
        int tailY = findTail();
        int[] path = new int[W];
        int x = W-1;
        while (x >= 0) {
            path[x] = tailY;
            tailY = lastVert[x][tailY];
            x--;
        }
        return path;
    }

    /* Uses a DP algorithm to calculate the lowest weight path horizontally across wMatrix.
    *  Space optimized to use cur and next arrays for tabulation instead of an x by y matrix    */
    private double[] calcPath() {
        // Stops at 2nd to last column because there are no moves forward from last column
        double[] cur = new double[H];  // cumulative weight
        for (int x = 0; x < W - 1; x++) {
            double[] next = new double[H]; // lowest next step weight seen so far
            Arrays.fill(next, Double.POSITIVE_INFINITY);
            for (int y = 0; y < H; y++) {
                double fromWeight = cur[y];
                int nextX = x + 1;
                for (int nextY : adj(y)) {
                    // vertex relaxation
                    double pathWeight = fromWeight + wMatrix.get(nextX, nextY);
                    if (pathWeight < next[nextY]) {
                        next[nextY] = pathWeight;
                        lastVert[nextX][nextY] = y;
                    }
                }
            }
            cur = next;
        }
        return cur;
    }

    // Provides y-1, y, y+1 (if available) from given y
    private int[] adj(int y) {
        if (y < 0 || y >= H) {
            throw new IllegalArgumentException("Adj received an out of range y value.");
        }
        int loY = (y == 0) ? y : y-1;
        int hiY =  (y == H-1) ? y: y + 1;
        int n = hiY- loY + 1;
        int[] adj = new int[n];
        for (int i = 0; i < n; i++) {
            adj[i] = loY + i;
        }
        return adj;
    }

    // Creates array with origin column (column: 0) set to FIRST_V and all others to 0.
    private int[][] createLastVert() {
        final int FIRST_V = -1;
        int[][] lastVert = new int[W][H];
        for (int y = 0; y < H; y++) {
            lastVert[0][y] = FIRST_V;
        }
        return lastVert;
    }
}
