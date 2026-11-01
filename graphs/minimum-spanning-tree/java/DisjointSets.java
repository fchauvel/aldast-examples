import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Keeps track of which elements are in the same set (union-find).
//
// Each set has a label. Finding the set of an element is immediate. To merge
// two sets, we give the elements of the smaller set the label of the larger.
public class DisjointSets {

    private final Map<String, Integer> labelOf = new HashMap<>();
    private final Map<Integer, Set<String>> membersOf = new HashMap<>();

    // At first, every element is alone in its own set
    public DisjointSets(Collection<String> elements) {
        int label = 0;
        for (var element : elements) {
            labelOf.put(element, label);
            membersOf.put(label, new HashSet<>(Set.of(element)));
            label++;
        }
    }

    // The label of the set that contains the element
    public int find(String element) {
        return labelOf.get(element);
    }

    // Merges the sets that contain the two elements
    public void union(String x, String y) {
        int labelOfX = find(x);
        int labelOfY = find(y);
        if (labelOfX == labelOfY) {
            return;
        }
        boolean xIsSmaller = membersOf.get(labelOfX).size() <= membersOf.get(labelOfY).size();
        int smaller = xIsSmaller ? labelOfX : labelOfY;
        int larger = xIsSmaller ? labelOfY : labelOfX;

        for (var element : membersOf.get(smaller)) {
            labelOf.put(element, larger);
        }
        membersOf.get(larger).addAll(membersOf.get(smaller));
        membersOf.remove(smaller);
    }
}
