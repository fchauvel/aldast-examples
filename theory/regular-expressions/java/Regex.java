// A regular expression is a tree. There are only four kinds of nodes.
public sealed interface Regex {

    record Symbol(char symbol) implements Regex {}

    record Then(Regex first, Regex second) implements Regex {}

    record Or(Regex left, Regex right) implements Regex {}

    record Star(Regex inner) implements Regex {}

    // Can this expression match the empty word? We "fold" the tree: we
    // compute the answer for each child, and combine them.
    static boolean matchesEmptyWord(Regex expression) {
        return switch (expression) {
            case Symbol s -> false;
            case Then t -> matchesEmptyWord(t.first()) && matchesEmptyWord(t.second());
            case Or o -> matchesEmptyWord(o.left()) || matchesEmptyWord(o.right());
            case Star s -> true;
        };
    }

    // The way we write patterns back, with as few parentheses as possible
    static String show(Regex expression) {
        return switch (expression) {
            case Symbol s -> String.valueOf(s.symbol());
            case Then t -> showInside(t.first(), Or.class) + showInside(t.second(), Or.class);
            case Or o -> show(o.left()) + "|" + show(o.right());
            case Star s -> showInside(s.inner(), Or.class, Then.class, Star.class) + "*";
        };
    }

    private static String showInside(Regex child, Class<?>... needingParentheses) {
        for (var each : needingParentheses) {
            if (each.isInstance(child)) {
                return "(" + show(child) + ")";
            }
        }
        return show(child);
    }

    static Regex parse(String pattern) {
        return new RegexParser(pattern).parse();
    }
}
