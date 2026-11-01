import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

// Each test states a property of heuristic searches, using the knapsack of the
// lecture. Together, they describe what random walks, hill climbing and
// simulated annealing can, and cannot, do.
//
// Run with: make test

public class HeuristicTests extends Testing {

    public static void main(String[] args) {

        // -- The search space: solutions and their neighbours

        test("from the empty box, we can add any item that fits", () -> {
            var neighbours = box().neighbours(Set.of());
            assertEquals(neighbours.size(), 4);
        });

        test("neighbours differ by one item, and always fit in the box", () -> {
            var space = box();
            for (var solution : allSolutions(space)) {
                for (var neighbour : space.neighbours(solution)) {
                    var added = new HashSet<>(neighbour);
                    added.removeAll(solution);
                    var removed = new HashSet<>(solution);
                    removed.removeAll(neighbour);
                    assertEquals(added.size() + removed.size(), 1);
                }
            }
        });

        test("nine boxes can be filled without breaking the limit", () -> {
            assertEquals(allSolutions(box()).size(), 9);
        });


        // -- Random walk

        test("a random walk remembers the best solution it has seen", () -> {
            var space = box();
            var best = Heuristics.randomWalk(space, 20, new Random(1));
            assertTrue(space.fitness(best) >= space.fitness(space.initial()));
        });

        test("if we walk long enough, we find the best solution", () -> {
            var space = box();
            var best = Heuristics.randomWalk(space, 1000, new Random(1));
            assertEquals(space.fitness(best), 16000);
        });


        // -- Hill climbing

        test("hill climbing only moves to better solutions", () -> {
            var space = box();
            var path = Heuristics.climb(space);
            for (int i = 1; i < path.size(); i++) {
                assertTrue(space.fitness(path.get(i)) > space.fitness(path.get(i - 1)));
            }
        });

        test("from the empty box, hill climbing finds the best box", () -> {
            var space = box();
            var best = Heuristics.hillClimbing(space);
            assertEquals(best, Set.of("music instruments", "cooking tools"));
            assertEquals(space.fitness(best), 16000);
        });

        test("but starting from the laptop, it gets stuck on a box worth 12 000", () -> {
            var space = boxStartingWith("laptop");
            var stuck = Heuristics.hillClimbing(space);
            assertEquals(stuck, Set.of("laptop", "music instruments"));
            assertEquals(space.fitness(stuck), 12000);
        });

        test("hill climbing stops where no neighbour is better", () -> {
            for (var start : allSolutions(box())) {
                var space = boxStartingWith(start);
                var end = Heuristics.hillClimbing(space);
                assertTrue(space.fitness(end) >= space.fitness(start));
                for (var neighbour : space.neighbours(end)) {
                    assertTrue(space.fitness(neighbour) <= space.fitness(end));
                }
            }
        });

        test("there are several solutions that no neighbour beats: the local optima", () -> {
            var space = box();
            var fitnessOfLocalOptima = new TreeSet<Integer>();
            for (var solution : allSolutions(space)) {
                boolean beaten = false;
                for (var neighbour : space.neighbours(solution)) {
                    beaten = beaten || space.fitness(neighbour) > space.fitness(solution);
                }
                if (!beaten) {
                    fitnessOfLocalOptima.add(space.fitness(solution));
                }
            }
            assertEquals(fitnessOfLocalOptima, new TreeSet<>(List.of(7000, 12000, 13000, 16000)));
        });


        // -- Simulated annealing

        test("a better move is always accepted", () -> {
            assertEquals(Heuristics.acceptanceProbability(1000, 500.0), 1.0);
            assertEquals(Heuristics.acceptanceProbability(0, 500.0), 1.0);
        });

        test("a worse move is less likely to be accepted when it loses more", () -> {
            assertTrue(Heuristics.acceptanceProbability(-1000, 2000.0) > Heuristics.acceptanceProbability(-3000, 2000.0));
        });

        test("a worse move is less likely to be accepted when it is colder", () -> {
            var hot = Heuristics.acceptanceProbability(-3000, 5000.0);
            var warm = Heuristics.acceptanceProbability(-3000, 1000.0);
            var cold = Heuristics.acceptanceProbability(-3000, 100.0);
            assertTrue(hot > warm);
            assertTrue(warm > cold);
        });

        test("without any heat, annealing gets stuck, like hill climbing", () -> {
            var space = boxStartingWith("laptop");
            for (long seed = 1; seed <= 12; seed++) {
                var best = Heuristics.simulatedAnnealing(space, 0.000000001, 0.99, 3000, new Random(seed));
                assertTrue(space.fitness(best) < 16000);
            }
        });

        test("with heat and a slow cooling, annealing escapes and finds the best box", () -> {
            var space = boxStartingWith("laptop");
            for (long seed = 1; seed <= 12; seed++) {
                var best = Heuristics.simulatedAnnealing(space, 5000.0, 0.99, 3000, new Random(seed));
                assertEquals(space.fitness(best), 16000);
            }
        });

        System.out.println("All tests passed.");
    }


    // -- Examples used in the tests

    // The items of the lecture, and a box that carries 10 kg
    static List<KnapsackSpace.Item> items() {
        return List.of(
            new KnapsackSpace.Item("music instruments", 2, 9000),
            new KnapsackSpace.Item("laptop", 7, 3000),
            new KnapsackSpace.Item("books", 3, 4000),
            new KnapsackSpace.Item("cooking tools", 8, 7000)
        );
    }

    static KnapsackSpace box() {
        return new KnapsackSpace(items(), 10);
    }

    static KnapsackSpace boxStartingWith(String... names) {
        return boxStartingWith(Set.of(names));
    }

    static KnapsackSpace boxStartingWith(Set<String> solution) {
        return new KnapsackSpace(items(), 10, solution);
    }

    // All the solutions we can reach, by moving from neighbour to neighbour
    static <S> Set<S> allSolutions(SearchSpace<S> space) {
        var found = new HashSet<S>();
        var toVisit = new ArrayDeque<S>();
        toVisit.add(space.initial());
        while (!toVisit.isEmpty()) {
            var solution = toVisit.poll();
            if (found.add(solution)) {
                toVisit.addAll(space.neighbours(solution));
            }
        }
        return found;
    }
}
