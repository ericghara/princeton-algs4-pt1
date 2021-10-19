import java.util.ArrayList;
import java.util.Comparator;
import java.util.Stack;
import java.util.LinkedList;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;


public class KdTree {
    private KdNode root;
    private int N;  //num elements
    private static final Comparator<KdNode> KD_ORDER = new KdOrder();
    private enum Direction {L, R}
    private enum Dimension {X, Y}


    public KdTree() {
        N = 0;
    }

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
                if ( head.equals(query) ) { return true; }
                head = head.right;
            }
        }
        return false;
    }

    public void insert(Point2D p) {
        errorOnNull(p);
        if ( contains(p) ) { return; }  // Avoid degenerates
        N ++;
        KdNode newNode = new KdNode(p);
        // First item
        if (root == null) {
            root = newNode;
            root.order = Dimension.X;
            root.Xrange = new KdRange();
            root.Yrange = new KdRange();
            return;
        }
        KdNode head = root;
        // Right after newNode is placed root == newNode (note more robust than root.equals(newNode))
        while (true) {
            int cmp = KD_ORDER.compare(newNode, head);
            if (cmp < 0) {
                if (head.left == null) { head.add(Direction.L, newNode); break; }
                head = head.left;
            }
            else {
                if (head.right == null) { head.add(Direction.R, newNode); break; }
                head = head.right;
            }
        }
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
        nodes.push(root);
        while (!nodes.empty()) {
            KdNode cur = nodes.pop();
            double curDist = cur.point.distanceSquaredTo(p);
            if (curDist < minDist) {
                minDist = curDist;
                minPoint = cur.point;
            }
            double Ldist = cur.SubtreeDistanceSquaredTo(p, Direction.L);
            double Rdist = cur.SubtreeDistanceSquaredTo(p, Direction.R);
            // if both directions are possibilities go toward query point first
            if (Ldist < minDist && Rdist < minDist) {
                if (KD_ORDER.compare(pNode, cur) < 0) {
                    nodes.push(cur.right); nodes.push(cur.left); } // left top of stack
                else { nodes.push(cur.left); nodes.push(cur.right); } // right top of stack
            }
            else if (Ldist < minDist) { nodes.push(cur.left); }
            else if (Rdist < minDist) { nodes.push(cur.right); }
            // else we've reached a leaf || neither child node can bring you closer to query
        }
        return minPoint;
    }

    public void draw() { allNodes().forEach(node -> node.point.draw()); }

    public int size() { return N; }

    public boolean isEmpty() {
        return size() == 0;
    }

    // returns nodes in level order from root (ie excluding sentinel)
    // Implemented for debugging, also used by draw (although overkill)
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
        KdNode left = null;
        KdNode right = null;
        final Point2D point;
        KdRange Xrange, Yrange;
        KdTree.Dimension order;

        public KdNode(Point2D p) {
            point = p;
        }

        private void add(Direction dir, KdNode child) {
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

        // Ignores dimension
        public boolean equals(KdNode that) { return this.point.equals(that.point); }

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

        public KdRange() { min = 0; max = 1; }  // for sentinel

        public KdRange(double parentCoord, KdRange parentRange, Direction dir) {
            if (dir == Direction.L) { max = parentCoord; min = parentRange.min; }
            else { min = parentCoord; max = parentRange.max; }
        }
    }


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
        // Check that all added points can be found in tree
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
        System.out.printf("-> Input items match tree size? %b%n", tree.size()==cnt);
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


