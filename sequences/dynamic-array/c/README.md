# Fixed and Dynamic Arrays (C)

Two implementations of the same sequence interface (`sequence.h`),
from the [Dynamic Arrays](https://franckc.folk.ntnu.no/idata2302/2026/sequences/dynamic_arrays.html) lecture
notes:

- `fixed_sequence.c`: a fixed capacity of 100 items. Inserting
  beyond that makes the program abort.
- `dynamic_sequence.c`: an array that grows (and shrinks) when its
  load factor crosses a threshold. The initial capacity defaults to 4
  and can be changed with `-DINITIAL_CAPACITY=n`.

`blowout.c` inserts 200 items in a sequence, so that it fails with the
fixed implementation and succeeds with the dynamic one:

```shell
$ make
$ ./blowout-fixed      # aborts on the 101st insertion
$ ./blowout-dynamic    # prints "Done."
```
