import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class BurrowsWheeler {

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        if (BinaryStdIn.isEmpty()) {
            return; }
        String s = BinaryStdIn.readString();
        int n = s.length();
        CircularSuffixArray CSA = new CircularSuffixArray(s);
        int first = -1; // sentinel
        StringBuilder transform = new StringBuilder(n);
        n--; // 0 index n
        for (int i = 0; i <= n; i++) {
            CircularSuffix suffix = new CircularSuffix(CSA.index(i), s);
            if (suffix.getOffset() == 0) {
                first = i; // find un-rotated suffix
            }
            transform.append(suffix.charAt(n));
        }
        BinaryStdOut.write(first); // write out sorted position of un-rotated suffix
        BinaryStdOut.write(transform.toString() );
        closeStreams();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int offset = BinaryStdIn.readInt();
        // keeps sorted appearance of char at the end of a sorted suffix.  For example ARD!R
        // [!] = 3, [A] = 0, [D] = 2, [R] = 1,4
        ArrayList<LinkedList<Integer>> iCharAppearsAtEnd = new ArrayList<>(CircularSuffix.RADIX);
        for (int i = 0; i < CircularSuffix.RADIX; i++) {
            iCharAppearsAtEnd.add(new LinkedList<>());
        }
        int[] buckets = new int[CircularSuffix.RADIX];
        int n = 0;
        for (; !BinaryStdIn.isEmpty(); n++) {
            int c = BinaryStdIn.readChar();
            buckets[c] += 1;
            iCharAppearsAtEnd.get(c)
                    .addLast(n);
        }
        char[] suffixB = new char[n];
        for(int i = 0, suffixI = 0; i < CircularSuffix.RADIX; i++) {
            int end = suffixI + buckets[i];
            char c = (char) i;
            while (suffixI < end) {
                suffixB[suffixI] = c;
                suffixI++;
            }
        }

        int[] next = new int[n];
        for (int i = 0; i < n; i++) {
            char c = suffixB[i];
            next[i] = iCharAppearsAtEnd.get(c).removeFirst();
        }
        int curSuffix = offset;
        for (int i = 0; i < n; i++) {
            BinaryStdOut.write( suffixB[curSuffix] );
            curSuffix = next[curSuffix];
        }
        closeStreams();
    }

    private static void closeStreams() {
        BinaryStdIn.close();
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args.length != 1 || !(args[0].equals("+") || args[0].equals("-")) ) {
            throw new IllegalArgumentException("Incorrect usage: + to encode - to decode." +
                    "example usage: java BurrowsWheeler + < \"test.txt\"");
        }
        if (args[0].equals("-")) {
            transform();
        }
        else {
            inverseTransform();
        }
    }
}