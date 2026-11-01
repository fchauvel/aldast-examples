# Minimum Spanning Trees (Java)

Prim's and Kruskal's algorithms, from the
[Minimum Spanning Trees](https://franckc.folk.ntnu.no/idata2302/2026/graphs/mst/index.html) lecture notes.

- `Prim.java` grows a single tree from a starting vertex. It only works
  on connected graphs.
- `Kruskal.java` builds a forest and merges its trees. It also works on
  graphs that are not connected, in which case it finds a minimum
  spanning forest.
- `DisjointSets.java` keeps track of which vertices are in the same tree
  (union-find), for Kruskal's algorithm.
- `Graph.java` and `Edge.java` are a small weighted graph.
- `MstTests.java` holds the tests, and `Testing.java` the few helpers they
  use.

The code favours clarity over efficiency.

## Running the tests

```shell
$ make test
```
