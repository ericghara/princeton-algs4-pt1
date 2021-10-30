import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import java.util.Arrays;

public class SAP {
    private final Digraph G;
    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        errorOnNull(G);
        //new DAG_Check(G);
        this.G = new Digraph(G);

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
        errorOnInvalid(v,w);
        MultiBFS mBFS = new MultiBFS(v, w, G);
        return mBFS.getMinLength();
    }

    // a common ancestor that participates in the shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        errorOnInvalid(v,w);
        MultiBFS mBfs = new MultiBFS(v, w, G);
        return mBfs.getMinAncestor();
    }

    private void errorOnNull(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Received a null input.");
        }
    }

    private void errorOnInvalid(Integer v) {
        errorOnNull(v);
        if (0 > v || v >= G.V()) {
            throw new IllegalArgumentException("Received a vertex that is not in the graph");
        }
    }

    private void errorOnInvalid(Iterable<Integer> a, Iterable<Integer> b) {
        errorOnNull(a);
        errorOnNull(b);
        a.forEach(this::errorOnInvalid);
        b.forEach(this::errorOnInvalid);
    }

    private int[] ancestralPath(int v, int w) {
        Integer[] vL = {v};
        Integer[] wL = {w};
        return ancestralPath(Arrays.asList(vL), Arrays.asList(wL));
    }

    private int[] ancestralPath(Iterable<Integer> v, Iterable<Integer> w) {
        MultiBFS mBFS = new MultiBFS(v, w, G);
        return mBFS.showPath();
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