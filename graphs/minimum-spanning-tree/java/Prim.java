import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class Prim {

    // The edges of a minimum spanning tree, grown from the given vertex
    public static Set<Edge> prim(Graph graph, String start) {
        var mst = new HashSet<Edge>();
        var visited = new HashSet<String>();
        var pending = new PriorityQueue<Edge>(Comparator.comparingInt(Edge::weight));

        visited.add(start);
        pending.addAll(graph.edgesFrom(start));

        while (visited.size() < graph.vertices().size()) {
            if (pending.isEmpty()) {
                throw new IllegalArgumentException("Graph is not connected");
            }

            var edge = pending.poll();
            var vertex = edge.target();

            if (!visited.contains(vertex)) {
                mst.add(edge);
                visited.add(vertex);

                // Add all the edges from this vertex to vertices not yet visited
                for (var nextEdge : graph.edgesFrom(vertex)) {
                    if (!visited.contains(nextEdge.target())) {
                        pending.add(nextEdge);
                    }
                }
            }
        }

        return mst;
    }
}
