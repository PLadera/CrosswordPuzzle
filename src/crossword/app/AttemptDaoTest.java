package crossword.app;

import crossword.dao.AttemptDao;
import crossword.dao.PuzzleDao;
import crossword.exception.DatabaseException;
import crossword.exception.NotFoundException;
import crossword.model.Attempt;

public class AttemptDaoTest {
    public static void main(String[] args) {
        AttemptDao dao = new AttemptDao();
        try {
            int puzzleId = new PuzzleDao().findAll().get(0).getId();

            int id1 = dao.insert(puzzleId, "O'Brien", 95);
            int id2 = dao.insert(puzzleId, "Paul", 60);

            System.out.println("Leaderboard (fastest first):");
            for (Attempt a : dao.findByPuzzle(puzzleId)) {
                System.out.println("  " + a.getPlayerName() + " - "
                        + a.getSolveSeconds() + "s");
            }

            dao.delete(id1);
            dao.delete(id2);
            System.out.println("After cleanup: "
                    + dao.findByPuzzle(puzzleId).size() + " scores");

            dao.delete(id1);   // already deleted, so this should fail
        } catch (NotFoundException e) {
            System.out.println("Not found (expected): " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("Database problem: " + e.getMessage());
        }
    }
}