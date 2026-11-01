import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Each test states a property of the search algorithms, using a small example.
// Together, they describe how searching by enumeration, with pruning and with
// branch-and-bound, behave.
//
// Run with: make test

public class SearchTests extends Testing {

    public static void main(String[] args) {

        // -- Enumerating all the solutions: the N queens

        test("four queens have two solutions", () -> {
            assertEquals(Search.exhaustive(new NQueens(4)).size(), 2);
        });

        test("eight queens have 92 solutions", () -> {
            assertEquals(Search.withPruning(new NQueens(8)).size(), 92);
        });

        test("there is no way to place three queens", () -> {
            assertEquals(Search.exhaustive(new NQueens(3)).size(), 0);
        });


        // -- Pruning

        test("pruning finds the same solutions as trying everything", () -> {
            var everything = Search.exhaustive(new NQueens(6));
            var pruned = Search.withPruning(new NQueens(6));
            assertEquals(new HashSet<>(pruned), new HashSet<>(everything));
        });

        test("pruning looks at far fewer configurations", () -> {
            var tryingEverything = new Counting.OfProblem<>(new NQueens(6));
            var withPruning = new Counting.OfProblem<>(new NQueens(6));
            Search.exhaustive(tryingEverything);
            Search.withPruning(withPruning);
            assertTrue(withPruning.expanded.size() * 10 < tryingEverything.expanded.size());
        });

        test("we can stop at the first solution", () -> {
            var problem = new NQueens(8);
            var search = new Counting.OfProblem<>(problem);
            var solution = Search.anySolution(search).get();
            assertTrue(problem.isComplete(solution));
            assertTrue(problem.isValid(solution));

            var searchForAll = new Counting.OfProblem<>(problem);
            Search.withPruning(searchForAll);
            assertTrue(search.expanded.size() < searchForAll.expanded.size());
        });

        test("when there is no solution, there is no first solution either", () -> {
            assertTrue(Search.anySolution(new NQueens(3)).isEmpty());
        });


        // -- The knapsack: some choices do not fit in the box

        test("nine of the sixteen ways to fill the box fit", () -> {
            assertEquals(Search.exhaustive(box(1000)).size(), 16);
            assertEquals(Search.withPruning(box(10)).size(), 9);
        });


        // -- Finding the best solution

        test("the best is to take the music instruments and the cooking tools", () -> {
            var problem = box(10);
            var best = Search.branchAndBound(problem).get();
            assertEquals(problem.namesOf(best), List.of("music instruments", "cooking tools"));
            assertEquals(problem.value(best), 16000);
        });

        test("branch-and-bound finds the best of all the solutions", () -> {
            var problem = box(10);
            int bestOfAll = Integer.MIN_VALUE;
            for (var solution : Search.withPruning(problem)) {
                bestOfAll = Math.max(bestOfAll, problem.value(solution));
            }
            var best = Search.branchAndBound(problem).get();
            assertEquals(problem.value(best), bestOfAll);
        });

        test("branch-and-bound looks at fewer configurations than pruning alone", () -> {
            var withPruning = new Counting.OfOptimization<>(box(10));
            var withBound = new Counting.OfOptimization<>(box(10));
            Search.withPruning(withPruning);
            Search.branchAndBound(withBound);
            assertTrue(withBound.expanded.size() < withPruning.expanded.size());
        });

        test("the bound never underestimates what we can still get", () -> {
            var problem = box(10);
            checkBoundsBelow(problem, problem.empty());
        });

        test("the order of the choices changes the work, not the result", () -> {
            var items = new ArrayList<>(items());
            var asListed = new Counting.OfOptimization<>(new Knapsack(items, 10));
            Collections.reverse(items);
            var reversed = new Counting.OfOptimization<>(new Knapsack(items, 10));

            var best = Search.branchAndBound(asListed).get();
            var bestReversed = Search.branchAndBound(reversed).get();
            assertEquals(box(10).value(best), 16000);
            assertEquals(new Knapsack(items, 10).value(bestReversed), 16000);
            assertFalse(asListed.expanded.size() == reversed.expanded.size());
        });

        test("the best box can be empty", () -> {
            var problem = box(0);
            var best = Search.branchAndBound(problem).get();
            assertEquals(problem.namesOf(best), List.of());
            assertEquals(problem.value(best), 0);
        });

        test("an item that is too heavy is never taken", () -> {
            var problem = box(5);
            var best = Search.branchAndBound(problem).get();
            assertEquals(problem.namesOf(best), List.of("music instruments", "books"));
        });


        // -- Going for the most promising configuration first

        test("going for the most promising first finds the best as well", () -> {
            var problem = box(10);
            var best = Search.bestFirst(problem).get();
            assertEquals(problem.value(best), 16000);
        });

        test("we only look at configurations that could be the best", () -> {
            var search = new Counting.OfOptimization<>(box(10));
            Search.bestFirst(search);
            for (var configuration : search.expanded) {
                assertTrue(search.bound(configuration) >= 16000);
            }
        });

        System.out.println("All tests passed.");
    }


    // -- Examples used in the tests

    // The items of the lecture: what we can carry, and what it is worth
    static List<Knapsack.Item> items() {
        return List.of(
            new Knapsack.Item("music instruments", 2, 9000),
            new Knapsack.Item("laptop", 7, 3000),
            new Knapsack.Item("books", 3, 4000),
            new Knapsack.Item("cooking tools", 8, 7000)
        );
    }

    // A box that can carry the given weight (in kg)
    static Knapsack box(int capacity) {
        return new Knapsack(items(), capacity);
    }

    // Checks that the bound of every configuration is at least what the
    // best valid solution below it is worth
    static <C> int checkBoundsBelow(OptimizationProblem<C> problem, C configuration) {
        int bestBelow = Integer.MIN_VALUE;
        if (problem.isComplete(configuration)) {
            if (problem.isValid(configuration)) {
                bestBelow = problem.value(configuration);
            }
        } else {
            for (var next : problem.extensions(configuration)) {
                bestBelow = Math.max(bestBelow, checkBoundsBelow(problem, next));
            }
        }
        if (bestBelow != Integer.MIN_VALUE) {
            assertTrue(problem.bound(configuration) >= bestBelow);
        }
        return bestBelow;
    }
}
