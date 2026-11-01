# Dynamic Programming (Java)

Recursion, memoization and dynamic programming, on the Fibonacci numbers and
on the knapsack problem, from the
[Dynamic Programming](https://franckc.folk.ntnu.no/idata2302/2026/optimization/dp/index.html) lecture notes.

- `Fibonacci.java` computes the Fibonacci numbers in three ways: `recursive`,
  `memoized` and `bottomUp`.
- `Knapsack.java` finds the best value we can put in a box, in the same
  three ways. It can also give the whole table filled by the bottom-up
  approach.
- `DynamicProgrammingTests.java` holds the tests, and `Testing.java` the few
  helpers they use. Both classes count their calls, so the tests can show
  how much work each approach does.

The code favours clarity over efficiency.

## Running the tests

```shell
$ make test
```
