# Edge List Pattern in Ruby

This walkthrough accompanies the [Graph Theory](https://franckc.folk.ntnu.no/idata2302/2026/graphs/intro/index.html) lecture notes. The complete source code is in [`edge_list.rb`](edge_list.rb).

## Running the code

```shell
$ ruby edge_list.rb
```

I present below a simple implementation of the edge list pattern, in
Ruby. Overall this implementation offers the following runtime
efficiency for the Graph ADT:

*Efficiency of the edge list operations*

| Operation | Best Case | Worst Case | Note |
|---|---|---|---|
| empty() | $`\Theta(1)`$ | $`\Theta(1)`$    | |
| add_vertex | $`\Theta(1)`$ | $`\Theta(\lvert E \rvert)`$ | Check for pending edges |
| remove_vertex | $`\Theta(1)`$ | $`\Theta(\lvert E \rvert)`$  | |
| add_edge(v1,v2) | $`\Theta(1)`$ | $`\Theta(1)`$ | No check for duplicated edges |
| remove_edge(v1,v2) | $`\Theta(\lvert E \rvert)`$ | $`\Theta(\lvert E \rvert)`$  | |
| edges_from(v) | $`\Theta(\lvert E \rvert)`$ | $`\Theta(\lvert E \rvert)`$  | |
| edges_to(v) | $`\Theta(\lvert E \rvert)`$ | $`\Theta(\lvert E \rvert)`$  | |

## Basic Layout

As often with graphs, the first step is to decide how we are going to
identify vertices. I chose to implement the vertex set using a hash
table and identify each vertex by a short unique "string" key. These
keys will be passed to the methods to identify what vertices to
connect, add, delete, etc.

The code below shows the basic of our Graph class
that uses the edge list pattern. It has two attributes `vertices`
and `edges`, which are a hash table and a sequence (dynamic array),
respectively.

*Storing vertices into a hash table, and the edges into a sequence.*

```ruby
class Graph

   def initialize()
      @vertices = {}  # Create a empty hash table
      @edges = []     # Create an empty sequence
   end

end
```
We can already create an *empty* graph using the default constructor,
using `friendship = Graph.new()`

## Managing Vertices

We can now implement the operations of the Graph ADT that enable
manipulating vertices. We will implement that as *methods* of the
Graph class above.

*Operations to create and delete vertices*

```ruby
def add_vertex(vertex_id, vertex)
   if @vertices.has_key?(vertex_id)
      raise "Duplicated vertex ID #{vertex_id}"
   end
   @vertices[vertex_id] = vertex
end

def remove_vertex(vertex_id)
   if @vertices.has_key?(vertex_id)
      if @edges.any{|edge| edge.is_incident_to?(vertex_id)}
         raise "Vertex #{vertex_id} still has incident edge!"
      else
         @vertices.delete(vertex_id)
      end
   end
end
```
To create a new vertex, I first check if the given ID is not already
assigned, in which case I raise an error. As I used an hash table, the
creation of a vertex takes a constant time, in the worst case
($`\Theta(1)`$). To remove a vertex, I first check if the vertex
exists, and if it does, if that vertex still has any incident edges
before to delete that vertex. To do so, we assume that there is a
method `is_incident_to(vertex_id)` available on edges. This makes
deleting a vertex an operation that takes linear time to the number of
edge ($`\Theta(|E|)`$).

Let us create a supporting class to capture an edge as ordered pair of
vertex IDs. The first will be the `source` and the second the
`target`.

*A simple Edge class *

```ruby
class Edge
   attr_reader :source,:target

   def initialize(source_id, target_id)
      @source = source_id
      @target = target_id
   end

   def is_incident_to?(vertex_id)
     @source == vertex_id or @target == vertex_id
   end

   def is_between?(v1_id, v2_id)
      self.is_incident_to?(v1) and self.incident_to?(v2)
   end

end
```
## Managing Edges

Now, we can add the graph operations to create and delete edges, as
shown below.

*Creation and deletion of edges *

```ruby
def add_edge(source_id, target_id, is_directed=false)
   unless @vertices.has_key?(source_id)
      raise "Unknown source vertex #{source_id}"
   end
   unless @vertices.has_key?(target_id)
      raise "Unknown target vertex #{target_id}"
   end
   @edges.append(Edge.new(source_id, target_id))
   unless is_directed
      @edges.append(Edge.new(target_id, source_id))
   end
end

def remove_edge(source_id, target_id, is_directed=false)
   @edges.delete_if{|edge| edge.is_between?(source_id, target_id)}
   unless is_directed
      self.remove_edge(target_id, source_id, true)
   end
end
```
To create a new edge between two given vertices, we first check that
these vertices exist and we raise an error if they do not. If they do,
we simply append a new edge at the end of the sequence, an operation
that takes constant time (amortized) with a dynamic array for
instance. Note that we could also ensure that every pair of vertices
has *at most* one edge´, but that would require another linear search
through our sequence of edges, and make this edge creation runs in
linear time. Finally, we offer the possibility to automatically
create the opposite if needed, using the `is_directed` parameter.

The edge removal is simpler, since we just need to delete all the
edges whose source and target match the given vertices ID. This is
done once again using a linear search through the sequence of edges,
which make this operation runs in linear time to the sequence of edge.

We can also create operations to fetch the edges that "point to" and
"point from" a given edge.

*Finding the edge from and to a given vertex*

```ruby
def edges_from(source_id)
    return @edges.select{|edge| edge.source == source_id}
end

def edges_to(target_id)
    return @edges.select{|edge| edge.target == target_id}
end
```
## Traversal

Let us add a depth-first traversal, as an example of algorithm that
runs against this graph ADT. Our traversal accepts two parameters,
namely the entry vertex to start from and an action to apply on each
vertex.

*Implementing a depth-first graph traversal*

```ruby
def depth_first(entry_vertex, &action)
   pending = [entry_vertex]
   processed = {}
   while not pending.empty?
      vertex = pending.pop()
      if not processed.has_key? vertex
        processed[vertex] = true
        action.call(vertex)
        pending += self.edges_from(vertex).map{|e| e.target}
      end
   end
end
```
