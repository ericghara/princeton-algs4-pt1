import java.awt.Color;
import edu.princeton.cs.algs4.Picture;
import java.lang.Math;

public class SeamCarver {
    private Picture pic;
    private int W, H;
    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        pic = new Picture(picture);
        W = pic.width();
        H = pic.height();
        double[][] energies = allEnergies();
        VertexWeightedSP SP = new VertexWeightedSP( energies, VertexWeightedSP.Dimension.X ); // remove me
    }

    // current picture
    public Picture picture() { return new Picture(pic); }

    // width of current picture
    public int width() { return W; }

    // height of current picture
    public int height() { return H; }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        // We define the energy of a pixel at the border of the image to be 1000, so that it is strictly larger than the energy of any interior pixel.
        checkPixel(x,y);
        if (x == 0 || x == W-1 || y == 0 || y == H-1) {
            return 1000;
        }
        /* For other pixels performs following operation with r,g,b representing r,g,b channels of picture
        *  sqrt((255-r)^2 + (255-g)^2 + (255-b)^2)  */
        int encRGB = pic.getRGB(x,y);
        double energySquared = 0;
        for (int i = 0; i < 3; i++) {
            energySquared += Math.pow(255 - (encRGB & 0xFF), 2);
            encRGB >>= 8;
        }
        return Math.sqrt(energySquared);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {}

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {}

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {}

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {}

    private double[][] allEnergies() {
        double[][] energies = new double[H][W];
        for (int x = 0; x < W; x++ ) {
            for (int y = 0; y < W; y++) {
                energies[x][y] = energy(x,y);
            }
        }
        return energies;
    }

    private void checkPixel(int x, int y) {
        if ( 0 < x || x >= W || 0 < y || y >= H) {
            throw new IllegalArgumentException("Pixel coordinates invalid: (" + x + ", " + y + ")." );
        }
    }

    //  unit testing (optional)
    public static void main(String[] args) {}

}