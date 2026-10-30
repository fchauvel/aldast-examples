# Sample code

from avl import AVLTree, AVLNode, Entry
from unittest import TestCase


class OrderedSetTests(TestCase):
    def test_empty_at_first(self):
        avl = AVLTree()
        self.assertEqual(0, avl.size)

    def test_insert_increases_size(self):
        tree = AVLTree()
        tree.insert(23, "test")
        self.assertEqual(1, tree.size)

    def test_non_members_have_not_been_inserted(self):
        tree = self._create_tree([(5, "five"), (6, "six"), (2, "two")])
        self.assertFalse(tree.contains(3))
        self.assertFalse(tree.contains(4))
        self.assertFalse(tree.contains(99))

    def test_inserted_items_are_members(self):
        tree = self._create_tree([(5, "five"), (6, "six"), (2, "two")])
        self.assertTrue(tree.contains(5))
        self.assertTrue(tree.contains(6))
        self.assertTrue(tree.contains(2))

    def test_inserted_items_are_available_by_key(self):
        tree = AVLTree()
        tree.insert(23, "twenty three")
        tree.insert(12, "twelve")
        twenty_three = tree.get(23)
        self.assertEqual(twenty_three, "twenty three")

    def test_duplicated_keys_are_rejected(self):
        tree = AVLTree()
        tree.insert(5, "five")
        tree.insert(6, "six")
        with self.assertRaises(ValueError):
            tree.insert(6, "another six")

    def test_unknown_keys_are_detected(self):
        tree = AVLTree()
        tree.insert(5, "five")
        tree.insert(6, "six")
        tree.insert(2, "two")
        with self.assertRaises(ValueError):
            tree.get(3)

    def test_items_are_traversed_in_order(self):
        tree = self._create_tree(
            [(5, "five"), (6, "six"), (2, "two"), (1, "one"), (4, "four"), (3, "three")]
        )
        self.assertEqual(
            list(tree.items()), ["one", "two", "three", "four", "five", "six"]
        )

    def test_predecessor_when_it_exists(self):
        tree = self._create_tree([(5, "five"), (7, "seven"), (6, "six")])
        self.assertEqual(tree.predecessor(7), (6, "six"))

    def test_predecessor_when_there_is_none(self):
        tree = self._create_tree([(5, "five"), (7, "seven"), (6, "six")])
        self.assertEqual(tree.predecessor(5), None)

    def test_successor_when_it_exists(self):
        tree = self._create_tree([(5, "five"), (7, "seven"), (6, "six")])
        self.assertEqual(tree.successor(6), (7, "seven"))

    def test_successor_when_there_is_none(self):
        tree = self._create_tree([(5, "five"), (7, "seven"), (6, "six")])
        self.assertEqual(tree.successor(7), None)

    def test_deleted_items_are_non_longer_members(self):
        tree = self._create_tree([(5, "five"), (7, "seven"), (6, "six")])
        tree.delete(6)
        self.assertFalse(tree.contains(6))

    def _create_tree(self, entries):
        tree = AVLTree()
        for key, item in entries:
            tree.insert(key, item)
        return tree


class AVLTreeTests(TestCase):
    def test_empty_tree_has_a_balance_of_zero(self):
        tree = AVLTree()
        self.assertEqual(tree.balance_factor, 0)

    def test_zero_balance_when_singleton(self):
        tree = AVLTree()
        tree.insert(12, "twelve")
        self.assertEqual(tree.balance_factor, 0)

    def test_balance_is_one_when_only_left_child(self):
        node = AVLNode(Entry(5, "five"), AVLNode(Entry(4, "four")))
        self.assertEqual(node.balance_factor, 1)

    def test_balance_is_minus_one_when_only_right_child(self):
        node = AVLNode(Entry(5, "five"), None, AVLNode(Entry(6, "six")))
        self.assertEqual(node.balance_factor, -1)

    def test_balance_is_two_with_only_left_descendants(self):
        node = AVLNode(
            Entry(5, "five"),
            AVLNode(Entry(4, "four"), AVLNode(Entry(3, "three")), None),
            None,
        )
        self.assertEqual(node.balance_factor, 2)

    def test_balance_is_minus_two_with_only_right_descendants(self):
        node = AVLNode(
            Entry(5, "five"),
            None,
            AVLNode(Entry(6, "six"), None, AVLNode(Entry(7, "seven"))),
        )
        self.assertEqual(node.balance_factor, -2)

    def test_right_right_rebalance(self):
        node = AVLNode(
            Entry(5, "five"),
            None,
            AVLNode(Entry(6, "six"), None, AVLNode(Entry(7, "seven"))),
        )
        self.assertEqual(node.balance_factor, -2)
        balanced = node.rebalance()
        self.assertEqual(balanced.balance_factor, 0)

    def test_left_left_rebalancing(self):
        node = AVLNode(
            Entry(5, "five"),
            AVLNode(Entry(4, "four"), AVLNode(Entry(3, "three")), None),
            None,
        )
        self.assertEqual(node.balance_factor, 2)
        balanced = node.rebalance()
        self.assertEqual(balanced.balance_factor, 0)

    def test_left_right_balancing(self):
        node = AVLNode(
            Entry(5, "five"),
            AVLNode(Entry(3, "three"), None, AVLNode(Entry(4, "four"))),
            None,
        )
        self.assertEqual(node.balance_factor, 2)
        balanced = node.rebalance()
        self.assertEqual(balanced.balance_factor, 0)

    def test_right_left_balancing(self):
        node = AVLNode(
            Entry(5, "five"),
            None,
            AVLNode(Entry(7, "seven"), AVLNode(Entry(6, "six")), None),
        )
        self.assertEqual(node.balance_factor, -2)
        balanced = node.rebalance()
        self.assertEqual(balanced.balance_factor, 0)
        self.assertEqual(
            repr(balanced),
            "AVLNode(Entry(6, six), AVLNode(Entry(5, five), None, None), AVLNode(Entry(7, seven), None, None))",
        )

    def test_deleting_the_minimum(self):
        node = AVLNode(
            Entry(6, "six"), AVLNode(Entry(5, "five")), AVLNode(Entry(7, "seven"))
        )
        node = node.delete(5)
        self.assertEqual(
            repr(node),
            "AVLNode(Entry(6, six), None, AVLNode(Entry(7, seven), None, None))",
        )

    def test_deleting_the_maximum(self):
        node = AVLNode(
            Entry(6, "six"), AVLNode(Entry(5, "five")), AVLNode(Entry(7, "seven"))
        )
        node = node.delete(7)
        self.assertEqual(
            repr(node),
            "AVLNode(Entry(6, six), AVLNode(Entry(5, five), None, None), None)",
        )

    def test_deleting_the_root(self):
        node = AVLNode(Entry(6, "six"), None, None)
        node = node.delete(6)
        self.assertIsNone(node)
