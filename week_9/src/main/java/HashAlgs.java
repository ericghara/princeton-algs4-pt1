import java.math.BigInteger;
import java.util.Random;

public class HashAlgs {

    public interface Hash {
        void init(WordHash WH);
        void append(int c, WordHash WH);
        void primeDiag();
    }

    public static class Modular implements Hash {

        private static final int PRIME32, RADIX;
        private static final long PRIME64;

        static {
            BigInteger BI0 =  BigInteger.probablePrime(31, new Random() );
            BigInteger BI1 = BigInteger.probablePrime(63, new Random() );
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

    public static class FNV1a implements Hash {
        // Adapted from "The FNV Non-Cryptographic Hash Algorithm": https://tools.ietf.org/pdf/draft-eastlake-fnv-16.pdf
        private static final int PRIME32, OFFSET32;
        private static final long PRIME64, OFFSET64;

        static {
            OFFSET32 = 0x811c9dc5;
            PRIME32 = 16777619;
            PRIME64 = 0xcbf29ce484222325L;
            OFFSET64 = 1099511628211L;
        }

        public void init(WordHash WH) {
            WH.hash32 = OFFSET32;
            WH.hash64 = OFFSET64;
        }

        public void append(int c, WordHash WH) {
            // WARNING: only applicable for non-negative c; for negatives: hash32 ^= (PRIME32 & 0xff)
            WH.hash32 ^= c;
            WH.hash32 *= PRIME32;
            // WARNING: only applicable for non-negative c; for negatives: hash64 ^= (PRIME64 & 0xffff)
            WH.hash64 ^= c;
            WH.hash64 *= PRIME64;
        }

        public void primeDiag() { System.out.printf("Prime32: %d%nPrime64: %d%n", PRIME32, PRIME64);}

    }

}
