import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.stream.Stream;

public class CircularSuffixArrayTest {
    private final String test = "ABCDE123!?";
    private final int N = test.length();

    @Test
    void CircularSuffixTest() {
        for (int i = 0; i < N; i++) {
            String expected = rotate(test,i);
            CircularSuffix cs = new CircularSuffix(i, test);
            String actual = cs.toString();
            Assertions.assertEquals(expected,actual);
        }
    }

    /**
     * Tests LSD sort (deprecated) implemented in CircularSuffix class.  The {@code Arrays.sort} is faster
     * for most inputs, so do not use.
     */
    @Test
    void SortTest() {;
        CircularSuffixArray CSA = new CircularSuffixArray(test);
        CircularSuffix[] unknown = new CircularSuffix[N];
        for (int i = 0; i < N; i++) {
            unknown[i] = new CircularSuffix(CSA.index(i),test);
        }
        CircularSuffix[] correct =  Stream.of(unknown)
                                          .map(CircularSuffix::new)
                                          .toArray(CircularSuffix[]::new);
        Arrays.sort(correct);
        Assertions.assertArrayEquals(unknown,correct);
    }

    /**
     * Rotates an input string left by i characters.  Uses a different method than implemented in CircularSuffixArray.
     *
     * ex. s = "1234", i = 1
     *     output: "2341"
     *
     * @param s string to rotate
     * @param i offset (left)
     * @return rotated string
     */
    private static String rotate(String s, int i) {
        int N = s.length();
        char[] chars = new char[N];
        for (int j = i; j < N; j++) {
            chars[j-i] = s.charAt(j);
        }
        for (int j = 0; j < i; j++) {
            chars[N-i+j] = s.charAt(j);
        }
        return new String(chars);
    }

    /**
     * A quick unit test of the test.
     *
     * @param args provide no args, unfortunately java has only one main method signature.
     */
    public static void main(String[] args) {
        if (args != null) {
            throw new IllegalArgumentException("Provide no arguments to main.");
        }
        CircularSuffixArrayTest test = new CircularSuffixArrayTest();
        test.CircularSuffixTest();
    }

}
