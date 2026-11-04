import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// An automaton where each state has at most one move per symbol: no choice,
// no guess. Each of its states is a *set* of states of the original automaton:
// all the places where the original may be, after reading the same symbols.
public class DeterministicAutomaton {

    private final Set<Integer> start;
    private final Set<Set<Integer>> accepting = new HashSet<>();
    private final Map<Set<Integer>, Map<Character, Set<Integer>>> moves = new HashMap<>();

    // We explore the sets we can reach, starting from the initial one, and
    // remembering those we have already seen. It is a search in a graph.
    public DeterministicAutomaton(Automaton original) {
        start = original.closure(Set.of(original.start()));
        var toVisit = new ArrayDeque<Set<Integer>>();
        moves.put(start, new HashMap<>());
        toVisit.push(start);
        while (!toVisit.isEmpty()) {
            var current = toVisit.pop();
            if (current.contains(original.accepting())) {
                accepting.add(current);
            }
            for (var symbol : original.alphabet()) {
                var next = original.step(current, symbol);
                if (next.isEmpty()) {
                    continue;
                }
                if (!moves.containsKey(next)) {
                    moves.put(next, new HashMap<>());
                    toVisit.push(next);
                }
                moves.get(current).put(symbol, next);
            }
        }
    }

    public int stateCount() {
        return moves.size();
    }

    public boolean accepts(String word) {
        var current = start;
        for (var symbol : word.toCharArray()) {
            current = moves.get(current).get(symbol);
            if (current == null) {
                return false;
            }
        }
        return accepting.contains(current);
    }
}
