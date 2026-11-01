import java.util.List;
import java.util.Set;
import java.util.TreeSet;

// Each test states a property of the minimum spanning tree algorithms, using
// a small example. Together, they describe how Prim's and Kruskal's
// algorithms behave.
//
// Run with: make test

// (Extending Testing gives us its helpers: test, assertEquals, and so on)
public class MstTests extends Testing {

    public static void main(String[] args) {

        // -- Prim's algorithm

        test("Prim's algorithm connects the offices at the lowest cost", () -> {
            var tree = Prim.prim(offices(), "A");
            assertEquals(describe(tree), Set.of("A-C", "B-D", "B-E", "C-D", "C-F"));
            assertEquals(weightOf(tree), 13);
        });

        test("a spanning tree has one edge less than there are vertices", () -> {
            var graph = offices();
            var tree = Prim.prim(graph, "A");
            assertEquals(tree.size(), graph.vertices().size() - 1);
        });

        test("Prim's algorithm finds the same tree wherever it starts", () -> {
            var graph = offices();
            var expected = describe(Prim.prim(graph, "A"));
            for (var start : graph.vertices()) {
                assertEquals(describe(Prim.prim(graph, start)), expected);
            }
        });

        test("a graph with a single vertex needs no edge", () -> {
            var graph = new Graph().addVertex("A");
            assertEquals(Prim.prim(graph, "A").size(), 0);
        });

        test("Prim's algorithm only works on connected graphs", () -> {
            assertThrows(() -> Prim.prim(twoSeparateGroups(), "A"));
        });


        // -- Kruskal's algorithm

        test("Kruskal's algorithm finds the same tree as Prim's algorithm", () -> {
            var tree = Kruskal.kruskal(offices());
            assertEquals(describe(tree), describe(Prim.prim(offices(), "A")));
            assertEquals(weightOf(tree), 13);
        });

        test("Kruskal's algorithm skips the edges that would create a cycle", () -> {
            var tree = describe(Kruskal.kruskal(offices()));
            assertFalse(tree.contains("A-B"));
            assertFalse(tree.contains("D-E"));
        });

        test("the cheapest edge is always part of the tree", () -> {
            var tree = describe(Kruskal.kruskal(offices()));
            assertTrue(tree.contains("C-D"));
        });

        test("Kruskal's algorithm also works on graphs that are not connected", () -> {
            var forest = Kruskal.kruskal(twoSeparateGroups());
            assertEquals(describe(forest), Set.of("A-B", "B-C", "D-E"));
            assertEquals(weightOf(forest), 7);
        });

        test("a spanning forest has one tree per group of connected vertices", () -> {
            var graph = twoSeparateGroups();
            var forest = Kruskal.kruskal(graph);
            var groups = 2;
            assertEquals(forest.size(), graph.vertices().size() - groups);
        });


        // -- Both algorithms

        test("both algorithms find a tree of the same weight", () -> {
            for (var graph : List.of(offices(), smallExample(), square())) {
                var weightWithPrim = weightOf(Prim.prim(graph, "A"));
                var weightWithKruskal = weightOf(Kruskal.kruskal(graph));
                assertEquals(weightWithKruskal, weightWithPrim);
            }
        });

        test("when weights are equal, any spanning tree will do", () -> {
            // Four equal edges around a square: dropping any one of them works
            assertEquals(Prim.prim(square(), "A").size(), 3);
            assertEquals(Kruskal.kruskal(square()).size(), 3);
            assertEquals(weightOf(Kruskal.kruskal(square())), 3);
        });

        test("a smaller example: the minimum spanning tree weighs 9", () -> {
            assertEquals(weightOf(Prim.prim(smallExample(), "A")), 9);
            assertEquals(weightOf(Kruskal.kruskal(smallExample())), 9);
        });


        // -- Keeping track of the trees (union-find)

        test("at first, every element is in a set of its own", () -> {
            var sets = new DisjointSets(List.of("A", "B", "C"));
            assertFalse(sets.find("A") == sets.find("B"));
            assertFalse(sets.find("B") == sets.find("C"));
        });

        test("after a union, the two elements are in the same set", () -> {
            var sets = new DisjointSets(List.of("A", "B", "C"));
            sets.union("A", "B");
            assertTrue(sets.find("A") == sets.find("B"));
            assertFalse(sets.find("A") == sets.find("C"));
        });

        test("elements that are connected through others are in the same set", () -> {
            var sets = new DisjointSets(List.of("A", "B", "C", "D"));
            sets.union("A", "B");
            sets.union("C", "B");
            assertTrue(sets.find("A") == sets.find("C"));
            assertFalse(sets.find("A") == sets.find("D"));
        });

        System.out.println("All tests passed.");
    }


    // -- Graphs used in the tests

    // The six offices of the lecture notes, and the cost of laying a cable
    // between them
    static Graph offices() {
        return new Graph()
            .addEdge("A", "B", 4)
            .addEdge("A", "C", 2)
            .addEdge("B", "D", 3)
            .addEdge("B", "E", 2)
            .addEdge("C", "D", 1)
            .addEdge("C", "F", 5)
            .addEdge("D", "E", 4)
            .addEdge("E", "F", 6);
    }

    // A smaller example, with three spanning trees of weight 9, 10 and 11
    static Graph smallExample() {
        return new Graph()
            .addEdge("A", "B", 2)
            .addEdge("A", "C", 3)
            .addEdge("B", "C", 1)
            .addEdge("B", "D", 4)
            .addEdge("C", "D", 5)
            .addEdge("C", "E", 6)
            .addEdge("D", "E", 2);
    }

    // Four vertices around a square, all the edges having the same weight
    static Graph square() {
        return new Graph()
            .addEdge("A", "B", 1)
            .addEdge("B", "C", 1)
            .addEdge("C", "D", 1)
            .addEdge("D", "A", 1);
    }

    // Two groups of vertices, with no edge between them: A, B and C on one
    // side, D and E on the other
    static Graph twoSeparateGroups() {
        return new Graph()
            .addEdge("A", "B", 1)
            .addEdge("B", "C", 2)
            .addEdge("A", "C", 3)
            .addEdge("D", "E", 4);
    }


    // -- Helpers to read the results

    // The edges as names such as "A-C", with the two ends in alphabetical order
    static Set<String> describe(Set<Edge> edges) {
        var names = new TreeSet<String>();
        for (var edge : edges) {
            var ends = List.of(edge.source(), edge.target()).stream().sorted().toList();
            names.add(ends.get(0) + "-" + ends.get(1));
        }
        return names;
    }

    static int weightOf(Set<Edge> edges) {
        int total = 0;
        for (var edge : edges) {
            total += edge.weight();
        }
        return total;
    }
}
