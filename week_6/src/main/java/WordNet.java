import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.IntStream;

public class WordNet {

    private final HashMap<String,Integer> ST = new HashMap<>();
    final private ArrayList<String> keys = new ArrayList<>();
    final private Digraph G;

    // constructor takes the name of the two input files
    // Hypernym more general: go up graph
    // Hyponym more specific: go down graph
    // Hypernym: child,parent,parent...
    // Synset: id,word,gloss (definition)
    public WordNet(String synsets, String hypernyms) {
        isNull(synsets); isNull(hypernyms);
        // Parse synsets
        In synFile = new In(synsets);
        while (synFile.hasNextLine()) {
            // csv: id,word,gloss (definition) (gloss is unused)
            String[] line = synFile.readLine().split(",");
            if (line.length < 2) {  continue; } // implement multiple root detection here
            int i = Integer.parseInt(line[0]);
            //String[] ks = line[1].split(" ");
            String ks = line[1];
            if (keys.size() != i) {
                throw new IllegalArgumentException("Found a Key : ID mismatch in synset input file at line: " + keys.size());
            }
            //keys.add(ks[0]);
            keys.add(ks);
            //for (String k : ks) { ST.put(k, i);}
            ST.put(ks, i);
        }
        keys.trimToSize();
        G = new Digraph(keys.size());
        // Parse hypernyms
        In hypFile = new In(hypernyms);
        while (hypFile.hasNextLine()) {
            String[] line = hypFile.readLine().split(",");
            int v = Integer.parseInt(line[0]);
            for (int i = 1; i < line.length; i++) {
                int w = Integer.parseInt(line[i]);
                G.addEdge(v,w);
            }
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() { return ST.keySet(); }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        isNull(word);
        return ST.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        nounError(nounA, nounB);
        int[] pathA = rootPath( ST.get(nounA) ).toArray();
        int[] pathB = rootPath( ST.get(nounB) ).toArray();
        int iA = pathA.length - 1;
        int iB = pathB.length - 1;
        if (pathA[iA--] != pathB[iB--]) { return -1; }
        while (iA >= 0 && iB >= 0) {
            if (pathA[iA] != pathB[iB]) { break; }
            iA--; iB--;
        }
        iA++; iB++;
        return iA + iB ;
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        int[] pathA = rootPath( ST.get(nounA) ).toArray();
        int[] pathB = rootPath( ST.get(nounB) ).toArray();
        int iA = pathA.length - 1;
        int iB = pathB.length - 1;
        if (pathA[iA--] != pathB[iB--]) { return ""; }
        while (iA >= 0 && iB >= 0) {
            if (pathA[iA] != pathB[iB]) { break; }
            --iA; --iB;
        }
        // need to move 1 step before fault was detected
        iA ++;
        return keys.get(pathA[iA]);
    }

    private void nounError(String a, String b) {
        isNull(a);
        isNull(b);
        if (!isNoun(a) || !isNoun(b)) {
            throw new IllegalArgumentException("Recieved a noun input which is not in the WordNet.");
        }
    }

    private void isNull(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Received a null input.");
        }
    }

    private IntStream rootPath(int v) {
        IntStream.Builder path = IntStream.builder();
        for (Iterable<Integer> adj = G.adj(v); G.outdegree(v) > 0; adj = G.adj(v)) {
            path.add(v);
            ArrayList<Integer> adjLst = new ArrayList<>();
            adj.iterator().forEachRemaining(adjLst::add);
            if (adjLst.size() == 1) { v = adjLst.get(0); }
            else {
                IntStream shortest = null;
                for (int w : adjLst ) {
                    IntStream cur = rootPath(w);
                    if (shortest == null || cur.count() < shortest.count()) { shortest = cur; }
                }
                // despite IDEA warnings shortest cannot be null because adjList.size() > 1
                return IntStream.concat(shortest, path.build());
            }
        }
        path.add(v);
        return path.build();
    }

    private int nounToIndex(String noun) {
        Object idx = ST.get(noun);
        if (idx == null) { throw new IllegalArgumentException("Couldn't find noun: " + noun+ "."); }
    return (int) idx;
    }



    // do unit testing of this class
    public static void main(String[] args) {
        if (args.length != 2) { throw new IllegalArgumentException("main: received in improper number of arguments." +
                "Usage: Wordnet synsetsFile.txt hypernymsFile.txt"); }
        String synsets = args[0];
        String hypernyms = args[1];
        WordNet WN = new WordNet(synsets, hypernyms);
        //WN.nouns().forEach(System.out::println);
        //String noun1 = "horse";
        String noun1 = "horse Equus_caballus";
        //String noun2 = "lion";
        String noun2 = "lion king_of_beasts Panthera_leo";
        int[] path1 = WN.rootPath(WN.nounToIndex(noun1)).toArray();
        int[] path2 = WN.rootPath(WN.nounToIndex(noun2)).toArray();
        System.out.printf("Path %30s: %s%n", noun1, Arrays.toString(path1));
        System.out.printf("Path %30s: %s%n", noun2, Arrays.toString(path2));
        System.out.printf("Mutual parent of %1$s and %2$s: %3$s.%n", noun1, noun2, WN.sap(noun1, noun2)  );
        System.out.printf( "Distance between %1$s and %2$s: %3$s,%n", noun1, noun2, WN.distance(noun1, noun2) ) ;

    }
}