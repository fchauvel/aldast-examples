import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Heuristics {

    // Random walk: we move to a random neighbour, again and again, and we
    // remember the best solution we have seen
    public static <S> S randomWalk(SearchSpace<S> space, int steps, Random random) {
        var current = space.initial();
        var best = current;
        for (int step = 0; step < steps; step++) {
            current = randomNeighbour(space, current, random);
            if (space.fitness(current) > space.fitness(best)) {
                best = current;
            }
        }
        return best;
    }

    // Hill climbing: from where we are, we move to the best neighbour, as long
    // as it is better than where we are. These are the solutions we go through.
    public static <S> List<S> climb(SearchSpace<S> space) {
        var path = new ArrayList<S>();
        var current = space.initial();
        path.add(current);
        while (true) {
            var best = bestNeighbour(space, current);
            if (best.isEmpty() || space.fitness(best.get()) <= space.fitness(current)) {
                return path;
            }
            current = best.get();
            path.add(current);
        }
    }

    // Where hill climbing stops
    public static <S> S hillClimbing(SearchSpace<S> space) {
        var path = climb(space);
        return path.get(path.size() - 1);
    }

    // Simulated annealing: we move to a random neighbour. We always accept it
    // if it is better, and we sometimes accept it if it is worse, less and less
    // often as the temperature drops. We remember the best solution we have seen.
    public static <S> S simulatedAnnealing(
        SearchSpace<S> space, double temperature, double cooling, int steps, Random random
    ) {
        var current = space.initial();
        var best = current;
        for (int step = 0; step < steps; step++) {
            var candidate = randomNeighbour(space, current, random);
            int change = space.fitness(candidate) - space.fitness(current);
            if (random.nextDouble() < acceptanceProbability(change, temperature)) {
                current = candidate;
            }
            if (space.fitness(current) > space.fitness(best)) {
                best = current;
            }
            temperature = temperature * cooling;
        }
        return best;
    }

    // How likely we accept a move that changes the fitness by this much. A better
    // move is always accepted. A worse move is less likely when it loses more,
    // and when the temperature is low.
    public static double acceptanceProbability(int change, double temperature) {
        if (change >= 0) {
            return 1.0;
        }
        return Math.exp(change / temperature);
    }

    private static <S> Optional<S> bestNeighbour(SearchSpace<S> space, S solution) {
        S best = null;
        for (var neighbour : space.neighbours(solution)) {
            if (best == null || space.fitness(neighbour) > space.fitness(best)) {
                best = neighbour;
            }
        }
        return Optional.ofNullable(best);
    }

    private static <S> S randomNeighbour(SearchSpace<S> space, S solution, Random random) {
        var neighbours = space.neighbours(solution);
        if (neighbours.isEmpty()) {
            return solution;
        }
        return neighbours.get(random.nextInt(neighbours.size()));
    }
}
