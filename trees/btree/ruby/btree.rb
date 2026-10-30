class Node

  def initialize(capacity)
    unless capacity > 1
      raise ArgumentError.new("Invalid capacity")
    end
    @capacity = capacity
  end

  def minimum_key
    raise "Abstract class"
  end

  def maximum_key
    raise
  end

  def size
    raise "Abstract class"
  end

  def find(key)
    raise "Abstract class"
  end

  def insert(key, item)
    raise "Abstract class"
  end

  def is_overflowing
    size > @capacity
  end

  def split
    raise "Abstract class"
  end

  def remove(key)
    raise "Abstract class"
  end

  def is_underflowing
    size < minimum_size
  end

  def minimum_size
    @capacity / 2
  end

  def has_extra
    return size > minimum_size
  end

  def merge_with(node)
    raise "Abstract class"
  end

  def take_maximum_from(node)
    raise "Abstract class"
  end

  def take_minimum_from(node)
    raise "Abstract class"
  end

end

class Branch < Node

  def initialize(capacity, branches)
    super(capacity)
    @branches = branches.sort_by{| b | b.minimum_key}
  end

  def branches
    return @branches
  end

  def minimum_key
    return @branches[0].minimum_key
  end

  def maximum_key
    return @branches.last.maximum_key
  end

  def size
    return @branches.count
  end

  def keys
    @branches.drop(1).map{|b| b.minimum_key}
  end

  def find(key)
    branch, index = pick_branch(key)
    return branch.find(key)
  end

  private
  def pick_branch(key)
    index = @branches.find_index{| b | b.minimum_key > key }
    if index.nil?
      return @branches.last, @branches.count - 1
    elsif index == 0
      return @branches.first, index
    else
      return @branches[index-1], index-1
    end
  end

  public
  def insert(key, item)
    branch, index = pick_branch(key)
    branch.insert(key, item)
    if branch.is_overflowing
      left, right = branch.split
      @branches[index,1] = [left, right]
    end
  end

  def split
    if not is_overflowing
      raise RuntimeError.new("Only split when overflowing!")
    end
    half = @branches.count / 2
    return Branch.new(@capacity, @branches.take(half)),
           Branch.new(@capacity, @branches.drop(half))
  end

  def remove(key)
    branch, index = pick_branch(key)
    branch.remove(key)
    if branch.is_underflowing
      repair_underflow(branch, index)
    end
  end

  private
  def repair_underflow(underflowing, index)
    left, right = siblings_of(index)
    if left and left.has_extra
      underflowing.take_maximum_from(left)
    elsif right and right.has_extra
      underflowing.take_minimum_from(right)
    elsif left
      @branches[index-1, 2] = left.merge_with(underflowing)
    elsif right
      @branches[index, 2] = underflowing.merge_with(right)
    else
      raise RuntimeError.new("Could not merge with or take from either side")
    end
  end

  private
  def siblings_of(index)
    if index == 0
      return nil, @branches[index+1]
    elsif index == @branches.count - 1
      return @branches[index-1], nil
    else
      return @branches[index-1], @branches[index+1]
    end
  end


  def take_minimum_from(node)
    unless node.is_a? Branch
      raise RuntimeError.new("Can only take from another Branch node!")
    end
    @branches.push(node.branches.shift)
  end

  def take_maximum_from(node)
    unless node.is_a? Branch
      raise RuntimeError.new("Can only take from another Branch node?")
    end
    @branches.insert(0, node.branches.pop)
  end

  def merge_with(other)
    return Branch.new(capacity, branches + other.branches)
  end

end

class Leaf < Node

  def self.empty(capacity)
    return Leaf.new(capacity, [])
  end

  def self.with_entries(capacity, entries)
    return Leaf.new(capacity, entries.map{|key, item| Entry.new(key, item)})
  end

  def initialize(capacity, entries)
    super(capacity)
    @entries = entries.sort_by{ |entry| entry.key}
  end

  def entries
    return @entries
  end

  def size
    return @entries.count
  end

  def minimum_key
    if size == 0
      raise RuntimeError.new("Empty leaf has no minimum key")
    end
    return @entries[0].key
  end

  def maximum_key
    if size == 0
      raise RuntimeError.new("Empty leaf has no maximum key")
    end
    return @entries.last.key
  end

  def find(key)
    match = @entries.find{|e| e.key == key}
    return match.item unless match.nil?
    return nil
  end

  def insert(key, item)
    index = 0
    inserted = false
    until inserted or index >= @entries.count
      entry = @entries[index]
      if entry.key > key
        @entries.insert(index, Entry.new(key, item))
        inserted = true
      end
      index += 1
    end
    @entries.push(Entry.new(key, item))
  end

  def split
    raise RuntimeError.new("Not overflowing") unless is_overflowing
    half = @entries.count / 2
    return Leaf.new(@capacity, @entries.take(half)),
           Leaf.new(@capacity, @entries.drop(half))
  end

  def remove(key)
    @entries.reject!{|e| e.key == key }
  end

  def merge_with(node)
    if node.is_underflowing or self.is_underflowing
      return Leaf.new(@capacity, @entries + node.entries)
    end
    raise ArgumentError.new("Neither node is underflowing")
  end

  def take_maximum_from(node)
    unless node.is_a?(Leaf)
      raise RuntimeError.new("A leaf can only take from another leaf")
    end
    @entries.insert(0, node.maximum_entry)
  end

  def take_minimum_from(node)
    unless node.is_a?(Leaf)
      raise RuntimeError.new("A leaf can only take from another leaf")
    end
    @entries.push(node.minimum_entry)
  end

end

Entry = Struct.new(:key, :item)

