import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class BurrowsWheelerTest {

    @Test
    void transformTest() {
        String s = "ABRACADABRA!";
        char[] correct = {0x00, 0x00, 0x00, 0x03, 0x41, 0x52, 0x44, 0x21, 0x52, 0x43, 0x41, 0x41, 0x41, 0x41, 0x42, 0x42};
        String unknown = BurrowsWheelerString.transform(s);
        Assertions.assertEquals(unknown, new String(correct));
    }

    @Test
    void inverseTransformTest() {
        int offset = 3;
        char[] encoded = {0x41, 0x52, 0x44, 0x21, 0x52, 0x43, 0x41, 0x41, 0x41, 0x41, 0x42, 0x42};
        String correct = "ABRACADABRA!";
        String unknown = BurrowsWheelerString.inverseTransform(new String(encoded), offset);
        Assertions.assertEquals(correct, unknown);
    }

    @Test
    void TransformInverseTransformTest() {
        String s = "THISisAtopSECRETmessage!!!^-^that just keeps going on and on... and on.. and o-";
        String t = BurrowsWheelerString.transform(s);
        // convert byte representation of an int to an int
        int offset = (t.charAt(0)<<24)&0xff000000|
                     (t.charAt(1)<<16)&0x00ff0000|
                     (t.charAt(2)<<8)&0x0000ff00|
                     (t.charAt(3)<<0)&0x000000ff;
        String actual = BurrowsWheelerString.inverseTransform(t.substring(4),offset);
        Assertions.assertEquals(s,actual);
    }

}
