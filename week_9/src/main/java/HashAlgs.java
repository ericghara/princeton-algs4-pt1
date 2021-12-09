import java.math.BigInteger;
import java.util.Random;

/**
 * A small collection of hash algorithms for use by WordHash objects and an interface for utilizing them.  Algorithms
 * calculate 32 bit and 64 bit hashes.
 */
public class HashAlgs {

    public interface Hash {
        /**
         * Sets initial values for the {@code hash32} and {@code hash64} objects
         *
         * @param WH WordHash object to initialize
         */
        void init(WordHash WH);

        /**
         * Recalculates hashes after adding a character to the WordHash object
         *
         * @param c character to be added
         * @param WH WordHash object to modify
         */
        void append(int c, WordHash WH);

        /**
         * Prints diagnostic info about the primes being used to stdout.
         */
        void primeDiag();
    }

    /**
     * A hash implementation of Horner's method (ie a modular hash function).
     */
    public static class Modular implements Hash {

        private static final int PRIME32, RADIX;
        private static final long PRIME64;

        // remove static block and replace probablePrimes with fixed primes for the autograder
        // (can't use the math package or static blocks)
        static {
            BigInteger BI0 =  BigInteger.probablePrime(31, new Random() ); // 31 bit prime
            BigInteger BI1 = BigInteger.probablePrime(63, new Random() ); // 63 bit prime
            PRIME32 = BI0.intValue();
            PRIME64 = BI1.longValue();
            RADIX = 26;
        }

        public void init(WordHash WH) {
            WH.hash32 = 0;
            WH.hash64 = 0;
        }

        public void append(int c, WordHash WH) {
            WH.hash32 = (RADIX * WH.hash32 + c) % PRIME32;
            WH.hash64 = (RADIX * WH.hash64 + c ) % PRIME64;
        }

        public void primeDiag() { System.out.printf("Prime32: %d%nPrime64: %d%n", PRIME32, PRIME64);}
    }

    /**
     * An implementation of a Fowler–Noll–Vo hash function, FNV-1a. Adapted from
     * @see <a href="https://tools.ietf.org/pdf/draft-eastlake-fnv-16.pdf">"The FNV Non-Cryptographic Hash Algorithm"</a>.
     */
    public static class FNV1a implements Hash {
        private static final int OFFSET32 = 0x811c9dc5, PRIME32 = 16777619;
        private static final long PRIME64 = 0xcbf29ce484222325L, OFFSET64 = 1099511628211L;

        public void init(WordHash WH) {
            WH.hash32 = OFFSET32;
            WH.hash64 = OFFSET64;
        }

        public void append(int c, WordHash WH) {
            // WARNING: only applicable for non-negative c; for negatives: hash32 ^= (c & 0xff)
            WH.hash32 ^= c;
            WH.hash32 *= PRIME32;
            // WARNING: only applicable for non-negative c; for negatives: hash64 ^= (c & 0xffff)
            WH.hash64 ^= c;
            WH.hash64 *= PRIME64;
        }

        public void primeDiag() { System.out.printf("Prime32: %d%nPrime64: %d%n", PRIME32, PRIME64);}
    }
}
