import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        int[] cypher = MTFData.initArray();
        int[] index = cypher.clone();
        while (!BinaryStdIn.isEmpty()) {
            int cur = BinaryStdIn.readChar();
            BinaryStdOut.write( MTFData.encodeChar(cur, cypher, index) );
        }
        closeStreams();
    }

    // for testing
    static char[] encode(char[] chars) {
        int n = chars.length;
        int[] cypher = MTFData.initArray();
        int[] index = cypher.clone();
        char[] out = new char[n];
        for (int i = 0; i < n; i++) {
            out[i] = MTFData.encodeChar(chars[i], cypher, index);
        }
        return out;
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        int[] cypher = MTFData.initArray();
        int[] index = cypher.clone();
        while (!BinaryStdIn.isEmpty()) {
            int pos = BinaryStdIn.readChar();
            int c = cypher[pos];
            MTFData.encodeChar(c, cypher, index);
            BinaryStdOut.write( (char) c);
        }
        closeStreams();
    }

    static char[] decode(char[] chars) {
        int n = chars.length;
        int[] cypher = MTFData.initArray();
        int[] index = cypher.clone();
        char[] out = new char[n];
        for (int i = 0; i < n; i++) {
            int pos = chars[i];
            int c = cypher[pos];
            out[i] = (char) c;
            MTFData.encodeChar(c, cypher, index);
        }
        return out;
    }

    private static void closeStreams() {
        BinaryStdIn.close();
        BinaryStdOut.close();
    }

        static class MTFData {
            static final int R = 256;

            static int[] initArray() {
                int[] arr = new int[R];
                for (int i = 0; i < R; i++) {
                    arr[i] = i;
                }
                return arr;
            }

            static char encodeChar(int encChar, int[] cypher, int[] index) {
                int charPos = index[encChar];
                for (int pos = charPos - 1; pos >= 0; pos--) {
                    int c = cypher[pos];
                    cypher[pos + 1] = c;
                    index[c]++;
                }
                cypher[0] = encChar;
                index[encChar] = 0;
                return (char) charPos;
            }
        }


    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main (String[]args){
        if (args.length != 1 || !(args[0].equals("+") || args[0].equals("-")) ) {
            throw new IllegalArgumentException("Incorrect usage: + to encode - to decode." +
                    "example usage: java MoveToFront + < \"test.txt\"");
        }
        if (args[0].equals("+")) {
            encode();
        }
        else {
            System.out.println("hi");
            decode();
        }
    }
}