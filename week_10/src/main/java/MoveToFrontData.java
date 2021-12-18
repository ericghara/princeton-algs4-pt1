class MoveToFrontData {
    private static final int R = CircularSuffix.RADIX;

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