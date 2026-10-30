class AVLTree:
    def __init__(self):
        self._size = 0
        self._root = None

    @property
    def balance_factor(self):
        if self.is_empty:
            return 0
        return self._root.balance_factor

    @property
    def is_empty(self):
        return self._size == 0

    @property
    def size(self) -> int:
        return self._size

    def insert(self, key, item):
        if self._root is None:
            self._root = AVLNode(Entry(key, item))
        else:
            self._root = self._root.insert(key, item)
        self._size += 1

    def get(self, key):
        if self._root is None:
            raise ValueError(f"No such key '{key}'")
        else:
            current_node = self._root
            while not current_node is None:
                if current_node.key == key:
                    return current_node.entry.item
                else:
                    current_node = current_node.branch_for(key)
            raise ValueError(f"No such key {key}")

    def predecessor(self, key):
        if self._root is not None:
            predecessor = self._root.predecessor(key)
            if predecessor is not None:
                return predecessor.entry.to_tuple
        return None

    def successor(self, key):
        if self._root is not None:
            successor = self._root.successor(key)
            if successor is not None:
                return successor.entry.to_tuple
        return None

    def contains(self, key):
        if self._root is not None:
            match = self._root.search(key)
            return match != None
        return False

    def delete(self, key):
        if self._root is None:
            raise ValueError(f"No such key {key}")
        self._root = self._root.delete(key)

    def items(self):
        if self._root is None:
            return
        current = self._root
        path = []
        while len(path) > 0 or current is not None:
            if current is not None:
                path.append(current)
                current = current.left if current.has_left else None
            else:
                current = path.pop()
                yield current.entry.item
                current = current.right if current.has_right else None

    def __repr__(self):
        return f"AVLTree({self._root})"


class Entry:
    def __init__(self, key, item):
        self._key = key
        self._item = item

    @property
    def key(self):
        return self._key

    @property
    def item(self):
        return self._item

    @property
    def to_tuple(self):
        return self._key, self._item

    def __repr__(self):
        return f"Entry({self._key}, {self._item})"


class AVLNode:
    def __init__(self, entry, left=None, right=None):
        if entry is None:
            raise ValueError("A node must have an entry")
        self._entry = entry
        self._left = left
        self._right = right

    @property
    def key(self):
        return self.entry.key

    @property
    def entry(self):
        return self._entry

    @property
    def has_left(self):
        return self._left is not None

    @property
    def left(self):
        if self._left is None:
            raise RuntimeError("Invalid state: No left branch")
        return self._left

    @left.setter
    def left(self, new_node):
        self._left = new_node

    @property
    def has_right(self):
        return self._right is not None

    @property
    def right(self):
        if self._right is None:
            raise RuntimeError(f"Invalid state: Node {self.key} has no right branch")
        return self._right

    @right.setter
    def right(self, new_node):
        self._right = new_node

    def branch_for(self, key):
        if key > self.key:
            return self._right
        else:
            return self._left

    def insert(self, key, item):
        if key == self.key:
            raise ValueError(f"Duplicate Key: {key}")
        elif key < self.key:
            if self.has_left:
                self.left = self.left.insert(key, item)
            else:
                self.left = AVLNode(Entry(key, item))
        else:
            if self.has_right:
                self.right = self.right.insert(key, item)
            else:
                self.right = AVLNode(Entry(key, item))
        return self.rebalance()

    @property
    def height(self):
        left_height = self._left.height if self.has_left else -1
        right_height = self._right.height if self.has_right else -1
        return 1 + max(left_height, right_height)

    @property
    def balance_factor(self):
        left_height = self._left.height if self.has_left else -1
        right_height = self._right.height if self.has_right else -1
        return left_height - right_height

    @property
    def is_balanced(self):
        return -2 < self.balance_factor < 2

    @property
    def is_unbalanced(self):
        return not self.is_balanced

    def rebalance(self):
        if self.is_balanced:
            return self
        if self.is_left_heavy and self.left.is_left_heavy:
            return self.rotate_right()
        elif self.is_left_heavy and self.left.is_right_heavy:
            self.left = self.left.rotate_left()
            return self.rotate_right()
        elif self.is_right_heavy and self.right.is_left_heavy:
            self.right = self.right.rotate_right()
            return self.rotate_left()
        else:
            return self.rotate_left()

    def rotate_right(self):
        new_root = self.left
        self.left = new_root.right if new_root.has_right else None
        new_root.right = self
        return new_root

    def rotate_left(self):
        new_root = self.right
        self.right = new_root.left if new_root.has_left else None
        new_root.left = self
        return new_root

    @property
    def is_right_heavy(self):
        return self.balance_factor < 0

    @property
    def is_left_heavy(self):
        return self.balance_factor > 0

    def predecessor(self, key):
        if self.key == key and self.has_left:
            return self.left.maximum
        elif key < self.key and self.has_left:
            return self.left.predecessor(key)
        elif key > self.key and self.has_right:
            return self.right.predecessor(key) or self
        else:
            return None

    def successor(self, key):
        if self.key == key and self.has_right:
            return self.right.minimum
        elif key < self.key and self.has_left:
            return self.left.successor(key) or self
        elif key > self.key and self.has_right:
            return self.right.successor(key)
        else:
            return None

    def __repr__(self):
        return f"AVLNode({self.entry}, {repr(self._left)}, {repr(self._right)})"

    @property
    def maximum(self):
        if self.has_right:
            return self.right.maximum
        else:
            return self

    @property
    def minimum(self):
        if self.has_left:
            return self.left.minimum
        else:
            return self

    def search(self, key):
        if self.key == key:
            return self
        elif key < self.key and self.has_left:
            return self.left.search(key)
        elif self.key < key and self.has_right:
            return self.right.search(key)
        else:
            return None

    def delete(self, key):
        if self.key == key:
            if self.has_left and self.has_right:
                predecessor = self.predecessor(key)
                self.delete(predecessor.key)
                predecessor.left = self.left if self.has_left else None
                predecessor.right = self.right if self.has_right else None
                return predecessor
            elif self.has_left:
                return self.left
            elif self.has_right:
                return self.right
            else:
                return None
        elif key < self.key and self.has_left:
            self.left = self.left.delete(key)
            return self.rebalance()
        elif self.key < key and self.has_right:
            self.right = self.right.delete(key)
            return self.rebalance()
        else:
            raise ValueError(f"No such key {key}")
