// A prefix tree stores a set of words. Each node carries a symbol:
// the root carries ROOT (the empty symbol), every other branch carries
// a letter, and each leaf carries END, which marks the end of a word.
//
// For example, the words "ape" and "apple" are stored as:
//
//   ε
//   └╴a
//     └╴p
//       ├╴e
//       │ └╴⊥
//       └╴p
//         └╴l
//           └╴e
//             └╴⊥
//
// The code favours clarity over efficiency.

val ROOT = 'ε'
val END = '⊥'


class PrefixTree:

  private var root: Node = Branch(ROOT, Map())

  def add(word: String): Unit =
    root = root.add(word)

  def remove(word: String): Unit =
    root = root.remove(word)

  def contains(word: String): Boolean =
    root.contains(word)

  def prefixSearch(prefix: String): Set[String] =
    root.prefixSearch(prefix)

  def words: Set[String] =
    root.words

  def size: Int =
    words.size

  def show(): Unit =
    root.show(0)


// In all the methods below, "word" is what remains to be processed,
// once the symbol of this node has been consumed.
abstract class Node(val symbol: Char):

  // The letters this node contributes to the words that go through it
  def text: String

  def contains(word: String): Boolean

  def add(word: String): Node

  def remove(word: String): Node

  // All the words stored below this node
  def words: Set[String]

  // All the words stored below this node, which start with the given prefix
  def prefixSearch(prefix: String): Set[String]

  // Is this node a dead end, that is, a branch without any child?
  def isDeadEnd: Boolean

  def show(depth: Int): Unit =
    println("  " * depth + symbol)


class Leaf extends Node(END):

  def text: String = ""

  def contains(word: String): Boolean =
    word.isEmpty

  def add(word: String): Node =
    throw new IllegalArgumentException("Nothing can be added below a leaf")

  def remove(word: String): Node =
    this

  def words: Set[String] =
    Set("")

  def prefixSearch(prefix: String): Set[String] =
    if prefix.isEmpty then Set("") else Set()

  def isDeadEnd: Boolean =
    false


class Branch(symbol: Char, val children: Map[Char, Node]) extends Node(symbol):

  def text: String = symbol.toString

  def contains(word: String): Boolean =
    if word.isEmpty then
      children.contains(END)
    else
      children.get(word.head) match
        case None => false
        case Some(child) => child.contains(word.tail)

  def add(word: String): Node =
    if word.isEmpty then
      Branch(symbol, children + (END -> Leaf()))
    else
      val child = children.getOrElse(word.head, Branch(word.head, Map()))
      Branch(symbol, children + (word.head -> child.add(word.tail)))

  def remove(word: String): Node =
    if word.isEmpty then
      Branch(symbol, children - END)
    else
      children.get(word.head) match
        case None => this
        case Some(child) =>
          val updatedChild = child.remove(word.tail)
          if updatedChild.isDeadEnd then
            Branch(symbol, children - word.head)
          else
            Branch(symbol, children + (word.head -> updatedChild))

  def words: Set[String] =
    val result = for
      child <- children.values
      rest <- child.words
    yield child.text + rest
    result.toSet

  def prefixSearch(prefix: String): Set[String] =
    if prefix.isEmpty then
      words
    else
      children.get(prefix.head) match
        case None => Set()
        case Some(child) =>
          child.prefixSearch(prefix.tail).map(rest => child.text + rest)

  def isDeadEnd: Boolean =
    children.isEmpty

  override def show(depth: Int): Unit =
    super.show(depth)
    children.values.foreach(child => child.show(depth + 1))
