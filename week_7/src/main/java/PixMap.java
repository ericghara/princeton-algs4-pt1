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
        // seam is x vals
        // (mostly) in place eager delete, extra space = W
        for (int y = 0; y < H; y++) {
            int x = seam[y];
            map[x][y] = -1; // mark for deletion
        }
        /* Fills in holes of pixels marked for deletion by moving pixels directly to the right into holes.
        *  After a move, pixel to the right now becomes a hole.  Process repeats until entire rightmost column
        *  is holes (and can be deleted) */
        for (int x = 0; x < W-1; x++) {
            for (int y = 0; y < W; y++) {
                if (map[x][y] == -1) {
                    map[x][y] = map[x+1][y]; // move pixel left
                    map[x+1][y] = -1; // mark hole to be filled
                }
            }
        }
        System.arraycopy(map, 0, map, 0, --W);  // decreases width of map array by 1
    }

    public void removeHorizontalSeam(int [] seam) {
        // seam is y vals
        // (mostly) in place eager delete, extra space = H
        H--;
        for (int x = 0; x < W; x++) {
            int[] aux = new int[H];
            int y = seam[x];
            System.arraycopy(map[x], 0, aux, 0, y);
            System.arraycopy(map[x], y+1, aux, y, H-y);
        }
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

    public int height() {
        return H;
    }

    public int width() {
        return W;
    }

}
