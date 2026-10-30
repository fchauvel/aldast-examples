

class MinimumHeap

  # Sort the given array of (item, priority) by ascending priority
  def self.heapsort(array)
    heap = MinimumHeap.new()
    heap.heapify
    sorted = []
    until heap.is_empty
      sorted.push(heap.dequeue)
    end
    return sorted
  end

  # Create a new heap with the given entries as underlying entries
  def initialize(initial_entries=[])
    @nodes = initial_entries.map{|item, priority| Node.new(item, priority) }
  end

  def peek()
    abort_if_empty
    return @nodes[0]
  end

    # Return the number of items in the heap
  def size()
    return @nodes.count
  end

  # Return true if the heap contains the given item associated with
  # the given priority
  def contains(item, priority)
    return @nodes.any? { |e| e.item == item and e.priority == priority}
  end

  # Return true if the there is no item in the heap
   def is_empty
    return size <= 0
  end

  private def abort_if_empty
    if is_empty
      raise "Invalid state: Empty queue"
    end
  end

  # Remove the node with the lowest priority and restore the heap
  # ordering
  def dequeue()
    abort_if_empty
    swap(first_node, last_node)
    minimum = @nodes.pop
    index = 0
    until is_leaf(index) or is_valid_parent(index)
        index = move_down(index)
    end
    return minimum
  end

  # Return the index of the first node, that is 0
  private def first_node
    return 0
  end

  # Returns the last node
  private def last_node
    return size - 1
  end

  # Returns true if an only if the given parent has as a lower
  # priority than its children
  private def is_valid_parent(parent)
    children = children_of(parent)
    return children.all? { |c| @nodes[c].priority >= @nodes[parent].priority }
  end

  # Swap the given parent with its child with the minimum priority
  # and returns that child's index
  private def move_down(parent)
    children = children_of(parent)
    chosen = children.min_by{ |i| @nodes[i].priority }
    swap(parent, chosen)
    return chosen
  end

  # Append a new node to the array and move it up until the heap
  # ordering is restored.
  def enqueue(item, priority)
    @nodes.push(Node.new(item, priority))
    index = last_node
    until is_root(index) or is_valid_child(index)
      index = move_up(index)
    end
  end

  # Return true if the node at the given index and its parent adhere
  # to the heap ordering
  private def is_valid_child(node)
    parent = parent_of(node)
    return @nodes[node].priority >= @nodes[parent].priority
  end

  # Swap a the given node with its parent and return its new index
  private def move_up(node)
    parent = parent_of(node)
    swap(node, parent)
    return parent
  end

  # Build restore the heap ordering in the whole underlying array
  def heapify
    node = parent_of(last_node)
    while is_defined(node)
      until is_leaf(node) or is_valid_parent(node)
        node = move_down(node)
      end
      node = previous_of(node)
    end
  end

  # Return true is given index
  private def is_defined(index)
    index >= 0 and index < @nodes.size
  end

  # Returns the node that precedes the given one
  private def previous_of(node)
    return node - 1
  end

  # Return the indices of the children of the given node.
  private def children_of(node)
    return [2 * node + 1, 2 * node + 2].select{ |i| i < @nodes.count }
  end

  # Compute the index where the parent of the given node is stored
  private def parent_of(index)
    return index == 0 ? nil : (index-1) / 2
  end

  # Returns true if the given node has no child, i.e., is a leaf
  # node
  private def is_leaf(node)
    return children_of(node).empty?
  end

  # Returns true if the given node has no parent, i.e., is the root
  # of the heap
  private def is_root(node)
    return parent_of(node) == nil
  end

  # Swap the two given nodes
  private def swap(left, right)
    tmp = @nodes[left]
    @nodes[left] = @nodes[right]
    @nodes[right] = tmp
  end
end


class Node

  def initialize(item, priority)
    @item = item
    @priority = priority
  end

  def priority
    return @priority
  end

  def item
    return @item
  end

end


puts "Hi there"

