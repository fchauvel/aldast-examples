// Each test states a property that a prefix tree must have, using a small
// example. Together, they describe how a prefix tree behaves.
//
// Run with: scala run PrefixTree.scala PrefixTreeTests.scala


// The words of the figure of the lecture notes
val figureWords = Set(
  "apart", "apartment", "ape", "apear", "apple", "apply", "apricot", "april"
)


@main def runTests(): Unit =

  // -- Creating a tree

  test("a new tree contains no word"):
    val tree = PrefixTree()
    assertEquals(tree.words, Set())
    assertEquals(tree.size, 0)

  test("a new tree does not contain the empty word either"):
    val tree = PrefixTree()
    assertFalse(tree.contains(""))


  // -- Adding words

  test("a word that was added can be found"):
    val tree = treeOf("ape")
    assertTrue(tree.contains("ape"))

  test("every word that was added can be found"):
    val tree = treeOf(figureWords)
    for word <- figureWords do
      assertTrue(tree.contains(word))

  test("a word that was not added cannot be found"):
    val tree = treeOf("ape")
    assertFalse(tree.contains("apt"))
    assertFalse(tree.contains("zebra"))

  test("a prefix of a word is not a word, unless it was added"):
    val tree = treeOf("apple")
    assertFalse(tree.contains("app"))
    assertFalse(tree.contains("a"))
    assertFalse(tree.contains(""))

  test("an extension of a word is not a word, unless it was added"):
    val tree = treeOf("app")
    assertFalse(tree.contains("apple"))

  test("a word and its extension can both be found"):
    val tree = treeOf("app", "apple")
    assertTrue(tree.contains("app"))
    assertTrue(tree.contains("apple"))

  test("adding a word twice is the same as adding it once"):
    val once = treeOf("ape")
    val twice = treeOf("ape", "ape")
    assertEquals(twice.words, once.words)

  test("the empty word can be added"):
    val tree = treeOf("")
    assertTrue(tree.contains(""))
    assertEquals(tree.words, Set(""))

  test("the words of a tree are those that were added"):
    val tree = treeOf(figureWords)
    assertEquals(tree.words, figureWords)
    assertEquals(tree.size, 8)


  // -- Searching by prefix

  test("a prefix search finds the words that start with the prefix"):
    val tree = treeOf(figureWords)
    assertEquals(tree.prefixSearch("apa"), Set("apart", "apartment"))
    assertEquals(tree.prefixSearch("appl"), Set("apple", "apply"))
    assertEquals(tree.prefixSearch("apr"), Set("apricot", "april"))

  test("a word starts with itself"):
    val tree = treeOf("app", "apple")
    assertEquals(tree.prefixSearch("app"), Set("app", "apple"))
    assertEquals(tree.prefixSearch("apple"), Set("apple"))

  test("a prefix search finds nothing when no word starts with the prefix"):
    val tree = treeOf(figureWords)
    assertEquals(tree.prefixSearch("b"), Set())
    assertEquals(tree.prefixSearch("apex"), Set())
    assertEquals(tree.prefixSearch("applesauce"), Set())

  test("every word starts with the empty prefix"):
    val tree = treeOf(figureWords)
    assertEquals(tree.prefixSearch(""), figureWords)

  test("a prefix search finds exactly the words that start with the prefix"):
    val tree = treeOf(figureWords)
    for prefix <- prefixesOf(figureWords) do
      val expected = figureWords.filter(word => word.startsWith(prefix))
      assertEquals(tree.prefixSearch(prefix), expected)


  // -- Removing words

  test("a word that was removed cannot be found anymore"):
    val tree = treeOf("ape", "apple")
    tree.remove("ape")
    assertFalse(tree.contains("ape"))

  test("removing a word keeps the other words"):
    val tree = treeOf("ape", "apple")
    tree.remove("ape")
    assertTrue(tree.contains("apple"))

  test("removing a word keeps the words it is a prefix of"):
    val tree = treeOf("app", "apple")
    tree.remove("app")
    assertFalse(tree.contains("app"))
    assertTrue(tree.contains("apple"))

  test("removing a word keeps the words that are its prefix"):
    val tree = treeOf("app", "apple")
    tree.remove("apple")
    assertTrue(tree.contains("app"))
    assertFalse(tree.contains("apple"))

  test("removing a word that is not in the tree changes nothing"):
    val tree = treeOf("ape")
    tree.remove("apt")
    tree.remove("ap")
    tree.remove("apes")
    assertEquals(tree.words, Set("ape"))

  test("removing every word leaves an empty tree"):
    val tree = treeOf("ape", "apple")
    tree.remove("ape")
    tree.remove("apple")
    assertEquals(tree.words, Set())
    assertEquals(tree.prefixSearch(""), Set())

  test("removing any word leaves exactly the other words"):
    for removed <- figureWords do
      val tree = treeOf(figureWords)
      tree.remove(removed)
      assertEquals(tree.words, figureWords - removed)

  println("All tests passed.")


// -- Helpers to write the tests

def treeOf(words: String*): PrefixTree =
  treeOf(words.toSet)

def treeOf(words: Set[String]): PrefixTree =
  val tree = PrefixTree()
  for word <- words do
    tree.add(word)
  tree

// All the prefixes of all the given words, including the empty one
def prefixesOf(words: Set[String]): Set[String] =
  for
    word <- words
    length <- 0 to word.length
  yield word.take(length)

def assertTrue(condition: Boolean): Unit =
  if !condition then
    throw AssertionError("expected true, but got false")

def assertFalse(condition: Boolean): Unit =
  if condition then
    throw AssertionError("expected false, but got true")

def assertEquals(actual: Any, expected: Any): Unit =
  if actual != expected then
    throw AssertionError(s"expected $expected, but got $actual")

// Runs one test, and reports whether it passed
def test(description: String)(body: => Unit): Unit =
  try
    body
    println("ok      " + description)
  catch
    case failure: Throwable =>
      println("FAILED  " + description + ": " + failure)
      sys.exit(1)

