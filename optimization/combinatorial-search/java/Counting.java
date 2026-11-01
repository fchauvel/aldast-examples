import java.util.ArrayList;
import java.util.List;

// Wrappers that remember which configurations a search has expanded, to see how
// much work the search does
public class Counting {

    public static class OfProblem<C> implements Problem<C> {
        private final Problem<C> problem;
        public final List<C> expanded = new ArrayList<>();

        public OfProblem(Problem<C> problem) {
            this.problem = problem;
        }

        public C empty() { return problem.empty(); }
        public List<C> children(C c) { expanded.add(c); return problem.children(c); }
        public boolean isComplete(C c) { return problem.isComplete(c); }
        public boolean isValid(C c) { return problem.isValid(c); }
    }

    public static class OfOptimization<C> implements OptimizationProblem<C> {
        private final OptimizationProblem<C> problem;
        public final List<C> expanded = new ArrayList<>();

        public OfOptimization(OptimizationProblem<C> problem) {
            this.problem = problem;
        }

        public C empty() { return problem.empty(); }
        public List<C> children(C c) { expanded.add(c); return problem.children(c); }
        public boolean isComplete(C c) { return problem.isComplete(c); }
        public boolean isValid(C c) { return problem.isValid(c); }
        public int value(C c) { return problem.value(c); }
        public int upperBound(C c) { return problem.upperBound(c); }
    }
}
