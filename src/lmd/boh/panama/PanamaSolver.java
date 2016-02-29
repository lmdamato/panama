package lmd.boh.panama;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class solves the Panama problem of finding peaks from which a river
 * could flow all the way to both sides of the isthmus. It uses a weighted
 * Union-Find data structure with path halving to efficiently find all the
 * peaks. For an input array of width W and height H, it takes about O(W*H)
 * time and O(W*H) space for the software to solve it. The exact time
 * complexity would involve the functional inverse of the Ackermann
 * function, which is less than 5 for pretty much every humanly conceivable
 * application, so we shall neglect it.
 * @author Luigi Maria Damato
 */
public final class PanamaSolver {

    /**
     * Byte value corresponding to boolean true
     */
    private static final byte TRUE = 0x1;

    /**
     * Byte value corresponding to boolean false
     */
    private static final byte FALSE = 0x0;

    /**
     * Width of the input array
     */
    private final int w;

    /**
     * Height of the input array
     */
    private final int h;

    /**
     * The result set
     */
    private final Set<Pair> peaks;

    /**
     * Inner class representing a pair of integers, corresponding to the
     * zero-based coordinates of a point in the array
     */
    private static final class Pair {

        /**
         * The x-coordinate
         */
        private final int x;

        /**
         * The y-coordinate
         */
        private final int y;

        /**
         * Creates a new Pair object
         * @param theX the x-coordinate
         * @param theY the y-coordinate
         */
        private Pair(final int theX, final int theY) {
            x = theX;
            y = theY;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof Pair)) {
                return false;
            }
            if (this == o) {
                return true;
            }
            Pair p = (Pair) o;
            return this.x == p.x && this.y == p.y;
        }

        @Override
        public int hashCode() {
            return 31 * Integer.valueOf(x).hashCode()
                    + Integer.valueOf(y).hashCode();
        }
    }

    /**
     * Computes the solution to the Panama problem.
     * @param in the input array
     */
    public PanamaSolver(int[][] in) {
        if (in == null || in.length == 0
                || in[0] == null || in[0].length == 0) {
            throw new IllegalArgumentException();
        }

        w = in.length;
        h = in[0].length;

        byte[][] visitedSea = new byte[w][h];
        WeightedQuickUnionPathHalvingUF leftLand =
                new WeightedQuickUnionPathHalvingUF(w * h + 1);
        WeightedQuickUnionPathHalvingUF rightLand =
                new WeightedQuickUnionPathHalvingUF(w * h + 1);

        connectSea(in, 0, 0, visitedSea, leftLand);
        connectSea(in, w - 1, h - 1, visitedSea, rightLand);

        peaks = new HashSet<Pair>();
        for (int i = 0; i < w * h; ++i) {
            if (leftLand.connected(i, w * h) && rightLand.connected(i, w * h)) {
                peaks.add(new Pair(i / w, i % w));
            }
        }

    }

    /**
     * Starting from a point where we know we have sea, recursively connect all
     * the neighbouring points in the sea. When we find a land mass, start
     * connecting all the points from which a river could flow in the last found
     * sea point.
     * @param isthmus the input array
     * @param i the x-position (expected to be a sea point)
     * @param j the x-position (expected to be a sea point)
     * @param sea array keeping track of visited sea points
     * @param land object keeping track of the visited land, to be passed as
     *             argument when triggering the connectLand method
     */
    private void connectSea(int[][] isthmus, int i, int j, byte[][] sea,
    		WeightedQuickUnionPathHalvingUF land) {

        sea[i][j] = TRUE;

        //try to go left
        if (i > 0) {
            if (isthmus[i - 1][j] == 0) {
                if (sea[i - 1][j] == FALSE) {
                    connectSea(isthmus, i - 1, j, sea, land);
                }
            } else {
                connectLand(isthmus, i - 1, j, land);
            }
        }

        //try to go right
        if (i < w - 1) {
            if (isthmus[i + 1][j] == 0) {
                if (sea[i + 1][j] == FALSE) {
                    connectSea(isthmus, i + 1, j, sea, land);
                }
            } else {
                connectLand(isthmus, i + 1, j, land);
            }
        }

        //try to go up
        if (j > 0) {
            if (isthmus[i][j - 1] == 0) {
                if (sea[i][j - 1] == FALSE) {
                    connectSea(isthmus, i, j - 1, sea, land);
                }
            } else {
                connectLand(isthmus, i, j - 1, land);
            }
        }

        //try to go down
        if (j < h - 1) {
            if (isthmus[i][j + 1] == 0) {
                if (sea[i][j + 1] == FALSE) {
                    connectSea(isthmus, i, j + 1, sea, land);
                }
            } else {
                connectLand(isthmus, i, j + 1, land);
            }
        }
    }

    /**
     * Starting from a point where we know we have land, recursively connect all
     * the neighbouring points from which a river could flow in the first found
     * land point.
     * @param isthmus the input array
     * @param i the x-position (expected to be a land point)
     * @param j the y-position (expected to be a land point)
     * @param land object keeping track of the visited land
     */
    private void connectLand(int[][] isthmus, int i, int j,
            WeightedQuickUnionPathHalvingUF land) {

        int pos = to1D(i, j);

        land.union(pos, w * h);

        int next;

        //try to go left 
        if (i > 0 && isthmus[i - 1][j] >= isthmus[i][j]) {
            next = to1D(i - 1, j);

            if (!land.connected(next, w * h)) {
                connectLand(isthmus, i - 1, j, land);
            }
        }

        //try to go right
        if (i < w - 1 && isthmus[i + 1][j] >= isthmus[i][j]) {
            next = to1D(i + 1, j);

            if (!land.connected(next, w * h)) {
                connectLand(isthmus, i + 1, j, land);
            }
        }

        //try to go up
        if (j > 0 && isthmus[i][j - 1] >= isthmus[i][j]) {
            next = to1D(i, j - 1);

            if (!land.connected(next, w * h)) {
                connectLand(isthmus, i, j - 1, land);
            }
        }

        //try to go down
        if (j < h - 1 && isthmus[i][j + 1] >= isthmus[i][j]) {
            next = to1D(i, j + 1);

            if (!land.connected(next, w * h)) {
                connectLand(isthmus, i, j + 1, land);
            }
        }
    }

    /**
     * Converts zero-based 2-dimensional position to the corresponding
     * zero-based 1-dimensional position
     * @param i the x-coordinate
     * @param j the y-coordinate
     * @return the zero-based 1-dimensional position
     */
    private int to1D(final int i, final int j) {
        return i * w + j;
    }

    @Override
    public String toString() {
        return peaks.stream()
                    .map(Pair::toString)
                    .collect(Collectors.joining("\n"));
    }

    /**
     * Testing
     * @param args arguments
     */
    public static final void main(String... args) {
        int[][] p = {{0, 1, 1, 1, 2, 1, 1, 1, 0},
                     {0, 1, 0, 0, 0, 0, 0, 1, 0},
                     {0, 1, 0, 1, 1, 1, 0, 1, 0},
                     {0, 1, 0, 1, 0, 1, 0, 1, 0},
                     {0, 1, 0, 1, 1, 1, 0, 1, 0},
                     {0, 1, 0, 0, 0, 0, 0, 1, 0},
                     {0, 1, 1, 1, 2, 1, 1, 1, 0}};

        PanamaSolver ps = new PanamaSolver(p);

        System.out.println(ps);
    }
}
