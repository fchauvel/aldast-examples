// A problem where we do not look for any solution, but for the best one
public interface OptimizationProblem<C> extends Problem<C> {

    // How good the choices made so far are. The higher, the better. For a
    // complete configuration, this is what the problem wants to maximize.
    int value(C configuration);

    // An optimistic estimate: no solution below this configuration is worth
    // more than this upper bound. It never underestimates.
    int upperBound(C configuration);
}
