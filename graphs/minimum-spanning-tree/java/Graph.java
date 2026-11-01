import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

// A weighted graph, whose edges can be followed in both directions
public class Graph {

    private final Set<String> vertices = new LinkedHashSet<>();
    private final List<Edge> edges = new ArrayList<>();

    public Graph addVertex(String vertex) {
        vertices.add(vertex);
        return this;
    }

    public Graph addEdge(String source, String target, int weight) {
        addVertex(source);
        addVertex(target);
        edges.add(new Edge(source, target, weight));
        return this;
    }

    public Set<String> vertices() {
        return vertices;
    }

    public List<Edge> edges() {
        return edges;
    }

    // The edges that start from the given vertex (an edge can be followed
    // from either of its ends)
    public List<Edge> edgesFrom(String vertex) {
        var result = new ArrayList<Edge>();
        for (var edge : edges) {
            if (edge.source().equals(vertex)) {
                result.add(edge);
            } else if (edge.target().equals(vertex)) {
                result.add(new Edge(vertex, edge.source(), edge.weight()));
            }
        }
        return result;
    }
}
