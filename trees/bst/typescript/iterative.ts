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
        if (this.isEmpty) return 0;
        let cardinal = 0;
        const stack: Array<Node<Item>> = [this._root!]
        while (stack.length > 0) {
            const current = stack.pop();
            cardinal += 1;
            for (const eachChild of current!.children) {
                stack.push(eachChild)
            }
        }
        return cardinal;
    }

    get isEmpty () { return this._root == null }

    contains (item: Item): boolean {
        if (this.isEmpty) return false;
        const path = this.findPathTo(item);
        return path[path.length-1]!.item == item
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
        return this.minimumFrom(this._root!);
    }

    minimumFrom(root: Node<Item>) {
        let node = root;
        while (node != null && node.hasLeft) {
            node = node.left!;
        }
        return node!.item;
    }

    maximum (): Item {
        if (this.isEmpty) {
            throw new Error("Invalid state: An empty ordered set has no maximum.");

        }
        return this.maximumFrom(this._root!);
    }

    private maximumFrom(root: Node<Item>) {
        let node = root;
        while (node != null && node.hasRight) {
            node = node.right!;
        }
        return node!.item;
    }

    predecessorOf (item: Item): Item | undefined {
        if (this.isEmpty) {
            throw new Error(`Invalid state: Ordered set is empty.`)

        }
        const path = this.findPathTo(item);
        if (path[path.length-1].item != item) {
            throw new Error(`Invalid state: Item '${item}' is not a member.`);

        } else {
            const node = path.pop();
            if (node!.hasLeft) {
                return this.maximumFrom(node!.left!);

            } else {
                return this.firstLesserAncestor(path, item);

            }
        }
    }

    private findPathTo(target: Item): Array<Node<Item>> {
        const path: Array<Node<Item>> = [];
        if (this.isEmpty) return path;
        let node = this._root;
        while (node != null) {
            path.push(node);
            if (this._order(node.item, target)) {
                if (node.item == target) {
                    break;
                } else {
                    node = node.right;
                }
            } else {
                node = node.left;
            }
        }
        return path;
    }

    private firstLesserAncestor(path: Array<Node<Item>>, item: Item): Item | undefined {
        while (path.length > 0) {
            const parent = path.pop();
            if (this._order(parent!.item, item)) {
                return parent!.item;
            }
        }
        return undefined;
    }

    successorOf (item: Item): Item | undefined {
        if (this.isEmpty) {
            throw new Error(`Invalid state: Ordered set is empty.`)

        }
        const path = this.findPathTo(item);
        if (path[path.length-1].item != item) {
            throw new Error(`Invalid state: Item '${item}' is not a member.`);

        } else {
            const node = path.pop();
            if (node!.hasRight) {
                return this.minimumFrom(node!.right!);

            } else {
                return this.firstLargerAncestor(path, item);

            }
        }
    }

    private firstLargerAncestor(path: Array<Node<Item>>, item: Item): Item | undefined {
        while (path.length > 0) {
            const parent = path.pop();
            if (this._order(item, parent!.item)) {
                return parent!.item;
            }
        }
        return undefined;
    }

    add (newItem: Item) {
        if (this.isEmpty) {
            this._root = new Node<Item>(this._order, newItem, null, null);

        } else {
            const path = this.findPathTo(newItem);
            const parent = path[path.length-1];
            if (this._order(newItem, parent.item)) {
                if (parent.item != newItem) {
                    parent.left = new Node<Item>(this._order, newItem, null, null)
                }
            } else {
                parent.right = new Node<Item>(this._order, newItem, null, null);

            }
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

        } else {
            const path = this.findPathTo(item);
            const node = path.pop()
            if (node!.item != item) {
                throw new Error(`Invalid state: No item ${item}`);

            } else {
                const parent = path.pop()
                if (node!.children.length < 2) {
                    if (parent) {
                        parent.drop(node!)

                    } else {
                        this._root = node!.children[0];

                    }

                } else {
                    const predecessor = this.predecessorOf(node!.item);
                    this.remove(predecessor!);
                    node!.item = predecessor!;

                }
            }
        }
    }

    toString(): string {
        let result = ""
        if (this.isEmpty) return result;
        return this._root!.toString();
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

    get item () {
        return this._item
    }

    set item(item: Item) {
        this._item = item;
    }

    set left (node: Node<Item> | null) {
        this._left = node;
    }

    get left () {
        return this._left;
    }

    set right (node: Node<Item> | null) {
        this._right = node;
    }

    get right () {
        return this._right;
    }

    get isLeaf(): boolean {
        return this._right == null && this._left == null;
    }

    drop (child: Node<Item>) {
        const descendant = child.isLeaf ? null : child.children[0];
        if (this._left == child) {
            this._left = descendant;
        }
        if (this._right == child) {
            this._right = descendant;
        }
    }

    get children (): Array<Node<Item>> {
        const children: Array<Node<Item>> = [];
        if (this.hasLeft) children.push(this._left!);
        if (this.hasRight) children.push(this._right!);
        return children;
    }

    get hasRight () { return this._right != null }

    get hasLeft () { return this._left != null }

    toString(): string {
        if (this.isLeaf)
            return `${this.item}`;
        const left = this.hasLeft ? this._left!.toString() : "?";
        const right = this.hasRight ? this._right!.toString() : "?";
        return `(${this.item}: ${left}, ${right})`;
    }

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
