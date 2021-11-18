import edu.princeton.cs.algs4.Picture;

public class PixMap {
    private int[][] map;
    private int W,H;

    public PixMap(Picture pic) {
        W = pic.width();
        H = pic.height();
        map = allPixels(pic,W,H);
    }


    private static int [][] allPixels(Picture pic, int W, int H) {
        int[][] map = new int[W][H];
        for (int x = 0; x < W; x++ ) {
            for (int y = 0; y < W; y++) {
                map[x][y] = pic.getRGB(x,y);
            }
        }
        return map;
    }

    public void removeVerticalSeam(int[] seam) {

    }

    public void removeHorizontalSeam(int [] seam) {


    }


    // returns int (32 bit) with least significant 24 bits corresponding to rgb values
    // (msb) 8 bit undefined | 8 bit r | 8 bit g | 8 bit b (lsb)
    public int getPixel(int x, int y) {
        return map[x][y];
    }

    // Converts the current map to a picture
    public Picture toPicture() {
        Picture pic = new Picture(W,H);
        for (int x = 0; x < W; x++) {
            for (int y = 0; y < H; y++) {
                pic.setRGB( x,y, getPixel(x,y) );
            }
        }
        return pic;
    }

    public int getH() {
        return H;
    }

    public int getW() {
        return W;
    }

}
