import java.util.ArrayList;
import java.util.List;

// We have a box that can carry a limited weight, and items that have a weight
// and a value. Which items shall we put in the box to maximize their value?
//
// A configuration says, for each of the first items, whether we take it (true)
// or leave it (false). The items we have not decided about yet are not listed.
public class Knapsack implements OptimizationProblem<List<Boolean>> {

    public record Item(String name, int weight, int value) {
    }

    private final List<Item> items;
    private final int capacity;

    public Knapsack(List<Item> items, int capacity) {
        this.items = List.copyOf(items);
        this.capacity = capacity;
    }

    public List<Boolean> empty() {
        return List.of();
    }

    // For the next item, we can take it or leave it
    public List<List<Boolean>> children(List<Boolean> configuration) {
        return List.of(
            with(configuration, true),
            with(configuration, false)
        );
    }

    public boolean isComplete(List<Boolean> configuration) {
        return configuration.size() == items.size();
    }

    // The items we take must not weigh more than the box can carry
    public boolean isValid(List<Boolean> configuration) {
        return weightOf(configuration) <= capacity;
    }

    public int value(List<Boolean> configuration) {
        int total = 0;
        for (int i = 0; i < configuration.size(); i++) {
            if (configuration.get(i)) {
                total += items.get(i).value();
            }
        }
        return total;
    }

    // At best, we will be able to take all the items we have not decided about
    public int upperBound(List<Boolean> configuration) {
        int total = value(configuration);
        for (int i = configuration.size(); i < items.size(); i++) {
            total += items.get(i).value();
        }
        return total;
    }

    public int weightOf(List<Boolean> configuration) {
        int total = 0;
        for (int i = 0; i < configuration.size(); i++) {
            if (configuration.get(i)) {
                total += items.get(i).weight();
            }
        }
        return total;
    }

    // The names of the items we take
    public List<String> namesOf(List<Boolean> configuration) {
        var names = new ArrayList<String>();
        for (int i = 0; i < configuration.size(); i++) {
            if (configuration.get(i)) {
                names.add(items.get(i).name());
            }
        }
        return names;
    }

    private List<Boolean> with(List<Boolean> configuration, boolean choice) {
        var extended = new ArrayList<>(configuration);
        extended.add(choice);
        return extended;
    }
}
