import java.util.ArrayList;
import java.util.List;

// Place n queens on a chessboard with n rows and n columns, so that no queen
// can capture another one.
//
// A configuration gives the row of the queens in the first columns: We place
// the queens one column after the other.
public class NQueens implements Problem<List<Integer>> {

    private final int size;

    public NQueens(int size) {
        this.size = size;
    }

    public List<Integer> empty() {
        return List.of();
    }

    // The next queen can go in any row of the next column
    public List<List<Integer>> extensions(List<Integer> configuration) {
        var result = new ArrayList<List<Integer>>();
        for (int row = 0; row < size; row++) {
            var extended = new ArrayList<>(configuration);
            extended.add(row);
            result.add(extended);
        }
        return result;
    }

    public boolean isComplete(List<Integer> configuration) {
        return configuration.size() == size;
    }

    // No two queens are on the same row, or on the same diagonal
    // (they are never on the same column)
    public boolean isValid(List<Integer> configuration) {
        for (int first = 0; first < configuration.size(); first++) {
            for (int second = first + 1; second < configuration.size(); second++) {
                int rowGap = Math.abs(configuration.get(first) - configuration.get(second));
                int columnGap = second - first;
                if (rowGap == 0 || rowGap == columnGap) {
                    return false;
                }
            }
        }
        return true;
    }
}
