public class CircularSuffixArray {
    private final int N;
    char[] chars;
    CircularSuffix[] suffixes;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        chars = s.toCharArray();
        N = chars.length;
        suffixes = new CircularSuffix[N];
        initSuffixes();
        sort(suffixes, N, MoveToFront.MTFData.R);
    }

    // length of s
    public int length() {
        return N;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        return suffixes[i].getOffset();
    }

    private void initSuffixes() {
        for (int i = 0; i < N; i++) {
            suffixes[i] = new CircularSuffix(i, chars);
        }
    }

    /**
     * A radix sort (LSD string sort) implementation for CircularSuffix arrays
     *
     * @param suffs the CircularSuffix array to be sorted
     * @param N Length of the CircularSuffix array [em]and[/em] length of chars array. These must be equal
     * @param R radix
     */
    public static void sort(CircularSuffix[] suffs, int N, int R) {
        CircularSuffix[] aux = new CircularSuffix[N];

        for (int d = N-1; d >= 0; d--) {
            int[] count = new int[R+1];
            for (CircularSuffix s : suffs) {
                int i = s.charAt(d) + 1;
                count[i]++;
            }
            for (int i = 1; i < R+1; i++) {
                count[i] += count[i-1];
            }
            for (CircularSuffix s : suffs) {
                int i = s.charAt(d);
                int pos = count[i];
                aux[pos] = s;
                count[i]++;
            }
            System.arraycopy(aux, 0, suffs, 0, N);
        }
    }

    static class CircularSuffix implements Comparable<CircularSuffix> {
        private final int offset;
        private final char[] chars;

        CircularSuffix(int offset, char[] chars) {
            this.offset = offset;
            this.chars = chars;
        }

        CircularSuffix(CircularSuffix that) {
            this.offset = that.offset;
            this.chars = that.chars;
        }

        private char charAt(int i) {
            int N = chars.length;
            int offsetI = (i+offset)%N;
            return chars[offsetI];
        }

        private int getOffset() {
            return offset;
        }

        @Override
        public String toString() {
            int N = chars.length;
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < N; i++) {
                builder.append(charAt(i));
            }
            return builder.toString();
        }


        /**
         * Compares only the underlying character representations, does not compare offsets.
         * Can only compare suffixes generated from equal length strings.
         *
         * @param that CircularSuffix object to compare to
         * @return -1 if that < this, 0 if that equals this, 1 if that is greater than this.
         */
        @Override
        public int compareTo(CircularSuffix that) {
            int N = this.chars.length;
            if (N != that.chars.length) {
                throw new IllegalArgumentException("It is only possible to compare equal length CircularSuffixes");
            }
            for (int i = 0; i < N; i++) {
                int comp = Character.compare( this.charAt(i), that.charAt(i) );
                if (comp != 0) {
                    return comp;
                }
            }
            return 0;
        }

        /**
         * Compares only the underlying character representations, does not compare offsets.
         * Can only compare suffixes generated from equal length strings.
         *
         * @param that object to compare to
         * @return false if not a CircularSuffix object or the char sequence of the two CircularSuffix objects differ,
         * true if the chars at all positions are equal.
         */
        @Override
        public boolean equals(Object that) {
            if (that instanceof CircularSuffix) {
                return compareTo((CircularSuffix) that) == 0;
            }
            return false;
        }

        /**
         * Returns a hash of the rotated string which it is a referential representation of.  This was only implemented
         * because equals was also implemented, it is not a very fast implementation (rehashes every call).
         *
         * @return a hash code.
         */
        @Override
        public int hashCode(){
            return toString().hashCode();
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Enter a single input string");
        }
        CircularSuffixArray CSA = new CircularSuffixArray(args[0]);
        System.out.printf("Length of input string: %s%n", CSA.length() );
        System.out.printf("Index of 0th sorted suffix: %d%n", CSA.index(0));
    }
}
