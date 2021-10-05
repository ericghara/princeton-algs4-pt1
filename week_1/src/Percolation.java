import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import edu.princeton.cs.algs4.StdIn;
// import java.util.Arrays;  // disable for submission, uncomment for testing along with array printing in main

// This class implements an API that allows modeling percolation through a 2d system as an n x n grid. sites
// are either open (porous) or closed (nonporous). A weighted quick union algorithm/data structure
// allows connected components to be merged and found.  Percolation is determined by assessing
// connectivity between any site at the bottom of the grid to any site at the top of the grid.
// time O: constructor: n, union: log(n), find log(n)
// space O: n

public class Percolation {
    private WeightedQuickUnionUF uf; //union find
    private boolean [] [] oGrid; // open grid
    private final int start, finish; // virtual start & end node
    private final int n; // dimmensions of grid (n x n)
    private int numOpen = 0;  // number of open sites

    // constructor - creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) 
            throw new IllegalArgumentException("Invalid input: Grid size must be > 0");
        this.n = n;
        this.uf = new WeightedQuickUnionUF(n*n+2);
        this.oGrid = new boolean[n][n];
        this.start = n * n;
        this.finish = n * n + 1;
        int fRow = n * (n - 1); // first item of finish row
        for (int col = 0; col < n; col ++) {  // connect all sites in start & end rows to start & finish virtual sites
            this.uf.union(0 + col, this.start);
            this.uf.union(fRow + col, this.finish);
        }   
    }

    private boolean valid(int row, int col, boolean silent) {
        if (row < 1 || row > this.n || col < 1 ||col > this.n) {
            if (silent) 
                return false;
            else
                throw new IllegalArgumentException("Invalid input: coordinates must lie between 1 and n.");
        } 
        return true;
    }
    
    //converts from row x col to linear
    private int flatten(int row, int col) {
        return ((row-1)*this.n) + col-1;
    }
    
    
    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (isOpen(row, col)) return;
        this.oGrid[row-1][col-1] = true;
        this.numOpen +=1;
        int[][] neighbors = { {row+1, col}, {row-1, col}, {row, col+1}, {row, col-1} };
        for (int[] nbr : neighbors) {
            if (isOpen(nbr[0], nbr[1], true))
                this.uf.union(flatten(nbr[0], nbr[1]), flatten(row, col));     
        }
    }


    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (valid(row, col, false) && oGrid[row-1][col-1]) return true;
        return false;
    }

    // overloaded isOpen allows for exception free checking
    private boolean isOpen(int row, int col, boolean silent) {
        if (valid(row, col, silent) && oGrid[row-1][col-1]) return true;
        return false;
    }
    

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (isOpen(row, col) && uf.find(this.start) == uf.find(this.flatten(row, col))) return true;
        return false;
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return this.numOpen;
    }

    // does the system percolate?
    public boolean percolates() {
        return uf.find(this.start) ==  uf.find(this.finish); // known bug - if n = 1 system always percolates 
    }                                                        // due to way virtual beginning and end nodes are implemented

    // test client
    public static void main(String[] args) {
        if ( !StdIn.isEmpty() ) {
            Percolation Perc = new Percolation(Integer.valueOf(StdIn.readLine()));
            while (!StdIn.isEmpty()) {
                String[] coord =  StdIn.readLine().trim().split("\\s+");
                if (coord[0].contains("#")) continue;  // comment out full line with #
                else if (coord.length == 2)
                    Perc.open(Integer.valueOf(coord[0]), Integer.valueOf(coord[1])); // enter coords "r c" (space delimter)
                else if (coord.length == 0)
                    System.out.printf("Warning - caught an empty line"); // tolerates an empty line
                else
                    throw new IllegalArgumentException("Couldn't parse input -- caught an improperly formatted line");
            }
            // for (boolean[] line : Perc.oGrid)  // enable for testing but not submission
                // System.out.println(Arrays.toString(line));
            System.out.printf("System Percolates: %1$b%n", Perc.percolates());
            System.out.printf("1 1 is full (connects to start)? %1$b %n%2$d %2$d is full? %3$b%n", Perc.isFull(1,1), Perc.n, Perc.isFull(Perc.n, Perc.n));
            System.out.printf("number open: %1$d%n", Perc.numberOfOpenSites());

        }
    }
}
