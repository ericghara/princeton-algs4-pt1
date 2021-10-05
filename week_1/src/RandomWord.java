import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class RandomWord {
    public static void main(String[] args){
        String champ="";
        int cnt = 1;
        while (!StdIn.isEmpty()) {
            String tmp=StdIn.readString();
            if (StdRandom.bernoulli((float)1/cnt)) {champ = tmp;}
            cnt +=1;
        }
       StdOut.println(champ); 
    }

}