import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// The knapsack again: a box that can carry a limited weight, and items that
// have a weight and a value.
//
// A solution is the set of the items we put in the box, as long as the box can
// carry them. Its neighbours are the solutions we get by adding one item, or
// by removing one.
public class KnapsackSpace implements SearchSpace<Set<String>> {

    public record Item(String name, int weight, int value) {
    }

    private final List<Item> items;
    private final int capacity;
    private final Set<String> initial;

    public KnapsackSpace(List<Item> items, int capacity) {
        this(items, capacity, Set.of());
    }

    public KnapsackSpace(List<Item> items, int capacity, Set<String> initial) {
        this.items = List.copyOf(items);
        this.capacity = capacity;
        this.initial = initial;
    }

    public Set<String> initial() {
        return initial;
    }

    // Add or remove one item, if the box can still carry what is in it
    public List<Set<String>> neighbours(Set<String> solution) {
        var result = new ArrayList<Set<String>>();
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
        return result;
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
