import java.util.NoSuchElementException;
import java.util.Iterator;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdRandom;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private Item[] rq = (Item[]) new Object[1];
    private int N = 0; 
    // construct an empty randomized queue
    public RandomizedQueue() {}

    // is the randomized queue empty?
    public boolean isEmpty() { return this.N == 0; }

    // return the number of items on the randomized queue
    public int size() { return this.N; }

    private int length() { return rq.length; }

    private void resize(int len) {
        Item[] tmp = (Item[]) new Object[len];
        for (int i = 0 ; i < this.N; i ++) {
            tmp[i] = this.rq[i];
        }
        this.rq = tmp;
    }
    
    //Returns -1 if no resize required OR the new size rq should be resized to
    private int needsResize() {
        if      (this.N >= this.rq.length)         { return this.rq.length * 2; }  // double array size at max capacity
        else if (this.N *4  == this.rq.length)     { return this.rq.length / 2; } // half array size at 1/4 capacity
        return -1;
    }
    
    // add the item
    public void enqueue(Item item) {
        if (item == null) { throw new IllegalArgumentException("You cannot add a null item to the queue."); }
        this.N++;
        int n = needsResize();
        if (n != -1) {resize(n);}
        this.rq[this.N-1] = item;
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) { throw new NoSuchElementException("You cannot dequeue from an empty list."); }
        int i = StdRandom.uniform(this.N);
        this.N--;
        Item elem = this.rq[i];
        this.rq[i] = this.rq[this.N]; // Fill in popped item from list with last element (stops list from becoming fragmented)
        this.rq[this.N] = null; // remove duplicate reference to last element
        int n = needsResize();
        if (n != -1) {resize(n);}
        return elem;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) { throw new NoSuchElementException("You cannot sample an empty list."); }
        return this.rq[StdRandom.uniform(this.N)];
    }

    private Item[] randomClone() {
        Item[] copy = (Item[]) new Object[this.N];
        for (int i = 0; i < this.N; i++)
            copy[i] = rq[i];
        return copy;
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() 
        { return new RandomizedQueueIterator(); }

    private class RandomizedQueueIterator implements Iterator<Item> {
        private Item[] copyRQ = (Item[]) randomClone();
        private int i = copyRQ.length-1;
        public boolean hasNext() { return i >= 0; }
        public void remove()     { throw new UnsupportedOperationException("Remove has not been implemented."); }
        public Item next()       { 
            if (i < 0) { throw new NoSuchElementException("You have iterated through the entire Queue."); }
            int randI = StdRandom.uniform(i+1); // uniform is 0-n EXclusive
            Item out = copyRQ[randI];
            copyRQ[randI] = copyRQ[i];
            copyRQ[i--] = null;
            return out; }
    } 

    // unit testing
    public static void main(String[] args) {
        RandomizedQueue<String> RQ = new RandomizedQueue<String>();
            while (!StdIn.isEmpty()) {
                String[] cmd =  StdIn.readLine().trim().split("\\s+", 2);
                if (cmd[0].length() == 0) {
                    System.out.printf("\033[1A\033[2K");
                    System.out.printf("Warning - caught an empty line%n%n"); // tolerates an empty line
                }
                else if (cmd.length > 2)
                   throw new IllegalArgumentException("Couldn't parse input -- caught an improperly formatted line");
                else {
                    System.out.printf("\033[1A\033[2K"); // move cursor up. Clear line.
                    String op = cmd[0].toLowerCase();
                    String arg = null; //arg purposely allowed to remain null for debugging
                    if (cmd.length == 2) { arg = cmd[1]; }

                    if (op.contains("#"))            { System.out.println("#"); }
                    else if (op.equals("isempty"))   { System.out.println( RQ.isEmpty() ? "Empty!" : "Not Empty" ); }
                    else if (op.equals("size"))      { System.out.printf("size: %d%n", RQ.size() ); }
                    else if (op.equals("enqueue")  
                             || op.equals("+"))      { RQ.enqueue(arg); System.out.printf("+ %s %n", arg); }
                    else if (op.equals("dequeue")   
                             || op.equals("-"))       { System.out.printf("- %s%n", RQ.dequeue()); }
                    else if (op.equals("sample"))    { System.out.printf("> %s%n", RQ.sample()); }
                    else if (op.equals("iterate"))   { for (String v : RQ) { System.out.println("i: " + v);} }
                    else if (op.equals("length"))    { System.out.printf("length: %d%n", RQ.length() ); }
                    else if (op.equals("itest"))     { Iterator iter = RQ.iterator();
                                                       iter.next();
                                                       iter.hasNext();
                                                       iter.remove();
                                                       iter.next(); }
                    else                             { throw new UnsupportedOperationException("Invalid input"); }
                }                                   
            }
            
        }

    }
