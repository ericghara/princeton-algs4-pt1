import java.util.Objects;
import java.util.Arrays;

public class CircularSuffixArray {
    private final int N;
    private final String s;
    private final CircularSuffix[] suffixes;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (Objects.isNull(s)) {
            throw new IllegalArgumentException("Received a null string input");
        }
        this.s = s;
        N = s.length();
        suffixes = new CircularSuffix[N];
        initSuffixes();
        Arrays.sort(suffixes);
    }

    // length of s
    public int length() {
        return N;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if ( i < 0 || i >= N) {
            throw new IllegalArgumentException("Index out of range");
        }
        return suffixes[i].getOffset();
    }


    private void initSuffixes() {
        for (int i = 0; i < N; i++) {
            suffixes[i] = new CircularSuffix(i, s);
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
