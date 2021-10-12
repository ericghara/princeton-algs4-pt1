import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import java.util.LinkedList;


// Solves slider puzzles using an A* search algorithm.  Also detects if a board is unsolvable
public class Solver {
    private int moves = -1;
    private SearchNode goalNode = null;

    /* A* implemented using a min priority queue and a priority function based on
    *  # moves made + summation of vertical and horizontal distances of all out of
    *  place tiles from their target positions. (see Manhattan function in Board module)
    *  Utilizes a game tree to keep track of all potential moves (see SearchNode) */
    public Solver(Board initial) {
        if (initial == null) { throw new IllegalArgumentException("Received a null board as an input."); }
        MinPQ<SearchNode> PQ = new MinPQ<>();
        PQ.insert(new SearchNode(initial, null));
        while ( !PQ.isEmpty() ) {
            SearchNode minNode = PQ.delMin();
            if (minNode.isGoal()) {
                moves = minNode.getMoves();
                goalNode = minNode;
                break;
            }
            if (( minNode.getPriority() - minNode.getMoves() ) == 2 && minNode.twinIsGoal()) { break; }  // only useful to start checking twin close to goal
            for (Board nbr : minNode.getNeighbors() ) {
                if (minNode.getMoves() > 0) {
                    if (!nbr.equals(minNode.getParent().getBoard())) {  // optimization
                        PQ.insert(new SearchNode(nbr, minNode));
                    }
                }
                else { PQ.insert(new SearchNode(nbr, minNode)); }
            }
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() { return moves > -1; }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() { return moves; }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        LinkedList<Board> out = null;
        if (isSolvable()) {
            out = new LinkedList<>();
            SearchNode curNode = goalNode;
            while (curNode != null ) {
                out.addFirst(curNode.getBoard());
                curNode = curNode.getParent();
            }
        }
        return out;
    }


    /*  Tree data structure where each node stores a board and caches its priority func values
    *   Structure: Root is null (level 0), initial board (level 1), all possible moves from initial board (level 2)...
    *   All Boards connect back to their parent, so when a solution is found traversal from solved board
    *   to parent boards is possible and this path represents (the reverse of) the shortest path from root to a
    *   solved board.
    */
    private class SearchNode implements Comparable<SearchNode> {
        private final  SearchNode parent;
        private final Board board;
        private int moves = 0;
        private int priority;
        SearchNode(Board curBoard, SearchNode parentNode) {
            board = curBoard;
            parent = parentNode;
            if (parent != null) { moves = parent.getMoves() + 1; }
            setMPriority();

        }
        private int getMoves() { return moves; }

        private SearchNode getParent() { return parent; }

        private void setHPriority() { priority = moves + board.hamming(); }

        private void setMPriority() { priority = moves + board.manhattan(); }

        private int getPriority() { return priority; }

        public int compareTo (SearchNode otherNode) { return Integer.compare(getPriority(), otherNode.getPriority()); }

        private Iterable<Board> getNeighbors() { return board.neighbors(); }

        private boolean isGoal() { return board.isGoal(); }

        private boolean twinIsGoal() { return board.twin().isGoal(); }

        private Board getBoard() { return board; }

    }

    // test client provided by instructor
    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

}