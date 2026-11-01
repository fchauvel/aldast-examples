import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Each test states a property of recursion, memoization and dynamic
// programming, using a small example. Together, they describe how the three
// approaches compare.
//
// Run with: make test

public class DynamicProgrammingTests extends Testing {

    public static void main(String[] args) {

        // -- Fibonacci

        test("the first Fibonacci numbers are 0, 1, 1, 2, 3, 5, 8, 13", () -> {
            var expected = List.of(0L, 1L, 1L, 2L, 3L, 5L, 8L, 13L);
            var recursive = new ArrayList<Long>();
            var memoized = new ArrayList<Long>();
            var bottomUp = new ArrayList<Long>();
            for (int n = 0; n < expected.size(); n++) {
                recursive.add(new Fibonacci().recursive(n));
                memoized.add(new Fibonacci().memoized(n));
                bottomUp.add(new Fibonacci().bottomUp(n));
            }
            assertEquals(recursive, expected);
            assertEquals(memoized, expected);
            assertEquals(bottomUp, expected);
        });

        test("the three approaches give the same numbers", () -> {
            for (int n = 0; n <= 25; n++) {
                long expected = new Fibonacci().bottomUp(n);
                assertEquals(new Fibonacci().recursive(n), expected);
                assertEquals(new Fibonacci().memoized(n), expected);
            }
        });

        test("with memoization or a table, the 50th number is instantaneous", () -> {
            assertEquals(new Fibonacci().memoized(50), 12586269025L);
            assertEquals(new Fibonacci().bottomUp(50), 12586269025L);
        });

        test("the recursion computes the same numbers over and over", () -> {
            var fibonacci = new Fibonacci();
            fibonacci.recursive(5);
            assertEquals(fibonacci.calls, 15);       // F(3) twice, F(2) three times...

            var bigger = new Fibonacci();
            bigger.recursive(20);
            assertEquals(bigger.calls, 21891);       // for the 20th number
        });

        test("with memoization, the work grows in step with n", () -> {
            for (int n = 1; n <= 40; n++) {
                var fibonacci = new Fibonacci();
                fibonacci.memoized(n);
                assertTrue(fibonacci.calls <= 2 * n);
            }
        });


        // -- The knapsack

        test("the best we can carry in the box is worth 16 000", () -> {
            assertEquals(lecture().recursive(10), 16000);
            assertEquals(lecture().memoized(10), 16000);
            assertEquals(lecture().bottomUp(10), 16000);
        });

        test("the three approaches agree, whatever the box can carry", () -> {
            for (int capacity = 0; capacity <= 30; capacity++) {
                int expected = lecture().bottomUp(capacity);
                assertEquals(lecture().recursive(capacity), expected);
                assertEquals(lecture().memoized(capacity), expected);
            }
        });

        test("a box that carries nothing holds nothing", () -> {
            assertEquals(lecture().bottomUp(0), 0);
        });

        test("without items, there is nothing to gain", () -> {
            assertEquals(new Knapsack(List.of()).bottomUp(10), 0);
        });

        test("a bigger box never holds less", () -> {
            var table = lecture().table(20);
            for (int capacity = 0; capacity < 20; capacity++) {
                assertTrue(table[0][capacity] <= table[0][capacity + 1]);
            }
        });

        test("each cell of the table is the better of leaving the item or taking it", () -> {
            // With 10 kg: leave the music instruments (9 kg of room for the
            // others), or take them (8 kg left)
            var table = lecture().table(10);
            int leave = table[1][10];
            int take = 9000 + table[1][8];
            assertEquals(table[0][10], Math.max(leave, take));
        });

        test("the recursion solves the same problems over and over", () -> {
            var recursive = new Knapsack(manyItems());
            var memoized = new Knapsack(manyItems());
            recursive.recursive(15);
            memoized.memoized(15);
            assertTrue(memoized.calls * 100 < recursive.calls);
        });

        test("with memoization, we solve each problem once: at most items times capacities", () -> {
            var knapsack = new Knapsack(manyItems());
            knapsack.memoized(15);
            int problems = manyItems().size() * (15 + 1);
            assertTrue(knapsack.calls <= 2 * problems + 1);
        });

        System.out.println("All tests passed.");
    }


    // -- Examples used in the tests

    // The items of the lecture
    static Knapsack lecture() {
        return new Knapsack(List.of(
            new Knapsack.Item("music instruments", 2, 9000),
            new Knapsack.Item("laptop", 7, 3000),
            new Knapsack.Item("books", 3, 4000),
            new Knapsack.Item("cooking tools", 8, 7000)
        ));
    }

    // Twenty light items, of a few different weights: many ways to reach the
    // same capacity
    static List<Knapsack.Item> manyItems() {
        var items = new ArrayList<Knapsack.Item>();
        for (int i = 0; i < 20; i++) {
            items.add(new Knapsack.Item("item " + i, 1 + (i * 7) % 4, 10 + (i * 13) % 17));
        }
        return items;
    }
}
