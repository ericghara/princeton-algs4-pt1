import edu.princeton.cs.algs4.In;
import java.util.Arrays;


public class Outcast {
    private WordNet WN;
    public Outcast(WordNet wordnet) {
        WN = wordnet;
    }

    public String outcast(String[] nouns) {
        int nounIdx = -1;
        int[] distances = new int[nouns.length];
        for (int i = 0; i < nouns.length-1; i++) {
            for (int j = i; j < nouns.length; j++) {
                int d = WN.distance(nouns[i], nouns[j]);
                distances[i] += d;
                distances[j] += d;
            }
        }
        int maxI = 0;
        for (int i = 1; i < distances.length; i ++)  {
            if (distances[maxI] < distances[i]) { maxI = i ;}
            }
        return nouns[maxI];
        }

    // Test client usage: synsets.txt hypernyms.txt noun1 noun2 noun3
    // Finds the least related of the inputs
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            System.out.printf("Nouns: %s %n", Arrays.toString(nouns));
            System.out.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}