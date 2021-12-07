import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.LinkedList;
import java.io.File;

public class WordMap {
    static int DICT_N_MULTIPLE = 3;
    static int SUBS_N_MULTIPLE = 9;
    static int MIN_WORD_LEN = 3;
    static int Q = 'Q';
    static int U = 'U';

    final HashMap<WordHash, String> dict; // Dictionary
    final HashSet<WordHash> subs; // Substrings that lead to valid words


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

    public boolean validSub(WordHash sub) { return subs.contains(sub); }

    /**
     *
     * @param word
     * @return null if word not found; word if word found.
     */
    public String validWord(WordHash word) { return dict.get(word); }

    private void makeMaps(String[] dictionary) {
        // It seems repetitive to clean the dict then to create dict and subs
        // maps, but it's very hard to backtrack when a Q without a trailing U is found.
        // Hashing is lossy so, no undo is possible for an illegal word
        // leading to zombie hashes in the subs set. The design decision was to
        // heavily prioritize word search speed over constructor speed.
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

    static String[] parseDict(String path) {
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

    public static void main(String[] args) {
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
