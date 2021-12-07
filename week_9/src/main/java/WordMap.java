import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.LinkedList;
import java.io.File;  // For testing, comment for autograder


/**
 *  Allows checking for valid boggle words and substrings that <i>could</i> lead to valid words.
 *  A valid word is: >= 3 letters long and only contain Q with a directly following U.  Words are
 *  stored based on their hash fingerprints, to find a word in the dictionary or to find a substring
 *  words are not actually compared to each other, instead a word match is approximated to a very
 *  high degree of certainty.
 */
public class WordMap {
    static int DICT_N_MULTIPLE = 3; // Note: modify to tune performance and memory usage
    static int SUBS_N_MULTIPLE = 9; // Note: modify to tune performance and memory usage
    static int MIN_WORD_LEN = 3;
    static int Q = 'Q';
    static int U = 'U';

    final HashMap<WordHash, String> dict; // Dictionary
    final HashSet<WordHash> subs; // Substrings that lead to valid words

    /**
     * Constructs a WordMap object
     *
     * @param dictionary words WordMap will parse for valid Boggle words and generate a substring set from.
     */
    public WordMap(String[] dictionary) {
        if (dictionary == null) {
            throw new IllegalArgumentException("Received a null dictionary");
        }
        int dictCap = DICT_N_MULTIPLE * dictionary.length;
        int subsCap = SUBS_N_MULTIPLE * dictionary.length;
        dict = new HashMap<>(dictCap);
        subs = new HashSet<>(subsCap);
        makeMaps(dictionary);

    }

    /**
     * Determines if the WordHash object represents the substring of a valid Boggle word.
     *
     * @param sub substring query
     * @return {@code true} if a valid substring {@code false} if not a valid substring.
     */
    public boolean validSub(WordHash sub) { return subs.contains(sub); }

    /**
     * Determines if word (hash) is in the dictionary <em>and</em> a playable Boggle word.
     *
     * @param word dictionary query
     * @return null if word is not found; word (String) if the word is found.
     */
    public String validWord(WordHash word) { return dict.get(word); }

    /**
     * Determines if a word is in the dictionary <em>and</em> a playable Boggle word.
     *
     * @param word dictionary query
     * @return null if word is not found; word (String) if the word is found.
     */
    public String validWord(String word) {
        WordHash wHash = new WordHash(WordHash.intArray(word)); // Must hash word to search
        return dict.get(wHash);
    }

    /* Genearates subs and dict objects */
    private void makeMaps(String[] dictionary) {
        /* There is no good way to backtrack when an invalid word is found as it is
        difficult to determine if a sub was added for the invalid word or was already
        in the dictionary.  We need to make sure a word is valid then parse its subs */
        for (String word : dictionary) {
            int n = word.length();
            if (n < MIN_WORD_LEN || word.charAt(n - 1) == Q) {
                continue;
            }
            int j;
            for (j = 0; j < n; j++) {
                if (word.charAt(j) == Q && word.charAt(++j) != U) {
                    break;
                }
            }
            if (j == n) {
                add(word);
            }
        }
    }

    /* Adds substring hashes to subs and maps WordHashes to strings  */
    private void add(String word) {
        WordHash wHash = new WordHash();
        int n = word.length();
        int i;
        for (i = 0; i < n - 1; i++) {
            int c = word.charAt(i);
            if (c == Q) {
                if (i < n - 2) {
                    i++;
                } // skip U
                else {
                    break;
                } // Word ends in QU so Q is effectively the last letter
            }
            wHash.append(c);
            subs.add(new WordHash(wHash));
        }
        wHash.append(word.charAt(i));
        dict.put(wHash, word);
    }


    /* Converts a dictionary file to a String Array.  File should be words separated by a whitespace character */
    static String[] parseDict(String path) {
        // for testing. Remove for autograder
        LinkedList<String> dList = new LinkedList<>();
        Scanner scanner;
        try {
            File file = new File(path);
            scanner = new Scanner(file);
        } catch (Exception e) {
            throw new IllegalArgumentException("Provide path to a dictionary file.");
        }
        while (scanner.hasNext()) {
            String word = scanner.next();
            dList.add(word);
        }
        scanner.close();
        return dList.toArray(new String[0]);
    }

    /* Type in a word to determine if it is a valid Boggle word and if it's a substring of another boggle word.
       Result is printed to stdout.
     */
    private void interactiveSearch() {
        Scanner in = new Scanner(System.in);
        System.out.println("Welcome to the Interactive Dictionary Search.\n" +
                "Type a word and press enter; !q to exit");
        while (in.hasNextLine()) {
            String q = in.next().toUpperCase();
            if (q.equals("!Q")) { break; }
            WordHash wh = new WordHash(WordHash.intArray(q));
            System.out.printf("Querry %s\tDictionary: %s\tSubstrings: %s%n", q,
                    dict.containsKey(wh) ? "Found" : "Not Found", subs.contains(wh) ? "Found" : "Not Found");
        }

    }

    /**
     * A test client which loads a dictionary file and opens an interactive search allowing a user
     * to determine via command line input if a word is a valid boggle word and if it's the substring
     * of a valid boggle word.
     *
     * @param args path to dictionary text file, format: words separated by a whitespace character.
     */
    public static void main(String[] args) {
        // for testing remove for autograder;
        if (args.length != 1) {
            throw new IllegalArgumentException("Provide path to a dictionary file.");
        }
        String[] dict = WordMap.parseDict(args[0]);
        WordHash.hashFn(new HashAlgs.FNV1a());
        WordMap WM = new WordMap(dict);
        System.out.printf("dict size: %d\tsubs size: %d%n", WM.dict.size(), WM.subs.size());
        WM.interactiveSearch();
    }
}
