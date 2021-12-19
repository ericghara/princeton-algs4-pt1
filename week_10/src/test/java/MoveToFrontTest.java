import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.ByteArrayOutputStream;

public class MoveToFrontTest {

    @AfterAll
    static void shutdown() {
        StreamHandler.resetAll();
    }

    @Test
    void testEncode() {
        String msg = "ABRACADABRA!";
        char[] expected = {0x41, 0x42, 0x52, 0x02, 0x44, 0x01, 0x45, 0x01, 0x04, 0x04, 0x02, 0x26};

        ByteArrayOutputStream found = StreamHandler.streamInStreamOut(msg);
        MoveToFront.encode();
        Assertions.assertEquals(new String(expected), found.toString() );
    }

    @Test
    void testDecode() {
        String expected = "ABRACADABRA!";
        char[] encodedMsg = {0x41, 0x42, 0x52, 0x02, 0x44, 0x01, 0x45, 0x01, 0x04, 0x04, 0x02, 0x26};

        ByteArrayOutputStream found = StreamHandler.streamInStreamOut(new String(encodedMsg) );
        MoveToFront.decode();
        Assertions.assertEquals(expected, found.toString() );
    }
}
