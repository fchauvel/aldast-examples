// Run with: scalac PrefixTree.scala PrefixTreeTests.scala && scala runTests

def check(condition: Boolean, description: String): Unit =
  if condition then
    println("ok     " + description)
  else
    throw new AssertionError("FAILED " + description)


def treeWith(words: String*): PrefixTree =
  val tree = PrefixTree()
  words.foreach(word => tree.add(word))
  tree


def testEmptyTree(): Unit =
  val tree = PrefixTree()
  check(tree.size == 0, "an empty tree has no words")
  check(!tree.contains(""), "an empty tree does not contain the empty word")
  check(!tree.contains("ape"), "an empty tree does not contain 'ape'")


def testAdd(): Unit =
  val tree = treeWith("ape")
  check(tree.contains("ape"), "a word that was added is found")
  check(tree.words == Set("ape"), "the words are the ones that were added")

def testAddTwice(): Unit =
  val tree = treeWith("ape", "ape")
  check(tree.size == 1, "adding a word twice changes nothing")

def testAddSeveral(): Unit =
  val tree = treeWith("ape", "apple", "april")
  check(tree.words == Set("ape", "apple", "april"), "all the words added are stored")


def testContainsPrefixOfAWord(): Unit =
  val tree = treeWith("apple")
  check(!tree.contains("app"), "a prefix of a word is not a word")
  check(!tree.contains(""), "the empty word is not stored")

def testContainsLongerWord(): Unit =
  val tree = treeWith("app")
  check(!tree.contains("apple"), "a longer word is not found")

def testContainsWordAndItsPrefix(): Unit =
  val tree = treeWith("app", "apple")
  check(tree.contains("app"), "a word that prefixes another is found")
  check(tree.contains("apple"), "the longer word is found too")

def testContainsOtherWord(): Unit =
  val tree = treeWith("ape")
  check(!tree.contains("apt"), "a word that differs by one letter is not found")
  check(!tree.contains("zebra"), "a word that shares nothing is not found")

def testEmptyWord(): Unit =
  val tree = treeWith("")
  check(tree.contains(""), "the empty word can be stored")
  check(tree.size == 1, "and counts as a word")


def testWordsStartingWith(): Unit =
  val tree = treeWith("apart", "apartment", "ape", "apple", "april", "bee")
  check(tree.prefixSearch("ap") == Set("apart", "apartment", "ape", "apple", "april"),
    "all the words that start with 'ap'")
  check(tree.prefixSearch("apa") == Set("apart", "apartment"),
    "all the words that start with 'apa'")
  check(tree.prefixSearch("ape") == Set("ape"),
    "a word starts with itself")
  check(tree.prefixSearch("apex") == Set(),
    "no word starts with 'apex'")
  check(tree.prefixSearch("") == tree.words,
    "every word starts with the empty prefix")


def testRemove(): Unit =
  val tree = treeWith("ape", "apple")
  tree.remove("ape")
  check(!tree.contains("ape"), "a word that was removed is no longer found")
  check(tree.contains("apple"), "the other words are still there")

def testRemoveMissingWord(): Unit =
  val tree = treeWith("ape")
  tree.remove("apt")
  tree.remove("a")
  tree.remove("apes")
  check(tree.words == Set("ape"), "removing a missing word changes nothing")

def testRemoveWordThatPrefixesAnother(): Unit =
  val tree = treeWith("app", "apple")
  tree.remove("app")
  check(!tree.contains("app"), "the shorter word is removed")
  check(tree.contains("apple"), "the longer word stays")

def testRemoveWordPrefixedByAnother(): Unit =
  val tree = treeWith("app", "apple")
  tree.remove("apple")
  check(tree.contains("app"), "the shorter word stays")
  check(!tree.contains("apple"), "the longer word is removed")

def testRemoveEveryWord(): Unit =
  val tree = treeWith("ape", "apple")
  tree.remove("ape")
  tree.remove("apple")
  check(tree.size == 0, "removing every word leaves an empty tree")
  check(tree.words == Set(), "and no words")


@main def runTests(): Unit =
  testEmptyTree()
  testAdd()
  testAddTwice()
  testAddSeveral()
  testContainsPrefixOfAWord()
  testContainsLongerWord()
  testContainsWordAndItsPrefix()
  testContainsOtherWord()
  testEmptyWord()
  testWordsStartingWith()
  testRemove()
  testRemoveMissingWord()
  testRemoveWordThatPrefixesAnother()
  testRemoveWordPrefixedByAnother()
  testRemoveEveryWord()
  println("All tests passed.")
