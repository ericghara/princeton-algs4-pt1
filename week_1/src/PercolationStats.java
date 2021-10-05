import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
//import java.util.*;

// Runs 2d percolation experiments and determines percolation threshold
// by randomly opening sites on an initially all closed grid/matrix.
// % open sites at time system percolates is percolation threshold of
// that run.  Experiments are repeated t times to estimate percolation
// threshold.

public class PercolationStats {
    private double[] results; // note this is filled in from end to beginning by PercolationStats
    private int totalSites;
    private final double Z = 1.96; // confidence level value (used for confidence interval calc)
    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n < 1 || trials < 1) 
            throw new IllegalArgumentException("Both grid size and number of trials must be >1");
        this.results = new double[trials];
        this.totalSites = n * n;
        for (int t = 0; t < trials; t++) {
            Percolation percExp = new Percolation(n);    
            while ( !percExp.percolates() ) {
                percExp.open( StdRandom.uniform(1,n+1), StdRandom.uniform(1,n+1));      // since we are relying on numberOfOpen sites to count
            }
            results[t] = (double) percExp.numberOfOpenSites()/ this.totalSites;              // we will not count repeat openings of same site
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(this.results);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(this.results);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean() - (this.Z * stddev() / Math.sqrt(this.results.length));

    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean() + (this.Z * stddev() / Math.sqrt(this.results.length));
    }

   // test client
   public static void main(String[] args) {
        if (args.length != 2)
            throw new IllegalArgumentException("Improper arguments: input n and T separated by a space");    
        int n = Integer.valueOf(args[0]);
        int T = Integer.valueOf(args[1]);
        if (n < 1 || T < 1) 
            throw new IllegalArgumentException("Both n and t must be >1");
        PercolationStats Stats = new PercolationStats(n, T);
        //System.out.println(Arrays.toString(Stats.results)); // for debug, make sure you enable import java.utils.* 
        /* uncomment for stats normalized to mean
        *System.out.printf("Mean: %1$31.1f%%%n", Stats.mean()*100);
        *System.out.printf("Relative Standard Deviation (%%): %1$.2f%%%n", Stats.stddev()/Stats.mean()*100);
        *System.out.printf("Low Confidence interval (%%): %1$9.2f%%%n", Stats.confidenceLo()/Stats.mean()*100);
        *System.out.printf("High Confidence interval (%%): %1$9.2f%%%n", Stats.confidenceHi()/Stats.mean()*100);
        */
        System.out.printf("%1$-24s=", "mean");
        System.out.println(Stats.mean()); // grader doesn't like double formatting from printf
        System.out.printf("%1$-24s=", "stdev"); 
        System.out.println(Stats.stddev()); // as before
        System.out.printf("%1$-24s= [%2$f,%3$f]%n", "95% confidence interval", Stats.confidenceLo(), Stats.confidenceHi());
   }

}