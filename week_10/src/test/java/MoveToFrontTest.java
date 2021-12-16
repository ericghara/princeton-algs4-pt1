import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.StringReader;

public class MoveToFrontTest {

    @Test
    void testEncode() {
        String msg = "ABRACADABRA!";
        char[] correctEncode = {0x41, 0x42, 0x52, 0x02, 0x44, 0x01, 0x45, 0x01, 0x04, 0x04, 0x02, 0x26};

        char[] result = MoveToFront.encode(msg.toCharArray());
        Assertions.assertArrayEquals(correctEncode, result);
    }

    @Test
    void testDecode() {
        String msg = "ABRACADABRA!";
        char[] encoded = {0x41, 0x42, 0x52, 0x02, 0x44, 0x01, 0x45, 0x01, 0x04, 0x04, 0x02, 0x26};

        char[] decodedMsg = MoveToFront.decode(encoded);
        Assertions.assertArrayEquals(decodedMsg, msg.toCharArray());
    }
}
