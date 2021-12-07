import java.util.HashMap;

/**
 * Container for string hash values.  Holds a 32 bit and 64 bit hash of the string.  No copy of the string is saved.
 * Equality between WordHash objects is determined by comparing the hash values of the objects.  Hash collisions
 * are very unlikely for different single word length strings, but are statistically possible.  The hash function
 * used should be provided through the {@code HashAlgs.Hash} interface.
 */
public class WordHash {
    private static int Q = 'Q', U = 'U';
    static HashAlgs.Hash hashFn = new HashAlgs.Modular();

    int hash32;
    long hash64;

    /**
     * Constructs a copy of a WordHash object
     *
     * @param that object to be copied.
     */
    public WordHash(WordHash that) {
        if (that == null) { throw new IllegalArgumentException("Received WordHash null input.");}
        hash32 = that.hash32;
        hash64 = that.hash64;
    }


    /**
     * Basic constructor.  Initializes a WordHash without adding any characters.
     */
    public WordHash() {
        hashFn.init(this);
    }

    /**
     * Constructs a WordHash object from a single character.
     *
     * @param c int value of character
     */
    public WordHash(int c) {
        hashFn.init(this);
        append(c);
    }

    /**
     * Converts a character array to a WordHash object.
     *
     * @param word character array (as an array of ints)
     */
    public WordHash(int[] word) {
        hash(word);
    }

    /**
     * Override the default hash function used by WordHash.  To avoid breaking everything, run this before any
     * WordHash objects have been constructed.  <em>Tread carefully</em>.
     *
     * @param hashFn a function that implements the {@code HashAlgs.Hash} interface.
     */
    static void hashFn(HashAlgs.Hash hashFn) { WordHash.hashFn = hashFn; }

    // Adds the character array to this object
    private void hash(int [] word) {
        hashFn.init(this);
        for (int c : word) {
            if (c == 0) { break; }
            hashFn.append(c,this);
        }
    }

    /**
     * Adds a single character to the WordHash object
     *
     * @param c character to be added (as an int)
     */
    public void append(int c) { hashFn.append(c,this); }

    /**
     * Returns a hashcode value for the object
     *
     * @return 32 bit hashcode
     */
    @Override
    public int hashCode() { return hash32; }

    /**
     *  This equals function <em>does not</em> conform to the general {@code equals} contract.
     *  Specifically equality is tested by comparing 2 different hashes from
     *  {@code this} object and that {@code that} to determine equality.  In very rare cases
     *  this could lead to WordHashes created from different words testing equally.  The probability
     *  of this is extremely low, however if an acceptable {@code hashFn} is used.
     *
     * @param o {@code Object} for comparison to this
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof WordHash)) {
            return false;
        }
        WordHash that = (WordHash) o;
        return this.hash32 == that.hash32 && this.hash64 == that.hash64;
    }

    /**
     * Converts a word into an int[] of its char values (akin to a char array).
     *
     * @param word to be converted to int array
     * @return array of character values as an int[].
     */
    static public int[] intArray(String word) {
        final int N = word.length();
        int[] intWord = new int[N];
        for (int i = 0, w = 0; w < N; i++, w++) {
            intWord[i] = word.charAt(w);
            if (intWord[w] == Q) {
                if (word.charAt( ++w ) != U) { return new int[0]; }
            }
        }
        return intWord;
    }

    private void hashDiag() {
        System.out.printf("Hash32: %d%nHash64: %d%n", hash32, hash64);
    }

    private void primeDiag() { hashFn.primeDiag(); }


    /**
     * Test client which permutes a substring's hash into that of a longer word.  Prints diagnostics to
     * stdout.
     *
     * @param args a substring and a parent word, e.g. ti titanium. Case-insensitive.
     */
    public static void main(String[] args){
        if (args.length != 2 || args[0].length() > args[1].length()) { throw new IllegalArgumentException("Input a prefix and a word i.e. \"ti titanium\" "); }
        HashMap<WordHash, String> ST = new HashMap<>();

        WordHash.hashFn = new HashAlgs.FNV1a();
        int[] prefix = WordHash.intArray( args[0].toUpperCase() );
        int[] word = WordHash.intArray( args[1].toUpperCase() );
        WordHash prefixHash = new WordHash(prefix);
        WordHash wordHash = new WordHash(word);
        ST.put(prefixHash, args[0]);
        ST.put(wordHash, args[1]);

        WordHash prefix2word = new WordHash(prefix);
        for (int i = 0; i < word.length && word[i] != 0; i++) {
            // This needs to handle cases where a U was dropped after a Q in both the prefix and the word
            if (i < prefix.length && prefix[i] != 0  && prefix[i] != word[i]) {
                throw new IllegalArgumentException("The prefix provided is not a prefix of the input word");
            }
            if (i >= prefix.length || prefix[i] == 0) { prefix2word.append(word[i]); }
        }

        prefixHash.hashDiag();
        wordHash.hashDiag();
        prefix2word.hashDiag();
        System.out.printf("Hash Collision: %b%n", wordHash.equals(prefixHash));
        System.out.printf("Get prefix from ST: %s%n", ST.get(new WordHash(prefix)));
        System.out.printf("Get word from ST: %s%n", ST.get(new WordHash(word)));
        System.out.printf("Prefix to Word conversion: %s%n", ST.get(wordHash).equals(ST.get(prefix2word)) ? "Passed" : "Failed");
    }
}
