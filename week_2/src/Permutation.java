import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdRandom;

// Pass on integer argument k and a list of strings.  This
// this program will return a k length uniformly random sample
// from the input while only using O(k) memory through them
// implementation of a reservoir sampling algorithm


public class Permutation {
    private RandomizedQueue<String> RQ = new RandomizedQueue<String>();
    public Permutation() { }


    public static void main(String[] args) {
        // Implements reservoir sampling
        Permutation Perm = new Permutation();
        int k, i; 
        i = k = 0;
        if (args.length == 1)
            i = k = Integer.parseInt(args[0]);
        else 
            throw new IllegalArgumentException("Provide arg for number of values you want to return");
        String cur;
        while (!StdIn.isEmpty()) {
            cur = StdIn.readString();
            if (Perm.RQ.size() < k) Perm.RQ.enqueue(cur);
            else {
                i++;
                if (StdRandom.uniform(0,i) < k) {
                    Perm.RQ.dequeue();
                    Perm.RQ.enqueue(cur);
                } 
            }
        }
        for (String s : Perm.RQ) { System.out.println(s); } 
    }
}