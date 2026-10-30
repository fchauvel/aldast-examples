# Adjacency List in JavaScript

This walkthrough accompanies the [Graph Theory](https://franckc.folk.ntnu.no/idata2302/2026/graphs/intro/index.html) lecture notes. The complete source code is in [`adjacency_list.js`](adjacency_list.js).

## Running the code

```shell
$ node adjacency_list.js
```

I detail below how one could implement a graph using the "adjacency
list" pattern. This is only one way to implement a graph: Others
common patterns include the edge list and the adjacency matrix.

Overall this provides faster operations than the edge list, for the
same storage requirements, but the code more involved. The operations
of the Graph ADT have the following runtime efficiencies, shown below.

*Efficiency of the adjacency list operations*

| Operation | Best Case | Worst Case | Note |
|---|---|---|---|
| empty() | $`\Theta(1)`$ | $`\Theta(1)`$                    | |
| addVertex(v) | $`\Theta(1)`$ | $`\Theta(1)`$ | Check for pending edges |
| removeVertex(v) | $`\Theta(1)`$ | $`\Theta(1)`$                    | |
| vertexCount | $`\Theta(1)`$ | $`\Theta(1)`$                    | |
| addEdge(v1,v2) | $`\Theta(1)`$ | $`\Theta(1)`$ | No check for duplicated edges |
| removeEdge(v1,v2) | $`\Theta(1)`$ | $`\Theta(\deg(v_1)+\deg(v_2))`$  | |
| edgeCount | $`\Theta(\lvert V \rvert)`$ | $`\Theta(\lvert V \rvert)`$                  | |
| edges_from(v) | $`\Theta(1)`$ | $`\Theta(\deg(v))`$              | |
| edges_to(v) | $`\Theta(1)`$ | $`\Theta(\deg(v))`$              | |

## Code Skeleton

The core idea of the adjacency list pattern is to *group edges* by
vertex. Compared to the *edge list* patterns, this yields faster
`edgesFrom` and `edgesTo` operations.

> **Note:** The name "adjacency list" reflects that every vertex is associated
> with its adjacent vertex. In practice however, we often store the
> list of incident edges, especially if edges carry important
> information (weights, labels, etc.).
For this JavaScript implementation, we create a `Graph` class that
holds its set of vertex in a hash table for fast access. I assume that
vertices are indexed using a unique "key". This hash table maps each
key to a specific vertex record that will hold the vertex's ID, its
data, and a list of its incident
edges. The code below shows the basic structure of
these classes.

*Basic structure of the "adjacency Lists" pattern in JavaScript*

```js
class Graph {

   constructor () {
      this._vertices = {}  // Set (Hash table) of 'Vertex'
   }

}

class Vertex {

   constructor(vertexId, payload) {
      this.vertexId = vertexId
      this.payload = payload
      this._incidentEdges = [] // Sequence of 'Edge'
   }

}

class Edge {

   constructor (sourceId, targetId, weight=0) {
       this.sourceId = sourceId;
       this.targetId = targetId;
       this.weight = weight;
   }

}
```
## Vertex Management

The short skeleton above already lets us create an empty graph. But, we
can now add the methods to add, remove and count vertices.

### Adding Vertex

Let us start with adding new vertices: The `addVertex` operation,
shown below. Here is the
"algorithm" it implements.

Given a vertex ID and the related vertex data:

1. Check if the given ID is already used in our internal hash
   table.

2. If there is a duplicate ID, we throw an error.

3. Otherwise, we create a new vertex object and store it in the hash
   table, with the given ID as key.

*Operations to manage vertices*

```js
class Graph { // ... continued ...

    addVertex (vertexId, payload) {
         if (vertexId in this._vertices)
             throw new Error(`Duplicated vertex ${vertexId}.`);
         this._vertices[vertexId] = new Vertex(vertexId, payload);
     }

}
```
`addVertex` is a constant time operation, because of our underlying
hash table, where reading and writing both run in constant time.

### Removing Vertices

Let us now look at how to remove vertices, that is, the
`removeVertex` operation, whose code in shown below.

This requires a little more work, because we need to check whether
there is any edge that still refers to the vertex we need to
delete. To delete a vertex whose ID is given, we proceed as follows:

1. Check if the given vertex ID exists. If it does not, we just
   return, no need to raise an error.

2. Otherwise, we fetch the vertex from our internal hash table.

3. If deleting this vertex would leave any edges "pending", we raise
   an error.

4. Otherwise, we delete the selected vertex from our hash table.

*Operations to manage vertices*

```js
class Graph { // ... continued ...

     removeVertex (vertexId) {
         vertex = this._vertices.vertexId
         if (vertex !== undefined) {
             if (vertex.hasAnyIncidentEdge())
                 throw new Error(`Vertex ${vertexId} still has incident edges!`);
             delete this._vertices.vertexId;
         }
     }

}

class Vertex { // ... continued ...

    hasAnyIncidentEdge() {
       return this.incidentEdgeCount() > 0;
    }

}
```
This is also an operation that runs in constant time. In the best
case, the selected vertex does not exists, and there is nothing to
do. Otherwise, either the vertex still has incident edges and we just
throw an error (getting the length of the array takes constant
time). Alternatively, we delete one entry from our hash table, which
also takes constant time.

### Counting Vertices

Finally, we can count vertices by simply returning the size of the
underlying hash table, as shown below. A constant time operation as
well.

*Counting vertices in the graph*

```js
class Graph { // ... continued ...

    vertexCount () {
         return Object.keys(this._vertices).length;
     }

}
```
## Edge Management

Now we can implement the operations we need to create and delete
*edges*. This is where our `Edge` class comes into play. However,
since edges are grouped by vertices, our `Graph` class only
delegates adding and removing edges to the appropriate vertex as we
shall see.

### Creating New Edges

To create a new edge, we need the IDs of its source and target
vertices, as well as boolean flag that indicates if the edge is
directed. Given those, we proceed as follows:

1. We check if the source and target vertex ID both exist in our internal
   hash table. We throw an error if any does not.

2. We create two Edge objects. One in the incidence list of the source
   vertex, and one in the incidence list of the target vertex.

3. If the edge is undirected, we also create the opposite edge, from
   target to source (using a recursive call).

The code below details the methods we need to do
that. We created a helper `_findVertexById`, which ensures the given
vertex ID exists and returns the associated vertex object. We also
extend the `Vertex` class with two new methods: One to add an edge
to another vertex, and one to add a new edge from another vertex.

*Adding new edges*

```js
class Graph { //  ... continued ...

    addEdge (sourceId, targetId, isDirected) {
       const source = this._findVertexById(sourceId);
       const target = this._findVertexById(targetId);
       source.addEdgeTo(targetId);
       target.addEdgeFrom(sourceId);
       if (!isDirected)
          this.addEdge(targetId, sourceId, true);
    }

    _findVertexById (vertexId) {
       const vertex = this._vertices[vertexId];
       if (vertex === undefined)
           throw new Error(`Unknown vertex ${vertexId}.`);
       return vertex
    }

}

class Vertex { // ... continued ...

    addEdgeTo (targetId) {
       const edge = new Edge(this.vertexId, targetId);
       this._incidentEdges.push(edge);
    }

    addEdgeFrom (sourceId) {
        const edge = new Edge(sourceId, this.vertexId);
        this._incidentEdges.push(edge);
    }

 }
```
How fast does that runs? In constant time as well. In the "best" case,
one of the given vertex ID does not exist, and the method raises an
error. In the worst case, both the source and target IDs exist, and
the edge is *undirected*. As a result, we have to create four Edge
objects: Two for the source-to-target edge, and two more for the
opposite edge (target-to-source). A little more work, but still in
constant time: We always create at most four objects.

### Deleting Edges

To delete an edge, we need to change two Vertex objects: The source
and the target vertices. In the source Vertex, we need to filter out
all the incident edges whose source matches the given
source ID. Similarly, in the target vertex, we need to filter out all
the incident edges whose target matches the given target ID. Given two
vertex IDs and a flag that indicates whether the edge is directed, we
proceed as follows:

1. Check whether both vertex IDs are known. If any is not defined, we
   raise an error.

2. We adjust the source vertex by removing all the edges whose source
   ID matches the given source ID.

3. We adjust the target vertex by removing all its edges whose target
   matches the given target ID.

4. If the edge is undirected, we remove as well the opposite edge
   (from target to source).

*Removing edges from a graph*

```js
class Graph { // ... continued ...

    removeEdge (sourceId, targetId, isDirected) {
        const source = this._findVertexById(sourceId);
        const target = this._findVertexById(targetId);
        source.removeEdgeTo(targetId);
        target.removeEdgeFrom(sourceId);
        if (!isDirected)
           this.removeEdge(targetId, sourceId, true);
    }

}

class Vertex { // ... continued ...

     removeEdgesFrom (sourceId) {
         this._incidentEdges =
             this._incidentEdges.filter(edge => edge.sourceId !== sourceId);
     }

     removeEdgeTo (targetId) {
         this._incidentEdges =
             this._incidentEdges.filter(edge => edge.targetId !== targetId);
     }

}
```
How fast does that run? It takes $`\Theta(\deg(s) + \deg(t))`$. In
the best case, one of the source and target vertex ID is not defined
and we raise an error. In the worst case, the selected edge is
undirected and we must delete four Edge objects: Two for the
source-to-target edge and two more for the target-to-source
edge. Deleting one such object requires to traverse the list of
incident edges of a vertex (say $`v`$), and takes
$`\Theta(\deg(v))`$. We have to do that twice for the source
vertex and twice for the target vertex.

### Counting Edges

To count the edges in a given graph, we have to loop through the
vertices and add up their count of incident vertices. Every edge is
recorded twice however: Once in the source vertex and once in the
target vertex. In graph theory, this is a known equality:

$$
\forall \, G=(V,E), \qquad \sum_{v \, \in\,V} \deg(v) = 2 \times |E|
$$
The code below details the `edgeCount`
operations of our Graph ADT.

*Counting Edges*

```js
class Graph { // ... continued ...

    edgeCount () {
         let sum = 0;
         for (const [key, vertex] of Object.entries(this._vertices)) {
             sum += vertex.degree();
         }
         return sum / 2;
     }

}

class Vertex { // ... continued ...

    degree () {
       return this._incidentEdges.length;
    }

}
```
As this operation iterates over the set of vertex, it takes a time
that is linear to the number of vertices, that is $`T \in \Theta(|V|)`$.

> **Note:** A faster way would be to add a new internal attribute (say
> `edgeCounter`) to our graph class to keep track of the number of
> edge in the graph. We would have to update it in `addEdge` and
> `removeEdge`, but that would yield an $`\Theta(1)`$
> `edgeCount` implementation.
### Incoming and Outgoing Edges

Finally, the two last operations we need to implement are `edgesTo`
and `edgesFrom`, which compute the subsets of outgoing and incoming
edges, respectively.

To get the incoming edges for instance, we filter only those incident
edges whose target is the desired vertex. Similarly to get the
outgoing edges, we filter only those incident edges whose source is
the desired vertex.

*Incoming and outgoing edges of a given vertex*

```js
class Graph { // ... continued ...

    edgesFrom (sourceId) {
        return this._findVertexById(sourceId).outgoingEdges();
    }

    edgesTo (targetId) {
        return this._findVertexById(targetId).incomingEdges();
    }

}

class Vertex { // ... continued ...

    outgoingEdges () {
       return this._incidentEdges.filter(edge => edge.sourceId === this.vertexId);
    }

    incomingEdges () {
       return this._incidentEdges.filter(edge => edge.targetId === this.vertexId);
    }

}
```
In the best case, the selected vertex $`v`$ has no incoming
(resp. outgoing) edges, and these operations run in constant time. In
the worst case, we have to look at every single incident edge to
decide whether we keep it. That gives a runtime linear to the
degree of Vertex $`v`$, $`\Theta(\deg(v))`$.

> **Note:** Storing the incident edges in a single list is a design decision. We could also have used two separate lists, one for the
> outgoing edges and one for the incoming edges. That yields faster
> implementations that runs in constant time.
