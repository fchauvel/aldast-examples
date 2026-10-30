import { assertEquals, assertThrows } from "https://deno.land/std@0.210.0/assert/mod.ts";


type Order<T> = (left: T, right: T) => boolean;

class OrderedSet<Item> {

    private _order: Order<Item>;
    private _root: Node<Item> | null;

    constructor(
        order: Order<Item>,
        root: Node<Item> | null) {
        this._order = order;
        this._root = root;
    }

    cardinal (): number {
        if (this.isEmpty) {
            return 0;

        } else {
            return this._root!.size();

        }
    }

    get isEmpty () { return this._root == null }

    contains (item: Item): boolean {
        if (this.isEmpty) {
            return false;

        } else {
            return this._root!.contains(item);

        }
    }

    containsMany(...items: Array<Item>): boolean {
        for (const eachItem of items) {
            if (!this.contains(eachItem)) {
                return false;
            }
        }
        return true;
    }

    minimum (): Item {
        if (this.isEmpty) {
            throw new Error("Invalid state: An empty ordered set has no minimum.");

        }
        return this._root!.minimum();
    }

    maximum (): Item {
        if (this.isEmpty) {
            throw new Error("Invalid state: An empty ordered set has no maximum.");

        }
        return this._root!.maximum();
    }

    predecessorOf (item: Item): Item | undefined {
        if (this.isEmpty) {
            throw new Error(`Invalid state: Ordered set is empty.`)

        }
        return this._root!.predecessorOf(item)
    }

    successorOf (item: Item): Item | undefined {
        if (this.isEmpty) {
            throw new Error("Invalid state: Ordered set is empty.");

        }
        return this._root!.successorOf(item);
    }

    add(newItem: Item) {
        if (this.isEmpty) {
            this._root = new Node<Item>(this._order, newItem, null, null);

        } else {
            this._root!.add(newItem);

        }
    }

    addMany(...items: Array<Item>) {
        for (const eachItem of items) {
            this.add(eachItem);
        }
    }


    remove(item: Item) {
        if (this.isEmpty) {
            throw new Error("Invalid state: Ordered set is empty.");
        }
        this._root = this._root!.remove(item);
    }

}


class Node<Item> {

    private _order: Order<Item>;
    private _item: Item;
    private _left: Node<Item> | null;
    private _right: Node<Item> | null;

    constructor (
        order: Order<Item>,
        item: Item, left: Node<Item> | null,
        right: Node<Item> | null
    ) {
        this._order = order
        this._item = item;
        this._left = left;
        this._right = right;
    }

    contains(item: Item): boolean {
        if (this.isLargerThanOrEqualTo(item)) {
            if (this.equalsTo(item)) {
                return true;

            } else {
                if (this.hasLeft) {
                    return this._left!.contains(item);

                } else {
                    return false;

                }
            }
        } else {
            if (this.hasRight) {
                return this._right!.contains(item);

            } else {
                return false;

            }
        }
    }

    private equalsTo(other: Item) {
        return this._item == other;
    }

    minimum (): Item {
        if (this.hasLeft) {
            return this._left!.minimum();
        }
        return this._item;
    }

    maximum (): Item {
        if (this.hasRight) {
            return this._right!.maximum();

        }
        return this._item;
    }

    predecessorOf(item: Item): Item | undefined {
        if (this.isLargerThanOrEqualTo(item)) {
            if (this.equalsTo(item)) {
                if (this.hasLeft) {
                    return this._left!.maximum();

                } else {
                    return undefined;

                }

            } else {
                if (this.hasLeft) {
                    return this._left!.predecessorOf(item);

                } else {
                    throw new Error(`Invalid state: Could not find item '${item}'`);

                }
            }
        } else {
            if (this.hasRight) {
                const predecessor = this._right!.predecessorOf(item);
                return predecessor || this._item;

            } else {
                throw new Error(`Invalid state: Could not find item '${item}'`);

            }
        }
    }

    successorOf(item: Item): Item | undefined {
        if (this.isLargerThanOrEqualTo(item)) {
            if (this.equalsTo(item)) {
                if (this.hasRight) {
                    return this._right!.minimum();

                } else {
                    return undefined;

                }
            } else {
                if (this.hasLeft) {
                    const successor = this._left!.successorOf(item);
                    return successor || this._item;

                } else {
                    throw new Error(`Invalid state: Could not find item '${item}'`);

                }
            }
        } else {
            if (this.hasRight) {
                return this._right!.successorOf(item);

            } else {
                throw new Error(`Invalid state: Could not find item '${item}'`);

            }
        }
    }

    add(item: Item) {
        if (this.isLargerThanOrEqualTo(item)) {
            if (this.equalsTo(item)) return;
            if (this.hasLeft) {
                this._left!.add(item);

            } else {
                this._left = new Node<Item>(this._order, item, null, null);

            }

        } else {
            if (this.hasRight) {
                this._right!.add(item);

            } else {
                this._right = new Node<Item>(this._order, item, null, null)

            }
        }
    }

    remove (item: Item): Node<Item> | null {
        if (this.isLargerThanOrEqualTo(item)) {
            if (this.equalsTo(item)) {
                if (this.isLeaf) {
                    return null;

                } else if (this.hasOnlyLeft) {
                    return this._left;

                } else if (this.hasOnlyRight) {
                    return this._right;

                } else {
                    const predecessor = this.predecessorOf(item);
                    this.remove(predecessor as Item);
                    this._item = predecessor as Item;
                    return this;
                }
            } else {
                if (this.hasLeft) {
                    this._left = this._left!.remove(item);
                    return this;

                } else {
                    throw new Error(`Invalid state: Ordered set does not include '${item}'`);

                }
            }

        } else {
            if (this.hasRight) {
                this._right = this._right!.remove(item);
                return this;

            } else {
                throw new Error(`Invalid state: Ordered set does not include '${item}'`);

            }
        }
    }

