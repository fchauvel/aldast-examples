import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

// A graph: states are the vertices, moves are the edges. A move may read a
// symbol, or nothing at all (a "silent" move, whose symbol is null).
//
// The automaton may have several moves for the same symbol, or silent moves:
// it is "non-deterministic". It accepts a word if *some* path reads the word
// and ends in the accepting state.
public record Automaton(int start, int accepting, List<Move> moves) {

    public record Move(int from, Character symbol, int to) {
        boolean isSilent() {
            return symbol == null;
        }
    }

    // The construction is a recursion on the tree: one case per kind of node.
    public static Automaton from(Regex expression) {
        return new Builder().build(expression);
    }

    private static class Builder {
        private int stateCount = 0;

        private int newState() {
            return stateCount++;
        }

        Automaton build(Regex expression) {
            return switch (expression) {
                case Regex.Symbol s -> symbol(s.symbol());
                case Regex.Then t -> then(build(t.first()), build(t.second()));
                case Regex.Or o -> or(build(o.left()), build(o.right()));
                case Regex.Star s -> star(build(s.inner()));
            };
        }

        private Automaton symbol(char symbol) {
            var start = newState();
            var accepting = newState();
            return new Automaton(start, accepting, List.of(new Move(start, symbol, accepting)));
        }

        private Automaton then(Automaton first, Automaton second) {
            var moves = union(first.moves, second.moves);
            moves.add(new Move(first.accepting, null, second.start));
            return new Automaton(first.start, second.accepting, moves);
        }

        private Automaton or(Automaton left, Automaton right) {
            var start = newState();
            var accepting = newState();
            var moves = union(left.moves, right.moves);
            moves.add(new Move(start, null, left.start));
            moves.add(new Move(start, null, right.start));
            moves.add(new Move(left.accepting, null, accepting));
            moves.add(new Move(right.accepting, null, accepting));
            return new Automaton(start, accepting, moves);
        }

        private Automaton star(Automaton inner) {
            var start = newState();
            var accepting = newState();
            var moves = new ArrayList<>(inner.moves);
            moves.add(new Move(start, null, inner.start));
            moves.add(new Move(start, null, accepting));
            moves.add(new Move(inner.accepting, null, inner.start));
            moves.add(new Move(inner.accepting, null, accepting));
            return new Automaton(start, accepting, moves);
        }

        private List<Move> union(List<Move> first, List<Move> second) {
            var result = new ArrayList<>(first);
            result.addAll(second);
            return result;
        }
    }

    public int stateCount() {
        var states = new HashSet<Integer>();
        states.add(start);
        states.add(accepting);
        for (var move : moves) {
            states.add(move.from);
            states.add(move.to);
        }
        return states.size();
    }

    public Set<Character> alphabet() {
        var result = new HashSet<Character>();
        for (var move : moves) {
            if (!move.isSilent()) {
                result.add(move.symbol);
            }
        }
        return result;
    }

    // All the states we can reach from the given ones, by silent moves only.
    // This is a plain graph traversal, with a work list.
    public Set<Integer> closure(Set<Integer> states) {
        var reached = new HashSet<>(states);
        var toVisit = new ArrayDeque<>(states);
        while (!toVisit.isEmpty()) {
            var current = toVisit.pop();
            for (var move : moves) {
                if (move.from == current && move.isSilent() && reached.add(move.to)) {
                    toVisit.push(move.to);
                }
            }
        }
        return reached;
    }

    // The states we reach from the given ones by reading one symbol
    public Set<Integer> step(Set<Integer> states, char symbol) {
        var reached = new HashSet<Integer>();
        for (var move : moves) {
            if (states.contains(move.from) && !move.isSilent() && move.symbol == symbol) {
                reached.add(move.to);
            }
        }
        return closure(reached);
    }

    // Follows all the paths at once: we keep the set of states we may be in.
    public boolean accepts(String word) {
        var current = closure(Set.of(start));
        for (var symbol : word.toCharArray()) {
            current = step(current, symbol);
        }
        return current.contains(accepting);
    }
}
