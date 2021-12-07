import java.util.LinkedList;
import java.util.HashSet;

public class BoggleSolver {

    enum Directions {
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

    private final WordMap WM;


    public BoggleSolver(String[] dictionary) {
        WordHash.hashFn = new HashAlgs.FNV1a();
        WM = new WordMap(dictionary);
    }

    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null) {
            throw new IllegalArgumentException("Received a null Board");
        }
        WordSearch search = new WordSearch(board);
        return search.found();
    }

    public int scoreOf(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Received a null word.");
        }
        int n = word.length();
        if (n < score.length) {
            return score[n];
        }
        return score[score.length-1];
    }

    private class WordSearch {

        final LinkedList<String> found;
        final int[] board;
        final int R, C, N;

        private WordSearch(BoggleBoard board) {
            R = board.rows();
            C = board.cols();
            N = R*C;
            found = new LinkedList<>();
            if (N >= WordMap.MIN_WORD_LEN) {
                this.board = new int[N];
                importBoard(board);
                dfs();
            }
            else { this.board = new int[0]; }
        }

        private void importBoard(BoggleBoard board) {
            int i = 0;
            for (int r = 0; r < R; r++) {
                for (char c = 0; c < C; c++) {
                    this.board[i++] = board.getLetter(r, c);
                }
            }
        }

        private int move(int i, Directions d) {
            int r = (i/C) + d.r;  // row after move
            int c = (i%C) + d.c;  // col after move
            if ( r < 0 || r >= R || c < 0 || c >= C) { return -1; }
            return r * C + c;
        }

        private int getBoardIndex(int r, int c) {  return r * C + c; }

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

        private void dfsWorker(LinkedList<DFSHash> stack,HashSet<DFSHash> foundSet, boolean[] seen) {
            for (DFSHash cur = stack.peek(); !(stack.isEmpty()); cur = stack.peek()) {
                int i = cur.index;
                if (seen[i]) {
                    seen[i] = false;
                    stack.pop();
                    continue;
                }
                seen[i] = true;
                for (Directions d : Directions.values()) {
                    int nextI = move(i, d);
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

        public Iterable<String> found() { return found; }
    }

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
            System.out.printf("%nScore: %d", score);
        }
    }
}
