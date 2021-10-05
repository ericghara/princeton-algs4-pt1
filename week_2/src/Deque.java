import java.util.NoSuchElementException;
import java.util.Iterator;
import edu.princeton.cs.algs4.StdIn;

public class Deque<Item> implements Iterable<Item> {
    private Node last, first;
    private int N = 0; // size  
    public Deque() {
    }

    private class Node {
        Node prev, next;
        Item val;
        public Node (Item val, Node prev, Node next) {
            this.val = val;
            setPrev(prev);
            setNext(next); 
            }
        public Item val()                 { return this.val;}
        public Node next()                { return this.next;}
        public Node prev()                { return this.prev;}
        //public void setVal(Item newVal)   { this.val = newVal; }  // works but unneeded and don't want to unit test
        public void setPrev(Node newPrev) { this.prev = newPrev; }
        public void setNext(Node newNext) { this.next = newNext; }


    }

    // is the deque empty?
    public boolean isEmpty() { return this.N == 0; }

    // use this to assert queue is not empty
    private void isEmpty(boolean loud) { 
        if (this.N == 0) { throw new NoSuchElementException("Queue underflow"); }
    }

    // return the number of items on the deque
    public int size() { return this.N; }

    private void firstItem(Item item) {
        this.first = new Node(item, null, null);
        this.last = this.first; 
    }

    private Item lastItem() {
        this.first = this.first.next();
        Item val = this.last.val();
        this.last = null;
        return val;
    }
    
    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) { throw new IllegalArgumentException("You cannot add a null item to the queue."); }
        if ( isEmpty() ) { firstItem(item); }
        else {
            this.first.setNext(new Node(item, this.first, null));
            this.first = this.first.next();
        }
        this.N++;
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) { throw new IllegalArgumentException("You cannot add a null item to the queue."); }
        if ( isEmpty() ) { firstItem(item); }
        else {
            this.last.setPrev(new Node(item, null, this.last));
            this.last = this.last.prev();
        }
        this.N++;    
    }

    // remove and return the item from the front
    public Item removeFirst() {
        this.isEmpty(true);
        N--;
        if ( isEmpty() ) { return lastItem(); } // this decrements N too
        Item val = this.first.val();
        this.first = this.first.prev();
        //this.first.next().setPrev(null); // perhaps may enhance GC
        this.first.setNext(null);
        return val;        
    }

    // remove and return the item from the back
    public Item removeLast() {
        this.isEmpty(true);
        N--;
        if ( isEmpty() ) { return lastItem(); }
        Item val = this.last.val();
        this.last = this.last.next();
        //this.last.prev().setNext(null); // perhaps may enhance GC
        this.last.setPrev(null);
        return val;
    }

    // Iterates next to prev
    public Iterator<Item> iterator() 
    { return new DequeueIterator(); }

    private class DequeueIterator implements Iterator<Item> {
        Node iterNext = first;
        public boolean hasNext() { 
            return iterNext != null; }
        public void remove()     { throw new UnsupportedOperationException("Remove has not been implemented."); }
        public Item next()  { 
            if (!hasNext())      { throw new NoSuchElementException("You have iterated through the entire Queue.");}
            Item out = iterNext.val();
            iterNext = iterNext.prev();
            return out;    
        }
    } 

    // unit testing
    public static void main(String[] args) {
        Deque<String> DQ = new Deque<String>();
            String clearline = ""; boolean INTERACTIVE = false;  // set to false if using an input file true for manual inputs
            if (INTERACTIVE) clearline = "\033[1A\033[2K";
            
            while (!StdIn.isEmpty()) {
                String[] cmd =  StdIn.readLine().trim().split("\\s+", 2);
                if (cmd[0].length() == 0) {
                    System.out.printf(clearline);  // moves cursor up after each input if INTERACTIVE
                    System.out.printf("Warning - caught an empty line%n%n"); // tolerates an empty line
                }
                else {
                    System.out.printf(clearline); // moves cursor up after each input if INTERACTIVE
                    String op = cmd[0].toLowerCase();
                    String arg = null; //arg purposely allowed to remain null for debugging
                    if (cmd.length == 2) { arg = cmd[1]; }

                    if (op.contains("#"))            { System.out.println("#"); }
                    else if (op.equals("isempty"))   { System.out.println( DQ.isEmpty() ? "Empty!" : "Not Empty" ); }
                    else if (op.equals("size"))      { System.out.printf("size: %d%n", DQ.size() ); }
                    else if (op.equals("append")  
                             || op.equals("+>"))      { DQ.addFirst(arg); System.out.printf("+> %s %n", arg); }
                    else if (op.equals("appendleft")  
                             || op.equals("<+"))      { DQ.addLast(arg); System.out.printf("<+ %s %n", arg); }
                    else if (op.equals("pop")  
                             || op.equals("->"))      { System.out.printf("-> %s %n", DQ.removeFirst()); }
                    else if (op.equals("popleft")  
                             || op.equals("<-"))      { System.out.printf("<- %s %n", DQ.removeLast()); }
                    else if (op.equals("iterate"))   { for (String v : DQ) { System.out.println("i: " + v);} }
                    else if (op.equals("itest"))     { Iterator iter = DQ.iterator();
                                                       iter.next();
                                                       iter.hasNext();
                                                       iter.remove();
                                                       iter.next(); }
                    else                             { throw new UnsupportedOperationException("Invalid input"); }
                }                                   
            }
            
        }

    }