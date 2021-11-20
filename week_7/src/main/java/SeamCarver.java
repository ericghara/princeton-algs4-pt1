import edu.princeton.cs.algs4.Picture;
import java.util.Arrays;

/* Implements seam carving algorithm which allows resizing images without cropping or distortion
*  Info: https://en.wikipedia.org/wiki/Seam_carving */
public class SeamCarver {
    private final Energies energies;
    private final VertexWeightedSP.MatrixInterface eInterface;
    private enum Direction {V,H};
    private int W, H;

    // create a SeamCarver object based on the given picture
    public SeamCarver(Picture pic) {
        validatePic(pic);
        energies = new Energies(pic);
        eInterface = energies.getEnergyInterface();
        W = eInterface.width();
        H = eInterface.height();
    }

    // current picture
    public Picture picture() {
        return new Picture( energies.redraw() ); // defensive copy
    }

    // width of current picture
    public int width() { return eInterface.width(); }

    // height of current picture
    public int height() { return eInterface.height(); }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() { return energies.getHorizontalSP(); }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() { return energies.getVerticalSP(); }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        //if (H <= 1) {
        //    throw new IllegalArgumentException("Cannot remove seam, height <= 1.");
        //}
        validateSeam(seam, Direction.H);
        energies.removeHorizontalSeam(seam);
        H = eInterface.height();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        //if (W < 2) {
        //    throw new IllegalArgumentException("Cannot remove seam, width <= 1.");
        //}
        validateSeam(seam, Direction.V);
        energies.removeVerticalSeam(seam);
        W = eInterface.width();
    }

    // Gets energy (ie vertex weight) of a pixel
    public double energy(int x, int y) {
        checkPixel(x,y);
        return eInterface.get(x,y);
    }

    private void checkPixel(int x, int y) {
        if ( x < 0 || x >= W || y < 0 || y >= H) {
            throw new IllegalArgumentException("Pixel coordinates invalid: (" + x + ", " + y + ")." );
        }
    }

    private void validatePic(Picture pic) {
        if (pic == null || pic.height() < 1 || pic.width() < 1) {
            throw new IllegalArgumentException("Received an invalid input picture.");
        }
    }

    private void validateSeam(int[] seam, Direction dir) {
        int onAxisSize = (dir == Direction.V) ? H : W;
        int offAxisSize = (dir == Direction.V) ? W : H;

        if (offAxisSize <= 1) {
            throw new IllegalArgumentException("Picture is too small to remove another seam");
        }
        if (seam == null || seam.length != onAxisSize) {
            throw new IllegalArgumentException("Invalid seam length.");
        }
        int lastVal = seam[0];
        for (int val : seam) {
            if (val < 0 || val >= offAxisSize || Math.abs(val-lastVal) > 1) {
                throw new IllegalArgumentException("Found an invalid coordinate in the seam.");
            }
            lastVal = val;
        }
    }

    // Provide a path to a picture and the number of columns and rows you'd like to remove
    // ex: gradle run -PmainClass=SeamCarver --args="src/test/resources/logo.png 100 0"
    public static void main(String[] args) {
        if (args.length != 3) {
            throw new IllegalArgumentException("Received incorrect argument.  Usage SeamCarver {path to image file}  #cols #rows");
        }
        Picture pic = new Picture(args[0]);
        int COL = Integer.parseInt(args[1]);
        int ROW = Integer.parseInt(args[2]);
        SeamCarver SC = new SeamCarver( pic );

        for (int i = 0; i < COL; i++) {
            int[] vSeam = SC.findVerticalSeam();
            SC.removeVerticalSeam(vSeam);
            System.out.printf("Removed vertical seam: %s%n", Arrays.toString(vSeam));
        }
        for (int i = 0; i < ROW; i++) {
            int[] hSeam = SC.findHorizontalSeam();
            SC.removeHorizontalSeam(hSeam);
            System.out.printf("Horizontal seam: %s%n", Arrays.toString(hSeam));
        }
        pic.show();
        SC.picture().show();
    }
}