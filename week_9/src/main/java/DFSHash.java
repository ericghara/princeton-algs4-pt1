public class DFSHash extends WordHash {

    final int index;

    public DFSHash(int index, int c) {
        super();
        this.index = index;
        this.append(c);
    }

    public DFSHash(WordHash parent,int index, int c) {
        super(parent);
        this.index = index;
        this.append(c);
    }
}
