import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        int[] cypher = MoveToFrontData.initArray();
        int[] index = cypher.clone();
        while (!BinaryStdIn.isEmpty()) {
            int cur = BinaryStdIn.readChar();
            BinaryStdOut.write( MoveToFrontData.encodeChar(cur, cypher, index) );
        }
        closeStreams();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        int[] cypher = MoveToFrontData.initArray();
        int[] index = cypher.clone();
        while (!BinaryStdIn.isEmpty()) {
            int pos = BinaryStdIn.readChar();
            int c = cypher[pos];
            MoveToFrontData.encodeChar(c, cypher, index);
            BinaryStdOut.write( (char) c);
        }
        closeStreams();
    }

    private static void closeStreams() {
        BinaryStdIn.close();
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main (String[]args){
        if (args.length != 1 || !(args[0].equals("+") || args[0].equals("-")) ) {
            throw new IllegalArgumentException("Incorrect usage: + to encode - to decode." +
                    "example usage: java MoveToFront + < \"test.txt\"");
        }
        if (args[0].equals("-")) {
            encode();
        }
        else {
            decode();
        }
    }
}