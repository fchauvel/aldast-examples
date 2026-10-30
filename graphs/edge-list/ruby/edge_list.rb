# coding: utf-8
#
# Sample Ruby class to illustrate how to implement a graph using and
# edge list.

class Graph

  def initialize()
    @vertices = {}  # Create a empty hash table
    @edges = []     # Create an empty sequence
  end

  def add_vertex(vertex_id, vertex)
    if @vertices.has_key?(vertex_id)
      raise "Duplicated vertex ID #{vertex_id}"
    end
    @vertices[vertex_id] = vertex
  end

  def remove_vertex(vertex_id)
    if @vertices.has_key?(vertex_id)
      if @edges.any{|edge| edge.is_incident_to(vertex_id)}
        raise "Vertex #{vertex_id} still has edge!"
      else
        @vertices.delete(vertex_id)
      end
    end
  end

  def vertex_count
    return @vertices.length
  end

  def edge_count
    return @edges.length
  end

  def add_edge(source_id, target_id, is_directed=false)
    unless @vertices.has_key?(source_id)
      raise "Unknown source vertex #{source_id}"
    end
    unless @vertices.has_key?(target_id)
      raise "Unknown target vertex #{source_id}"
    end
    @edges.append(Edge.new(source_id, target_id))
    unless is_directed
      @edges.append(Edge.new(target_id, source_id))
    end
  end

  def remove_edge(source_id, target_id)
    @edges.delete_if{|e| e.is_incident_to(source_id) and e.is_incident_to(target_id)}
  end

  def edges_from(source_id)
       return @edges.reject{|e| e.source != source_id}
  end

  def degree_of(vertex_id)
       return @edges.count{|e| e.is_incident_to?(vertex_id)}
  end

  def adjacent?(v1, v2)
    @edges.any{|e| e.self.is_incident_to?(v1) and e.is_incident_to?(v2)}
  end

  def depth_first(entry_vertex, &action)
    pending = [entry_vertex]
    processed = {}
    while not pending.empty?
      vertex = pending.pop()
      if not processed.has_key? vertex
        processed[vertex] = true
        action.call(vertex)
        pending += self.edges_from(vertex).map{|e| e.target}.sort.reverse
      end
    end
  end

  def has_path(source, target)
    pending = [source]
    processed = {}
    while not pending.empty?
      vertex = pending.pop()
      return true if vertex == target
      if not processed.has_key? vertex
        processed[vertex] = true
        pending += self.edges_from(vertex).map{|e| e.target}
      end
    end
    return false
  end

end


class Edge
  attr_reader :source,:target

  def initialize(source_id, target_id)
    @source = source_id
    @target = target_id
  end

  def is_incident_to?(vertex_id)
    @source == vertex_id or @target == vertex_id
  end

end



persons = ["Denis", "Erik", "Frank", "John", "Lisa", "Mary", "Olive", "Peter", "Thomas"]

g = Graph.new()
persons.each{|p| g.add_vertex(p[0], p) }

g.add_edge("D", "F")
g.add_edge("D", "O")
g.add_edge("F", "T")
g.add_edge("F", "L")
g.add_edge("L", "J")
g.add_edge("M", "J")
g.add_edge("M", "P")
g.add_edge("M", "O")
g.add_edge("O", "E")

puts g.edge_count

g.depth_first("D") {|v| puts(v)}
