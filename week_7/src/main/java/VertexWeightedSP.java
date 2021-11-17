import java.util.function.BiFunction;
import java.lang.FunctionalInterface;

/* API to calculate vertex weighted shortest paths with each vertex being represented by a weight in weights away
*  and referenced by its x,y coordinate in the array
 */
public class VertexWeightedSP {
    private final double[][] weights, weightTo;
    private final int[][] lastVert;
    private static int W, H;
    public enum Dimension {X, Y}
    private Dimension D;
    
    public VertexWeightedSP(double[][] weights, Dimension D) {
        checkWeights(weights);
        errorOnNull(D);
        W = weights[0].length;
        H = weights.length;
        this.D = D;
        this.weights = new double[W][];
        for (int x = 0; x < H; x++) {
            this.weights[x] = weights[x].clone();
        }
        weightTo = createWeightTo();
        lastVert = createEdgeTo();
    }

    public int[] getVertPath() {    }


    private void calcHorzPath() {
        for (int y = 0; y < H-1; y++) {
            for (int x = 0; x < W; x++) {
                for (int toY: adjX(x,y)) {
                    int toX = x + 1;
                    double fromWeight = weightTo[x][y];
                    relax(y, fromWeight, toX, toY);
                }
            }
        }
    }

    private int getMinPathHead() {
        int startX, startY;
        startX = startY = 0;
        if (D == Dimension.X) {
            startX = W - 1;
        }
        else {
            startY = H - 1;
        }
        double minVal = Double.POSITIVE_INFINITY;
        int minX, minY;
        minX = minY = -1;
        for (int x = startX; x < W; startX++) {
            for (int y = startY; x < H; startY++) {
                if (minVal > weightTo[x][y]) {
                    minVal = weightTo[x][y];
                    minX = x;
                    minY = y;
                }
            }
        }
        return (D == Dimension.X) ? minY : minX;
    }

    // returns path as a 2d [x,y] matrix
    private int[][] getMinPath() {
        int i, val, onAxis, offAxis;
        // Since lastVert only stores off axis data, need to
        if (D == Dimension.X) {
            i = W;
            onAxis = 0; // i.e. x
            offAxis = 1; // i.e. y
        }
        else {
            i = H;
            onAxis = 1; // i.e. y
            offAxis = 0; // i.e. x
        }
        val = getMinPathHead();
        int[][] path = new int[i--][2];
        while (i > 0) {
            path[i][onAxis] = i;
            path[i][offAxis] = val;
            val = lastVert[--i][val];
        }
        return path;
    }




    private void errorOnNull(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Received a null input");
        }
    }

    private void checkWeights(double [][] energies) {
        if (energies.length == 0 || energies[0].length == 0) {
            throw new IllegalArgumentException("Energies must be a valid 2 dimensional array.");
        }
    }

    // Creates array with origin row or column set to 0 and all others to inf.
    private double[][] createWeightTo() {
        final double INIT_WT = Double.POSITIVE_INFINITY;
        double[][] weightTo = new double[W][H];
        int startX = 0;
        int startY = 0;
        // Using startX and startY to avoid an "if Dimension == XX" conditional at each matrix element
        if (D == Dimension.X) {
            startX = 1;
        }
        else {
            startY = 1;
        }
        for (int x = startX; x < W; x++) {
            for (int y = startY; y < H; y++) {
                weightTo[x][y] = INIT_WT;
            }
        }
        return weightTo;
    }

    // Creates array with origin row or column set to -1 and all others set to 0.
    private int[][] createEdgeTo() {
        final int FIRST_V = -1;
        int[][] edgeTo = new int[W][H];
        if (D == Dimension.X) {
            for (int y = 0; y < H; y++) {
                edgeTo[0][y] = FIRST_V;
            }
        }
        else {
            for (int x = 0; x < W; x++) {
                edgeTo[x][0] = FIRST_V;
            }
        }
        return edgeTo;
    }

    /* "from" requires a single (x or y) component since paths proceed along a single axis,
    *  the other component may be inferred by the x or y pos in the edgeTo array.
    *  This ~halves the space required by the edgeTo array */
    private void relax(int fromComponent, double fromWeight, int toX, int toY) {
        double pathWeight = fromWeight + weights[toX][toY];
        if (pathWeight  < weightTo[toX][toY]) {
            weightTo[toX][toY] = pathWeight;
            lastVert[toX][toY] = fromComponent;
        }
    }

    private void calcPath() {
        final Adjacent Adj;
        int x, y;
        x = y = 0;
        if (D == Dimension.X ) {
            Adj = VertexWeightedSP::adjX;
        }
        else {
            Adj = VertexWeightedSP::adjY;
        }
        for (int i = 0; i < 10; i++) {
            final int[] result = Adj.get(x,y);
        }



    }
    
    
    // returns pixes adjacent to incident pixel either strictly to the right
    @FunctionalInterface
    public interface Adjacent {
        int[] get(int x, int y);
    }

    private static int[] adjX(int x, int y) {
        if (++x >= W) {
            return new int[0];
        }
        int startY = (y == 0) ? y : y-1;
        int stopY = (y == W-1) ? y: y + 1;
        return getAdj(startY, stopY);
    }

    private static int[] adjY(int x, int y) {
        if (++y >= H) {
            return new int[0];
        }
        int startX = (x == 0) ? x : x-1;
        int stopX = (x == H-1) ? x: x + 1;
        return getAdj(startX, stopX);
    }

    private static int[] getAdj(int start, int stop) {
        int n = stop - start + 1;
        int[] adj = new int[n];
        for (int i = 0; i < n; i++) {
            adj[i] = start + i;
        }
        return adj;
    }
}
