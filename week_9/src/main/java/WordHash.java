import java.util.HashMap;

public class WordHash {
    static int Q,U;
    static HashAlgs.Hash hashFn;

    static {
        Q = 'Q';
        U = 'U';
        hashFn = new HashAlgs.Modular();
    }

    int hash32;
    long hash64;

    public WordHash(WordHash that) {
        if (that == null) { throw new IllegalArgumentException("Received WordHash null input.");}
        hash32 = that.hash32;
        hash64 = that.hash64;
    }

    public WordHash() {
        hashFn.init(this);
    }

    public WordHash(int c) {
        hashFn.init(this);
        append(c);
    }

    public WordHash(int[] word) {
        hash(word);
    }

    public static void hashFn(HashAlgs.Hash hashFn) { WordHash.hashFn = hashFn; }

    private void hash(int [] word) {
        hashFn.init(this);
        for (int c : word) {
            if (c == 0) { break; }
            hashFn.append(c,this);
        }
    }

    public void append(int c) { hashFn.append(c,this); }

    @Override
    public int hashCode() { return hash32; }

    /**
     *  This equals function <em>does not</em> conform to the general {@code equals} contract.
     *  Specifically equality is tested by comparing 2 different 32 bit hashes from
     *  {@code this} object and that {@code that} to determine equality.  In very rare cases
     *  this could lead to different words testing equal. The probability of this is <em>extremely</em>
     *  low therefore a design decision was made to implement this style equality for speed.
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
