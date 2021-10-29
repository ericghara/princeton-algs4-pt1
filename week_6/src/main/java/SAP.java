import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class SAP {
    private final Digraph DAG;
    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        errorOnNull(G);
        new DAG_Check(G);
        DAG = new Digraph(G);

    }

    // length of the shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        Integer[] vL = {v};
        Integer[] wL = {w};
        return length(Arrays.asList(vL), Arrays.asList(wL) );
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        Integer[] vL = {v};
        Integer[] wL = {w};
        return ancestor(Arrays.asList(vL), Arrays.asList(wL) );
    }

    // length of the shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        errorOnNull(v,w);
        MultiBFS bfs = new MultiBFS(v, w, DAG);
        return calcDistance(bfs);
    }

    // a common ancestor that participates in the shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        errorOnNull(v,w);
        MultiBFS bfs = new MultiBFS(v, w, DAG);
        return bfs.getAncestor();
    }

    private void errorOnNull(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Received a null input.");
        }
    }

    private void errorOnNull(Iterable<Integer> a, Iterable<Integer> b) {
        errorOnNull(a);
        errorOnNull(b);
        a.forEach(this::errorOnNull);
        b.forEach(this::errorOnNull);
    }

    private int[] ancestralPath(int v, int w) {
        Integer[] vL = {v};
        Integer[] wL = {w};
        return ancestralPath(Arrays.asList(vL), Arrays.asList(wL));
    }

    private int[] ancestralPath(Iterable<Integer> v, Iterable<Integer> w) {
        MultiBFS bfs = new MultiBFS(v, w, DAG);
        return showPath(bfs);
    }

    private int calcDistance(MultiBFS bfs) {
        int ancestor = bfs.getAncestor();
        HashMap<Integer,Integer>  aPath = bfs.getPathA();
        HashMap<Integer,Integer> bPath = bfs.getPathB();
        if (ancestor == -1) { return -1; }
        int length = 0;
        for (int nxt = ancestor; aPath.containsKey(nxt); nxt = aPath.get(nxt)) {
            length++;
        }
        for (int nxt = ancestor; bPath.containsKey(nxt); nxt = bPath.get(nxt)) {
            length++;
        }
        return length;
    }

    // Returns path to ancestor with A at path[0] and B at path[n-1] and ancestor somewhere between
    // not required for assignment
    private int[] showPath(MultiBFS BFS) {
        int ancestor = BFS.getAncestor();
        HashMap<Integer,Integer>  aPath = BFS.getPathA();
        HashMap<Integer,Integer> bPath = BFS.getPathB();
        if (ancestor == -1) {
            throw new IllegalArgumentException("Paths do not intersect");
        }
        LinkedList<Integer> path = new LinkedList<>();
        path.add(ancestor);
        for (int cur = ancestor; aPath.containsKey(cur); path.addLast(cur)) {
            cur = aPath.get(cur);
        }
        for (int cur = ancestor; bPath.containsKey(cur); path.addFirst(cur)) {
            cur = bPath.get(cur);
        }
        int[] out = new int[path.size()];
        for (int i = 0; i < out.length; i ++) { out[i] = path.removeLast(); }
        return out;
    }

    private static class DAG_Check {
        private final boolean[] seen;
        private final Digraph G;
        private int root;

        DAG_Check(Digraph G) {
            this.G = G;
            root = -1;
            seen = new boolean[G.V()];
            for (int i = 0; i < seen.length; i++) {
                if (dfs(i, i)) {
                    throw new IllegalArgumentException("Cycle found: Digraph is not a DAG.");
                }
            }
        }

        // returns true if a cycle is detected
        boolean dfs(int v, int origin) {
            if (seen[v]) {
                return false;
            }
            seen[v] = true;
            // Multi-root detection: 0 out-degree == root
            if (G.outdegree(v) == 0 ) {
                if (root == -1) {
                    root = v;
                } else if (v != root) {
                    throw new IllegalArgumentException("Found multiple roots " + root + " & " + v);
                }
            }
            // if origin point for this dfs is seen twice: cycle detected
            for (int w : G.adj(v)) {
                if (w == origin || dfs(w, origin)) {
                    return true;
                }
            }
            return false;
        }
    }

    // hypfile format: # vertices, followed by # edges, pairs of vertices, (each entry separated by whitespace)
    public static void main(String[] args) {
        int aVert = Integer.parseInt(args[1]);
        int bVert = Integer.parseInt(args[2]);
        if (args.length != 3) { throw new IllegalArgumentException("Invalid input.  Input should be path to hypernyms.txt"); }
        In hypFile = new In(args[0]);
        Digraph G = new Digraph(hypFile);
        SAP sap = new SAP(G);
        System.out.printf("Distance from %s to %s: %8d%n", aVert, bVert,sap.length(bVert, aVert));
        System.out.printf("Closest Ancestor of %1$s and %2$s: %3$d%n", aVert, bVert,sap.ancestor(bVert, aVert));
        System.out.printf("Path %s", Arrays.toString(sap.ancestralPath(bVert, aVert)));
    }
}