
public class Energies {

    private int W, H;
    private PixMap pixMap;
    private static double[][] energies;

    public Energies(PixMap pixMap) {
        this.pixMap = pixMap;
        W = pixMap.width();
        H = pixMap.height();
        energies = createEnergies();
    }

    public void removeHorizontalSeam(int[] seam) {

    }

    public void removeVerticalSeam(int[] seam) {

    }

    // energy of pixel at column x and row y
    // output range: 0 - 441.7, with 1000 being reserved for boarder pixels
    private double calcEnergy(int x, int y) {
        // Cannot use this energy function for boarder pixels so arbitrarily set to 1000,
        // precluding any path from going through the boarder
        if (x == 0 || x == W-1 || y == 0 || y == H-1) {
            return 1000;
        }
        // sqrt( Δ(East,West) + Δ(North,South) )
        double deltaSqSums = deltaSqSum(x-1,y,x+1,y);
        deltaSqSums += deltaSqSum(x,y-1,x,y+1);
        return Math.sqrt(deltaSqSums);
    }

    // for pixels p1 and p2 returns (r1-r2)^2 + (g1-g2)^2 + (b1-b2)^2
    private double deltaSqSum(int x1, int y1, int x2, int y2) {
        int p1 = pixMap.getPixel(x1,y1);
        int p2 = pixMap.getPixel(x2, y2);
        int sum = 0;
        for (int i = 0; i < 3; i++) {
            int delta = (p1 & 0xFF) - (p2 & 0xFF);
            sum += delta*delta;
            p1 >>= 8;
            p2 >>= 8;
        }
        // avoids multiple widening conversions, working with ints until very end
        return (double) sum;
    }

    private double[][] createEnergies() {
        double[][] energies = new double[W][H];
        for (int x = 0; x < W; x++ ) {
            for (int y = 0; y < W; y++) {
                energies[x][y] = calcEnergy(x,y);
            }
        }
        return energies;
    }

    public int[] getVerticalSP() {
        VertexWeightedSP SP = new VertexWeightedSP( new Transposed() );
        return SP.shortestPath();
    }

    public int[] getHorizontalSP() {
        VertexWeightedSP SP = new VertexWeightedSP( new Normal() );
        return SP.shortestPath();
    }

    public double getEnergy(int x, int y) {
        return energies[x][y];
    }

    // use for horizontal shortest paths
    public static class Normal implements VertexWeightedSP.WeightMatrixInterface {
        public double getWeight(int x, int y) {
            return energies[x][y];
        }

        public int height() {
            return energies.length;
        }

        public int width() {
            return energies[0].length;
        }
    }

    // use for vertical shortest paths; provides client access to energies
    // as a transposed matrix
    public static class Transposed implements VertexWeightedSP.WeightMatrixInterface {
        public double getWeight(int x, int y) {
            return energies[y][x];  // notice transposed
        }

        public int height() {
            return energies[0].length; // ...transposed
        }

        public int width() {
            return energies.length;  // ...and transposed
        }

    }



}
