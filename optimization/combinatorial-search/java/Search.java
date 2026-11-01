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

    // The best solution, with branch-and-bound. We keep the *floor*: the highest
    // lower bound we have seen, that is, the value we are sure to reach. We give
    // up on a configuration that cannot beat what we already have.
    public static <C> Optional<C> branchAndBound(OptimizationProblem<C> problem) {
        var progress = new Progress<C>();
        branchAndBound(problem, problem.empty(), progress);
        return Optional.ofNullable(progress.best);
    }

    private static <C> void branchAndBound(OptimizationProblem<C> problem, C configuration, Progress<C> progress) {
        if (!problem.isValid(configuration)) {
            return;
        }
        progress.floor = Math.max(progress.floor, problem.lowerBound(configuration));
        if (progress.isHopeless(problem, configuration)) {
            return;
        }
        if (problem.isComplete(configuration)) {
            progress.keepIfBetter(problem, configuration);
        } else {
            for (var next : problem.children(configuration)) {
                branchAndBound(problem, next, progress);
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

        var progress = new Progress<C>();
        while (!promising.isEmpty()) {
            var configuration = promising.poll();
            if (!problem.isValid(configuration)) {
                continue;
            }
            progress.floor = Math.max(progress.floor, problem.lowerBound(configuration));
            if (progress.isHopeless(problem, configuration)) {
                break; // The most promising one is not promising enough: we are done
            }
            if (problem.isComplete(configuration)) {
                progress.keepIfBetter(problem, configuration);
            } else {
                promising.addAll(problem.children(configuration));
            }
        }
        return Optional.ofNullable(progress.best);
    }

    // What we know so far: the best solution found, and the value we are sure
    // to reach, even if we have not found the solution yet
    private static class Progress<C> {
        C best = null;
        int floor = Integer.MIN_VALUE;

        // Is it pointless to explore this configuration? It is if it cannot reach
        // the floor, or if it cannot do better than the best solution we hold.
        boolean isHopeless(OptimizationProblem<C> problem, C configuration) {
            int upperBound = problem.upperBound(configuration);
            return upperBound < floor
                || (best != null && upperBound <= problem.value(best));
        }

        void keepIfBetter(OptimizationProblem<C> problem, C solution) {
            if (best == null || problem.value(solution) > problem.value(best)) {
                best = solution;
            }
        }
    }
}
