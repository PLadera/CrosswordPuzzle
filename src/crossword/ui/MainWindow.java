package crossword.ui;

import crossword.exception.DatabaseException;
import crossword.exception.NotFoundException;
import crossword.exception.ValidationException;
import crossword.model.Difficulty;
import crossword.model.Puzzle;
import crossword.service.GameSession;
import crossword.service.PuzzleService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

/** The first screen: the list of puzzles, with a difficulty filter. */
public class MainWindow extends JFrame {
    private static final String ALL = "All";

    private final PuzzleService puzzleService = new PuzzleService();
    private final PuzzleTableModel tableModel = new PuzzleTableModel();
    private final JTable table = new JTable(tableModel);
    private final JComboBox<String> difficultyBox = new JComboBox<>();
    private final JButton refreshButton = new JButton("Refresh");
    private final JButton playButton = new JButton("Play");
    private final JButton leaderboardButton = new JButton("Leaderboard");
    private final JLabel statusLabel = new JLabel(" ");

    public MainWindow() {
        super("Crossword Puzzle");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        buildLayout();
        wireEvents();
        setSize(560, 420);
        setLocationRelativeTo(null);
        loadPuzzles();
    }

    private void buildLayout() {
        difficultyBox.addItem(ALL);
        for (Difficulty d : Difficulty.values()) {
            difficultyBox.addItem(d.name());
        }
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Difficulty:"));
        top.add(difficultyBox);
        top.add(refreshButton);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(leaderboardButton);
        buttons.add(playButton);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 4));
        bottom.add(statusLabel, BorderLayout.WEST);
        bottom.add(buttons, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        updateButtons();
    }

    private void wireEvents() {
        difficultyBox.addActionListener(e -> loadPuzzles());
        refreshButton.addActionListener(e -> loadPuzzles());
        table.getSelectionModel().addListSelectionListener(e -> updateButtons());
        playButton.addActionListener(e -> openPlayScreen());
        leaderboardButton.addActionListener(e -> {
            Puzzle p = getSelectedPuzzle();
            if (p != null) {
                new LeaderboardWindow(this, p).setVisible(true);
            }
        });
    }

    /** Loads puzzles in the background so the window never freezes. */
    private void loadPuzzles() {
        Difficulty selected = getSelectedDifficulty();
        setBusy(true, "Loading puzzles...");

        new SwingWorker<List<Puzzle>, Void>() {
            @Override
            protected List<Puzzle> doInBackground() throws DatabaseException {
                return puzzleService.getPuzzles(selected);    // slow part: runs off the screen thread
            }

            @Override
            protected void done() {                            // back on the screen thread
                try {
                    List<Puzzle> puzzles = get();
                    tableModel.setPuzzles(puzzles);
                    setBusy(false, puzzles.size() + " puzzle(s)");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    tableModel.setPuzzles(Collections.emptyList());
                    setBusy(false, "Loading was interrupted.");
                } catch (ExecutionException e) {
                    tableModel.setPuzzles(Collections.emptyList());
                    setBusy(false, "Could not load puzzles.");
                    showError(e.getCause());
                }
            }
        }.execute();
    }

    /** Loads the chosen puzzle (with its words) in the background, then opens the play screen. */
    private void openPlayScreen() {
        Puzzle chosen = getSelectedPuzzle();
        if (chosen == null) {
            return;
        }
        setBusy(true, "Opening puzzle...");

        new SwingWorker<Puzzle, Void>() {
            @Override
            protected Puzzle doInBackground() throws Exception {
                return puzzleService.getPuzzleForPlay(chosen.getId());
            }

            @Override
            protected void done() {
                try {
                    Puzzle puzzle = get();
                    GameSession session = new GameSession(puzzle);   // checks the puzzle data
                    setBusy(false, " ");
                    new PlayWindow(MainWindow.this, puzzle, session).setVisible(true);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    setBusy(false, "Opening was interrupted.");
                } catch (ExecutionException e) {
                    setBusy(false, "Could not open the puzzle.");
                    showError(e.getCause());
                } catch (ValidationException e) {
                    setBusy(false, "This puzzle has bad data.");
                    showError(e);
                }
            }
        }.execute();
    }

    private Difficulty getSelectedDifficulty() {
        String item = (String) difficultyBox.getSelectedItem();
        if (item == null || ALL.equals(item)) {
            return null;
        }
        return Difficulty.valueOf(item);
    }

    private Puzzle getSelectedPuzzle() {
        int row = table.getSelectedRow();
        return row < 0 ? null : tableModel.getPuzzleAt(row);
    }

    private void setBusy(boolean busy, String message) {
        statusLabel.setText(message);
        difficultyBox.setEnabled(!busy);
        refreshButton.setEnabled(!busy);
        updateButtons();
        if (busy) {
            playButton.setEnabled(false);
            leaderboardButton.setEnabled(false);
        }
    }

    private void updateButtons() {
        boolean hasSelection = table.getSelectedRow() >= 0;
        playButton.setEnabled(hasSelection);
        leaderboardButton.setEnabled(hasSelection);
    }

    private void showError(Throwable cause) {
        boolean friendly = cause instanceof DatabaseException
                || cause instanceof NotFoundException
                || cause instanceof ValidationException;
        String message = friendly
                ? cause.getMessage()
                : "Something unexpected went wrong: " + cause;
        JOptionPane.showMessageDialog(this, message, "Problem", JOptionPane.ERROR_MESSAGE);
    }
}