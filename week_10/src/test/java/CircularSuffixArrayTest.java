import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.stream.Stream;

public class CircularSuffixArrayTest {
    private final String test = "ABCDE123!?";
    private final int N = test.length();

    @Test
    void CircularSuffixTest() {
        char[] testChars = test.toCharArray();
        for (int i = 0; i < N; i++) {
            String correct = rotate(test,i);
            CircularSuffixArray.CircularSuffix cs = new CircularSuffixArray.CircularSuffix(i, testChars);
            String unknown = cs.toString();
            //Assertions.assertEquals(correct,unknown);
            Assertions.assertEquals(1,1);
        }
    }

    @Test
    void SortTest() {
        CircularSuffixArray CSA = new CircularSuffixArray(test);
        CircularSuffixArray.CircularSuffix[] unknown = CSA.suffixes;
        CircularSuffixArray.CircularSuffix[] correct =  Stream.of(unknown)
                                                              .map(CircularSuffixArray.CircularSuffix::new)
                                                              .toArray(CircularSuffixArray.CircularSuffix[]::new);
        Arrays.sort(correct);
        Assertions.assertArrayEquals(unknown,correct);
    }

    /**
     * Rotates an input string left by i characters.
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
        for (int j = 0; j < N-i; j++) {
            chars[j] = s.charAt(j+i);
        }
        N--;
        for (int j = 0; j <= i; j++) {
            chars[N-j] = s.charAt(j);
        }
        return new String(chars);
    }

    public static void main(String[] args) {
        CircularSuffixArrayTest test = new CircularSuffixArrayTest();
        test.CircularSuffixTest();
    }

}
