import java.util.List;

// A problem whose solutions are built by making a sequence of choices.
//
// A *configuration* is a solution under construction: some choices have been
// made, others have not. The configurations form a tree. The root is the
// *empty* configuration, where nothing is decided. Each level of the tree is
// one more choice that a solution needs, and each choice leads to a child.
// A solution needs all its choices to be made, so only the leaves, the
// *complete* configurations, can be solutions.
public interface Problem<C> {

    // The configuration where nothing is decided yet
    C empty();

    // The configurations we get by making one more choice
    List<C> children(C configuration);

    // Has everything been decided?
    boolean isComplete(C configuration);

    // Does the configuration respect the constraints of the problem? If a
    // configuration is not valid, none of its children is.
    boolean isValid(C configuration);
}
