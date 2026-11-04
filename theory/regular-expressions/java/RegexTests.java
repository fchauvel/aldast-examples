import java.util.ArrayList;
import java.util.List;

public class RegexTests extends Testing {

    static boolean matches(String pattern, String word) {
        return Automaton.from(Regex.parse(pattern)).accepts(word);
    }

    // All the words, up to the given length, over the given symbols
    static List<String> allWords(String symbols, int maxLength) {
        var words = new ArrayList<String>(List.of(""));
        for (int from = 0, length = 1; length <= maxLength; length++) {
            int to = words.size();
            for (int i = from; i < to; i++) {
                for (char symbol : symbols.toCharArray()) {
                    words.add(words.get(i) + symbol);
                }
            }
            from = to;
        }
        return words;
    }

    public static void main(String[] args) {

        // The tree

        test("concatenation binds tighter than the alternative", () -> {
            assertEquals(Regex.parse("ab|c"),
                new Regex.Or(
                    new Regex.Then(new Regex.Symbol('a'), new Regex.Symbol('b')),
                    new Regex.Symbol('c')));
        });

        test("repetition binds tighter than concatenation", () -> {
            assertEquals(Regex.parse("ab*"),
                new Regex.Then(
                    new Regex.Symbol('a'),
                    new Regex.Star(new Regex.Symbol('b'))));
        });

        test("parentheses override the precedence", () -> {
            assertEquals(Regex.parse("(ab)*"),
                new Regex.Star(
                    new Regex.Then(new Regex.Symbol('a'), new Regex.Symbol('b'))));
        });

        test("showing an expression gives back the pattern", () -> {
            for (var pattern : List.of("ab*|c", "(ab)*", "a(b|c)d", "(a|b)*abb")) {
                assertEquals(Regex.show(Regex.parse(pattern)), pattern);
            }
        });

        test("unbalanced parentheses are rejected", () -> {
            try {
                Regex.parse("(ab");
                throw new AssertionError("expected an error");
            } catch (IllegalArgumentException expected) {
            }
        });

        // Folding the tree

        test("a star can match the empty word, a symbol cannot", () -> {
            assertTrue(Regex.matchesEmptyWord(Regex.parse("a*")));
            assertFalse(Regex.matchesEmptyWord(Regex.parse("a")));
        });

        test("a concatenation matches the empty word when both parts do", () -> {
            assertTrue(Regex.matchesEmptyWord(Regex.parse("a*b*")));
            assertFalse(Regex.matchesEmptyWord(Regex.parse("a*b")));
        });

        test("an alternative matches the empty word when one side does", () -> {
            assertTrue(Regex.matchesEmptyWord(Regex.parse("a|b*")));
            assertFalse(Regex.matchesEmptyWord(Regex.parse("a|b")));
        });

        // The automaton

        test("a*|b accepts a run of a, or a single b", () -> {
            assertTrue(matches("a*|b", ""));
            assertTrue(matches("a*|b", "aaa"));
            assertTrue(matches("a*|b", "b"));
            assertFalse(matches("a*|b", "bb"));
            assertFalse(matches("a*|b", "ab"));
        });

        test("sheep say b, then any number of a, then !", () -> {
            assertTrue(matches("ba*!", "b!"));
            assertTrue(matches("ba*!", "baaa!"));
            assertFalse(matches("ba*!", "baaa"));
            assertFalse(matches("ba*!", "ab!"));
        });

        test("the + wildcard is a shorthand for a symbol, then a star", () -> {
            for (var word : allWords("ba!", 5)) {
                assertEquals(matches("baa+!", word), matches("baaa*!", word));
            }
        });

        test("the sheep language: baa*!", () -> {
            assertTrue(matches("baaa*!", "baa!"));
            assertTrue(matches("baaa*!", "baaaaa!"));
            assertFalse(matches("baaa*!", "ba!"));
            assertFalse(matches("baaa*!", "baa"));
        });

        test("(ab)* accepts ab repeated, and nothing else", () -> {
            assertTrue(matches("(ab)*", ""));
            assertTrue(matches("(ab)*", "ababab"));
            assertFalse(matches("(ab)*", "aba"));
            assertFalse(matches("(ab)*", "abba"));
        });

        test("an automaton accepts the empty word exactly when the fold says so", () -> {
            for (var pattern : List.of("a", "a*", "ab", "a*b*", "a|b*", "(a|b)*c", "(ab*)*")) {
                assertEquals(matches(pattern, ""), Regex.matchesEmptyWord(Regex.parse(pattern)));
            }
        });

        // Determinisation

        test("the deterministic automaton accepts the same words", () -> {
            for (var pattern : List.of("a*|b", "ab*|c", "(a|b)*abb", "(ab|a)*b")) {
                var original = Automaton.from(Regex.parse(pattern));
                var deterministic = new DeterministicAutomaton(original);
                for (var word : allWords("abc", 6)) {
                    assertEquals(deterministic.accepts(word), original.accepts(word));
                }
            }
        });

        test("the deterministic automaton can have fewer states than the original", () -> {
            var original = Automaton.from(Regex.parse("(a|b)*abb"));
            var deterministic = new DeterministicAutomaton(original);
            assertTrue(deterministic.stateCount() < original.stateCount());
        });

        test("the n-th symbol from the end needs 2^n states", () -> {
            for (int n = 1; n <= 8; n++) {
                var pattern = "(a|b)*a" + "(a|b)".repeat(n);
                var deterministic = new DeterministicAutomaton(Automaton.from(Regex.parse(pattern)));
                assertTrue(deterministic.stateCount() >= 1 << n);
            }
        });

        System.out.println("All tests passed");
    }
}
