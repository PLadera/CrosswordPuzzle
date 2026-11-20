package crossword.dao;

import crossword.exception.DatabaseException;
import crossword.exception.NotFoundException;
import crossword.model.Attempt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AttemptDao {

    /** Saves a score and returns its new id. */
    public int insert(int puzzleId, String playerName, int solveSeconds)
            throws DatabaseException {
        String sql = "INSERT INTO attempt (puzzle_id, player_name, solve_seconds) "
                + "VALUES (?, ?, ?)";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, puzzleId);
            ps.setString(2, playerName);
            ps.setInt(3, solveSeconds);
            if (ps.executeUpdate() != 1) {
                throw new DatabaseException("The score was not saved.", null);
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new DatabaseException("The score was saved but has no id.", null);
                }
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not save the score.", e);
        }
    }

    /** Leaderboard for one puzzle: fastest time first. */
    public List<Attempt> findByPuzzle(int puzzleId) throws DatabaseException {
        String sql = "SELECT id, puzzle_id, player_name, solve_seconds, finished_at "
                + "FROM attempt WHERE puzzle_id = ? "
                + "ORDER BY solve_seconds ASC, finished_at ASC";
        List<Attempt> result = new ArrayList<>();
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, puzzleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new Attempt(
                            rs.getInt("id"),
                            rs.getInt("puzzle_id"),
                            rs.getString("player_name"),
                            rs.getInt("solve_seconds"),
                            rs.getTimestamp("finished_at").toLocalDateTime()));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new DatabaseException("Could not load the leaderboard.", e);
        }
    }

    public void delete(int attemptId) throws DatabaseException, NotFoundException {
        String sql = "DELETE FROM attempt WHERE id = ?";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, attemptId);
            if (ps.executeUpdate() == 0) {
                throw new NotFoundException("Score " + attemptId + " was not found.");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Could not delete the score.", e);
        }
    }
}