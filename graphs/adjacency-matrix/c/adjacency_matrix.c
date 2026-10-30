#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#include "adjacency_matrix.h"

struct edge_s {
  double weight;
};


struct graph_s {
  int vertex_count;
  Edge** matrix;
};


Graph*
create_isolated_vertices (int count) {
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

Edge*
get_edge (Graph* graph, int source, int target) {
  assert(source >= 0 && source < graph->vertex_count);
  assert(target >= 0 && target < graph->vertex_count);
  return graph->matrix[source * graph->vertex_count + target];
}

int
edge_count (Graph* graph) {
  int edge_count = 0;
  int vertex_count = graph->vertex_count;
  for (int row=0 ; row<vertex_count ; row++) {
    for (int column=0 ; column<vertex_count ; column++) {
      if (get_edge(graph, row, column) != NULL)
        edge_count++;
    }
  }
  return edge_count;
}

void
delete_graph (Graph* graph) {
  for (int row=0 ; row<graph->vertex_count ; row++) {
    for (int column=0 ; column<graph->vertex_count ; column++) {
      free(get_edge(graph, row, column));
    }
  }
  free(graph->matrix);
  free(graph);
}

Edge*
create_edge (double weight) {
  Edge* new_edge = malloc(sizeof(Edge));
  new_edge->weight = weight;
  return new_edge;
}


void
set_edge(Graph* graph, int source, int target, Edge* edge) {
  assert(source >= 0 && source < graph->vertex_count);
  assert(target >= 0 && target < graph->vertex_count);
  graph->matrix[source * graph->vertex_count + target] = edge;
}

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

void
remove_vertex(Graph* graph, int vertex_id) {
  assert(graph->vertex_count > vertex_id);
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

      } else {
        set_edge(new_graph, row-1, column-1, edge);

      }
    }
  }
  graph->vertex_count = new_graph->vertex_count;
  free(graph->matrix);
  graph->matrix = new_graph->matrix;
  free(new_graph);
}

void
add_edge(Graph* graph, int source, int target, double weight) {
  assert (source >= 0 && graph->vertex_count > source);
  assert (target >= 0 && graph->vertex_count > target);
  assert (get_edge(graph, source, target) == NULL);
  set_edge (graph, source, target, create_edge(weight));
}

void
remove_edge(Graph* graph, int source, int target) {
  assert(source >= 0 && graph->vertex_count > source);
  assert(target >= 0 && graph->vertex_count > target);
  Edge* edge = get_edge(graph, source, target);
  if (edge != NULL) free(edge);
  set_edge(graph, source, target, NULL);
}

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

int
edge_to(Graph* graph, int target, Edge** matches)
{
  int match_count = 0;
  for (int row=0; row<graph->vertex_count ; row++) {
    Edge* edge = get_edge(graph, row, target);
    if (edge != NULL) {
      matches[match_count] = edge;
      match_count++;
    }
  }
  return match_count;
}

void
show_stats(Graph* graph) {
  printf("Graph: %d vertices, %d edge(s).\n",
         graph->vertex_count,
         edge_count(graph));
}

void
print_graph(Graph* graph) {
  printf("Graph: %d vertices, %d edge(s).\n",
         graph->vertex_count,
         edge_count(graph));
  int vertex_count = graph->vertex_count;
  for (int row=0 ; row<vertex_count ; row++) {
    printf("V%d -> { ", row);
    for (int column=0 ; column<vertex_count ; column++) {
      Edge* edge = get_edge(graph, row, column);
      if (edge != NULL) {
        printf("%d:%.2f ", column, edge->weight);
      }
      if (column >= vertex_count-1) printf("}\n");
    }
  }
}


int
main(int argc, char** argv) {
  Graph* graph = create_empty_graph();
  add_vertex(graph);
  add_vertex(graph);
  print_graph(graph);
  add_edge(graph, 0, 1, 0.75);
  print_graph(graph);
  add_vertex(graph);
  add_edge(graph, 1, 2, 0.5);
  add_edge(graph, 0, 2, 0.23);
  print_graph(graph);
  delete_graph(graph);
  return 1;
}
