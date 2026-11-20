package crossword.app;

import crossword.dao.PuzzleDao;
import crossword.exception.DatabaseException;
import crossword.exception.NotFoundException;
import crossword.model.Difficulty;
import crossword.model.Entry;
import crossword.model.Puzzle;

import java.util.List;

public class PuzzleDaoTest {
    public static void main(String[] args) {
        PuzzleDao dao = new PuzzleDao();
        try {
            List<Puzzle> all = dao.findAll();
            for (Puzzle p : all) {
                System.out.println(p.getId() + ": " + p);
            }
            System.out.println("HARD only: " + dao.findByDifficulty(Difficulty.HARD));

            Puzzle first = dao.findById(all.get(0).getId());
            System.out.println(first + " has " + first.getEntries().size() + " words:");
            for (Entry e : first.getEntries()) {
                System.out.println("  " + e.getDirection() + " " + e.getWord()
                        + " - " + e.getClue());
            }

            dao.findById(9999);
        } catch (NotFoundException e) {
            System.out.println("Not found (expected): " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("Database problem: " + e.getMessage());
        }
    }
}