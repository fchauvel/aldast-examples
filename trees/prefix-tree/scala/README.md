# Prefix Trees and Suffix Trees (Scala)

A prefix tree (also known as a trie) and a suffix tree built on top of
it, from the [Prefix and Suffix Trees](https://franckc.folk.ntnu.no/idata2302/2026/trees/tries/index.html) lecture
notes.

- `PrefixTree.scala` stores a set of words and offers `add`, `remove`,
  `contains` and `prefixSearch`.
- `SuffixTree.scala` finds where a fragment occurs in a text. It stores
  all the suffixes of the text in a `PrefixTree`, and offers a single operation, `find`.
- `PrefixTreeTests.scala` and `SuffixTreeTests.scala` hold the tests, and
  `Testing.scala` the few helpers they use.

The code favours clarity over efficiency: it is meant to show how these
trees work, not to be fast.

## Running the tests

The code is written for Scala 3. With the `scala` command (Scala 3.10, or
[scala-cli](https://scala-cli.virtuslab.org)):

```shell
$ scala run . --main-class runPrefixTreeTests
$ scala run . --main-class runSuffixTreeTests
```
