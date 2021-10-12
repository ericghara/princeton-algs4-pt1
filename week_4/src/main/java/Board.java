import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import java.util.Iterator;
import java.util.LinkedList;

/* This creates a board data type that is used by the solver.
*  Board 2d board is represented as a 1d array of chars.
*  While slightly cumbersome this is done more than half
*  memory usage vs a 2d array of ints.
*
* Board incorperates 2 priority functions, manhattan and hamming;
* Manhattan is more efficient and is what is actually used by solver.
 */


public class Board {

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    private final char[] brd; // using char to save memory; since solver requires exponential time,
    private final char N;  // num of rows and cols (virtual as board was flattened to 1d array)
    private final char NbyN; // total number of elements in board

    // converts 2D array to a board
    public Board(int[][] tiles) {
        N = (char) tiles.length;
        NbyN = (char) (N * N);
        brd = new char[NbyN];
        for (char r = 0; r < N; r++) {
            for (char c = 0; c < N; c++) {
                brd[r * N + c] = (char) tiles[r][c];
            }
        }
    }

    // Overloaded to allow board creation from 1D arrays
    // Be careful no input data checking
    private Board(char[] tiles) {
        brd = tiles.clone();
        N = (char) Math.sqrt(tiles.length);
        NbyN = (char) (N * N);
    }


    // This is the sequence of a solved board i.e.: 1, 2, 3...NxN-1, 0
    private Iterable<Character> goalBoard() {
        LinkedList<Character> goal = new LinkedList<>();

        for (char pos = 1; pos < this.NbyN; pos++) { goal.addLast(pos); }
        goal.addLast((char) 0);
        return goal;
    }

    // All potential boards accessible in one move.
    // Practically this means all possible moves where
    // Tiles surrounding blank space are moved into
    // Blank space.
    public Iterable<Board> neighbors() {
        int N = this.N;
        char posBlank = 0;
        for (char tile : brd) {
            if (tile == 0) { break; }
            posBlank++;
        }
        char rowBlank = (char) (posBlank/N);
        char colBlank = (char) (posBlank % N);
        LinkedList<Board> boards = new LinkedList<>();
        int[][] MOVES = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int[] move : MOVES) {
            int rNew = rowBlank + move[0];
            int cNew = colBlank + move[1];
            if (0 <= rNew && rNew < N && 0 <= cNew && cNew < N) {
                char posNew = (char) (rNew * N + cNew);
                char[] thisMove = brd.clone();
                swap(posNew, posBlank, thisMove);
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
        for (char tile: brd) {
            str.append(String.format("%2d ", (int) tile ));
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
        int oops = 0; // out of place
        Iterator<Character> goalIter = goalBoard().iterator();
        for (char cur: brd) {
            char targ = goalIter.next();
            if (targ != cur && targ != 0) { oops++;}
        }
        return oops;
    }

    // Sum of horizontal and vertical distances that out of place tiles need to move
    // to solve board
    public int manhattan() {
        Iterator<Character> goalIter = goalBoard().iterator();
        int manSum = 0;
        for (char curTile : brd) {
            char targ = goalIter.next();
            if (curTile != targ  && curTile != 0) {
                if (targ == 0) { targ = NbyN; } // placement of blank tile is at end
                curTile --; targ--;
                int dRow = Math.abs(curTile  / N - targ / N);
                int dCol = Math.abs(curTile % N - targ % N);
                manSum += dRow + dCol;
            }
        }
        return manSum;
    }


    // is this board solved (goal)?
    public boolean isGoal() {
        return hamming() == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y instanceof Board) {
            Board that = (Board) y;
            if (that.dimension() == this.dimension()) {
                for (char i = 0; i < NbyN; i++) {
                    if (this.brd[i] != that.brd[i]) { return false; }
                }
                return true;
            }
        }
        return false;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        char[] twinBoard = brd.clone();
        char ia = 0, ib = 1;
        if (twinBoard[ia] == 0 || twinBoard[ib] == 0) { ia += N; ib += N;  }  // a = {0, 1}, b = {1, 1};
        swap(ia, ib, twinBoard);
        return new Board(twinBoard);
    }

    // swap tiles at index ia and ib
    private void swap(char ia, char ib, char[] board) {
        char tmp = board[ia];
        board[ia] = board[ib];
        board[ib] = tmp;
    }

    /* unit testing
    * Provide location to a board text file as a command line argument
    * Text file format:
    * 3
    *  8  1  3
    *  4  0  2
    *  7  6  5
    */
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


