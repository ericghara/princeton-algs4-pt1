import edu.princeton.cs.algs4.Digraph;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

// Designed to be controlled by MultiBFS but technically could be used in other cases
// To find path (if any) from one set of points to another set
public class BFS {
    private Digraph G;
    private LinkedList<Integer> Q;
    public HashSet<Integer> seen, otherSeen, ancestors;
    public HashMap<Integer,Integer> path;

    public BFS(Digraph G, Iterable<Integer> startNodes, HashSet<Integer> ancestors ) {
        this.G = G;
        Q = new LinkedList<>();
        seen = new HashSet<>();
        path = new HashMap<>();
        this.ancestors = ancestors;
        startNodes.forEach((v) -> {
            Q.add(v);
            seen.add(v);
        });
    }

    // Connect with another BFS object, allows sharing of seen hashset
    public void sync(BFS other) {
        HashSet<Integer> large, small;
        otherSeen = other.seen;
        other.otherSeen = seen;
        // Check intersections, using the smallest set (faster)
        if (otherSeen.size() > seen.size()) {
            small = seen;
            large = otherSeen;
        }
        else {
            large = otherSeen;
            small = seen;
        }
        for (int k : small) {
            if (large.contains(k)) {
                ancestors.add(k);
                break;
                }
            }
        }

    // Each BFS originating from all the input start nodes takes a step forward
    public int step() {
        int size = Q.size();
        for (int i = 0; i < size; i++) {
            int cur = Q.pop();
            for (int nbr : G.adj(cur)) {
                if (seen.add(nbr)) {
                    Q.addLast(nbr);
                    path.put(nbr, cur);
                    if (otherSeen.contains(nbr)) {
                        ancestors.add(nbr);
                    }
                }
            }
        }
        return Q.size();
    }
}