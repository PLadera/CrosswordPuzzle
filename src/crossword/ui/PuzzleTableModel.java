package crossword.ui;

import crossword.model.Puzzle;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/** Feeds the puzzle list table with data. */
public class PuzzleTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {"No.", "Title", "Difficulty"};

    private List<Puzzle> puzzles = new ArrayList<>();

    public void setPuzzles(List<Puzzle> newPuzzles) {
        puzzles = new ArrayList<>(newPuzzles);
        fireTableDataChanged();
    }

    public Puzzle getPuzzleAt(int row) {
        return puzzles.get(row);
    }

    @Override
    public int getRowCount() {
        return puzzles.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        Puzzle p = puzzles.get(row);
        switch (column) {
            case 0:
                return row + 1;
            case 1:
                return p.getTitle();
            default:
                return p.getDifficulty();
        }
    }
}