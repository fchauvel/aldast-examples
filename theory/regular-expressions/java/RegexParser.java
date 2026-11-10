// One method per rule of the grammar. Each choice has its own line, and the
// symbols of the pattern are between quotes:
//
//   expression  :=  term
//   expression  :=  term '|' expression
//
//   term        :=  factor
//   term        :=  factor term
//
//   factor      :=  atom
//   factor      :=  atom '*'
//   factor      :=  atom '+'
//
//   atom        :=  a symbol
//   atom        :=  '(' expression ')'
//
// The rules call each other, and even themselves, just like the nodes of the
// tree contain each other. The lower a rule is, the tighter it binds: this is
// the operator precedence.
//
// The wildcard '+' brings no new kind of node: "E+" is read as "EE*".
class RegexParser {

    private final String pattern;
    private int position = 0;

    RegexParser(String pattern) {
        this.pattern = pattern;
    }

    Regex parse() {
        var result = expression();
        if (position < pattern.length()) {
            throw new IllegalArgumentException("Unexpected '" + peek() + "' at " + position);
        }
        return result;
    }

    private Regex expression() {
        var first = term();
        if (hasNext() && peek() == '|') {
            position++;
            return new Regex.Or(first, expression());
        }
        return first;
    }

    private Regex term() {
        var first = factor();
        if (hasNext() && peek() != '|' && peek() != ')') {
            return new Regex.Then(first, term());
        }
        return first;
    }

    private Regex factor() {
        var inner = atom();
        if (hasNext() && peek() == '*') {
            position++;
            return new Regex.Star(inner);
        }
        if (hasNext() && peek() == '+') {
            position++;
            return new Regex.Then(inner, new Regex.Star(inner));
        }
        return inner;
    }

    private Regex atom() {
        if (!hasNext()) {
            throw new IllegalArgumentException("Unexpected end of pattern");
        }
        var next = peek();
        position++;
        if (next == '(') {
            var result = expression();
            expect(')');
            return result;
        }
        if (next == '|' || next == '*' || next == '+' || next == ')') {
            throw new IllegalArgumentException("Unexpected '" + next + "' at " + (position - 1));
        }
        return new Regex.Symbol(next);
    }

    private void expect(char expected) {
        if (!hasNext() || peek() != expected) {
            throw new IllegalArgumentException("Expected '" + expected + "' at " + position);
        }
        position++;
    }

    private boolean hasNext() {
        return position < pattern.length();
    }

    private char peek() {
        return pattern.charAt(position);
    }
}
