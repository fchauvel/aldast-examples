# Adjacency Matrix in C

This walkthrough accompanies the [Graph Theory](https://franckc.folk.ntnu.no/idata2302/2026/graphs/intro/index.html) lecture notes. The complete source code is in [`adjacency_matrix.c`](adjacency_matrix.c).

## Running the code

```shell
$ gcc -Wall -o adjacency_matrix adjacency_matrix.c
$ ./adjacency_matrix
```

I illustrate here how I would implement the adjacency matrix pattern
in C. This approach consumes more memory than the edge list or the
adjacency list ($`\Theta(|V|^2 + |E|)`$
vs. $`\Theta(|V|+|E|)`$). It offers however a constant time edge
access, but suffer from quadratic time vertex set changes.

*Efficiency of the adjacency matrix operations*

| Operation | Best Case | Worst Case | Note |
|---|---|---|---|
| empty | $`\Theta(1)`$ | $`\Theta(1)`$     | |
| vertex_count | $`\Theta(1)`$ | $`\Theta(1)`$     | |
| add_vertex | $`\Theta(\lvert V \rvert^2)`$ | $`\Theta(\lvert V \rvert^2)`$ | Check for pending edges |
| remove_vertex | $`\Theta(\lvert V \rvert^2)`$ | $`\Theta(\lvert V \rvert^2)`$ | |
| add_edge(v1, v2) | $`\Theta(1)`$ | $`\Theta(1)`$ | No check for duplicated edges |
| remove_edge(v1, v2) | $`\Theta(1)`$ | $`\Theta(1)`$     | |
| edges_from(v) | $`\Theta(\lvert V \rvert)`$ | $`\Theta(\lvert V \rvert)`$   | |
| edges_to(v) | $`\Theta(\lvert V \rvert)`$ | $`\Theta(\lvert V \rvert)`$   | |

## Data Structure Skeleton

To implement the graph ADT, the adjacency matrix pattern uses a direct
analogy with the mathematical concept. Given a graph $`G=(V,E)`$,
its adjacency matrix $`A`$ is a $`|V| \times |V|`$ matrix
where each cell $`c_{i,j}`$ contains the number of vertices
between Vertex $`v_i`$ and Vertex $`v_j`$. This patterns
stores this adjacency matrix of edges, explicitly.

In terms of storage efficiency, this pattern therefore has a memory
footprint that is quadratic to the number of vertices (i.e.,
$`\Theta(|V|^2 + |E|)`$).

> **C Modular Programming:** C is a pure imperative programming language. There is no class nor
> object ; only procedures, structures (i.e., records), and pointers
> (i.e., memory addresses). We can create modules, that is, a set of
> procedures and the related data types. A module is made of two
> files: A header file (`.h`) that lists the signature of all the
> "public" procedures the module offers, and an implementation file
> (`.c`) where these procedures are fleshed out. Modules are good
> fit for ADT because they enable hiding implementation details.
Our graph implementation is therefore a C module, whose header file
is shown below. This
implementation focuses on *simple weighted directed graphs*. An edge is
therefore fully identified by the (ordered) pair of vertices it
connects.

*The header file (file `graph.h`) for our C-module that implements the Graph ADT .*

```c
#ifndef ADJACENCY_LIST_H
#define ADJACENCY_LIST_H

typedef struct graph_s Graph;
typedef struct edge_s Edge;

// Creation & Destruction
Graph* create_empty_graph ();
void destroy_graph (Graph* graph);

// Vertex Management
int vertex_count (Graph* graph);
int add_vertex (Graph* graph);
void remove_vertex (Graph* graph, int vertex_id);

// Edge Management
int edge_count (Graph* graph);
Edge* get_edge (Graph* graph, int source, int target);
void add_edge (Graph* graph, int source, int target, double weight);
void remove_edge (Graph* graph, int source, int target);
int edge_from (Graph* graph, int source, Edge** matches);
int edge_to (Graph* graph, int target, Edge** matches);

// Helpers
void print_graph (Graph* graph);

#endif
```
As shown below, we define two
kinds of *record* (i.e., structure in C): One to hold the graph and
one to hold edges. The `Graph` record stores the number of vertices
and the matrix of edges. I choose to represent this matrix as an
array. The `Edge` record only stores the weight associated, but
could be extended to hold any kind of application-specific data.

To keep things simple, we shall store this matrix of $`|V| \times |V|`$ `Edge` records in a single one-dimensional C array. Given that
vertices are numbered from 0 to $`|V|-1`$, the edge between Vertex
$`v_i`$ and $`v_j`$ would therefore have index $`i \cdot |V| + j`$ in our 1D array.

*Data structures to hold an adjacency matrix in memory*

```c
struct edge_s {
  double weight;
};

struct graph_s {
  int vertex_count;
  Edge** matrix;     // Array of pointer to Edge
};
```
## Graph Creation & Destruction

The C language does not offer any automated memory management, so we
have to explicitly allocate and free memory. We shall use the
procedure `malloc` to reserve blocks of memory, and the `free`
procedure to release them. Both procedures belongs to the standard C
library and come from the `stdlib.h` header file. We also leverage
the `sizeof` built-in C operator, which returns the size of its
operand in bytes.

### Graph Creation

To create a graph, we have to allocate memory for the `Graph`
record. This includes creating the Graph record itself, and
initializing the $`|V|^2`$-long array of Edge pointer.

The procedure `create_isolated_vertices` below creates a graph with
a given number of vertices, but without edge. It allocates an array of
$`|V|^2`$ "null" pointers, since a null pointer represents a
"missing" edge. Creating an empty graph therefore boils down to
creating a graph with zero isolated vertex.

*Creating empty graphs*

```c
Graph*
create_isolated_vertices (int count)
{
  assert (count >= 0);
  Graph* new_graph = malloc(sizeof(Graph));
  new_graph->vertex_count = count;
  if (count > 0)
    new_graph->matrix = malloc(count * count * sizeof(Edge*));
  return new_graph;
}

Graph*
create_empty_graph () {
  return create_isolated_vertices(0);
}
```
Both of these operations run in constant time in all cases: There is
no loop involved an I assume that `malloc` and `sizeof` also run
in constant time.

### Graph Destruction

To release the memory associated with a graph, we need to release
every single edge record, release the array of edge pointers, and
release the graph record itself. We must proceed in this very order,
because if we were to start by releasing the graph record first, we
would loose access to its inner matrix of edges and thus create a
memory leak.

*Disposing of graphs---freeing the memory*

```c
void
delete_graph (Graph* graph)
{
  for (int row=0 ; row<graph->vertex_count ; row++) {
    for (int column=0 ; column<graph->vertex_count ; column++) {
      free(get_edge(graph, row, column));
    }
  }
  free(graph->matrix);
  free(graph);
}
```
Provided that `free` runs in constant time, this destruction
procedure runs in time that is quadratic to the number of vertices
(i.e., $`\Theta(|V|^2)`$).

## Edge Management

### Adjacency Matrix Operations

We need to store edges in our adjacency matrix according to their
source and target vertices. For better readability, we define two
"private" procedures `get_edge` and `set_edge`, which read and
write, respectively, the edge associated with a pair of
vertices. The code below details
these procedures.

*Getting and setting edges from and to the adjacency matrix.*

```c
 Edge*
 get_edge (Graph* graph, int source, int target) {
   assert(source >= 0 && source < graph->vertex_count);
   assert(target >= 0 && target < graph->vertex_count);
   return graph->matrix[source * graph->vertex_count + target];
 }

 void
 set_edge (Graph* graph, int source, int target, Edge* edge) {
   assert(source >= 0 && source < graph->vertex_count);
   assert(target >= 0 && target < graph->vertex_count);
   graph->matrix[source * graph->vertex_count + target] = edge;
 }
```
These two operations runs in constant time, because we use an array
underneath. Accessing an array by "index" runs in constant time. This
makes the adjacency matrix pattern fast for situation that requires a
lot of edge access.

### Counting Edges

To count how many edges the graph contains, we need to traverse the
underlying array and count how many Edge pointers are defined (i.e.,
not `NULL`). The code below shows one way
to implement this `count_edge` operation.

*Counting edges in the adjacency matrix*

```c
int
edge_count (Graph* graph) {
  int edge_count = 0;
  for (int row=0 ; row<graph->vertex_count ; row++) {
    for (int column=0 ; column<graph->vertex_count ; column++) {
      if (get_edge(graph, row, column) != NULL)
        edge_count++;
    }
  }
  return edge_count;
}
```
This operation runs in quadratic time with respect to the number of
vertex (i.e., $`\Theta(|V|^2)`$). Here, the actual number of edges
does not matter because the edges that are not defined still have a
dedicated pointer in our matrix---but it is a null pointer. The
underlying array always has $`|V|^2`$ items.

### Adding Edges

To add a specific edge identified by its source and target vertices,
we get the current edge between these two vertices. If there is none,
we first allocate some memory for a new edge record, and then place
this record into our adjacency matrix. Finally we set the given
weight. The code below illustrates how we can
do that in C.

*Adding a new edge.*

```c
void
add_edge (Graph* graph, int source, int target, double weight) {
  assert (source >= 0 && graph->vertex_count > source);
  assert (target >= 0 && graph->vertex_count > target);
  Edge* edge = get_edge(graph, source, target);
  if (edge == NULL) {
     edge = malloc(sizeof(Edge));
     set_edge (graph, source, target, edge);
  }
  edge->weight = weight;
}
```
This operation runs in constant time (assuming `malloc` does too).

### Removing Edges

To remove an edge between two vertices, we first look into our
adjacency matrix for the corresponding `Edge` record. If there is
none, we have nothing to do. Otherwise, we must free the associated
memory and set to NULL the corresponding entry in the adjacency
matrix. In C, we proceed as follows :

*Removing edges*

```c
void
remove_edge(Graph* graph, int source, int target) {
  assert(source >= 0 && graph->vertex_count > source);
  assert(target >= 0 && graph->vertex_count > target);
  Edge* edge = get_edge(graph, source, target);
  if (edge != NULL) free(edge);
  set_edge(graph, source, target, NULL);
}          
```
This also runs in constant time. Whatever is the size of the graph, in
the best case we do nothing, and in the worst case we modify only one
edge in the adjacency matrix. Accessing it is a constant time
operation because of the underlying array.

### Incoming & Outgoing Edges

Computing the incoming edges (resp. the outgoing edges) of a given
vertex is more technical in C, because we need to manipulate arrays.

To compute the outgoing edges of Vertex $`v_i`$, we need to filer
the i-th row of our adjacency matrix and retain only these pointers
that are not `NULL`. The code below
details the related C code. To get the *incoming edges*, we proceed
the same way, but with the i-th column instead of the i-th row.

*Computing the outgoing edges of a vertex*

```c
int
edge_from (Graph* graph, int source, Edge** matches)
{
  int match_count = 0;
  for (int column=0; column<graph->vertex_count ; column++) {
    Edge* edge = get_edge(graph, source, column);
    if (edge != NULL) {
      matches[match_count] = edge;
      match_count++;
    }
  }
  return match_count;
}
```
In this procedure, the `matches` argument is a array of pointers to
`Edge`, where we will store the edges that match. We return the number
of matches, so that the client knows how many edges are in that
array.

For a graph $`G=(V,E)`$, this operation's runtime is linear to the
number of vertices (i.e., $`\Theta(|V|)`$), because the number of
vertices defines how many vertices there are in a row of the adjacency
matrix.

## Vertex Management

Handling vertices is more tedious, because any change to the vertex
set requires resizing the underlying adjacency matrix.

### Counting Vertices

Counting the vertices is straightforward however, since the `Graph`
record stores it explicitly, as shown below.

*Counting the graph's vertices*

```c
int
vertex_count(Graph* graph) {
   return graph->vertex_count;
}
```
This runs in constant time, regardless of the size of the given
graph. Accessing a field by name in a record takes constant time.

### Adding Vertices

Adding a new vertex requires appending both a new row and a new column
to the adjacency matrix. This, in turn, requires creating another
"bigger" matrix and copying the pointers to the existing edges into
this new matrix, Finally we delete the old matrix, and we replace it
with the new one.

*Adding new vertices*

```c
int
add_vertex(Graph* graph) {
  Graph* new_graph = create_isolated_vertices(graph->vertex_count + 1);
  for (int row=0 ; row<graph->vertex_count ; row++) {
    for (int column=0 ; column<graph->vertex_count ; column++) {
      Edge* edge = get_edge(graph, row, column);
      set_edge(new_graph, row, column, edge);
    }
  }
  graph->vertex_count = new_graph->vertex_count;
  free(graph->matrix);
  graph->matrix = new_graph->matrix;
  free(new_graph);
  return graph->vertex_count;
}
```
This operation runs in quadratic time with respect to the number of
vertices (i.e., $`\Theta(|V|^2)`$). Regardless of the size of the
graph, we must copy the duplicate the whole matrix.

### Removing Vertices

Finally, removing a vertex requires to remove both a row and column in
the adjacency matrix. This operation also requires duplicating the
adjacency matrix, but is more convoluted because we need to shift back
the edges in rows and columns past the deleted ones by one
row. The code below shows the related C
code.

*Removing edges*

```c
void
remove_vertex(Graph* graph, int vertex_id) {
  assert(vertex_id >= 0 && graph->vertex_count > vertex_id);
  Graph* new_graph = create_isolated_vertices(graph->vertex_count - 1);
  for (int row=0 ; row<graph->vertex_count ; row++) {
    for(int column=0 ; column<graph->vertex_count; column++) {
      Edge* edge = get_edge(graph, row, column);
      if (row < vertex_id && column && vertex_id) {
        set_edge(new_graph, row, column, edge);

      } else if (row == vertex_id || column == vertex_id) {
        continue;

      } else if (row < vertex_id && column > vertex_id) {
        set_edge(new_graph, row, column-1, edge);

      } else if (row > vertex_id && column < vertex_id) {
        set_edge(new_graph, row-1, column, edge);

      } else { // Edge on the diagonal
        set_edge(new_graph, row-1, column-1, edge);

      }
    }
  }
  graph->vertex_count = new_graph->vertex_count;
  free(graph->matrix);
  graph->matrix = new_graph->matrix;
  free(new_graph);
}                
```
Regardless of the technicalities, this operation runtime is quadratic
to the number of vertices (i.e., $`\Theta(|V|^2)`$). Regardless
which edge we delete, we still have to duplicate the whole adjacency
matrix.
