import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

public class Search {

    // All the solutions, found by trying every possible sequence of choices
    public static <C> List<C> exhaustive(Problem<C> problem) {
        var solutions = new ArrayList<C>();
        exhaustive(problem, problem.empty(), solutions);
        return solutions;
    }

    private static <C> void exhaustive(Problem<C> problem, C configuration, List<C> solutions) {
        if (problem.isComplete(configuration)) {
            if (problem.isValid(configuration)) {
                solutions.add(configuration);
            }
        } else {
            for (var next : problem.children(configuration)) {
                exhaustive(problem, next, solutions);
            }
        }
    }

    // All the solutions, but we give up on a configuration as soon as it is
    // invalid: none of its children can be valid
    public static <C> List<C> withPruning(Problem<C> problem) {
        var solutions = new ArrayList<C>();
        withPruning(problem, problem.empty(), solutions);
        return solutions;
    }

    private static <C> void withPruning(Problem<C> problem, C configuration, List<C> solutions) {
        if (!problem.isValid(configuration)) {
            return;
        }
        if (problem.isComplete(configuration)) {
            solutions.add(configuration);
        } else {
            for (var next : problem.children(configuration)) {
                withPruning(problem, next, solutions);
            }
        }
    }

    // Any solution: we stop as soon as we find one
    public static <C> Optional<C> anySolution(Problem<C> problem) {
        return anySolution(problem, problem.empty());
    }

    private static <C> Optional<C> anySolution(Problem<C> problem, C configuration) {
        if (!problem.isValid(configuration)) {
            return Optional.empty();
        }
        if (problem.isComplete(configuration)) {
            return Optional.of(configuration);
        }
        for (var next : problem.children(configuration)) {
            var solution = anySolution(problem, next);
            if (solution.isPresent()) {
                return solution;
            }
        }
        return Optional.empty();
    }

    // The best solution, with branch-and-bound. Once we have found a solution, we
    // give up on a configuration if even its upper bound is no better than it
    public static <C> Optional<C> branchAndBound(OptimizationProblem<C> problem) {
        var best = new Best<C>();
        branchAndBound(problem, problem.empty(), best);
        return Optional.ofNullable(best.solution);
    }

    private static <C> void branchAndBound(OptimizationProblem<C> problem, C configuration, Best<C> best) {
        if (!problem.isValid(configuration)) {
            return;
        }
        if (best.isAtLeastAsGoodAs(problem, configuration)) {
            return;
        }
        if (problem.isComplete(configuration)) {
            best.solution = configuration;
        } else {
            for (var next : problem.children(configuration)) {
                branchAndBound(problem, next, best);
            }
        }
    }

    // The same, but we always continue with the most promising configuration
    // (the one with the highest upper bound), instead of going deep first
    public static <C> Optional<C> bestFirst(OptimizationProblem<C> problem) {
        var promising = new PriorityQueue<C>(
            (first, second) -> Integer.compare(problem.upperBound(second), problem.upperBound(first))
        );
        promising.add(problem.empty());

        var best = new Best<C>();
        while (!promising.isEmpty()) {
            var configuration = promising.poll();
            if (!problem.isValid(configuration)) {
                continue;
            }
            if (best.isAtLeastAsGoodAs(problem, configuration)) {
                break; // The most promising one is not promising enough: we are done
            }
            if (problem.isComplete(configuration)) {
                best.solution = configuration;
            } else {
                promising.addAll(problem.children(configuration));
            }
        }
        return Optional.ofNullable(best.solution);
    }

    // The best solution found so far, if any
    private static class Best<C> {
        C solution = null;

        // Is the best solution at least as good as anything this configuration
        // can lead to? Before we have found any solution, nothing is.
        boolean isAtLeastAsGoodAs(OptimizationProblem<C> problem, C configuration) {
            return solution != null && problem.upperBound(configuration) <= problem.value(solution);
        }
    }
}
