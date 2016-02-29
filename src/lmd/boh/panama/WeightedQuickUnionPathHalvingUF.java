package lmd.boh.panama;

/**
 * Class implementing the weighted union-find data structure
 * with path halving. Based on the Algorithms I lectures by
 * R Sedgewick and K Wayne on Coursera.
 * @author Luigi Maria Damato
 *
 */
public class WeightedQuickUnionPathHalvingUF {

    /**
     * parent[i] is the parent of the i-th element
     */
    private int[] parent;

    /**
     * size[i] is the size of the set containing i
     */
    private int[] size;

    /**
     * Number of distinct sets
     */
    private int count;

    /**
     * Initialize a new disjoint set of size n
     * @param n the number of initially disjoint elements
     */
    public WeightedQuickUnionPathHalvingUF(int n) {
        count = n;
        parent = new int[n];
        size = new int[n];

        for (int i = 0; i < n; i++) {
            parent[i] = i;
            this.size[i] = 1;
        }
    }

    /**
     * Unifies two elements
     * @param p the first element
     * @param q the second element
     */
    public void union(int p, int q) {
        int a = find(p);
        int b = find(q);

        if (a != b) {
            if (size[b] >= size[a]) {
                parent[a] = b;
                size[b] += size[a];
            } else {
                parent[b] = a;
                size[a] += size[b];
            }
            count--;
        }
    }

    /**
     * Find the parent of p
     * @param p the element we want to look up
     * @return the parent of p
     */
    public int find(int p) {
        validate(p);

        while (parent[p] != p) {
            parent[p] = parent[parent[p]];
            p = parent[p];
        }

        return p;
    }

    /**
     * Checks whether two elements are connected
     * @param p the first elements
     * @param q the second elements
     * @return true if the elements are connected, false otherwise
     */
    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    /**
     * Returns the number of disjoint sets
     * @return the number of disjoint sets
     */
    public int count() {
        return count;
    }

    /**
     * Check whether p is a valid element of the disjoint set
     * @param p the element to be validated
     */
    private void validate(int p) {
        int n = parent.length;
        if (p < 0 || p >= n) {
            throw new IndexOutOfBoundsException();
        }
    }
}
