import java.util.*;

/**
 * Missions 3 and 4. Find a word in a long text.
 */
public class Marker {

    /**
     * Mission 3a. The prefix function of p. pi[i] = the length of the longest proper prefix of p[0..i] that is
     * also a suffix of p[0..i] ("proper" means shorter than p[0..i] itself).
     * Example: p = "ACGAC" gives [0, 0, 0, 1, 2]. The last number is the "Border length" line.
     */
    public static int[] prefixFunction(String p) {
        int m = p.length();
        int[] pi = new int[m];
        for (int i = 1, k = 0; i < m; i++) {
            while (k > 0 && p.charAt(i) != p.charAt(k)) {
                k = pi[k - 1];
            }
            if (p.charAt(i) == p.charAt(k)) {
                k++;
            }
            pi[i] = k;
        }
        return pi;
    }

    /**
     * Mission 3b. KMP. All start positions (counting from 0, in increasing order) where p occurs in text.
     * Overlapping occurrences count: "ACG" occurs in "ACGACG" at 0 and 3, and "AA" in "AAA" at 0 and 1.
     * Use the prefix function: the text pointer never goes back. The number of positions is "Marker hits",
     * the first one is "First hit" (-1 when there is none).
     */
    public static int[] findAll(String text, String p) {
        int n = text.length();
        int m = p.length();
        if (m == 0 || n < m) return new int[0];

        int[] pi = prefixFunction(p);
        List<Integer> hits = new ArrayList<>();
        int k = 0;

        for (int i = 0; i < n; i++) {
            while (k > 0 && text.charAt(i) != p.charAt(k)) {
                k = pi[k - 1];
            }
            if (text.charAt(i) == p.charAt(k)) {
                k++;
            }
            if (k == m) {
                hits.add(i - m + 1);
                k = pi[k - 1]; // fall back to allow overlapping matches
            }
        }

        int[] result = new int[hits.size()];
        for (int i = 0; i < hits.size(); i++) {
            result[i] = hits.get(i);
        }
        return result;
    }

    /**
     * Mission 4. Rabin-Karp. The number of positions i in a such that the word of k letters starting at i
     * (a[i..i+k-1]) also occurs somewhere in b. Count positions, not different words. This is the "Shared words" line.
     * The idea: give every window of k letters a number (a rolling hash, updated in O(1) when the window moves by one),
     * look the number up among the windows of b, and when it is found COMPARE THE LETTERS, because two different words
     * can have the same hash.
     * Example: a = "ACGTAC", b = "TTACGT", k = 4: the words of a are ACGT, CGTA, GTAC; only ACGT is in b, so the answer is 1.
     */
    public static int sharedWords(String a, String b, int k) {
        if (k <= 0 || a.length() < k || b.length() < k) return 0;

        final long BASE = 131L;
        final long MOD = 1_000_000_007L;

        long topPower = 1L;
        for (int i = 1; i < k; i++) {
            topPower = (topPower * BASE) % MOD;
        }

        // Map each rolling hash in B to the list of start positions with that hash
        Map<Long, List<Integer>> bWindows = new HashMap<>();
        long hashB = 0;
        for (int i = 0; i < k; i++) {
            hashB = (hashB * BASE + b.charAt(i)) % MOD;
        }
        bWindows.computeIfAbsent(hashB, x -> new ArrayList<>()).add(0);

        for (int i = 1; i + k <= b.length(); i++) {
            hashB = (hashB - b.charAt(i - 1) * topPower % MOD + MOD) % MOD;
            hashB = (hashB * BASE + b.charAt(i + k - 1)) % MOD;
            bWindows.computeIfAbsent(hashB, x -> new ArrayList<>()).add(i);
        }

        // Roll across A and verify characters when hashes match
        int sharedCount = 0;
        long hashA = 0;
        for (int i = 0; i < k; i++) {
            hashA = (hashA * BASE + a.charAt(i)) % MOD;
        }
        if (matchesInB(a, 0, b, bWindows.get(hashA), k)) {
            sharedCount++;
        }

        for (int i = 1; i + k <= a.length(); i++) {
            hashA = (hashA - a.charAt(i - 1) * topPower % MOD + MOD) % MOD;
            hashA = (hashA * BASE + a.charAt(i + k - 1)) % MOD;
            if (matchesInB(a, i, b, bWindows.get(hashA), k)) {
                sharedCount++;
            }
        }

        return sharedCount;
    }

    private static boolean matchesInB(String a, int posA, String b, List<Integer> candidates, int k) {
        if (candidates == null) return false;
        for (int posB : candidates) {
            boolean same = true;
            for (int j = 0; j < k; j++) {
                if (a.charAt(posA + j) != b.charAt(posB + j)) {
                    same = false;
                    break;
                }
            }
            if (same) return true;
        }
        return false;
    }
}
