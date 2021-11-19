import edu.princeton.cs.algs4.Picture;
import java.util.Arrays;

public class SeamCarver {
    private final Energies energies;
    private final VertexWeightedSP.MatrixInterface eInterface;
    private enum Direction {V,H};
    private Picture pic;
    private boolean redrawPic;
    private int W, H;

    // create a SeamCarver object based on the given picture
    public SeamCarver(Picture pic) {
        validatePic(pic);
        this.pic = new Picture(pic);  // Picture constructor performs a defensive copy
        redrawPic = false;
        energies = new Energies(pic);
        eInterface = energies.getEnergyInterface();
        W = eInterface.width();
        H = eInterface.height();
    }

    // current picture
    public Picture picture() {
        if (redrawPic) {
            pic = energies.redraw();
            redrawPic = false;
        }
        return new Picture(pic); // defensive copy
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
        if (H <= 1) {
            throw new IllegalArgumentException("Cannot remove seam, height <= 1.");
        }
        validateSeam(seam, Direction.H);
        energies.removeHorizontalSeam(seam);
        H = eInterface.height();
        redrawPic = true;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (W <= 1) {
            throw new IllegalArgumentException("Cannot remove seam, width <= 1.");
        }
        validateSeam(seam, Direction.V);
        energies.removeVerticalSeam(seam);
        W = eInterface.width();
        redrawPic = true;
    }

    public double energy(int x, int y) {
        checkPixel(x,y);
        return eInterface.get(x,y);
    }

    private void checkPixel(int x, int y) {
        if ( 0 < x || x >= W || 0 < y || y >= H) {
            throw new IllegalArgumentException("Pixel coordinates invalid: (" + x + ", " + y + ")." );
        }
    }

    private void validatePic(Picture pic) {
        if (pic == null || pic.height() < 1 || pic.width() < 1) {
            throw new IllegalArgumentException("Received an invalid input picture.");
        }
    }

    private void validateSeam(int[] seam, Direction dir) {
        int onAxisSize = (dir == Direction.H) ? H : W;
        int offAxisSize = (dir == Direction.H) ? W : H;

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

    //  unit testing
    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Received incorrect argument.  Usage SeamCarver {path to image file}");
        }
        Picture pic = new Picture( args[0] );
        SeamCarver SC = new SeamCarver( pic );

        int[] vSeam = SC.findVerticalSeam();
        System.out.printf("Vertical seam: %s", Arrays.toString(vSeam));

        int[] hSeam = SC.findHorizontalSeam();
        System.out.printf("Vertical seam: %s", Arrays.toString(hSeam));
    }

}