import edu .princeton.cs.algs4.StdOut;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Board implements Iterable<Integer> {

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    private int[][] brd;
    private final int N;
    public Board(int[][] tiles) {
        brd = tiles.clone();
        N = brd.length;

    }

    // Iterates next to prev
    public Iterator<Integer> iterator()
    { return new BoardIterator(); }

    private class BoardIterator implements Iterator<Integer> {
        int row = 0, col = 0;
        public boolean hasNext() {
            return col < N; }
        public void remove()     { throw new UnsupportedOperationException("Remove has not been implemented."); }
        public Integer next()  {
            if (!hasNext())      { throw new NoSuchElementException("You have iterated through the entire board.");}
            int val = brd[row++][col];
            if (row == N) { row = 0; col++; }
            return val;
        }
    }

    // string representation of this board
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append( dimension() ).append("\n");
        short cnt = 0; // from prompt max N = 128
        for (int tile : Board.this) {
            str.append(' ');
            str.append(tile);
            if (++cnt % N == 0) {
                str.append('\n');
            }
        }
        return str.toString();
    }


    // board dimension N
    public int dimension() { return N; }



    // number of tiles out of place
    public int hamming() {
        int targ = 1;
        int oops = 0; // out of place
        final int BLANK_POS = N * N;
        for (int curTile : Board.this) {
            if (targ == BLANK_POS) targ = 0;  // handles empty curTile at end
            if (curTile != targ++) { oops++; }
        }
        return oops;
    }

    public int manhattan() {
        int targ = 1;
        int manSum = 0;
        final int BLANK_POS = N * N;
        for (int curTile : Board.this) {
            if (targ == BLANK_POS) targ = 0;  // handles empty tile at end
            if (curTile != targ) { manSum +=calcManhattan(curTile, targ); }
            targ++;
        }
        return manSum;
    }

    private int calcManhattan(int cur, int targ) {
        int moves = 0;
        int delta = Math.abs(cur-targ);
        while (delta > N) {
            delta -= N;
            moves ++;
        }
        return moves + delta;
    }


    // is this board the goal board?
    public boolean isGoal() { return         }

    // does this board equal y?
    public boolean equals(Object y) { return y.toString().equals(brd.toString()); }

    // all neighboring boards
    public Iterable<Board> neighbors() {}

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {}

    // unit testing (not graded)
    public static void main(String[] args) {}

}


}