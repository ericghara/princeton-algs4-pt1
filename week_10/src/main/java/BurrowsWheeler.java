import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.ArrayList;
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
        int offset = -1; // position of un-rotated suffix
        StringBuilder transform = new StringBuilder(n);
        n--; // 0 index n
        for (int i = 0; i <= n; i++) {
            CircularSuffix suffix = new CircularSuffix(CSA.index(i), s);
            if (suffix.getOffset() == 0) {
                offset = i; // find un-rotated suffix
            }
            transform.append(suffix.charAt(n));
        }
        BinaryStdOut.write(offset);
        BinaryStdOut.write(transform.toString() );
        closeStreams();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int offset = BinaryStdIn.readInt();
        // keeps sorted appearance of char at the end of a sorted suffix.  For example ARD!R
        // [!] = 3, [A] = 0, [D] = 2, [R] = 1,4
        ArrayList<LinkedList<Integer>> charIndex = new ArrayList<>(CircularSuffix.RADIX);
        // number of chars read in
        int n = createCharIndex(charIndex);
        // first char of each sorted suffix; equivalent to sorting all the chars read in.
        char[] firstCharOfSortedCircularSuffix = createFirstCharOfSortedCircularSuffix(charIndex, n);
        // index i represents position in CircularSuffixArray (during transform) of next rotated suffix after i.
        int[] suffixUnsortKey = new int[n];
        for (int i = 0; i < n; i++) {
            char c = firstCharOfSortedCircularSuffix[i];
            suffixUnsortKey[i] = charIndex.get(c).removeFirst();
        }
        int curSuffix = offset;
        // uses suffix unsort key to get first char of each rotated suffix in original, unsorted order;
        // i.e. inverts the Burrows Wheeler transform
        for (int i = 0; i < n; i++) {
            BinaryStdOut.write( firstCharOfSortedCircularSuffix[curSuffix] );
            curSuffix = suffixUnsortKey[curSuffix];
        }
        closeStreams();
    }

    private static void closeStreams() {
        BinaryStdIn.close();
        BinaryStdOut.close();
    }

    private static int createCharIndex(ArrayList<LinkedList<Integer>> charIndex) {
        for (int i = 0; i < CircularSuffix.RADIX; i++) {
            charIndex.add(new LinkedList<>());
        }
        int n = 0;
        for (; !BinaryStdIn.isEmpty(); n++) {
            int c = BinaryStdIn.readChar();
            charIndex.get(c)
                    .addLast(n);
        }
        return n;
    }

    private static char[] createFirstCharOfSortedCircularSuffix(ArrayList<LinkedList<Integer>> charIndex, int n) {
        char[] suffixB = new char[n];
        for(int i = 0, suffixI = 0; i < CircularSuffix.RADIX; i++) {
            int end = suffixI + charIndex.get(i).size();
            char c = (char) i;
            while (suffixI < end) {
                suffixB[suffixI] = c;
                suffixI++;
            }
        }
        return suffixB;
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