package ericghara.week_9;

import java.util.LinkedList;
import java.util.HashSet;


/**
 * This is a solver for Boggle (essentially a word search).  It implements hash based data structures (hashmap
 * and hashset) as opposed to the more commonly used trie.  This solver is able to maintain comparable speed
 * to a trie by using a continuously updated hash (similar to a rolling hash, but unbounded).  In order to match words
 * hash based fingerprinting is used, strings are never directly compared, hence this is a Monte Carlo algorithm.
 * Benchmarks (compared to an optimized Trie solver): Speed: 1.88x (slower), Space: 0.58x (less).
 */
public class BoggleSolver {

    private final WordSearch search = new WordSearch();

    private enum Directions {
        S(1,0),   N(-1,0),
        SE(1,1),  NW(-1,-1),
        E(0,1),   W(0,-1),
        NE(-1,1), SW(1,-1);

        final int r,c;

        Directions(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    private static final  int[] score = {0,0,0,1,1,2,3,5,11}; // index: word length, value: points
    private static final int NUM_DIRECTIONS = Directions.values().length;
    private final WordMap WM;

    /**
     * Constructs a new BoggleSolver object.
     * @param dictionary The dictionary to be used by the solver
     */
    public BoggleSolver(String[] dictionary) {
        WM = new WordMap(dictionary);
    }

    /**
     * Solves the board by returning all valid words based on the input dictionary
     * @param board the board to be solved
     * @return words found, as an Iterable of Strings
     */
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null) {
            throw new IllegalArgumentException("Received a null Board");
        }
        search.solveBoard(board);
        return search.found();
    }

