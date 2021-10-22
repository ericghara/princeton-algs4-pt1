import java.util.ArrayList;
import java.util.Comparator;
import java.util.Stack;
import java.util.LinkedList;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;


public class KdTree {
    private static final Comparator<KdNode> KD_ORDER = new KdOrder();
    private KdNode root;
    private int N = 0;  //num elements
    private enum Direction {L, R}
    private enum Dimension {X, Y}

    public void insert(Point2D p) {
        errorOnNull(p);
        KdNode newNode = new KdNode(p);
        // First item
        if (root == null) {
            root = newNode;
            root.order = Dimension.X;
            root.Xrange = new KdRange();
            root.Yrange = new KdRange();
            N++;
            return;
        }
        // For loop breaks on a duplicate while traversing tree
        // After a new node is added, loop detects it as a 'duplicate' ending loop
        for (KdNode head = root; head.compareTo(newNode) != 0;) {
            int cmp = KD_ORDER.compare(newNode, head);
            if (cmp < 0) {
                if (head.left == null) { head.add(Direction.L, newNode); }
                head = head.left;
            }
            else if (cmp >= 0) {
                if (head.right == null) { head.add(Direction.R, newNode); }
                head = head.right;
            }
        }
    }

    // standard iterative pre-order DFS
    public boolean contains(Point2D p) {
        errorOnNull(p);
        KdNode query = new KdNode(p);
        KdNode head = root;
        while (head != null) {
            int cmp = KD_ORDER.compare(query, head);
            if (cmp < 0) {
                head = head.left;
            }
            else if (cmp > 0) {
                head = head.right;
            }
            else {
                if ( head.compareTo(query) == 0 ) { return true; }
                head = head.right;
            }
        }
        return false;
    }

    // A modified DFS, first goes towards point P, but maintains a stack
    // of nodes seen during search for p that could have contained a point
    // closer to p.  After the initial DFS, DFSs from other candidates in
    // stack are considered
    public Point2D nearest(Point2D p) {
        errorOnNull(p);
        if (root == null) { return null; }
        // Note using **distance squared** throughout
        double minDist = Double.POSITIVE_INFINITY;  // sentinel
        KdNode pNode = new KdNode(p); // need to convert p to a node for compares
        Point2D minPoint = root.point;
        Stack <KdNode> nodes = new Stack<>();
        Stack <Double> dists = new Stack<>();
        nodes.push(root); dists.push(0.0);
        while (!nodes.empty()) {
            KdNode curN = nodes.pop();
            if (minDist < dists.pop()) { continue; };
            double curDist = curN.point.distanceSquaredTo(p);
            if (curDist < minDist) {
                minDist = curDist;
                minPoint = curN.point;
            }
            double Ldist = curN.SubtreeDistanceSquaredTo(p, Direction.L);
            double Rdist = curN.SubtreeDistanceSquaredTo(p, Direction.R);
            // if both directions are possibilities go toward query point first
            if (Ldist < minDist && Rdist < minDist) {
                if (KD_ORDER.compare(pNode, curN) < 0) {
                    nodes.push(curN.right); nodes.push(curN.left);
                    dists.push(Rdist);      dists.push(Ldist);} // left node @ top of stack
                else { nodes.push(curN.left); nodes.push(curN.right);
                       dists.push(Ldist);     dists.push(Rdist); } // right node @ top of stack
            }
            else if (Ldist < minDist) { nodes.push(curN.left); dists.push(Ldist); }
            else if (Rdist < minDist) { nodes.push(curN.right); dists.push(Rdist); }
            // else we've reached a leaf || neither child node can bring you closer to query
        }
        return minPoint;
    }

    public void draw() { allNodes().forEach(node -> node.point.draw()); }

    // prints points in tree in level order
    private void print() { allNodes().forEach(node -> System.out.println(node.point)); }

    public int size() { return N; }

    public boolean isEmpty() {
        return size() == 0;
    }

    // returns nodes in level order from root
    private Iterable<KdNode> allNodes() {
        ArrayList<KdNode> nodes = new ArrayList<>(N);
        LinkedList<KdNode> deque = new LinkedList<>();
        deque.addLast(root);
        while (!deque.isEmpty()) {
            for (int i = 0; i < deque.size(); i++) {
                KdNode cur = deque.removeFirst();
                if (cur != null) {
                    nodes.add(cur);
                    deque.addLast(cur.right); deque.addLast(cur.left);
                }
            }
        }
        return nodes;
    }

