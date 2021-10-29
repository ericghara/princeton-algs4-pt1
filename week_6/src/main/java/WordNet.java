import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;

public class WordNet {

    private final HashMap<String,LinkedList<Integer> > ST = new HashMap<>();
    private final  ArrayList<String> keys = new ArrayList<>();
    private final  Digraph G;
    private final SAP sap;

    // constructor takes the name of the two input files
    // Hypernyms: child id parent id
    // Synsets: id,word,gloss (definition)
    public WordNet(String synsets, String hypernyms) {
        errorOnNull(synsets); errorOnNull(hypernyms);
        parseSynsets(synsets);
        G = new Digraph(keys.size());
        parseHypernyms(hypernyms);
        sap = new SAP(G);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        // Defensive copy: edits to keySet can affect HasMap
        String[] ks = ST.keySet().toArray(new String[0]);
        return Arrays.asList(ks);
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        errorOnNull(word);
        return ST.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        nounError(nounA, nounB);
        LinkedList <Integer> idsA = ST.get(nounA);
        LinkedList <Integer>  idsB = ST.get(nounB);
        return sap.length(idsA, idsB);
    }

    // a synsets (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in the shortest ancestral path
    public String sap(String nounA, String nounB) {
        nounError(nounA, nounB);
        LinkedList<Integer> idsA = ST.get(nounA);
        LinkedList<Integer> idsB = ST.get(nounB);
        int ancestor = sap.ancestor(idsA, idsB);
        if (ancestor == -1) {
            return String.format("%s and %s are not connected.", nounA, nounB);
        }
        return keys.get(ancestor);
    }

    // Input csv: id,word,gloss (gloss is unused)
    private void parseSynsets (String file) {
        In synFile = new In(file);
        while (synFile.hasNextLine()) {
            String[] line = synFile.readLine().split(",");
            if (line.length < 2) {
                continue;
            } // implement multiple root detection here
            int i = Integer.parseInt(line[0]);
            if (keys.size() != i) {
                throw new IllegalArgumentException("Found a Key : ID mismatch in synsets input file at line: " + keys.size());
            }
            String[] ks = line[1].split(" ");
            keys.add(ks[0]);
            for (String k : ks) {
                LinkedList<Integer> vals;
                if (ST.containsKey(k)) {
                    vals = ST.get(k);
                }
                else {
                    vals = new LinkedList<>();
                    ST.put(k, vals);
                }
                vals.add(i);
            }
        }
        keys.trimToSize();
    }

    private void parseHypernyms (String file) {
        // Parse hypernyms
        In hypFile = new In(file);
        while (hypFile.hasNextLine()) {
            String[] line = hypFile.readLine().split(",");
            int v = Integer.parseInt(line[0]);
            for (int i = 1; i < line.length; i++) {
                int w = Integer.parseInt(line[i]);
                G.addEdge(v,w);
            }
        }
    }

    private void nounError(String a, String b) {
        errorOnNull(a); errorOnNull(b);
        if (!isNoun(a) || !isNoun(b)) {
            throw new IllegalArgumentException("Received a noun input which is not in the WordNet.");
        }
    }

    private void errorOnNull(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Received a null input.");
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        if (args.length != 4) { throw new IllegalArgumentException("main: received in improper number of arguments." +
                "Usage: Wordnet synsetsFile.txt hypernymsFile.txt nounA nounB"); }
        String synsets = args[0];
        String hypernyms = args[1];
        WordNet WN = new WordNet(synsets, hypernyms);
        //WN.nouns().forEach(System.out::println);
        String nounA = args[2];
        String nounB = args[3];
        System.out.printf("Mutual parent of %1$s and %2$s: %3$s.%n", nounA, nounB, WN.sap(nounA, nounB)  );
        System.out.printf( "Distance between %1$s and %2$s: %3$s,%n", nounA, nounB, WN.distance(nounA, nounB) ) ;
    }
}