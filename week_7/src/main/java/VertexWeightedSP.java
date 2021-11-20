
// Vertex Weighted Shortest Path
// Finds the lowest weight horizontal path across a matrix
public class VertexWeightedSP {

    private final double[][] weightTo;  // cumulative weight to reach vertex
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
        weightTo = createWeightTo();
        lastVert = createLastVert();
        calcPath();
    }

    // finds tail of min weight path and returns its y coordinate;
    // x coordinate is implicitly last column.
    private int findTail() {
        final int END_X = W-1;
        double minWt = Double.POSITIVE_INFINITY;
        int minY = -1;
        for (int y = 0; y < H; y++) {
            if (minWt > weightTo[END_X][y]) {
                minWt = weightTo[END_X][y];
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

    // Uses a DP algorithm to calculate the lowest weight path to any vertex
    private void calcPath() {
        // Stops at 2nd to last column because this looks at the next possible move from current;
        // there are no possible moves from the last column.
        for (int fromX = 0; fromX < W-1; fromX++) {
            for (int fromY = 0; fromY < H; fromY++) {
                double fromWeight = weightTo[fromX][fromY];
                int toX = fromX + 1;
                for (int toY: adj(fromY)) {
                    relax(fromY, fromWeight, toX, toY);
                }
            }
        }
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

    /* "from" requires only y component x component can be inferred by position
     *  in lastVert matrix.  This ~halves space required by lastVert */
    private void relax(int fromY, double fromWeight, int toX, int toY) {
        double pathWeight = fromWeight + wMatrix.get(toX, toY);
        if (pathWeight  < weightTo[toX][toY]) {
            weightTo[toX][toY] = pathWeight;
            lastVert[toX][toY] = fromY;
        }
    }

    // Creates array with origin column (column: 0) set to 0 and all others to inf.
    private double[][] createWeightTo() {
        final double INIT_WT = Double.POSITIVE_INFINITY;
        double[][] weightTo = new double[W][H];
        for (int x = 1; x < W; x++) {
            for (int y = 0; y < H; y++) {
                weightTo[x][y] = INIT_WT;
            }
        }
        return weightTo;
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
