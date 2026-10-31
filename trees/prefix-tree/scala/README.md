# Prefix Trees (Scala)

A prefix tree (also known as a trie), from the
[Prefix and Suffix Trees](https://franckc.folk.ntnu.no/idata2302/2026/trees/tries/index.html) lecture notes.

- `PrefixTree.scala` stores a set of words and offers `add`, `remove`,
  `contains` and `prefixSearch`.
- `PrefixTreeTests.scala` holds the tests.

The code favours clarity over efficiency: it is meant to show how a
prefix tree works, not to be fast.

## Running the tests

The code is written for Scala 3. With the `scala` command (Scala 3.10, or
[scala-cli](https://scala-cli.virtuslab.org)):

```shell
$ scala run PrefixTree.scala PrefixTreeTests.scala
```
