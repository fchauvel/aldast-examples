#ifndef ADJACENCY_LIST_H
#define ADJACENCY_LIST_H

typedef struct graph_s Graph;

typedef struct edge_s Edge;

Graph* create_empty_graph();

void destroy_graph (Graph* graph);


int vertex_count(Graph* graph);

int add_vertex(Graph* graph);

void remove_vertex(Graph* graph, int vertex_id);


int edge_count(Graph* graph);

Edge* get_edge(Graph* graph, int source, int target);

void add_edge(Graph* graph, int source, int target, double weight);

void remove_edge(Graph* graph, int source, int target);

int edge_from(Graph* graph, int source, Edge** matches);

int edge_to(Graph* graph, int target, Edge** matches);

void print_graph (Graph* graph);


#endif
