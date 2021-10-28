import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

import java.util.*;

public class SAP {
    private final Digraph DAG;
    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        errorOnNull(G);
        new ErrorOnCycle(G);
        DAG = new Digraph(G);

    }

    // length of shortest ancestral path between v and w; -1 if no such path
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

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        MultiBFS bfs = new MultiBFS(v,w);
        return bfs.getLength();
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        MultiBFS bfs = new MultiBFS(v,w);
        return bfs.getAncestor();
    }

    private void errorOnNull(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Received a null input.");
        }
    }

    static class ErrorOnCycle {
        boolean[] seen;
        Digraph G;

        ErrorOnCycle(Digraph G) {
            this.G = G;
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
            for (int w : G.adj(v)) {
                if (w == origin || dfs(w, origin)) {
                    return true;
                }
            }
            return false;
        }
    }

        // Finds first intersection of 2 groups of synsets (input as a collection)
        // Necessary b/c 1 noun can map to multiple synsets (meanings):
        // ie "passing" maps to: 59442, 59443, 59444, 59445 , 59446
        private class MultiBFS {
            private int[] pathA, pathB;   // Path to common Ancestor
            private int ancestor;
            private int length;
            HashMap<Integer, Integer> aPath;
            HashMap<Integer, Integer> bPath;

            public MultiBFS(Iterable<Integer> A, Iterable<Integer> B) {
                ancestor = -1; // if no common ancestor -1
                bfs(A, B);
                followPath();
            }

            // Retraces BFS to find shortest path implemented for testing
            private void tracePath() {

            }

            public int getAncestor() {return ancestor; }

            public int getLength() {return length; }

            private void followPath() {
                if (ancestor > -1) {
                    length = 0;
                    for (int nxt = ancestor; aPath.containsKey(nxt); nxt = aPath.get(nxt) ) {
                        length++; }
                    for (int nxt = ancestor; bPath.containsKey(nxt); nxt = bPath.get(nxt) ) {
                        length++; }
                }
                else { length = -1; }
            }

            private void bfs(Iterable<Integer> A, Iterable<Integer> B) {
                LinkedList<Integer> aQ = new LinkedList<>();
                LinkedList<Integer> bQ = new LinkedList<>();
                HashSet<Integer> aSeen = new HashSet<>();
                HashSet<Integer> bSeen = new HashSet<>();
                aPath = new HashMap<>();
                bPath = new HashMap<>();
                A.forEach((v) -> { aQ.add(v); aSeen.add(v); } );
                B.forEach((v) -> { bQ.add(v); bSeen.add(v); } );

                while (!aQ.isEmpty() || !bQ.isEmpty()) {
                    int size = aQ.size();
                    for (int i = 0; i < size; i++) {
                        int cur = aQ.pop();
                        for (int nbr : DAG.adj(cur)) {
                            if (aSeen.add(nbr)) {
                                aQ.addLast(nbr);
                                aPath.put(nbr, cur);
                                if (bSeen.contains(nbr)) {
                                    aQ.clear(); bQ.clear();
                                    ancestor = nbr;
                                    size = 0; // clean up how this exits
                                    break;
                                }
                            }
                        }
                    }
                    size = bQ.size();
                    for (int i = 0; i < size; i++) {
                        int cur = bQ.pop();
                        for (int nbr : DAG.adj(cur)) {
                            if (bSeen.add(nbr)) {
                                bQ.addLast(nbr);
                                bPath.put(nbr, cur);
                                if (aSeen.contains(nbr)) {
                                    aQ.clear(); bQ.clear();
                                    ancestor = nbr;
                                    size = 0;
                                    break;
                                }
                            }
                        }
                    }
                }
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
    }
}