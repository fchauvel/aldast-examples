import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// The knapsack again: a box that can carry a limited weight, and items that
// have a weight and a value.
//
// A solution is the set of the items we put in the box, as long as the box can
// carry them. Its neighbours are the solutions we get by adding one item, or
// by removing one. If we allow swaps, they also include the solutions we get by
// replacing an item of the box with one that is missing.
public class KnapsackSpace implements SearchSpace<Set<String>> {

    public record Item(String name, int weight, int value) {
    }

    private final List<Item> items;
    private final int capacity;
    private final Set<String> initial;
    private final boolean allowSwaps;

    public KnapsackSpace(List<Item> items, int capacity) {
        this(items, capacity, Set.of(), false);
    }

    public KnapsackSpace(List<Item> items, int capacity, Set<String> initial) {
        this(items, capacity, initial, false);
    }

    public KnapsackSpace(List<Item> items, int capacity, Set<String> initial, boolean allowSwaps) {
        this.items = List.copyOf(items);
        this.capacity = capacity;
        this.initial = initial;
        this.allowSwaps = allowSwaps;
    }

    public Set<String> initial() {
        return initial;
    }

    // The solutions we reach by one change
    public List<Set<String>> neighbours(Set<String> solution) {
        var result = new ArrayList<Set<String>>();
        addOrRemoveOneItem(solution, result);
        if (allowSwaps) {
            swapOneItem(solution, result);
        }
        return result;
    }

    // Add or remove one item, if the box can still carry what is in it
    private void addOrRemoveOneItem(Set<String> solution, List<Set<String>> result) {
        for (var item : items) {
            var changed = new HashSet<>(solution);
            if (solution.contains(item.name())) {
                changed.remove(item.name());
            } else {
                changed.add(item.name());
            }
            if (weightOf(changed) <= capacity) {
                result.add(Set.copyOf(changed));
            }
        }
    }

    // Replace one item of the box with an item that is missing, if the box can
    // carry the result
    private void swapOneItem(Set<String> solution, List<Set<String>> result) {
        for (var inBox : items) {
            for (var missing : items) {
                if (solution.contains(inBox.name()) && !solution.contains(missing.name())) {
                    var changed = new HashSet<>(solution);
                    changed.remove(inBox.name());
                    changed.add(missing.name());
                    if (weightOf(changed) <= capacity) {
                        result.add(Set.copyOf(changed));
                    }
                }
            }
        }
    }

    public int fitness(Set<String> solution) {
        int total = 0;
        for (var item : items) {
            if (solution.contains(item.name())) {
                total += item.value();
            }
        }
        return total;
    }

    private int weightOf(Set<String> solution) {
        int total = 0;
        for (var item : items) {
            if (solution.contains(item.name())) {
                total += item.weight();
            }
        }
        return total;
    }
}
