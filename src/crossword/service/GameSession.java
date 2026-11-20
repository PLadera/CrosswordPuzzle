package crossword.service;

import crossword.exception.ValidationException;
import crossword.model.Entry;
import crossword.model.Puzzle;

import java.util.ArrayList;
import java.util.List;

/** One play-through of a puzzle. Holds the answers and what the player typed. */
public class GameSession {
    public static final int SIZE = 10;

    private final char[][] solution = new char[SIZE][SIZE];   // '\0' = blocked cell
    private final char[][] entries = new char[SIZE][SIZE];    // '\0' = empty cell
    private boolean completed = false;

    /** Rules W3 and W4: checks the puzzle data fits and crosses correctly. */
    public GameSession(Puzzle puzzle) throws ValidationException {
        for (Entry e : puzzle.getEntries()) {
            String word = e.getWord();
            for (int i = 0; i < word.length(); i++) {
                int r = e.rowAt(i);
                int c = e.colAt(i);
                if (r < 0 || r >= SIZE || c < 0 || c >= SIZE) {
                    throw new ValidationException(
                            "Word " + word + " does not fit inside the grid.");
                }
                if (solution[r][c] != '\0' && solution[r][c] != word.charAt(i)) {
                    throw new ValidationException("Crossing words disagree at row "
                            + r + ", column " + c + ".");
                }
                solution[r][c] = word.charAt(i);
            }
        }
    }

    public boolean isPlayable(int row, int col) {
        return inGrid(row, col) && solution[row][col] != '\0';
    }

    /** Returns the letter typed in a cell, or '\0' if it is empty. */
    public char getEntry(int row, int col) {
        return entries[row][col];
    }

    public boolean isCompleted() {
        return completed;
    }

    /** Rules G1, G2: blocked cells refuse input, one letter A-Z, lowercase becomes uppercase. */
    public void setLetter(int row, int col, String input) throws ValidationException {
        if (completed) {
            throw new ValidationException("This puzzle is already complete.");
        }
        if (!isPlayable(row, col)) {
            throw new ValidationException("That cell is blocked.");
        }
        String text = (input == null) ? "" : input.trim();
        if (text.isEmpty()) {
            entries[row][col] = '\0';    // player deleted the letter
            return;
        }
        char letter = Character.toUpperCase(text.charAt(0));
        if (text.length() != 1 || letter < 'A' || letter > 'Z') {
            throw new ValidationException("Only one letter from A to Z is allowed.");
        }
        entries[row][col] = letter;
    }

    /** Rule G3: wrong letters only. Empty cells are left alone and nothing is erased. */
    public List<int[]> findWrongCells() {
        List<int[]> wrong = new ArrayList<>();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (solution[r][c] != '\0' && entries[r][c] != '\0'
                        && entries[r][c] != solution[r][c]) {
                    wrong.add(new int[] {r, c});
                }
            }
        }
        return wrong;
    }

    /** Rule G4: complete only when every playable cell is correct. */
    public boolean checkComplete() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (solution[r][c] != '\0' && entries[r][c] != solution[r][c]) {
                    return false;
                }
            }
        }
        completed = true;
        return true;
    }

    /** Rule G5: the screen asks "are you sure?" only if this is true. */
    public boolean hasEntries() {
        for (char[] row : entries) {
            for (char ch : row) {
                if (ch != '\0') {
                    return true;
                }
            }
        }
        return false;
    }

    public void restart() {
        for (char[] row : entries) {
            java.util.Arrays.fill(row, '\0');
        }
        completed = false;
    }

    private boolean inGrid(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }
}