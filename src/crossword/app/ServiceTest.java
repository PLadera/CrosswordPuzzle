package crossword.app;

import crossword.exception.DatabaseException;
import crossword.exception.NotFoundException;
import crossword.exception.ValidationException;
import crossword.model.Entry;
import crossword.model.Puzzle;
import crossword.service.AttemptService;
import crossword.service.GameSession;
import crossword.service.PuzzleService;

public class ServiceTest {
    public static void main(String[] args) {
        try {
            PuzzleService puzzles = new PuzzleService();
            AttemptService attempts = new AttemptService();

            Puzzle puzzle = puzzles.getPuzzleForPlay(puzzles.getPuzzles(null).get(0).getId());
            GameSession game = new GameSession(puzzle);
            Entry first = puzzle.getEntries().get(0);
            int r = first.rowAt(0);
            int c = first.colAt(0);

            try {
                game.setLetter(0, 0, "A");
            } catch (ValidationException e) {
                System.out.println("Blocked cell: " + e.getMessage());
            }
            try {
                game.setLetter(r, c, "AB");
            } catch (ValidationException e) {
                System.out.println("Two letters: " + e.getMessage());
            }
            try {
                game.setLetter(r, c, "7");
            } catch (ValidationException e) {
                System.out.println("A number: " + e.getMessage());
            }

            game.setLetter(r, c, "q");
            System.out.println("Typed q, stored: " + game.getEntry(r, c));
            System.out.println("Wrong cells: " + game.findWrongCells().size());
            System.out.println("Complete yet? " + game.checkComplete());

            for (Entry e : puzzle.getEntries()) {
                for (int i = 0; i < e.getWord().length(); i++) {
                    game.setLetter(e.rowAt(i), e.colAt(i),
                            String.valueOf(e.getWord().charAt(i)));
                }
            }
            System.out.println("Wrong cells after filling: " + game.findWrongCells().size());
            System.out.println("Complete now? " + game.checkComplete());

            try {
                game.setLetter(r, c, "Z");
            } catch (ValidationException e) {
                System.out.println("After completing: " + e.getMessage());
            }
            game.restart();
            System.out.println("After restart, has entries? " + game.hasEntries());

            try {
                attempts.saveScore(puzzle.getId(), "   ", 50);
            } catch (ValidationException e) {
                System.out.println("Blank name: " + e.getMessage());
            }
            try {
                attempts.saveScore(puzzle.getId(), "Paul", -5);
            } catch (ValidationException e) {
                System.out.println("Negative time: " + e.getMessage());
            }
            int id = attempts.saveScore(puzzle.getId(), "  O'Brien  ", 75);
            System.out.println("Saved: " + attempts.getLeaderboard(puzzle.getId()).get(0).getPlayerName());
            attempts.deleteScore(id);
            System.out.println("Scores left: " + attempts.getLeaderboard(puzzle.getId()).size());
        } catch (ValidationException e) {
            System.out.println("Rule broken: " + e.getMessage());
        } catch (NotFoundException e) {
            System.out.println("Not found: " + e.getMessage());
        } catch (DatabaseException e) {
            System.out.println("Database problem: " + e.getMessage());
        }
    }
}