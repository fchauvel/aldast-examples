

require "minitest/autorun"

require_relative "heap"


class TestMinimumHeap < Minitest::Test

  def test_new_queue_are_empty
    heap = MinimumHeap.new()
    assert heap.size == 0
  end

  def test_inserted_items_are_contained
    heap = MinimumHeap.new()

    heap.enqueue("Franck", 5)

    assert heap.contains("Franck", 5)
    assert heap.size == 1
  end

  def test_size_increases_by_one_with_each_new_item
    heap = MinimumHeap.new

    heap.enqueue("Task 1", 1)
    heap.enqueue("Task 2", 2)
    heap.enqueue("Task 3", 3)

    assert heap.size == 3
  end

  def test_peek_is_not_available_on_an_empty_queue
    heap = MinimumHeap.new()
    assert_raises RuntimeError do
      heap.peek()
    end
  end

  def test_peek_yields_the_minimum_when_size_is_one
    heap = MinimumHeap.new()
    heap.enqueue("Task B", 1)

    assert heap.peek.item == "Task B"
  end

  def test_peek_yields_the_minimum_when_size_is_two
    heap = MinimumHeap.new()
    heap.enqueue("Task A", 3)
    heap.enqueue("Task B", 1)

    assert heap.peek.item == "Task B"
  end

  def test_peek_yields_the_minimum_when_size_is_three
    heap = MinimumHeap.new()
    heap.enqueue("Task A", 3)
    heap.enqueue("Task B", 1)
    heap.enqueue("Task C", 5)

    assert heap.peek.item == "Task B"
  end

  def test_dequeue_is_not_available_on_an_empty_queue
    heap = MinimumHeap.new

    assert_raises RuntimeError do
      heap.dequeue
    end
  end

  def test_dequeued_items_are_not_member_anymore
    heap = MinimumHeap.new
    heap.enqueue("Task 1", 1)
    assert heap.size == 1

    heap.dequeue

    assert (not heap.contains("Task 1", 1))
  end

  def test_dequeued_items_are_still_member_when_there_are_duplicates
    heap = MinimumHeap.new
    heap.enqueue("Task 1", 1)
    heap.enqueue("Task 1", 1)
    assert heap.size == 2

    heap.dequeue

    assert heap.contains("Task 1", 1)
  end

  def test_dequeue_yields_the_lowest_priority_item_with_one_item
    heap = MinimumHeap.new
    heap.enqueue("Task 1", 1)

    minimum = heap.dequeue

    assert (not heap.contains("Task 1", 1))
  end

  def test_dequeue_yields_the_lowest_priority_item_with_two_items
    heap = MinimumHeap.new
    heap.enqueue("Task 1", 1)
    heap.enqueue("Task 2", 0.5)

    minimum = heap.dequeue

    assert (not heap.contains("Task 2", 0.5))
  end

  def test_dequeue_yields_the_lowest_priority_item_with_three_items
    heap = MinimumHeap.new
    heap.enqueue("Task 1", 1)
    heap.enqueue("Task 2", 0.5)
    heap.enqueue("Task 3", 5)

    minimum = heap.dequeue

    assert (not heap.contains("Task 2", 0.5))
  end

  def test_dequeue_decreases_the_queue_size
    heap = MinimumHeap.new
    heap.enqueue("Task 1", 3)
    heap.enqueue("Task 2", 2)
    heap.enqueue("Task 3", 4)
    assert heap.size == 3

    heap.dequeue

    assert heap.size == 2
  end

  def test_heapsort_with_ascending_sequence
    sorted = MinimumHeap.heapsort([["Item 9", 9],
                                 ["Item 3", 3],
                                 ["Item 1", 1],
                                 ["Item 4", 4],
                                 ["Item 2", 2]
                                ])
    for index in 0 .. sorted.size-1
      assert sorted[index].priority < sorted[index+1].priority
    end
  end
end
