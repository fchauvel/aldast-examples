require "minitest/autorun"

require_relative "btree"


class TestBranch < Minitest::Test

  # def test_a_branch_cannot_have_less_than_half_capacity
  #   assert_raises (ArgumentError) do
  #     branch = Branch.new(2, [])
  #   end
  # end

  def test_a_branch_knows_its_minimum_key
    branch = Branch.new(2,
                        [ Leaf.with_entries(2, [[1, "Item 1"],
                                                [2, "Item 2"]]),
                          Leaf.with_entries(2, [[3, "Item 3"]])])
    assert branch.minimum_key == 1
  end

  def test_a_branch_knows_its_maximum_key
    branch = Branch.new(2,
                        [ Leaf.with_entries(2, [[1, "Item 1"],
                                                [2, "Item 2"]]),
                          Leaf.with_entries(2, [[3, "Item 3"]])])
    assert branch.maximum_key == 3
  end

  def test_a_branch_exposes_its_keys
    branch = Branch.new(2,
                        [ Leaf.with_entries(2, [[1, "Item 1"],
                                                [2, "Item 2"]]),
                          Leaf.with_entries(2, [[3, "Item 3"]])])
    assert branch.keys == [3]
  end

  def test_a_branch_exposes_items_from_its_leaves
    branch = Branch.new(2,
                        [ Leaf.with_entries(2, [[1, "Item 1"],
                                                [2, "Item 2"]]),
                          Leaf.with_entries(2, [[3, "Item 3"]])])
    match = branch.find(1)
    assert match == "Item 1"
  end

  def test_a_branch_is_not_overflowing_when_there_is_room
    branch = Branch.new(2,
                        [ Leaf.with_entries(2, [[1, "Item 1"],
                                                [2, "Item 2"]]),
                          Leaf.with_entries(2, [[3, "Item 3"]])])
    assert (not branch.is_overflowing)
  end

  def test_a_branch_is_not_overflowing_when_it_is_full
    branch = Branch.new(2,
                        [ Leaf.with_entries(2, [[1, "Item 1"],
                                                [2, "Item 2"]]),
                          Leaf.with_entries(2, [[3, "Item 3"],
                                                [4, "Item 4"]])])
    assert (not branch.is_overflowing)
  end

  def test_a_branch_is_overflowing_when_it_is_beyond_capacity
    branch = Branch.new(2,
                        [ Leaf.with_entries(2, [[1, "Item 1"],
                                                [2, "Item 2"]]),
                          Leaf.with_entries(2, [[3, "Item 3"],
                                                [4, "Item 4"]])])
    branch.insert(5, "Item 5")
    assert branch.is_overflowing
  end

  def test_a_branch_splits_in_two_branches
    branch = Branch.new(2,
                        [ Leaf.with_entries(2, [[1, "Item 1"],
                                                [2, "Item 2"]]),
                          Leaf.with_entries(2, [[3, "Item 3"],
                                                [4, "Item 4"]])])
    branch.insert(5, "Item 5")
    left, right = branch.split
    assert left.is_a? Branch
    assert right.is_a? Branch
  end

  def test_merge_when_underflowing
    branch = Branch.new(
      4,
      [
        Leaf.with_entries(4, [[1, "Item 1"],
                              [2, "Item 2"]]),

        Leaf.with_entries(4, [[3, "Item 3"],
                              [4, "Item 4"]])
      ])

    branch.remove(2)
    assert branch.size == 1
  end

  def test_not_merge_when_not_underflowing
    branch =  Branch.new(
      3,
      [
        Leaf.with_entries(3, [[1, "Item 1"],
                              [2, "Item 2"]]),
        Leaf.with_entries(3, [[3, "Item 3"],
                              [4, "Item 3"]])
      ])

    branch.remove(3)
    assert branch.size == 2
  end
end

class TestLeaf < Minitest::Test

  def test_empty_leaf_has_zero_size
    leaf = Leaf.empty(10)
    assert leaf.size == 0
  end

  def test_empty_leaf_is_not_overflowing
    leaf = Leaf.empty(10)
    assert (not leaf.is_overflowing)
  end


  def test_leaf_exposes_its_minimum_key
    leaf = Leaf.with_entries(2, [[1, "Item 1"],
                                 [2, "Item 2"],
                                 [3, "Item 3"]])
    assert leaf.minimum_key == 1
  end

  def test_leaf_exposes_its_maximum_key
    leaf = Leaf.with_entries(2, [[1, "Item 1"],
                                 [2, "Item 2"],
                                 [3, "Item 3"]])
    assert leaf.maximum_key == 3
  end

  def test_inserted_entries_are_available_by_key
    leaf = Leaf.empty(5)
    leaf.insert(1, "Test 1")
    item = leaf.find(1)
    assert item == "Test 1"
  end

  def test_insertion_increases_size
    leaf = Leaf.empty(2)
    leaf.insert(1, "Test")
    assert leaf.size == 1
  end

  def test_insert_should_not_overflow_when_there_is_room
    leaf = Leaf.empty(2)
    leaf.insert(1, "Test")
    assert (not leaf.is_overflowing)
  end

  def test_insert_should_not_overflow_when_capacity_reached
    leaf = Leaf.empty(2)
    leaf.insert(1, "Test 1")
    leaf.insert(2, "Test 2")
    assert (not leaf.is_overflowing)
  end

  def test_insert_should_overflow_when_capacity_is_exceeded
    leaf = Leaf.empty(2)
    leaf.insert(1, "Test 1")
    leaf.insert(2, "Test 2")
    leaf.insert(3, "Test 3")
    assert leaf.is_overflowing
  end

  def test_split_fails_is_not_is_not_overflowing
    leaf = Leaf.empty(2)
    assert_raises(RuntimeError) do
      leaf.split
    end
  end

  def test_split_returns_all_the_entries
    leaf = Leaf.empty(2)
    leaf.insert(1, "Test 1")
    leaf.insert(2, "Test 2")
    leaf.insert(3, "Test 3")
    left, right = leaf.split
    assert left.size == 1
    assert right.size == 2
  end




end


