import edu.princeton.cs.algs4.Picture;
import java.util.Arrays;

public class Energies {

    private final VertexWeightedSP.MatrixInterface eInterface;
    private int[][] pixMap;
    private int W, H;
    private double[][] energies;
    private enum Direction {V,H};

    public Energies(Picture pic) {
        W = pic.width();
        H = pic.height();
        pixMap = createPixMap(pic);
        createEnergies();
        eInterface = new Normal();
    }

    public void removeHorizontalSeam(int[] seam) {
        // seam is y vals
        // (mostly) in place eager delete, extra space = H
        H--;
        for (int x = 0; x < W; x++) {
            int y = seam[x];
            // pixMap
            int[] auxP = new int[H];
            System.arraycopy(pixMap[x], 0, auxP, 0, y);
            System.arraycopy(pixMap[x], y+1, auxP, y, H-y);
            pixMap[x] = auxP;
            // energies
            double[] auxE = new double[H];
            System.arraycopy(energies[x], 0, auxE, 0, y);
            System.arraycopy(energies[x], y+1, auxE, y, H-y);
            energies[x] = auxE;
        }
        calcEnergy(seam, Direction.H);
    }

    public void removeVerticalSeam(int[] seam) {
        // seam is x vals
        // (mostly) in place eager delete, extra space = W
        for (int y = 0; y < H; y++) {
            int x = seam[y];
            pixMap[x][y] = 0; // mark for deletion (valid pixels are negative ints)
        }
        /* Fills in holes of pixels marked for deletion by moving pixels directly to the right into holes.
         *  After a move, pixel to the right now becomes a hole.  Process repeats until entire rightmost column
         *  is holes (and can be deleted) */
        for (int x = 0; x < W-1; x++) {
            for (int y = 0; y < H; y++) {
                if (pixMap[x][y] == 0) {
                    pixMap[x][y] = pixMap[x+1][y]; // move pixel left
                    energies[x][y] = energies[x+1][y]; // move weight left
                    pixMap[x+1][y] = 0; // mark hole to be filled
                }
            }
        }
        pixMap = Arrays.copyOfRange(pixMap,0, --W); // decreases width of matrix by 1
        energies = Arrays.copyOfRange(energies,0, W);
        calcEnergy(seam, Direction.V);
    }

    // Note overloaded, this should be used when recalculating energies around a removed seam
    private void calcEnergy(int[] seam, Direction dir ) {
        if (dir == Direction.V) {
            for (int y = 0; y < H; y++) {
                int x = seam[y];
                if ( validX(x) ) { calcEnergy(x,y); }
                if ( validX(--x) ) { calcEnergy(x,y); }
            }
        }
        else {
            for (int x = 0; x < W; x++) {
                int y = seam[x];
                if ( validY(y) ) { calcEnergy(x, y); }
                if ( validY(--y) ) { calcEnergy(x, y); }
            }
        }
    }

    // energy of pixel at column x and row y
    // output range: 0 - 441.7, with 1000 being reserved for boarder pixels
    private void calcEnergy(int x, int y) {
        // Cannot use this energy function for boarder pixels so arbitrarily set to 1000,
        // precluding any path from going through the boarder
        if (x == 0 || x == W-1 || y == 0 || y == H-1) {
            energies[x][y] = 1000d;
            return;
        }
        // sqrt( Δ(East,West) + Δ(North,South) )
        double deltaSqSums = deltaSqSum(x-1,y,x+1,y);
        deltaSqSums += deltaSqSum(x,y-1,x,y+1);
        energies[x][y] = Math.sqrt(deltaSqSums);
    }

    // for pixels p1 and p2 returns (r1-r2)^2 + (g1-g2)^2 + (b1-b2)^2
    private double deltaSqSum(int x1, int y1, int x2, int y2) {
        int p1 = pixMap[x1][y1];
        int p2 = pixMap[x2][y2];
        int sum = 0;
        for (int i = 0; i < 3; i++) {
            int delta = (p1 & 0xFF) - (p2 & 0xFF);
            sum += delta*delta;
            p1 >>= 8;
            p2 >>= 8;
        }
        // avoids multiple conversions, working with ints until the very end
        return (double) sum;
    }

    private void createEnergies() {
        energies = new double[W][H];
        for (int x = 0; x < W; x++ ) {
            for (int y = 0; y < H; y++) {
                calcEnergy(x,y);
            }
        }
    }

    private int [][] createPixMap(Picture pic) {
        int[][] map = new int[W][H];
        for (int x = 0; x < W; x++ ) {
            for (int y = 0; y < H; y++) {
                map[x][y] = pic.getRGB(x,y);
            }
        }
        return map;
    }

    public Picture redraw() {
        Picture pic = new Picture(W,H);
        for (int x = 0; x < W; x++) {
            for (int y = 0; y < H; y++) {
                pic.setRGB( x,y, pixMap[x][y] );
            }
        }
        return pic;
    }

    private boolean validX(int x) { return 0 <= x && x < W; }

    private boolean validY(int y) { return 0 <= y && y < H; }

    public int[] getVerticalSP() {
        VertexWeightedSP SP = new VertexWeightedSP( new Transposed() );
        return SP.shortestPath();
    }

    public int[] getHorizontalSP() {
        VertexWeightedSP SP = new VertexWeightedSP( eInterface );
        return SP.shortestPath();
    }

    public VertexWeightedSP.MatrixInterface getEnergyInterface() { return eInterface; }

    // use for horizontal shortest paths
    public class Normal implements VertexWeightedSP.MatrixInterface {
        public double get(int x, int y) { return energies[x][y]; }

        public int height() { return H; }

        public int width() { return W; }
    }

    // use for vertical shortest paths; provides client access to energies
    // as a transposed matrix
    public class Transposed implements VertexWeightedSP.MatrixInterface {
        public double get(int x, int y) { return energies[y][x]; }   // notice transposed

        public int height() { return W; } // ...transposed

        public int width() { return H; } // ...and transposed
    }
}
