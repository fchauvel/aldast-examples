// A problem where we do not look for any solution, but for the best one.
//
// Only a solution, that is, a complete and valid configuration, has a value.
// For the other configurations, the problem anticipates the worst and the best
// outcome of exploring them further, with a lower and an upper bound.
public interface OptimizationProblem<C> extends Problem<C> {

    // How good a solution is. The higher, the better.
    int value(C solution);

    // The worst outcome: from a valid configuration, we can reach a solution
    // worth at least this lower bound.
    int lowerBound(C configuration);

    // The best outcome: no solution below this configuration is worth more
    // than this upper bound.
    int upperBound(C configuration);
}
