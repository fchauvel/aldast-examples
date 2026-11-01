// A problem where we do not look for any solution, but for the best one.
//
// Only a solution, that is, a complete and valid configuration, has a value.
// For the other configurations, the problem anticipates the best outcome of
// exploring them further, with an upper bound.
public interface OptimizationProblem<C> extends Problem<C> {

    // How good a solution is. The higher, the better.
    int value(C solution);

    // The best outcome: no solution below this configuration is worth more
    // than this upper bound. For a solution, the upper bound is its value.
    int upperBound(C configuration);
}
