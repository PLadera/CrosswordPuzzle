package crossword.service;

import crossword.dao.PuzzleDao;
import crossword.exception.DatabaseException;
import crossword.exception.NotFoundException;
import crossword.model.Difficulty;
import crossword.model.Puzzle;

import java.util.List;

public class PuzzleService {
    private final PuzzleDao puzzleDao = new PuzzleDao();

    /** Pass null to get every puzzle, or a difficulty to filter. */
    public List<Puzzle> getPuzzles(Difficulty difficulty) throws DatabaseException {
        if (difficulty == null) {
            return puzzleDao.findAll();
        }
        return puzzleDao.findByDifficulty(difficulty);
    }

    public Puzzle getPuzzleForPlay(int puzzleId)
            throws DatabaseException, NotFoundException {
        return puzzleDao.findById(puzzleId);
    }
}