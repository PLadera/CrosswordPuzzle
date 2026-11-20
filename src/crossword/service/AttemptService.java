package crossword.service;

import crossword.dao.AttemptDao;
import crossword.dao.PuzzleDao;
import crossword.exception.DatabaseException;
import crossword.exception.NotFoundException;
import crossword.exception.ValidationException;
import crossword.model.Attempt;

import java.util.List;

public class AttemptService {
    private static final int MAX_NAME_LENGTH = 30;

    private final AttemptDao attemptDao = new AttemptDao();
    private final PuzzleDao puzzleDao = new PuzzleDao();

    /** Rule S1: needs a non-blank name and a time of 0 or more. */
    public int saveScore(int puzzleId, String playerName, int solveSeconds)
            throws ValidationException, NotFoundException, DatabaseException {
        if (playerName == null || playerName.trim().isEmpty()) {
            throw new ValidationException("Please enter your name.");
        }
        String name = playerName.trim();
        if (name.length() > MAX_NAME_LENGTH) {
            throw new ValidationException(
                    "Name must be " + MAX_NAME_LENGTH + " characters or fewer.");
        }
        if (solveSeconds < 0) {
            throw new ValidationException("Time cannot be negative.");
        }
        puzzleDao.findById(puzzleId);   // throws NotFoundException if it doesn't exist
        return attemptDao.insert(puzzleId, name, solveSeconds);
    }

    public List<Attempt> getLeaderboard(int puzzleId) throws DatabaseException {
        return attemptDao.findByPuzzle(puzzleId);
    }

    public void deleteScore(int attemptId) throws DatabaseException, NotFoundException {
        attemptDao.delete(attemptId);
    }
}