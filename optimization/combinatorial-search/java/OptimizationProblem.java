// A problem where we do not look for any solution, but for the best one
public interface OptimizationProblem<C> extends Problem<C> {

    // How good the choices made so far are. The higher, the better.
    int value(C configuration);

    // An optimistic estimate: no complete valid configuration that extends
    // this one is worth more than this. It never underestimates.
    int bound(C configuration);
}
