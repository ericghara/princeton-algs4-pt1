import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class BurrowsWheelerTest {
    static final int NUM_BYTES = Integer.BYTES;
    static final int BITS_PER_BYTE = 8;


    static char[] getBytes(int i) {
        int mask = 0xff << (BITS_PER_BYTE*(NUM_BYTES -1));
        char[] charArray = new char[NUM_BYTES];
        for (int j = 0; j < NUM_BYTES; j++) {
            charArray[j] |= mask & i;
            mask >>= BITS_PER_BYTE;
        }
        return charArray;
    }

    static int extractInt(String s) {
        int mask = 0xff;
        int out = 0;
        for (int j = 0; j < NUM_BYTES; j++) {
            char c = s.charAt(j);
            out |= (c & mask);
        }
        s = s.substring(NUM_BYTES);
        return out;
    }


    @AfterAll
    static void shutdown() {
        StreamHandler.resetAll();
    }

    /**
     * Tests transforming a string.
     */
    @Test
    void transformTest() {
        char[] expected = {0x00, 0x00, 0x00, 0x03, 0x41, 0x52, 0x44, 0x21, 0x52, 0x43, 0x41, 0x41, 0x41, 0x41, 0x42, 0x42};
        String input = "ABRACADABRA!";
        ByteArrayOutputStream found = StreamHandler.streamInStreamOut(input);
        BurrowsWheeler.transform();
        Assertions.assertEquals(new String(expected), found.toString() );
    }


    /**
     * Tests decoding ({@code inverseTransform}) a string with specified offset.
     */
    @Test
    void inverseTransformTest() {
        String expected = "ABRACADABRA!";
        int offset = 3;
        char[] offsetBytes = getBytes(offset);
        char[] encoded = {0x41, 0x52, 0x44, 0x21, 0x52, 0x43, 0x41, 0x41, 0x41, 0x41, 0x42, 0x42};
        String input = new String(offsetBytes) + new String(encoded);
        ByteArrayOutputStream found = StreamHandler.streamInStreamOut(input);
        BurrowsWheeler.inverseTransform();
        Assertions.assertEquals(expected, found.toString() );
    }

    /**
     * Tests that the original string is obtained after a transform followed by inverse transform of the result (
     * i.e. input -> encode -> decode -> output == input)
     */
    @Test
    void TransformInverseTransformTest() {
        String start = "THISisAtopSECRETmessage!!!^-^that just keeps going on and on... and on.. and o-";
        ByteArrayOutputStream transform = StreamHandler.streamInStreamOut(start);
        BurrowsWheeler.transform();
        ByteArrayOutputStream found = StreamHandler.streamInStreamOut(transform.toString() );
        BurrowsWheeler.inverseTransform();
        Assertions.assertEquals(start,found.toString());
    }

}
