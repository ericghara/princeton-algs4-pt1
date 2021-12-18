public class CircularSuffix implements Comparable<CircularSuffix> {
    static final int RADIX = 256;

    private final int offset;
    private final String s;

    CircularSuffix(int offset, String s) {
        this.offset = offset;
        this.s = s;
    }

    CircularSuffix(CircularSuffix that) {
        this.offset = that.offset;
        this.s = that.s;
    }

    char charAt(int i) {
        int N = s.length();
        int offsetI = (i+offset)%N;
        return s.charAt(offsetI);
    }

    int getOffset() {
        return offset;
    }

    /**
     * A radix sort (LSD string sort) implementation for CircularSuffix arrays
     *
     * @param suffixes the CircularSuffix array to be sorted
     * @param N Length of the CircularSuffix array [em]and[/em] length of chars array. These must be equal
     * @param R radix
     */
    static void sort(CircularSuffix[] suffixes, int N, int R) {
        CircularSuffix[] aux = new CircularSuffix[N];

        for (int d = N-1; d >= 0; d--) {
            int[] count = new int[R+1];
            for (CircularSuffix s : suffixes) {
                int i = s.charAt(d) + 1;
                count[i]++;
            }
            for (int i = 1; i < R+1; i++) {
                count[i] += count[i-1];
            }
            for (CircularSuffix s : suffixes) {
                int i = s.charAt(d);
                int pos = count[i];
                aux[pos] = s;
                count[i]++;
            }
            System.arraycopy(aux, 0, suffixes, 0, N);
        }
    }

    @Override
    public String toString() {
        int N = s.length();
        StringBuilder builder = new StringBuilder(N);
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
        int N = this.s.length();
        if (N != that.s.length()) {
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
     * Compares only the underlying character representation and the offset.
     * Can only compare suffixes generated from equal length strings.
     *
     * @param o object to compare to
     * @return false if not a CircularSuffix object or the char sequence of the two CircularSuffix objects differ,
     * true if the chars at all positions are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof CircularSuffix) {
            CircularSuffix that = (CircularSuffix) o;
            if (this.offset == that.offset){
                return compareTo(that) == 0;
            }
            return false;
        }
        return false;
    }

    /**
     * Hash considers both the rotated string that CircularSuffix represents and the offset.
     * This was only implemented because equals was also implemented, it is a slow implementation
     * (rehashes every call) but requires no extra space.
     *
     * @return a hash code.
     */
    @Override
    public int hashCode(){
        return 37 * toString().hashCode() + offset;
    }
}
