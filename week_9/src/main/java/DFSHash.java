/**
 * Extends WordHash to add an immutable index value.  The index refers to the index of the last letter added to the
 * hash on the (flattened) Boggle board.
 */
public class DFSHash extends WordHash {

    final int index;

    /**
     * Create a single character DFSHash object.
     *
     * @param index index value
     * @param c character to be hashed
     */
    public DFSHash(int index, int c) {
        super();
        this.index = index;
        this.append(c);
    }

    /**
     * Copies the hash values from another WordHash object and adds another character to the hash.  Creates a new
     * index field.
     * @param parent containing the hash to be copied
     * @param index index value
     * @param c character to be added to the hash.
     */
    public DFSHash(WordHash parent,int index, int c) {
        super(parent);
        this.index = index;
        this.append(c);
    }
}
