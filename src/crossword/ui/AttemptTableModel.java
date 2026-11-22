package crossword.ui;

import crossword.model.Attempt;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Feeds the leaderboard table with data. */
public class AttemptTableModel extends AbstractTableModel {
    private static final String[] COLUMNS = {"Rank", "Player", "Time", "Finished"};
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private List<Attempt> attempts = new ArrayList<>();

    public void setAttempts(List<Attempt> newAttempts) {
        attempts = new ArrayList<>(newAttempts);
        fireTableDataChanged();
    }

    public Attempt getAttemptAt(int row) {
        return attempts.get(row);
    }

    @Override
    public int getRowCount() {
        return attempts.size();
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
        Attempt a = attempts.get(row);
        switch (column) {
            case 0:
                return row + 1;
            case 1:
                return a.getPlayerName();
            case 2:
                return String.format("%02d:%02d", a.getSolveSeconds() / 60, a.getSolveSeconds() % 60);
            default:
                return a.getFinishedAt().format(DATE_FORMAT);
        }
    }
}