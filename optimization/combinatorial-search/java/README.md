# Combinatorial Search (Java)

Searching by enumeration, with pruning, and with branch-and-bound, from the
[Combinatorial Search](https://franckc.folk.ntnu.no/idata2302/2026/optimization/exhaustive/index.html) lecture notes.

- `Problem.java` and `OptimizationProblem.java` describe what a problem must
  offer for the searches to work: configurations, the choices we can make,
  and, for optimization, a value, and a lower and an upper bound.
- `Search.java` has the searches: `exhaustive`, `withPruning`,
  `anySolution`, `branchAndBound` and `bestFirst`.
- `Knapsack.java` and `NQueens.java` are two problems to search.
- `SearchTests.java` holds the tests. `Testing.java` has the few helpers they
  use, and `Counting.java` lets them count the configurations a search looks at.

The code favours clarity over efficiency.

## Running the tests

```shell
$ make test
```
