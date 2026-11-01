# Heuristic Search (Java)

Random walk, hill climbing and simulated annealing, on the knapsack, from the
[Heuristic Search](https://franckc.folk.ntnu.no/idata2302/2026/optimization/heuristic/index.html) lecture notes.

- `SearchSpace.java` describes what a heuristic search needs: a solution to
  start from, the neighbours of a solution, and its fitness.
- `KnapsackSpace.java` is the knapsack as a search space: a solution is a set
  of items, and its neighbours are the sets we get by adding or removing one
  item.
- `Heuristics.java` has the searches: `randomWalk`, `hillClimbing` (and
  `climb`, which gives the solutions it goes through) and
  `simulatedAnnealing`.
- `HeuristicTests.java` holds the tests, and `Testing.java` the few helpers
  they use. The random searches use a seed, so the tests give the same results
  every time.

The code favours clarity over efficiency.

## Running the tests

```shell
$ make test
```
