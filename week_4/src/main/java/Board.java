import edu.princeton.cs.algs4.StdOut;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.LinkedList;

public class Board implements Iterable<Integer> {

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    private int[][] brd;
    private final int N;

    public Board(int[][] tiles) {
        brd = tiles.clone();
        N = brd.length;

    }

    public Iterator<Integer> iterator() {
        return new BoardIterator();
    }

    private class BoardIterator implements Iterator<Integer> {
        int row = 0, col = 0;

        public boolean hasNext() {
            return col < N;
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove has not been implemented.");
        }

        public Integer next() {
            if (!hasNext()) {
                throw new NoSuchElementException("You have iterated through the entire board.");
            }
            int val = brd[row++][col];
            if (row == N) {
                row = 0;
                col++;
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

        public boolean hasNext() {
            return cur < END;
        }

        public void remove() {
            throw new UnsupportedOperationException("Remove has not been implemented.");
        }

        public Integer next() {
            return ++cur < END ? cur : 0;
        } // returns 1, 2, 3, END-1, 0
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        int N = this.dimension();
        Iterator thisBoard = new BoardIterator();
        int[][] brd = new int[N][N];
        int rowBlank = -1, colBlank = -1;
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                brd[r][c] = (int) thisBoard.next();
                if (brd[r][c] == 0) {
                    rowBlank = r;
                    colBlank = c;
                }
            }
        }
        LinkedList<Board> boards = new LinkedList<>();
        int[][] MOVES = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] move : MOVES) {
            int r = rowBlank + move[0], c = colBlank + move[1];
            if (0 <= r && r < N && 0 <= c && c < N) {
                int[][] thisMove = brd.clone();
                thisMove[rowBlank][colBlank] = thisMove[r][c];
                thisMove[r][c] = 0;
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
        for (int tile : Board.this) {
            str.append(' ').append(tile);
            if (++cnt % N == 0) {
                str.append('\n');
            }
        }
        return str.toString();
    }

    // board dimension N
    public int dimension() {
        return N;
    }

    // number of tiles out of place
    public int hamming() {
        Iterator GoalIter = new GoalIterator();
        int oops = 0; // out of place
        for (int curTile : Board.this) {
            if (!GoalIter.next().equals(curTile)) {
                oops++;
            }
        }
        return oops;
    }

    public int manhattan() {
        Iterator GoalIter = new GoalIterator();
        int manSum = 0;
        for (int curTile : Board.this) {
            int targ = (int) GoalIter.next();
            if (curTile != targ) {
                int delta = Math.abs(curTile - targ);
                manSum += (delta / N) + (delta % N);
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
                for (int thatVal : that) {
                    int thisVal = (int) thisIter.next();
                    if (thatVal != thisVal) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int[][] twinBoard = this.brd.clone();
        int tmp = twinBoard[0][0];
        twinBoard[0][0] = twinBoard[1][0];
        twinBoard[1][0] = tmp;
        return new Board(twinBoard);
    }

    // unit testing (not graded)
    public static void main(String[] args) {
    }

}


