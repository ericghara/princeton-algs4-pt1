import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class BurrowsWheelerString extends BurrowsWheeler {

    //@SuppressWarnings("unchecked")
    static String inverseTransform(String s, Integer offset) {
        final int N = s.length();
        // keeps sorted appearance of char at the end of a sorted suffix.  For example ARD!R
        // [!] = 3, [A] = 0, [D] = 2, [R] = 1,4
        ArrayList<LinkedList<Integer>> iCharAppearsAtEnd = new ArrayList<>(CircularSuffix.RADIX);
        for (int i = 0; i < CircularSuffix.RADIX; i++) {
            iCharAppearsAtEnd.add(new LinkedList<>());
        }
        int[] buckets = new int[CircularSuffix.RADIX];
        for (int i = 0; i < N; i++) {
            int c = s.charAt(i);
            buckets[c] += 1;
            iCharAppearsAtEnd.get(c)
                             .addLast(i);
        }
        char[] suffixB = new char[N];
        for(int i = 0, suffixI = 0; i < CircularSuffix.RADIX; i++) {
            int end = suffixI + buckets[i];
            char c = (char) i;
            while (suffixI < end) {
                suffixB[suffixI] = c;
                suffixI++;
            }
        }
        int[] next = new int[N];
        for (int i = 0; i < N; i++) {
            char c = suffixB[i];
            next[i] = iCharAppearsAtEnd.get(c).removeFirst();
        }

        StringBuilder message = new StringBuilder(N);
        int curSuffix = offset;
        for (int i = 0; i < N; i++) {
            message.append( suffixB[curSuffix] );
            curSuffix = next[curSuffix];
        }
        return message.toString();
    }

    static String transform(String s) {
        final int offset = Integer.SIZE/8;
        int n = s.length();
        CircularSuffixArray CSA = new CircularSuffixArray(s);
        char[] transform = new char[n+offset];
        n--;
        for (int i = 0; i <= n; i++) {
            CircularSuffix suffix = new CircularSuffix(CSA.index(i), s);
            if (suffix.getOffset() == 0) {
                for (int j = 0; j < offset; j++) {
                    int shiftBy = (offset-1-j)*8;
                    transform[j] = (char) (i >> shiftBy);
                }
            }
            transform[i+offset] = suffix.charAt(n);
        }
        return new String(transform);
    }

}
