// Each test states a property that a suffix tree must have, using a small
// example. Together, they describe how a suffix tree behaves.
//
// Run with: scala run . --main-class runSuffixTreeTests


@main def runSuffixTreeTests(): Unit =

  // -- Finding out whether a pattern occurs

  test("every piece of the text occurs in it"):
    val text = "banana"
    val tree = SuffixTree(text)
    for start <- 0 to text.length do
      for end <- start to text.length do
        assertTrue(tree.contains(text.substring(start, end)))

  test("the whole text occurs in itself"):
    val tree = SuffixTree("banana")
    assertTrue(tree.contains("banana"))

  test("something that is not a piece of the text does not occur in it"):
    val tree = SuffixTree("banana")
    assertFalse(tree.contains("nab"))
    assertFalse(tree.contains("bananas"))
    assertFalse(tree.contains("x"))

  test("the empty pattern occurs in any text, even an empty one"):
    assertTrue(SuffixTree("banana").contains(""))
    assertTrue(SuffixTree("").contains(""))

  test("nothing but the empty pattern occurs in an empty text"):
    assertFalse(SuffixTree("").contains("a"))


  // -- Finding where a pattern occurs

  test("a pattern that occurs once is found once"):
    val tree = SuffixTree("banana")
    assertEquals(tree.occurrences("ban"), List(0))
    assertEquals(tree.occurrences("banana"), List(0))

  test("a pattern that occurs several times is found everywhere"):
    val tree = SuffixTree("banana")
    assertEquals(tree.occurrences("na"), List(2, 4))
    assertEquals(tree.occurrences("a"), List(1, 3, 5))

  test("occurrences may overlap"):
    val tree = SuffixTree("banana")
    assertEquals(tree.occurrences("ana"), List(1, 3))

  test("a pattern that does not occur is found nowhere"):
    val tree = SuffixTree("banana")
    assertEquals(tree.occurrences("nab"), List())

  test("in a longer text"):
    val tree = SuffixTree("mississippi")
    assertEquals(tree.occurrences("issi"), List(1, 4))
    assertEquals(tree.occurrences("ss"), List(2, 5))
    assertEquals(tree.occurrences("pp"), List(8))

  test("the occurrences are exactly the places where the pattern starts"):
    val text = "mississippi"
    val tree = SuffixTree(text)
    for start <- 0 to text.length do
      for end <- start to text.length do
        val pattern = text.substring(start, end)
        val expected = (0 to text.length).filter(i => text.startsWith(pattern, i)).toList
        assertEquals(tree.occurrences(pattern), expected)

  println("All tests passed.")