    public Iterable<Point2D> range(RectHV rect) {
        errorOnNull(rect);
        if (root == null) { return null; }
        ArrayList<Point2D> insidePoints = new ArrayList<>();
        Stack <KdNode> nodes = new Stack<>();
        nodes.push(root);
        while (!nodes.empty()) {
            KdNode cur = nodes.pop();
            if (rect.contains(cur.point)) { insidePoints.add(cur.point); }
            if (cur.left != null && cur.left.intersects(rect)) {
                nodes.push(cur.left);
            }
            if (cur.right != null && cur.right.intersects(rect)) {
                nodes.push(cur.right);
            }
        }
        return insidePoints;
    }

    private static class KdOrder implements Comparator<KdNode> {
        public int compare(KdNode key, KdNode p) {
            // key is item being looked up, p is item in list
            // Dimension **determined by p**
            if (p.order == Dimension.X) {
                return Point2D.X_ORDER.compare(key.point, p.point);
            }
            return Point2D.Y_ORDER.compare(key.point, p.point);
        }
    }

    private void errorOnNull(Object obj) {
        if (obj == null) throw new IllegalArgumentException("Recieved a null input.");
    }

    private class KdNode implements Comparable<KdNode> {
        final Point2D point;
        KdNode left = null;
        KdNode right = null;
        KdRange Xrange, Yrange;
        KdTree.Dimension order;

        public KdNode(Point2D p) {
            point = p;
        }

        private void add(Direction dir, KdNode child) {
            N++;
            if (order == Dimension.X) {
                child.order = Dimension.Y;
                child.Xrange = new KdRange(point.x(), Xrange, dir);
                child.Yrange = Yrange;
            } else {
                child.order = Dimension.X;
                child.Xrange = Xrange;
                child.Yrange = new KdRange(point.y(), Yrange, dir);
            }
            if (dir == Direction.L) { left = child;}
            else { right = child; }
        }

        // Natural order. Priority Y then X; ignores dimension
        public int compareTo(KdNode that) { return this.point.compareTo(that.point); }

        public double SubtreeDistanceSquaredTo(Point2D p, Direction dir) {
            KdNode child;
            if (dir == Direction.L) { child = left; }
            else { child = right; }
            if (child == null) { return Double.POSITIVE_INFINITY; }
            double xmin = child.Xrange.min, xmax = child.Xrange.max,
                    ymin = child.Yrange.min, ymax = child.Yrange.max;
            //Adapted from edu.princeton.cs.algs4.RectHV.distanceSquaredTo
            double dx = 0.0, dy = 0.0;
            if      (p.x() < xmin) dx = p.x() - xmin;
            else if (p.x() > xmax) dx = p.x() - xmax;
            if      (p.y() < ymin) dy = p.y() - ymin;
            else if (p.y() > ymax) dy = p.y() - ymax;
            return dx*dx + dy*dy;
        }

        public boolean intersects(RectHV rect) {
            return Xrange.max >= rect.xmin() && Yrange.max >= rect.ymin()
                    && rect.xmax() >= Xrange.min && rect.ymax() >= Yrange.min;
        }

    }
    private class KdRange {
        double min, max;

        public KdRange() { min = 0; max = 1; }  // only used by first node added

        public KdRange(double parentCoord, KdRange parentRange, Direction dir) {
            if (dir == Direction.L) { max = parentCoord; min = parentRange.min; }
            else { min = parentCoord; max = parentRange.max; }
        }
    }

    // test client input filetype point coordinates separated by spaces. 1 point per line
    // Input coordinates should range from 0 0 to 1 1
    public static void main(String[] args) {
        String filename = args[0];
        In in = new In(filename);
        KdTree tree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            tree.insert(p);
        }
        in = new In(filename);
        int cnt = 0; int found = 0;
        // Check that all added points can be found in the tree
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            if (!tree.contains(p)) {
                System.out.printf("Couldn't find %1$s in set!%n", p);
            }
            else { found++; }
            cnt++;
        }
        System.out.printf("-> Set contained %1$d items; %2$d were found using \"contains\" method.%n", cnt,found);
        System.out.printf("-> # of input items (%1$d) match tree size (%2$d)? %3$b%n", cnt, tree.size(), tree.size()==cnt);
        StdDraw.enableDoubleBuffering();
        while (true) {  // Test nearest neighbor.

            // the location (x, y) of the mouse
            double x = StdDraw.mouseX();
            double y = StdDraw.mouseY();
            Point2D query = new Point2D(x, y);

            // draw all of the points
            StdDraw.clear();
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.01);
            tree.draw();

            // draw in red the nearest neighbor
            StdDraw.setPenRadius(0.03);
            StdDraw.setPenColor(StdDraw.RED);
            tree.nearest(query).draw();
            if (tree.contains(query)) { System.out.println("Found: " + query); };
            StdDraw.setPenRadius(0.02);
            StdDraw.show();
            StdDraw.pause(40);
        }
   }
}


