import edu.princeton.cs.algs4.Digraph;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

// Finds first intersection of 2 groups of synsets (input as a collection)
// Necessary b/c 1 noun can map to multiple synsets (meanings):
// ie "passing" maps to: 59442, 59443, 59444, 59445 , 59446
public class MultiBFS {

    private HashMap<Integer, Integer> aPath, bPath;
    private int ancestor;
    private final Digraph DAG;

    public MultiBFS(Iterable<Integer> A, Iterable<Integer> B, Digraph DAG) {
        this.DAG = DAG;
        ancestor = -1; // if no common ancestor -1
        bfsMaster(A, B);
    }

    private int bfsWorker(LinkedList<Integer> Q, HashSet<Integer> seen, HashSet<Integer> otherSeen, HashMap<Integer, Integer> path) {
        int size = Q.size();
        for (int i = 0; i < size; i++) {
            int cur = Q.pop();
            for (int nbr : DAG.adj(cur)) {
                if (seen.add(nbr)) {
                    Q.addLast(nbr);
                    path.put(nbr, cur);
                    if (otherSeen.contains(nbr)) {
                        ancestor = nbr;
                        return -1;
                    }
                }
            }
        }
        return Q.size();
    }

    private void bfsMaster(Iterable<Integer> A, Iterable<Integer> B) {
        LinkedList<Integer> aQ = new LinkedList<>();
        LinkedList<Integer> bQ = new LinkedList<>();
        HashSet<Integer> aSeen = new HashSet<>();
        HashSet<Integer> bSeen = new HashSet<>();
        aPath = new HashMap<>();
        bPath = new HashMap<>();
        A.forEach((v) -> {
            aQ.add(v);
            aSeen.add(v);
        });
        B.forEach((v) -> {
            // Make sure starting sets don't already intersect
            if (aSeen.contains(v)) {
                ancestor = v;
                return;
            }
            bQ.add(v);
            bSeen.add(v);
        });
        double aSize = 1;
        double bSize = 1;
        // Keep running as long as either has a queue and neither has
        // broken loop by finding an ancestor (sentinel value is -1)
        while ( aSize > 0 || bSize > 0 && ancestor < 0 ) {
            aSize = bfsWorker(aQ, aSeen, bSeen, aPath);
            // skips B if A already found ancestor
            if (ancestor < 0) bSize = bfsWorker(bQ, bSeen, aSeen, bPath);
        }
    }


    public int getAncestor() { return ancestor; }

    public HashMap<Integer,Integer> getPathA() { return aPath; }

    public HashMap<Integer,Integer> getPathB() { return bPath; }
}