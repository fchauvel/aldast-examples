import java.util.ArrayList;
import java.util.List;

// A maze is a grid of cells, which are open or walls. Where do we stand? That is
// a solution: a cell of the maze. Its neighbours are the open cells next to it,
// to the north, east, south and west. How good is the position? The closer to
// the exit, the better, counting the cells we would cross in a straight line,
// walls or not.
//
// The maze is written with a character per cell: '#' for a wall, 'S' for the
// start, 'E' for the exit, and '.' for the other open cells.
public class MazeSpace implements SearchSpace<MazeSpace.Cell> {

    public record Cell(int row, int column) {
    }

    private final String[] rows;
    private final Cell start;
    private final Cell exit;

    public MazeSpace(String... rows) {
        this.rows = rows;
        this.start = find('S');
        this.exit = find('E');
    }

    public Cell initial() {
        return start;
    }

    public Cell exit() {
        return exit;
    }

    // The open cells to the north, east, south and west
    public List<Cell> neighbours(Cell cell) {
        var result = new ArrayList<Cell>();
        int[][] moves = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
        for (var move : moves) {
            var next = new Cell(cell.row() + move[0], cell.column() + move[1]);
            if (isOpen(next)) {
                result.add(next);
            }
        }
        return result;
    }

    // Minus the distance to the exit, as the crow flies
    public int fitness(Cell cell) {
        return -(Math.abs(cell.row() - exit.row()) + Math.abs(cell.column() - exit.column()));
    }

    private boolean isOpen(Cell cell) {
        return cell.row() >= 0 && cell.row() < rows.length
            && cell.column() >= 0 && cell.column() < rows[cell.row()].length()
            && rows[cell.row()].charAt(cell.column()) != '#';
    }

    private Cell find(char symbol) {
        for (int row = 0; row < rows.length; row++) {
            int column = rows[row].indexOf(symbol);
            if (column >= 0) {
                return new Cell(row, column);
            }
        }
        throw new IllegalArgumentException("No " + symbol + " in the maze");
    }
}
