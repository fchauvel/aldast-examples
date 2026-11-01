import java.util.HashMap;
import java.util.List;
import java.util.Map;

// We have a box that can carry a limited weight, and items that have a weight
// and a value. What is the highest value we can put in the box?
//
// We look at the items one after the other. For each item, we can leave it,
// or take it if it fits. Each time, what remains to decide is the same
// problem, with fewer items, and possibly less room in the box.
public class Knapsack {

    public record Item(String name, int weight, int value) {
    }

    // What remains to solve: the items from this index on, with this much room
    private record Subproblem(int index, int capacity) {
    }

    private final List<Item> items;

    // How many times we have called the methods below
    public int calls = 0;

    public Knapsack(List<Item> items) {
        this.items = List.copyOf(items);
    }

    // The best value we can get with the given capacity
    public int recursive(int capacity) {
        return recursive(0, capacity);
    }

    private int recursive(int index, int capacity) {
        calls++;
        if (index == items.size()) {
            return 0;
        }
        var item = items.get(index);
        int leave = recursive(index + 1, capacity);
        if (item.weight() > capacity) {
            return leave;
        }
        int take = item.value() + recursive(index + 1, capacity - item.weight());
        return Math.max(leave, take);
    }

    // The same, but we remember the problems we have already solved (memoization)
    public int memoized(int capacity) {
        return memoized(0, capacity, new HashMap<>());
    }

    private int memoized(int index, int capacity, Map<Subproblem, Integer> cache) {
        calls++;
        if (index == items.size()) {
            return 0;
        }
        var subproblem = new Subproblem(index, capacity);
        if (cache.containsKey(subproblem)) {
            return cache.get(subproblem);
        }
        var item = items.get(index);
        int leave = memoized(index + 1, capacity, cache);
        int best = leave;
        if (item.weight() <= capacity) {
            int take = item.value() + memoized(index + 1, capacity - item.weight(), cache);
            best = Math.max(leave, take);
        }
        cache.put(subproblem, best);
        return best;
    }

    // We solve the smallest problems first, and fill a table (bottom-up)
    public int bottomUp(int capacity) {
        return table(capacity)[0][capacity];
    }

    // The cell table[index][c] is the best value we can get from the items from
    // this index on, with the capacity c. We fill the table from the last item.
    public int[][] table(int capacity) {
        var table = new int[items.size() + 1][capacity + 1];
        for (int index = items.size() - 1; index >= 0; index--) {
            var item = items.get(index);
            for (int c = 0; c <= capacity; c++) {
                int leave = table[index + 1][c];
                if (item.weight() > c) {
                    table[index][c] = leave;
                } else {
                    int take = item.value() + table[index + 1][c - item.weight()];
                    table[index][c] = Math.max(leave, take);
                }
            }
        }
        return table;
    }
}
