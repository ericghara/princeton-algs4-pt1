public class MoveToFrontChar extends MoveToFront {

    // for testing
    static char[] encode(char[] chars) {
        int n = chars.length;
        int[] cypher = MoveToFrontData.initArray();
        int[] index = cypher.clone();
        char[] out = new char[n];
        for (int i = 0; i < n; i++) {
            out[i] = MoveToFrontData.encodeChar(chars[i], cypher, index);
        }
        return out;
    }
    static char[] decode(char[] chars) {
        int n = chars.length;
        int[] cypher = MoveToFrontData.initArray();
        int[] index = cypher.clone();
        char[] out = new char[n];
        for (int i = 0; i < n; i++) {
            int pos = chars[i];
            int c = cypher[pos];
            out[i] = (char) c;
            MoveToFrontData.encodeChar(c, cypher, index);
        }
        return out;
    }
}
