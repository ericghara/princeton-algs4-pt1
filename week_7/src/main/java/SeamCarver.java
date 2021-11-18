import edu.princeton.cs.algs4.Picture;
import java.lang.Math;

public class SeamCarver {
    private Picture pic;
    private int W, H;
    private PixMap pixMap;
    private Energies energies;
    private enum Direction {V,H};
    // create a seam carver object based on the given picture
    public SeamCarver(Picture pic) {
        validatePic(pic);
        this.pic = new Picture(pic);  // Picture constructor performs a defensive copy
        W = pic.width();
        H = pic.height();
        pixMap = new PixMap(this.pic);
        energies = new Energies(pixMap);
    }

    // current picture
    public Picture picture() { return pixMap.toPicture(); }

    // width of current picture
    public int width() { return W; }

    // height of current picture
    public int height() { return H; }



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
        pixMap.removeHorizontalSeam(seam);
        energies.removeHorizontalSeam(seam);
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        validateSeam(seam, Direction.V);
        pixMap.removeVerticalSeam(seam);
        energies.removeHorizontalSeam(seam);
    }

    public double energy(int x, int y) {
        return energies.getEnergy(x,y);
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
            if (val < 0 || val >= offAxisSize || val < lastVal-1 || val > lastVal+1 ) {
                throw new IllegalArgumentException("Found an invalid coordinate in the seam.");
            }
            lastVal = val;
        }
    }

    //  unit testing (optional)
    public static void main(String[] args) {}

}