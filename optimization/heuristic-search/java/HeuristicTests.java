import java.util.ArrayDeque;
import java.util.ArrayList;
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

        test("being neighbours is mutual", () -> {
            var space = box();
            for (var solution : allSolutions(space)) {
                for (var neighbour : space.neighbours(solution)) {
                    assertTrue(space.neighbours(neighbour).contains(solution));
                }
            }
        });

        test("nine boxes can be filled without breaking the limit", () -> {
            assertEquals(allSolutions(box()).size(), 9);
        });


        // -- A maze: a graph of cells

        test("the neighbours of a cell are the open cells next to it", () -> {
            var maze = maze();
            assertEquals(new HashSet<>(maze.neighbours(maze.initial())),
                Set.of(new MazeSpace.Cell(1, 2), new MazeSpace.Cell(2, 1)));
        });

        test("from the start, we can reach every open cell of the maze", () -> {
            assertEquals(allSolutions(maze()).size(), 24);
        });

        test("being neighbours is mutual in the maze too", () -> {
            var maze = maze();
            for (var cell : allSolutions(maze)) {
                for (var neighbour : maze.neighbours(cell)) {
                    assertTrue(maze.neighbours(neighbour).contains(cell));
                }
            }
        });

        test("the exit is the best position", () -> {
            var maze = maze();
            assertEquals(maze.fitness(maze.exit()), 0);
            for (var cell : allSolutions(maze)) {
                assertTrue(cell.equals(maze.exit()) || maze.fitness(cell) < 0);
            }
        });

        test("hill climbing walks towards the exit, and stops behind a wall", () -> {
            var maze = maze();
            var stuck = Heuristics.hillClimbing(maze);
            assertEquals(stuck, new MazeSpace.Cell(3, 5));
            assertFalse(stuck.equals(maze.exit()));
            for (var neighbour : maze.neighbours(stuck)) {
                assertTrue(maze.fitness(neighbour) < maze.fitness(stuck));
            }
        });

        test("a random walk in the maze finds the exit, if we wait", () -> {
            var maze = maze();
            for (long seed = 1; seed <= 5; seed++) {
                assertEquals(Heuristics.randomWalk(maze, 5000, new Random(seed)), maze.exit());
            }
        });

        test("simulated annealing in the maze finds the exit", () -> {
            var maze = maze();
            for (long seed = 1; seed <= 5; seed++) {
                assertEquals(Heuristics.simulatedAnnealing(maze, 2.0, 0.999, 5000, new Random(seed)), maze.exit());
            }
        });

        test("tabu search in the maze finds the exit", () -> {
            var maze = maze();
            assertEquals(Heuristics.tabuSearch(maze, 60, 3), maze.exit());
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


        // -- Tabu search

        test("tabu search escapes the local optimum where hill climbing is stuck", () -> {
            var space = boxStartingWith("laptop");
            assertEquals(space.fitness(Heuristics.hillClimbing(space)), 12000);
            assertEquals(space.fitness(Heuristics.tabuSearch(space, 10, 3)), 16000);
        });

        test("tabu search accepts a worse move, to leave a local optimum", () -> {
            var space = boxStartingWith("laptop");
            var path = Heuristics.tabuWalk(space, 10, 3);
            // laptop, then music instruments (better), then a worse box
            assertEquals(path.get(1), Set.of("laptop", "music instruments"));
            assertEquals(path.get(2), Set.of("music instruments"));
            assertTrue(space.fitness(path.get(2)) < space.fitness(path.get(1)));
        });

        test("tabu search does not go back to the last solutions it went through", () -> {
            var space = box();
            var path = Heuristics.tabuWalk(space, 50, 3);
            for (int i = 0; i < path.size(); i++) {
                for (int j = i + 1; j < path.size() && j <= i + 3; j++) {
                    assertFalse(path.get(i).equals(path.get(j)));
                }
            }
        });

        test("tabu search stops when every neighbour is tabu", () -> {
            var space = box();
            var path = Heuristics.tabuWalk(space, 100, 10);   // a memory of 10 solutions, out of 9
            assertTrue(path.size() < 101);   // we stopped before the 100 steps
        });


        // -- Several objectives: the Pareto front

        test("a lighter and more valuable box dominates another one", () -> {
            var light = new Pareto.Candidate("music instruments and books", 5, 13000);
            var heavy = new Pareto.Candidate("laptop and music instruments", 9, 12000);
            assertTrue(Pareto.dominates(light, heavy));
            assertFalse(Pareto.dominates(heavy, light));
        });

        test("a box does not dominate itself", () -> {
            var box = new Pareto.Candidate("books", 3, 4000);
            assertFalse(Pareto.dominates(box, box));
        });

        test("a lighter box that is less valuable is not dominated: it is a compromise", () -> {
            var light = new Pareto.Candidate("music instruments", 2, 9000);
            var valuable = new Pareto.Candidate("music instruments and cooking tools", 10, 16000);
            assertFalse(Pareto.dominates(light, valuable));
            assertFalse(Pareto.dominates(valuable, light));
        });

        test("six boxes are the best compromises between weight and value", () -> {
            var front = Pareto.front(allBoxes());
            var names = new TreeSet<String>();
            for (var candidate : front) {
                names.add(candidate.name());
            }
            assertEquals(names, new TreeSet<>(List.of(
                "nothing",
                "music instruments",
                "music instruments + books",
                "music instruments + cooking tools",
                "music instruments + books + cooking tools",
                "music instruments + laptop + books + cooking tools"
            )));
        });

        test("no box of the front is dominated by another box", () -> {
            var boxes = allBoxes();
            for (var onFront : Pareto.front(boxes)) {
                for (var other : boxes) {
                    assertFalse(Pareto.dominates(other, onFront));
                }
            }
        });

        test("every box that is not on the front is dominated by a box of the front", () -> {
            var boxes = allBoxes();
            var front = Pareto.front(boxes);
            for (var box : boxes) {
                if (!front.contains(box)) {
                    assertTrue(front.stream().anyMatch(other -> Pareto.dominates(other, box)));
                }
            }
        });

        test("the best box within 10 kg is one of the compromises", () -> {
            var best = allBoxes().stream()
                .filter(box -> box.cost() <= 10)
                .max((first, second) -> Integer.compare(first.gain(), second.gain()))
                .get();
            assertEquals(best.gain(), 16000);
            assertTrue(Pareto.front(allBoxes()).contains(best));
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

    // The exit is just behind a wall, but the way out starts by going away from it
    static MazeSpace maze() {
        return new MazeSpace(
            "#########",
            "#S..#...#",
            "#.#.#.#.#",
            "#.#...#E#",
            "#.#####.#",
            "#.......#",
            "#########"
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

    // Every way to fill a box with the items of the lecture, with its weight and
    // its value, whether the box can carry it or not
    static List<Pareto.Candidate> allBoxes() {
        var items = items();
        var boxes = new ArrayList<Pareto.Candidate>();
        for (int mask = 0; mask < (1 << items.size()); mask++) {
            var names = new ArrayList<String>();
            int weight = 0;
            int value = 0;
            for (int i = 0; i < items.size(); i++) {
                if ((mask & (1 << i)) != 0) {
                    names.add(items.get(i).name());
                    weight += items.get(i).weight();
                    value += items.get(i).value();
                }
            }
            boxes.add(new Pareto.Candidate(names.isEmpty() ? "nothing" : String.join(" + ", names), weight, value));
        }
        return boxes;
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
