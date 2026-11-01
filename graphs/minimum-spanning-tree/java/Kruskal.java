import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Kruskal {

    // The edges of a minimum spanning tree, or of a minimum spanning forest
    // if the graph is not connected
    public static Set<Edge> kruskal(Graph graph) {
        var mst = new HashSet<Edge>();
        var trees = new DisjointSets(graph.vertices());

        // Sort all the edges by weight
        var edges = new ArrayList<>(graph.edges());
        edges.sort(Comparator.comparingInt(Edge::weight));

        // Process the edges in order
        for (var edge : edges) {
            if (trees.find(edge.source()) != trees.find(edge.target())) {
                mst.add(edge);
                trees.union(edge.source(), edge.target());
            }
        }

        return mst;
    }
}
