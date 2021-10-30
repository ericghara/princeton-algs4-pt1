import edu.princeton.cs.algs4.Digraph;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

// Finds first intersection of 2 groups of synsets (input as a collection)
// Necessary b/c 1 noun can map to multiple synsets (meanings):
// ie "passing" maps to: 59442, 59443, 59444, 59445 , 59446
public class MultiBFS {

    private final HashSet<Integer> ancestors;
    private final Digraph G;
    private final BFS A, B;
    private int minAncestor, minLength;

    public MultiBFS(Iterable<Integer> nodesA, Iterable<Integer> nodesB, Digraph G) {
        this.G = G;
        ancestors = new HashSet<>();
        A = new BFS(G,nodesA, ancestors);
        B = new BFS(G, nodesB, ancestors);
        A.sync(B);
        bfsBoss();
        calcLength();
    }

    public int getMinLength() { return minLength; }

    public int getMinAncestor() { return minAncestor; }

    private void bfsBoss() {
        if (!ancestors.isEmpty()) { return; } // Starting sets intersect
        double aSize = 1;
        double bSize = 1;
        int dir = 1;
        int cnt = 0;

        /* Keeps running as long as either has a queue, counts up before first ancestor is detected,
        *  then counts down.  This is because first detected ancestor is not necessarily the shortest path
        *  ex. A--x--B @ step: 3, length: 5; A--->B--->  @ step 4, length 4
        *  these examples represent lower and higher bounds for # of dfs steps (n-1 steps after first encounter) */

        while ( aSize > 0 || bSize > 0  && cnt >= 0 ) {
            if (!ancestors.isEmpty() && dir == 1) {
                dir = -1;
                cnt += dir; // (n-1) steps after first encounter
            }
            else { cnt += dir; }
            aSize = A.step();
            bSize = B.step();
        }
    }

    private void calcLength() {
        HashMap<Integer,Integer>  aPath = A.path;
        HashMap<Integer,Integer> bPath = B.path;
        if (ancestors.isEmpty()) {
            minAncestor = minLength = -1;
            return;
        }
        int minLen = ~(-1<<31);
        int minAnc = -1;
        for (int a : ancestors) {
            int len = 0;
            for (int nxt = a; aPath.containsKey(nxt); nxt = aPath.get(nxt)) {
                len++;
            }
            for (int nxt = a; bPath.containsKey(nxt); nxt = bPath.get(nxt)) {
                len++;
            }
            if (len < minLen) {
                minLen = len;
                minAnc = a;
            }
        }
        minAncestor = minAnc;
        minLength = minLen;
    }

    // Returns path to ancestor with A at path[0] and B at path[n-1] and ancestor somewhere between
    // not required for assignment
    public int[] showPath() {
        HashMap<Integer,Integer>  aPath = A.path;
        HashMap<Integer,Integer> bPath = B.path;
        if (minAncestor == -1) {
            System.out.println("Paths do not intersect");
        }
        LinkedList<Integer> path = new LinkedList<>();
        path.add(minAncestor);
        for (int cur = minAncestor; aPath.containsKey(cur); path.addLast(cur)) {
            cur = aPath.get(cur);
        }
        for (int cur = minAncestor; bPath.containsKey(cur); path.addFirst(cur)) {
            cur = bPath.get(cur);
        }
        int[] out = new int[path.size()];
        for (int i = 0; i < out.length; i ++) { out[i] = path.removeLast(); }
        return out;
    }


    public static void main(String[] args) {
        System.out.println("Unit test through SAP class");
    }
}