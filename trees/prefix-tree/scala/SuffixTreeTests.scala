// Each test states a property that a suffix tree must have, using a small
// example. Together, they describe how a suffix tree behaves.
//
// Run with: scala run . --main-class runSuffixTreeTests


@main def runSuffixTreeTests(): Unit =

  // -- Fragments that occur

  test("a fragment that occurs once is found once"):
    val tree = SuffixTree("banana")
    assertEquals(tree.find("ban"), List(0))
    assertEquals(tree.find("nana"), List(2))
    assertEquals(tree.find("banana"), List(0))

  test("a fragment that occurs several times is found everywhere"):
    val tree = SuffixTree("banana")
    assertEquals(tree.find("na"), List(2, 4))
    assertEquals(tree.find("a"), List(1, 3, 5))

  test("occurrences may overlap"):
    val tree = SuffixTree("banana")
    assertEquals(tree.find("ana"), List(1, 3))

  test("every fragment of the text is found where it starts"):
    val text = "banana"
    val tree = SuffixTree(text)
    for start <- 0 to text.length do
      for end <- start to text.length do
        val fragment = text.substring(start, end)
        assertTrue(tree.find(fragment).contains(start))

  test("in a longer text"):
    val tree = SuffixTree("mississippi")
    assertEquals(tree.find("issi"), List(1, 4))
    assertEquals(tree.find("ss"), List(2, 5))
    assertEquals(tree.find("pp"), List(8))


  // -- Fragments that do not occur

  test("a fragment that does not occur is found nowhere"):
    val tree = SuffixTree("banana")
    assertEquals(tree.find("nab"), List())
    assertEquals(tree.find("bananas"), List())
    assertEquals(tree.find("x"), List())


  // -- The empty fragment and the empty text

  test("the empty fragment is found everywhere, including at the very end"):
    val tree = SuffixTree("banana")
    assertEquals(tree.find(""), List(0, 1, 2, 3, 4, 5, 6))

  test("only the empty fragment is found in an empty text"):
    val tree = SuffixTree("")
    assertEquals(tree.find(""), List(0))
    assertEquals(tree.find("a"), List())


  // -- All the properties, at once

  test("a fragment is found exactly where it starts in the text"):
    val text = "mississippi"
    val tree = SuffixTree(text)
    for start <- 0 to text.length do
      for end <- start to text.length do
        val fragment = text.substring(start, end)
        val expected = (0 to text.length).filter(i => text.startsWith(fragment, i)).toList
        assertEquals(tree.find(fragment), expected)

  println("All tests passed.")