    /**
     * Calculates the points for finding a word.  A word not found in the dictionary scores 0 points.
     * Additionally, words that cannot be formed with a Boggle board, score 0.  These are words that
     * have a Q without a trailing U, eg "QATAR".
     *
     * @param word word to search for in the dictionary
     * @return point value of word
     */
    public int scoreOf(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Received a null word.");
        }
        int n = word.length();
        if (n > 2 && WM.validWord(word) == null) { return 0; }
        if (n < score.length) {
            return score[n];
        }
        return score[score.length-1];
    }

    private class WordSearch {

        LinkedList<String> found;
        int[] board;    // boggle board converted to char vals as a flattened matrix
        int[] moves;    // available moves from each board position. Flattened matrix.
        int R, C, N;

        /**
         * Initializes everything to zero or empty values.  For use with {@code solveBoard}
         */
        private WordSearch(){
            R = 0;  // number of rows
            C = 0;  // number of columns
            N = 0;  // number of letters
            found = new LinkedList<>(); // found valid words are stored here
            moves = new int[0];
            board = new int[0];
        }

        /**
         * Finds all valid words in an input board, which can subsequently be accessed by {@code getFound}.
         * There is no need to call {@code solveBoard}.  Although {@code solveBoard} can be used if another board
         * needs to be solved without creating a new WordSearch object.
         *
         * @param board BoggleBoard to search for valid words in.
         */
        private WordSearch(BoggleBoard board) {
            R = board.rows();
            C = board.cols();
            N = R*C;
            found = new LinkedList<>(); // found valid words are stored here
            if (N >= WordMap.MIN_WORD_LEN) {
                this.board = new int[N];
                importBoard(board);
                calcMoves();
                dfs();
            }
            else { this.board = new int[0]; } // don't search if board to small to create scorable words
        }

        /**
         * Used to solve a new board with an already constructed wordSearch object.  Faster than creating
         * a new WordSearch for each new board when board dimensions are the same.  If the board dimensions
         * are different there is no performance penalty vs constructing a new WordSearch.
         *
         * @param board BoggleBoard to solve
         */
        void solveBoard(BoggleBoard board) {
            int r = board.rows();
            int c = board.cols();
            int newN = r * c;
            found = new LinkedList<>();
            if (newN != N && newN >= WordMap.MIN_WORD_LEN ) {
                N = newN;
                this.board = new int[N];
            }
            if (r != R || c != C) {
                R = r;
                C = c;
                calcMoves();
            }
            importBoard(board);
            dfs();
        }

        /**
         * Calculates all legal moves from each board position.  Moves are stored in a flattened
         * matrix.  Offers a significant performance improvement over a non-memoized approach.
         * Valid moves >= 0 invalid moves == -1.
         */
        private void calcMoves() {
            Directions[] dirs = Directions.values();
            moves = new int[N * dirs.length];
            for (int movesI = 0, boardI = 0; boardI < N; ) {
                for (Directions d : dirs) {
                    moves[movesI++] = move(boardI, d);
                }
                boardI++;
            }
        }


        /**
         * Pulls all letters from a BoggleBoard object
         * @param board to be imported and solved.
         */
        private void importBoard(BoggleBoard board) {
            int i = 0;
            for (int r = 0; r < R; r++) {
                for (char c = 0; c < C; c++) {
                    this.board[i++] = board.getLetter(r, c);
                }
            }
        }

        /**
         * Gives the destination index after moving from a start index in a given direction.
         * @param i start index
         * @param d direction
         * @return position after moving in the given direction; -1 if the move is impossible.
         */
        private int move(int i, Directions d) {
            int r = (i/C) + d.r;  // row after move
            int c = (i%C) + d.c;  // col after move
            if ( r < 0 || r >= R || c < 0 || c >= C) { return -1; }
            return r * C + c;
        }

        /**
         * Converts a row and column to an index in the flattened board.
         *
         * @param r row
         * @param c column
         * @return index in flattened board
         */
        private int getBoardIndex(int r, int c) {  return r * C + c; }

        /**
         * Begins the word search. Provides DFS worker a stack with only a single tile in it (ie will call dfsWorker
         * N times).
         */
        private void dfs() {
            LinkedList<DFSHash> stack = new LinkedList<>();
            boolean[] seen = new boolean[N];
            HashSet<DFSHash> foundSet = new HashSet<>();
            for (int i = 0; i < N; i++) {
                DFSHash parent = new DFSHash(i, board[i]);
                stack.push(parent);
                dfsWorker(stack, foundSet, seen);
            }
        }

        /**
         * Iterative DFS to find board words.  Uses WordMap's validSub to determine when to backtrack (ie when
         * a substring cannot lead to valid words).  The entire matching process is hash based.  A hash is
         * continuously updated with each added letter and word matches are found in the HashMap using the hash
         * as a key and String as the value.  Duplicates are prevented using a set of found word hashes.  To implement
         * this, WordHash and DFSHash data types are used which are lightweight containers for hashes.
         *
         * Modifies the {@code found} instance variable
         *
         * @param stack origin of the DFS, in typical operation stack should contain a single DFSHash item
         * @param foundSet hashes of words which have already been added to found (prevents duplicates)
         * @param seen seen words.  dfsworker should receive an array filled with false values and will
         *             return an array of false values.
         */
        private void dfsWorker(LinkedList<DFSHash> stack,HashSet<DFSHash> foundSet, boolean[] seen) {
            for (DFSHash cur = stack.peek(); !(stack.isEmpty()); cur = stack.peek()) {
                int i = cur.index;
                if (seen[i]) {
                    seen[i] = false;
                    stack.pop();
                    continue;
                }
                seen[i] = true;
                final int startM = i*NUM_DIRECTIONS;  // calc starting position in moves array
                for (int m = startM; m < startM + NUM_DIRECTIONS; m++) {
                    int nextI = moves[m];
                    if (nextI == -1 || seen[nextI]) { continue; }

                    DFSHash nextH = new DFSHash(cur, nextI, board[nextI]);
                    if (WM.validSub(nextH)) {
                        stack.push(nextH);
                    }
                    String word = WM.validWord(nextH);
                    if (word != null && foundSet.add(nextH)) {
                        found.push(word);
                    }
                }
            }
        }

        /**
         * Allows client to retrieve found words
         * @return list of found words based on the dictionary provided, Contains no duplicates.
         */
        public Iterable<String> found() { return found; }
    }

    /**
     * Solves the input boards and prints to stdout the words found and the total score for the board.
     * Board files should be UTF-8 encoded text files.  Below is the example of a file for a 4x4 board.
     *<table>
     * <tr>4 4</tr>
     * <tr>U  T  G  W</tr>
     * <tr>L  T  N  T</tr>
     * <tr>P  S  R  N</tr>
     * <tr>B  C  X  C</tr>
     *</table>
     *
     * @param args Path to dictionary and path to board files separated by a space
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Provide [path to a dictionary file] [path to board1] [board2]...");
        }
        String[] dict = WordMap.parseDict(args[0]);
        BoggleSolver solver = new BoggleSolver(dict);
        for (int i = 1; i < args.length; i++) {
            BoggleBoard board = new BoggleBoard(args[i]);
            System.out.printf("Board %d: ", i);
            int score = 0;
            for (String word : solver.getAllValidWords(board) ) {
                System.out.printf("%s ", word);
                score += solver.scoreOf(word);
            }
            System.out.printf("%nScore board %d: %d%n", i, score);
        }
    }
}