    private get isLeaf (): boolean {
        return !this.hasLeft && !this.hasRight;
    }

    private get hasOnlyLeft (): boolean {
        return this.hasLeft && !this.hasRight;
    }

    private get hasOnlyRight (): boolean {
        return !this.hasLeft && this.hasRight;
    }

    private isLargerThanOrEqualTo(other: Item) {
        return this._order(other, this._item)
    }

    size (): number {
        let size = 1;
        if (this.hasLeft) {
            size += this._left!.size();
        }
        if (this.hasRight) {
            size += this._right!.size();
        }
        return size;
    }

    get hasRight () { return this._right != null }

    get hasLeft () { return this._left != null }

}


Deno.test("cardinal is zero on create", () => {
    const tree = new OrderedSet<number>((l,r) => l<=r, null);
    assertEquals(tree.cardinal(), 0)
});


Deno.test("cardinal increases after add", () => {
    const oset = new OrderedSet<number>((l,r) => l<=r, null);
    assertEquals(oset.cardinal(), 0)
    oset.add(25);
    assertEquals(oset.cardinal(), 1)
});

Deno.test("cardinal does not increases when adding duplicate", () => {
    const oset = new OrderedSet<number>((l,r) => l<=r, null);
    assertEquals(oset.cardinal(), 0)
    oset.add(25);
    oset.add(25);
    assertEquals(oset.cardinal(), 1)
});

Deno.test("contains reveals added and missing items", () => {
    const oset = new OrderedSet<number>((l,r) => l<=r, null);

    oset.addMany(25, 27, 12, 32, 12);

    assertEquals(oset.contains(25), true)
    assertEquals(oset.contains(27), true)
    assertEquals(oset.contains(32), true)
    assertEquals(oset.contains(12), true)

    assertEquals(oset.contains(13), false)
    assertEquals(oset.contains(26), false)
    assertEquals(oset.contains(0), false)
    assertEquals(oset.contains(999), false)
});


Deno.test("maximum returns the largest item", () => {
    const oset = new OrderedSet<number>((l,r) => l<=r, null);

    oset.addMany(23, 12, 58, 28);

    assertEquals(oset.maximum(), 58)
});

Deno.test("mminimum returns the smallest item", () => {
    const oset = new OrderedSet<number>((l,r) => l<=r, null);

    oset.addMany(23, 12, 58, 28);

    assertEquals(oset.minimum(), 12)
});

Deno.test("predecessor when the reference exists", () => {
    const oset = new OrderedSet<number>((l,r) => l<=r, null);

    oset.addMany(23, 12, 58, 28);

    assertEquals(oset.predecessorOf(23), 12)
    assertEquals(oset.predecessorOf(12), undefined)
    assertEquals(oset.predecessorOf(58), 28)
})

Deno.test("predecessor when the reference does not exists", () => {
    const oset = new OrderedSet<number>((l,r) => l<=r, null);

    oset.addMany(23, 12, 58, 28);

    assertThrows(() => oset.predecessorOf(0), Error)
    assertThrows(() => oset.predecessorOf(25), Error)
    assertThrows(() => oset.predecessorOf(99), Error)
})

Deno.test("successor when the reference exists", () => {
    const oset = new OrderedSet<number>((l,r) => l<=r, null);

    oset.addMany(23, 12, 58, 28);

    assertEquals(oset.successorOf(12), 23)
    assertEquals(oset.successorOf(23), 28)
    assertEquals(oset.successorOf(58), undefined)
})

Deno.test("successor when the reference does not exists", () => {
    const oset = new OrderedSet<number>((l,r) => l<=r, null);

    oset.addMany(23, 12, 58, 28)

    assertThrows(() => oset.successorOf(0), Error)
    assertThrows(() => oset.successorOf(25), Error)
    assertThrows(() => oset.successorOf(99), Error)
})


Deno.test("remove when the target does not exist", () => {
    const oset = new OrderedSet<number>((l,r) => l<=r, null);

    oset.addMany(23, 12, 58, 28)

    assertThrows(() => oset.remove(0), Error)

})

Deno.test("removed minimum are not available anymore", () => {
    const oset = new OrderedSet<number>((l,r) => l<=r, null);
    oset.addMany(23, 12, 58, 28)

    oset.remove(12)

    assertEquals(oset.contains(12), false)
    assertEquals(oset.containsMany(23, 28, 58), true);
})

Deno.test("removed root are not available anymore", () => {
    const oset = new OrderedSet<number>((l,r) => l<=r, null);
    oset.addMany(23, 12, 58, 28)

    oset.remove(23)

    assertEquals(oset.contains(23), false)
    assertEquals(oset.containsMany(12, 28, 58), true);
})

Deno.test("removed maximum is not available anymore", () => {
    const oset = new OrderedSet<number>((l,r) => l<=r, null);
    oset.addMany(23, 12, 58, 28)

    oset.remove(58)

    assertEquals(oset.contains(58), false)
    assertEquals(oset.containsMany(12, 23, 28), true);
})

Deno.test("remove singleton yields the empty set", () => {
    const oset = new OrderedSet<number>((l,r) => l<=r, null);
    oset.add(1);

    oset.remove(1);

    assertEquals(oset.contains(1), false);
    assertEquals(oset.cardinal(), 0);
});
