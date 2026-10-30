class PrefixTree:

  private var _root: Node = new Branch('ε', Map())

  def add(word: String): Unit =
    _root = _root.add(word)

  def contains(word: String): Boolean =
    _root.contains(word)

  def prefixSearch(prefix: String): Set[String] =
    _root.prefixSearch(prefix).map{ w => w.replaceAll("ε|⊥", "") }

  def remove(word: String): Unit =
    _root = (_root.remove(word) match {
      case None => new Branch('ε', Map())
      case Some(newRoot) => newRoot
    })

  def size: Int = words.size

  def words: Set[String] =
    _root.words.map { w => w.replaceAll("ε|⊥", "") }

  def debug(): Unit =
    _root.show(0)



abstract class Node(val symbol: Char):

  val children: Map[Char, Node]

  def contains(word: String): Boolean

  def add(word: String): Node

  def words: Set[String]

  def prefixSearch(prefix: String): Set[String]

  def remove(word: String): Option[Node]

  def show(level: Int): Unit



object Node:

  def fromWord(word: String): Node =
    if word.isEmpty
    then new Leaf('⊥')
    else {
      val node = Node.fromWord(word.tail)
      new Branch(word.head, Map(node.symbol -> node))
    }


class Leaf(symbol: Char) extends Node(symbol):

  val children: Map[Char, Node] = Map()

  def add(word: String): Node =
    throw new Error("TODO")

  def contains(word: String): Boolean =
    return word.isEmpty && symbol == '⊥'

  def words: Set[String] =
    Set(symbol.toString)

  def prefixSearch(prefix: String): Set[String] =
    if prefix.isEmpty
    then Set(symbol.toString)
    else Set.empty[String]

  def remove(word: String): Option[Node] =
    if word.isEmpty && symbol == '⊥'
    then
      None
    else
      Some(this)

  def show(level: Int): Unit =
    println(("│ " * level) + "└╴" + symbol.toString)



class Branch(symbol: Char, val children: Map[Char, Node]) extends Node(symbol):

  def add(word: String): Node =
    if word.isEmpty
    then
      new Branch(
        symbol,
        children ++ Map('⊥' -> new Leaf('⊥'))
      )
    else children.get(word.head) match {
      case None => {
        new Branch(
          symbol,
          children ++ Map(word.head -> Node.fromWord(word))
        )
      }
      case Some(n) => new Branch(
        symbol,
        children ++ Map(n.symbol -> n.add(word.tail))
      )
    }

  def contains(word: String): Boolean =
    children.get(word.head) match {
      case None => false
      case Some(n) => n.contains(word.tail)
    }

  def words: Set[String] =
    var words = Set.empty[String]
    for ((key, value) <- children) {
      words =  words.union(value.words.map{ w => symbol.toString + w})
    }
    words

  def prefixSearch(prefix: String): Set[String] =
    if prefix.isEmpty
    then
      words
    else
      children.get(prefix.head) match {
        case None => Set.empty[String]
        case Some(node) =>
          node
            .prefixSearch(prefix.tail)
            .map{ word => symbol.toString + word }
      }

  def remove(word: String): Option[Node] =
    if word.isEmpty
    then
      children.get('⊥') match {
        case None => Some(this)
        case Some(node) => if children.size == 1
                           then None
                           else Some(new Branch(symbol, children.removed('⊥')))
      }
    else
      children.get(word.head) match {
        case None => Some(this)
        case Some(node) =>
          node.remove(word.tail) match {
            case None => if children.size == 1
                         then None
                         else Some(new Branch(symbol, children.removed(node.symbol)))
            case Some(updatedChild) => Some(new Branch(symbol, children ++ Map(updatedChild.symbol -> updatedChild)))
          }
      }

  def show(level: Int): Unit =
    println(("│ " * level) + "└╴" + symbol.toString)
    children.foreach{ (key, value) => value.show(level+1) }


// --------------------------------------------------------------------------------
// Simple Tests


def test_leave_remove(): Unit =
  val leaf = new Leaf('⊥')
  assert(leaf.remove("").isEmpty)

def test_when_branch_remove_should_yield_something(): Unit = {
  val branch = new Branch('ε', Map(
    'a' -> new Branch('a', Map('⊥' -> new Leaf('⊥'))),
    'b' -> new Branch('b', Map('⊥' -> new Leaf('⊥')))
  ))
  branch.remove("a") match {
    case None => assert(false, "remove should yield something")
    case Some(node) => assert(
      node.children.size == 1,
      s"wrong number of children: Expected 1, got ${node.children.size}"
    )
  }
}

def test_when_branch_remove_should_yield_nothing(): Unit = {
  val branch = new Branch('ε', Map(
    'a' -> new Branch('a', Map('⊥' -> new Leaf('⊥'))),
  ))
  val updated = branch.remove("a")
  assert(updated.isEmpty, "remove should yield nothing!")
}

def test_when_branch_remove_when_no_matchg(): Unit = {
  val branch = new Branch('ε', Map(
    'a' -> new Branch('a', Map('⊥' -> new Leaf('⊥'))),
  ))
  val updated = branch.remove("b")
  branch.remove("a") match {
    case None => assert(false, "remove should yield something")
    case Some(node) => assert(
      node.children.size == 1,
      s"wrong number of children: Expected 1, got ${node.children.size}"
    )
  }
}

@main
def test =
  test_leave_remove()
  test_when_branch_remove_should_yield_something()

  val tree = new PrefixTree()
  println(tree.words)
  tree.add("Franck")
  println(tree.words)
  tree.add("Di")
  println(tree.words)
  tree.add("Francois")
  println(tree.words)
  tree.add("Didier")
  println(tree.words)
  tree.add("Diane")
  println(tree.prefixSearch("Di"))
  tree.debug()
  tree.remove("Franck")
  println(tree.words)
  tree.debug()

