import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class MoveToFrontTest {
    static Runnable encode = MoveToFront::encode;
    static Runnable decode = MoveToFront::decode;

    @AfterAll
    static void shutdown() {
        StreamWrapper.resetAll();
    }

    @Test
    void testEncode() {
        String msg = "ABRACADABRA!";
        char[] expected = {0x41, 0x42, 0x52, 0x02, 0x44, 0x01, 0x45, 0x01, 0x04, 0x04, 0x02, 0x26};
        String found = StreamWrapper.run(msg, encode);
        Assertions.assertEquals(new String(expected), found );
    }

    @Test
    void testDecode() {
        String expected = "ABRACADABRA!";
        char[] encodedMsg = {0x41, 0x42, 0x52, 0x02, 0x44, 0x01, 0x45, 0x01, 0x04, 0x04, 0x02, 0x26};
        String found = StreamWrapper.run(new String(encodedMsg), decode );
        Assertions.assertEquals(expected, found );
    }
}
