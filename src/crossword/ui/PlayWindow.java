package crossword.ui;

import crossword.exception.DatabaseException;
import crossword.exception.NotFoundException;
import crossword.exception.ValidationException;
import crossword.model.Direction;
import crossword.model.Entry;
import crossword.model.Puzzle;
import crossword.service.AttemptService;
import crossword.service.GameSession;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;

/** The play screen: the 10x10 grid, the clues, a timer, and the buttons. */
public class PlayWindow extends JDialog {
    private static final int SIZE = GameSession.SIZE;
    private static final int CELL_PIXELS = 40;
    private static final Color BLOCKED = new Color(40, 40, 40);
    private static final Color NORMAL = Color.WHITE;
    private static final Color WRONG = new Color(255, 190, 190);

    private final Puzzle puzzle;
    private final GameSession session;
    private final AttemptService attemptService = new AttemptService();

    private final JPanel[][] cellPanels = new JPanel[SIZE][SIZE];
    private final JTextField[][] cells = new JTextField[SIZE][SIZE];
    private final JLabel timerLabel = new JLabel("Time: 00:00");
    private final JLabel statusLabel = new JLabel(" ");
    private final JButton checkButton = new JButton("Check");
    private final JButton restartButton = new JButton("Restart");
    private final JButton closeButton = new JButton("Close");
    private final JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private final JTextField nameField = new JTextField(14);
    private final JButton saveButton = new JButton("Save score");
    private final Timer timer;

    private int seconds = 0;
    private int finalSeconds = 0;

    public PlayWindow(JFrame owner, Puzzle puzzle, GameSession session) {
        super(owner, puzzle.getTitle(), true);
        this.puzzle = puzzle;
        this.session = session;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        timer = new Timer(1000, e -> {
            seconds++;
            timerLabel.setText("Time: " + formatTime(seconds));
        });

        buildLayout();
        wireEvents();
        pack();
        setLocationRelativeTo(owner);
        timer.start();
    }

    // ---------- building the screen ----------

    private void buildLayout() {
        Map<Integer, Integer> numbers = clueNumbers();

        JLabel title = new JLabel(puzzle.getTitle() + " (" + puzzle.getDifficulty() + ")");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(8, 10, 4, 10));
        top.add(title, BorderLayout.WEST);
        top.add(timerLabel, BorderLayout.EAST);

