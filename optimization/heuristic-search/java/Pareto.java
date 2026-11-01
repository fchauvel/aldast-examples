import java.util.ArrayList;
import java.util.List;

// With two objectives, such as a cost we want low and a gain we want high, there
// may be no single best solution, but a set of best compromises: the Pareto
// front.
public class Pareto {

    public record Candidate(String name, int cost, int gain) {
    }

    // Is the first candidate at least as good as the second on both objectives,
    // and better on at least one?
    public static boolean dominates(Candidate first, Candidate second) {
        boolean noWorse = first.cost() <= second.cost() && first.gain() >= second.gain();
        boolean better = first.cost() < second.cost() || first.gain() > second.gain();
        return noWorse && better;
    }

    // The candidates that no other candidate dominates
    public static List<Candidate> front(List<Candidate> candidates) {
        var front = new ArrayList<Candidate>();
        for (var candidate : candidates) {
            boolean dominated = false;
            for (var other : candidates) {
                dominated = dominated || dominates(other, candidate);
            }
            if (!dominated) {
                front.add(candidate);
            }
        }
        return front;
    }
}
