import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.LinkedList;


// Note autograder would not allow implementing iterator interface on Board...therefore was forced to use raw iterator in while loops instead of for each
// Hence some loops may look a little odd.
public class Board {

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    private int[][] brd;
    private final int N;

    public Board(int[][] tiles) {
        N = tiles.length;
        brd = new int[N][N];
        for (int r = 0; r < N; r++) { brd[r] = tiles[r].clone(); }
    }

    private Iterator<Integer> iterator() {
        return new BoardIterator();
    }

    private class BoardIterator implements Iterator<Integer> {
        int row = 0, col = 0;

        public boolean hasNext() { return row < N; }

        public void remove() { throw new UnsupportedOperationException("Remove has not been implemented."); }

        public Integer next() {
            if (!hasNext()) {
                throw new NoSuchElementException("You have iterated through the entire board.");
            }
            int val = brd[row][col++];
            if (col == N) {
                col = 0;
                row++;
            }
            return val;
        }
    }

    private Iterator<Integer> GoalIterator() {
        return new GoalIterator();
    }

    private class GoalIterator implements Iterator<Integer> {

        final int END = N * N;
        int cur = 0;

        public boolean hasNext() { return cur < END; }

        public void remove() { throw new UnsupportedOperationException("Remove has not been implemented."); }

        public Integer next() { return ++cur < END ? cur : 0; } // returns 1, 2, 3, END-1, 0
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        int N = this.dimension();
        int pos = 0;
        Iterator<Integer> boardItr =  new BoardIterator();
         while (boardItr.hasNext()) {
            if (boardItr.next() == 0) { break; }
            pos++;
        }
        int rowBlank = pos/N, colBlank = pos % N;
        LinkedList<Board> boards = new LinkedList<>();
        int[][] MOVES = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] move : MOVES) {
            int r = rowBlank + move[0], c = colBlank + move[1];
            if (0 <= r && r < N && 0 <= c && c < N) {
                int[][] thisMove = boardToArray();
                swap(rowBlank, colBlank, r, c, thisMove);
                boards.add(new Board(thisMove));
            }
        }
        return boards;
    }

    // string representation of this board
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(dimension()).append("\n");
        short cnt = 0; // from prompt max N = 128
        Iterator<Integer> boardItr =  new BoardIterator();
        while (boardItr.hasNext()) {
            str.append(String.format("%2d ", boardItr.next()));
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
        Iterator GoalIter = new GoalIterator();
        Iterator<Integer> boardItr =  new BoardIterator();
        int oops = 0; // out of place
        while (boardItr.hasNext()) {
            int targ = (int) GoalIter.next();
            if (targ != boardItr.next() && targ != 0) {
                oops++;
            }
        }
        return oops;
    }

    public int manhattan() {
        Iterator GoalIter = new GoalIterator();
        Iterator<Integer> boardItr =  new BoardIterator();
        int manSum = 0;
        while (boardItr.hasNext()) {
            int targ = (int) GoalIter.next();
            int curTile = boardItr.next();
            if (curTile != targ  && curTile != 0) {
                if (targ == 0) { targ = N * N; } // placement of blank tile is N*N
                curTile --; targ--;
                int dRow = Math.abs(curTile  / N - targ / N);
                int dCol = Math.abs(curTile % N - targ % N);
                manSum += dRow + dCol;
            }
        }
        return manSum;
    }


    // is this board the goal board?
    public boolean isGoal() {
        return hamming() == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y instanceof Board) {
            Board that = (Board) y;
            if (that.dimension() == this.dimension()) {
                Iterator thisIter = new BoardIterator();
                for (int r = 0; r < N; r++) {
                    for (int c = 0; c < N; c++) {
                        if (that.brd[r][c] != (int) thisIter.next()) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int[][] twinBoard = boardToArray();
        int ar = 0, ac = 0, br = 1, bc = 0;
        if (twinBoard[ar][ac] == 0 || twinBoard[br][bc] == 0) { ac = 1; bc = 1;  }  // a = {0, 1}, b = {1, 1};
        swap(ar, ac, br, bc, twinBoard);
        return new Board(twinBoard);
    }

    // Swap values in NxN (board} arrays
    // Usage: ar (a row), ac (a col)...array
    private void swap(int ar, int ac, int br, int bc, int[][] board) {
        int tmp = board[ar][ac];
        board[ar][ac] = board[br][bc];
        board[br][bc] = tmp;
    }

    private int[][] boardToArray() {
        int N = this.N;
        int[][] out = new int[N][N];
        Iterator curBoard = new BoardIterator();
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) { out[r][c] = (int) curBoard.next(); }
        }
        return out;
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        // for each command-line argument
        for (String filename : args) {

            // read in the board specified in the filename
            In in = new In(filename);
            int n = in.readInt();
            int[][] tiles = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    tiles[i][j] = in.readInt();
                }
            }
            Board curBoard = new Board(tiles);
            StdOut.println(curBoard.toString());
            StdOut.printf("Is Goal? %1$b %n", curBoard.isGoal());
            StdOut.printf("Hamming: %1$d %n", curBoard.hamming());
            StdOut.printf("Manhattan %1$d %n", curBoard.manhattan());
            StdOut.printf("Twin Board: %n%s", curBoard.twin().toString());
            int c = 0;
            for (Board nBrd : curBoard.neighbors()) {
                StdOut.printf("Neighbor %1$d: %n%2$s", ++c, nBrd.toString());
            }
        }
    }

}