        JPanel grid = new JPanel(new GridLayout(SIZE, SIZE));
        grid.setPreferredSize(new Dimension(SIZE * CELL_PIXELS, SIZE * CELL_PIXELS));
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                grid.add(buildCell(r, c, numbers));
            }
        }
        JPanel gridHolder = new JPanel(new FlowLayout(FlowLayout.CENTER));
        gridHolder.add(grid);

        JScrollPane clues = buildCluePanel(numbers);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(checkButton);
        buttons.add(restartButton);
        buttons.add(closeButton);

        savePanel.add(new JLabel("Solved! Your name:"));
        savePanel.add(nameField);
        savePanel.add(saveButton);
        savePanel.setVisible(false);

        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.add(statusLabel, BorderLayout.WEST);
        bottomRow.add(buttons, BorderLayout.EAST);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 10, 6, 6));
        bottom.add(savePanel, BorderLayout.NORTH);
        bottom.add(bottomRow, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(gridHolder, BorderLayout.CENTER);
        add(clues, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
    }

    private JPanel buildCell(int row, int col, Map<Integer, Integer> numbers) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new LineBorder(Color.GRAY));
        cellPanels[row][col] = panel;

        if (!session.isPlayable(row, col)) {
            panel.setBackground(BLOCKED);
            return panel;
        }

        panel.setBackground(NORMAL);
        Integer number = numbers.get(row * SIZE + col);
        JLabel numberLabel = new JLabel(number == null ? " " : String.valueOf(number));
        numberLabel.setFont(numberLabel.getFont().deriveFont(9f));
        panel.add(numberLabel, BorderLayout.NORTH);

        JTextField field = new JTextField();
        field.setHorizontalAlignment(SwingConstants.CENTER);
        field.setFont(field.getFont().deriveFont(Font.BOLD, 18f));
        field.setBorder(null);
        field.setOpaque(false);
        ((javax.swing.text.AbstractDocument) field.getDocument())
                .setDocumentFilter(new LetterFilter(row, col));
        field.addKeyListener(new ArrowKeys(row, col));
        cells[row][col] = field;
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    /** Gives each word-start cell a number: left to right, top to bottom. */
    private Map<Integer, Integer> clueNumbers() {
        TreeSet<Integer> starts = new TreeSet<>();
        for (Entry e : puzzle.getEntries()) {
            starts.add(e.getStartRow() * SIZE + e.getStartCol());
        }
        Map<Integer, Integer> numbers = new HashMap<>();
        int next = 1;
        for (int key : starts) {
            numbers.put(key, next++);
        }
        return numbers;
    }

    private JScrollPane buildCluePanel(Map<Integer, Integer> numbers) {
        StringBuilder text = new StringBuilder();
        appendClues(text, "ACROSS", Direction.ACROSS, numbers);
        text.append("\n");
        appendClues(text, "DOWN", Direction.DOWN, numbers);

        JTextArea area = new JTextArea(text.toString());
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        area.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(270, SIZE * CELL_PIXELS));
        return scroll;
    }

    private void appendClues(StringBuilder text, String heading, Direction direction,
                             Map<Integer, Integer> numbers) {
        List<Entry> list = new ArrayList<>();
        for (Entry e : puzzle.getEntries()) {
            if (e.getDirection() == direction) {
                list.add(e);
            }
        }
        list.sort((a, b) -> numberOf(a, numbers) - numberOf(b, numbers));

        text.append(heading).append("\n");
        for (Entry e : list) {
            text.append(numberOf(e, numbers)).append(". ")
                    .append(e.getClue())
                    .append(" (").append(e.getWord().length()).append(")\n");
        }
    }

    private int numberOf(Entry e, Map<Integer, Integer> numbers) {
        return numbers.get(e.getStartRow() * SIZE + e.getStartCol());
    }

    // ---------- buttons ----------

    private void wireEvents() {
        checkButton.addActionListener(e -> checkAnswers());
        restartButton.addActionListener(e -> restart());
        closeButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> saveScore());
    }

    /** Rule G3: mark wrong letters in red. Nothing is erased. */
    private void checkAnswers() {
        paintAllNormal();
        List<int[]> wrong = session.findWrongCells();
        for (int[] cell : wrong) {
            setCellColor(cell[0], cell[1], WRONG);
        }
        statusLabel.setText(wrong.isEmpty()
                ? "No wrong letters so far!"
                : wrong.size() + " wrong letter(s), shown in red.");
    }

    /** Rule G5: ask first, but only if the player has typed something. */
    private void restart() {
        if (session.hasEntries()) {
            int answer = JOptionPane.showConfirmDialog(this,
                    "Restart this puzzle? All your letters will be cleared.",
                    "Restart", JOptionPane.YES_NO_OPTION);
            if (answer != JOptionPane.YES_OPTION) {
                return;
            }
        }
        session.restart();                       // clear the answers first
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (cells[r][c] != null) {
                    cells[r][c].setText("");
                    cells[r][c].setEditable(true);
                }
            }
        }
        paintAllNormal();
        savePanel.setVisible(false);
        saveButton.setEnabled(true);
        nameField.setEnabled(true);
        checkButton.setEnabled(true);
        statusLabel.setText("Puzzle restarted.");
        seconds = 0;
        timerLabel.setText("Time: " + formatTime(seconds));
        timer.restart();
        pack();
    }

    /** Called when every letter is correct (rule G4). */
    private void onSolved() {
        timer.stop();
        finalSeconds = seconds;
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (cells[r][c] != null) {
                    cells[r][c].setEditable(false);
                }
            }
        }
        paintAllNormal();
        checkButton.setEnabled(false);
        statusLabel.setText("Puzzle solved in " + formatTime(finalSeconds) + "!");
        savePanel.setVisible(true);
        pack();
        nameField.requestFocusInWindow();
        JOptionPane.showMessageDialog(this,
                "You solved it in " + formatTime(finalSeconds)
                        + "!\nType your name and click Save score.");
    }

    /** Saves the score in the background so the window never freezes. */
    private void saveScore() {
        String name = nameField.getText();
        saveButton.setEnabled(false);        // stops double-clicking
        nameField.setEnabled(false);
        statusLabel.setText("Saving...");

        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                return attemptService.saveScore(puzzle.getId(), name, finalSeconds);
            }

            @Override
            protected void done() {
                try {
                    get();
                    statusLabel.setText("Score saved!");
                    JOptionPane.showMessageDialog(PlayWindow.this,
                            "Your score was saved. Check the Leaderboard!");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    saveButton.setEnabled(true);
                    nameField.setEnabled(true);
                } catch (ExecutionException e) {
                    saveButton.setEnabled(true);       // let the player try again
                    nameField.setEnabled(true);
                    statusLabel.setText("Score not saved.");
                    showError(e.getCause());
                }
            }
        }.execute();
    }

    private void showError(Throwable cause) {
        boolean friendly = cause instanceof ValidationException
                || cause instanceof NotFoundException
                || cause instanceof DatabaseException;
        String message = friendly
                ? cause.getMessage()
                : "Something unexpected went wrong: " + cause;
        JOptionPane.showMessageDialog(this, message, "Problem", JOptionPane.ERROR_MESSAGE);
    }

    // ---------- small helpers ----------

    private void paintAllNormal() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (cells[r][c] != null) {
                    setCellColor(r, c, NORMAL);
                }
            }
        }
    }

    private void setCellColor(int row, int col, Color color) {
        cellPanels[row][col].setBackground(color);
    }

    private static String formatTime(int totalSeconds) {
        return String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);
    }

    @Override
    public void dispose() {
        timer.stop();
        super.dispose();
    }

    // ---------- typing rules ----------

    /** Sits between the keyboard and one cell. The GameSession decides what is allowed. */
    private class LetterFilter extends DocumentFilter {
        private final int row;
        private final int col;

        LetterFilter(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text,
                            AttributeSet attrs) throws BadLocationException {
            if (text == null || text.isEmpty()) {
                clearCell(fb);
                return;
            }
            try {
                session.setLetter(row, col, text);                  // rules G1 and G2
                fb.replace(0, fb.getDocument().getLength(), text.trim().toUpperCase(), attrs);
                setCellColor(row, col, NORMAL);
                if (session.checkComplete()) {                      // rule G4
                    SwingUtilities.invokeLater(PlayWindow.this::onSolved);
                } else {
                    SwingUtilities.invokeLater(() -> cells[row][col].transferFocus());
                }
            } catch (ValidationException e) {
                Toolkit.getDefaultToolkit().beep();                 // bad input: ignore it
            }
        }

        @Override
        public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
            clearCell(fb);
        }

        private void clearCell(FilterBypass fb) throws BadLocationException {
            try {
                session.setLetter(row, col, "");
                fb.replace(0, fb.getDocument().getLength(), "", null);
                setCellColor(row, col, NORMAL);
            } catch (ValidationException e) {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }

    /** Lets the arrow keys jump to the next playable cell. */
    private class ArrowKeys extends KeyAdapter {
        private final int row;
        private final int col;

        ArrowKeys(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int dr = 0;
            int dc = 0;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:    dr = -1; break;
                case KeyEvent.VK_DOWN:  dr = 1;  break;
                case KeyEvent.VK_LEFT:  dc = -1; break;
                case KeyEvent.VK_RIGHT: dc = 1;  break;
                default: return;
            }
            int r = row + dr;
            int c = col + dc;
            while (r >= 0 && r < SIZE && c >= 0 && c < SIZE) {
                if (cells[r][c] != null) {
                    cells[r][c].requestFocusInWindow();
                    return;
                }
                r += dr;
                c += dc;
            }
        }
    }
}