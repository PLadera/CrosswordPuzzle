package crossword.ui;

import crossword.exception.DatabaseException;
import crossword.exception.NotFoundException;
import crossword.model.Attempt;
import crossword.model.Puzzle;
import crossword.service.AttemptService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

/** Shows the best times for one puzzle. Scores can be deleted. */
public class LeaderboardWindow extends JDialog {
    private final Puzzle puzzle;
    private final AttemptService attemptService = new AttemptService();
    private final AttemptTableModel tableModel = new AttemptTableModel();
    private final JTable table = new JTable(tableModel);
    private final JButton refreshButton = new JButton("Refresh");
    private final JButton deleteButton = new JButton("Delete score");
    private final JButton closeButton = new JButton("Close");
    private final JLabel statusLabel = new JLabel(" ");

    public LeaderboardWindow(JFrame owner, Puzzle puzzle) {
        super(owner, "Leaderboard - " + puzzle.getTitle(), true);
        this.puzzle = puzzle;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildLayout();
        wireEvents();
        setSize(520, 380);
        setLocationRelativeTo(owner);
        loadScores();
    }

    private void buildLayout() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);

        JLabel title = new JLabel(puzzle.getTitle() + " (" + puzzle.getDifficulty() + ")");
        title.setBorder(BorderFactory.createEmptyBorder(8, 10, 4, 10));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(refreshButton);
        buttons.add(deleteButton);
        buttons.add(closeButton);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 4));
        bottom.add(statusLabel, BorderLayout.WEST);
        bottom.add(buttons, BorderLayout.EAST);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(480, 260));

        add(title, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        updateButtons();
    }

    private void wireEvents() {
        refreshButton.addActionListener(e -> loadScores());
        deleteButton.addActionListener(e -> deleteSelected());
        closeButton.addActionListener(e -> dispose());
        table.getSelectionModel().addListSelectionListener(e -> updateButtons());
    }

    /** Loads the scores in the background so the window never freezes. */
    private void loadScores() {
        setBusy(true, "Loading scores...");

        new SwingWorker<List<Attempt>, Void>() {
            @Override
            protected List<Attempt> doInBackground() throws DatabaseException {
                return attemptService.getLeaderboard(puzzle.getId());
            }

            @Override
            protected void done() {
                try {
                    List<Attempt> scores = get();
                    tableModel.setAttempts(scores);
                    setBusy(false, scores.isEmpty() ? "No scores yet. Be the first!"
                            : scores.size() + " score(s)");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    setBusy(false, "Loading was interrupted.");
                } catch (ExecutionException e) {
                    tableModel.setAttempts(Collections.emptyList());
                    setBusy(false, "Could not load scores.");
                    showError(e.getCause());
                }
            }
        }.execute();
    }

    /** Deletes the selected score, but only after the player confirms. */
    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        Attempt chosen = tableModel.getAttemptAt(row);
        int answer = JOptionPane.showConfirmDialog(this,
                "Delete the score of " + chosen.getPlayerName() + "?",
                "Delete score", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        setBusy(true, "Deleting...");

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                attemptService.deleteScore(chosen.getId());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    showError(e.getCause());
                }
                loadScores();        // show the list as it is now
            }
        }.execute();
    }

    private void setBusy(boolean busy, String message) {
        statusLabel.setText(message);
        refreshButton.setEnabled(!busy);
        updateButtons();
        if (busy) {
            deleteButton.setEnabled(false);
        }
    }

    private void updateButtons() {
        deleteButton.setEnabled(table.getSelectedRow() >= 0);
    }

    private void showError(Throwable cause) {
        boolean friendly = cause instanceof DatabaseException
                || cause instanceof NotFoundException;
        String message = friendly
                ? cause.getMessage()
                : "Something unexpected went wrong: " + cause;
        JOptionPane.showMessageDialog(this, message, "Problem", JOptionPane.ERROR_MESSAGE);
    }
}