import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

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
