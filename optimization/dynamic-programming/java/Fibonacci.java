import java.util.HashMap;
import java.util.Map;

// The Fibonacci numbers: F(0) = 0, F(1) = 1, and every other number is the sum
// of the two before it.
public class Fibonacci {

    // How many times we have called the methods below. It tells how much work
    // they do.
    public int calls = 0;

    // Straight from the definition
    public long recursive(int n) {
        calls++;
        if (n == 0) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }
        return recursive(n - 1) + recursive(n - 2);
    }

    // The same, but we remember the numbers we have already computed (memoization)
    public long memoized(int n) {
        return memoized(n, new HashMap<>());
    }

    private long memoized(int n, Map<Integer, Long> cache) {
        calls++;
        if (n == 0) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }
        if (cache.containsKey(n)) {
            return cache.get(n);
        }
        long result = memoized(n - 1, cache) + memoized(n - 2, cache);
        cache.put(n, result);
        return result;
    }

    // We compute the numbers in order, from the smallest, and keep them in a
    // table (bottom-up)
    public long bottomUp(int n) {
        var table = new long[Math.max(n + 1, 2)];
        table[0] = 0;
        table[1] = 1;
        for (int i = 2; i <= n; i++) {
            table[i] = table[i - 1] + table[i - 2];
        }
        return table[n];
    }
}
