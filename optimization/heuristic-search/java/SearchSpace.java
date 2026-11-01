import java.util.List;

// Where a heuristic search looks for a good solution. It is a set of solutions,
// with a notion of neighbours (solutions that are close to one another), and a
// measure of how good a solution is.
public interface SearchSpace<S> {

    // A solution to start from
    S initial();

    // The solutions we reach from this solution by one small change. They are
    // all solutions too.
    List<S> neighbours(S solution);

    // How good a solution is. The higher, the better.
    int fitness(S solution);
}
