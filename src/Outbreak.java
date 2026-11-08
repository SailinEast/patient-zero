import java.util.*;

/**
 * Mission 5. Dynamic programming on the tree of infections. Root the tree in person 0.
 * Input.Tree.neighbours() gives the neighbours of every person. The number of people is at most about 100, so a recursive DFS is fine here.
 */
public class Outbreak {

    /**
     * Choose people to test so that for every link (somebody infected somebody) at least one of the two is tested,
     * and the total cost of testing is as small as possible. cost[v] is the price of testing person v.
     * Returns tested[v] = true if person v is tested.
     *
     * State for every person v (looking only at v and everything below it): the smallest cost when v IS tested, and
     * the smallest cost when v is NOT tested. What must then be true about the children of v in each case?
     * Do not forget to rebuild the answer (who exactly is tested), not only the cost: the picture needs it.
     * Main calls this twice: with all costs equal to 1 (the line "Tests needed") and with the real costs ("Cheapest testing").
     */
    public static boolean[] cheapestTests(int n, int[][] nb, int[] cost) {
        if (n == 0) return new boolean[0];

        int[] take = new int[n];
        int[] skip = new int[n];
        dfsCover(0, -1, nb, cost, take, skip);

        boolean[] tested = new boolean[n];
        tested[0] = (take[0] <= skip[0]);
        rebuildCover(0, -1, nb, take, skip, tested);
        return tested;
    }

    private static void dfsCover(int u, int parent, int[][] nb, int[] cost, int[] take, int[] skip) {
        take[u] = cost[u];
        skip[u] = 0;
        for (int v : nb[u]) {
            if (v == parent) continue;
            dfsCover(v, u, nb, cost, take, skip);
            take[u] += Math.min(take[v], skip[v]);
            skip[u] += take[v];
        }
    }

    private static void rebuildCover(int u, int parent, int[][] nb, int[] take, int[] skip, boolean[] tested) {
        for (int v : nb[u]) {
            if (v == parent) continue;
            if (!tested[u]) {
                tested[v] = true; // parent not tested -> child must be tested
            } else {
                tested[v] = (take[v] <= skip[v]);
            }
            rebuildCover(v, u, nb, take, skip, tested);
        }
    }

    /**
     * The number of links on the longest chain of infections, that is the longest path in the tree (counted in links).
     * A path goes up from one person and down to another; for every person you need the two longest ways down.
     * Example: a line 0-1-2-3 has chain 3; a star with centre 0 and leaves 1, 2, 3 has chain 2.
     */
    public static int longestChain(int n, int[][] nb) {
        if (n <= 1) return 0;
        int[] best = new int[1];
        dfsDepth(0, -1, nb, best);
        return best[0];
    }

    private static int dfsDepth(int u, int parent, int[][] nb, int[] best) {
        int top1 = 0; // longest branch down from u
        int top2 = 0; // second longest branch down from u

        for (int v : nb[u]) {
            if (v == parent) continue;
            int h = dfsDepth(v, u, nb, best) + 1;
            if (h > top1) {
                top2 = top1;
                top1 = h;
            } else if (h > top2) {
                top2 = h;
            }
        }

        best[0] = Math.max(best[0], top1 + top2);
        return top1;
    }
}
