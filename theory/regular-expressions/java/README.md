# Regular expressions, automata and determinisation

Companion code for the lecture on regular expressions. It shows how trees,
recursion and graphs come together:

- `Regex` and `RegexParser`: a regular expression is a *tree*, and the parser has
  one method per rule of the grammar;
- `Automaton`: built from the tree by a *recursion*, and it is a *graph*;
- `DeterministicAutomaton`: a graph *search* over sets of states.

```
make test
```
