package crossword.dao;

import crossword.exception.DatabaseException;
import crossword.exception.NotFoundException;
import crossword.model.Difficulty;
import crossword.model.Direction;
import crossword.model.Entry;
import crossword.model.Puzzle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PuzzleDao {

    public List<Puzzle> findAll() throws DatabaseException {
        String sql = "SELECT id, title, difficulty FROM puzzle ORDER BY id";
        List<Puzzle> result = new ArrayList<>();
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(toPuzzle(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new DatabaseException("Could not load the puzzles.", e);
        }
    }

    public List<Puzzle> findByDifficulty(Difficulty difficulty) throws DatabaseException {
        String sql = "SELECT id, title, difficulty FROM puzzle "
                + "WHERE difficulty = ? ORDER BY id";
        List<Puzzle> result = new ArrayList<>();
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, difficulty.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(toPuzzle(rs));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new DatabaseException("Could not filter the puzzles.", e);
        }
    }

    public Puzzle findById(int id) throws DatabaseException, NotFoundException {
        String sql = "SELECT id, title, difficulty FROM puzzle WHERE id = ?";
        try (Connection conn = Db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            Puzzle puzzle;
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new NotFoundException("Puzzle " + id + " was not found.");
                }
                puzzle = toPuzzle(rs);
            }
            loadEntries(conn, puzzle);
            return puzzle;
        } catch (SQLException e) {
            throw new DatabaseException("Could not load the puzzle.", e);
        }
    }

    private void loadEntries(Connection conn, Puzzle puzzle) throws SQLException {
        String sql = "SELECT id, puzzle_id, word, clue, start_row, start_col, direction "
                + "FROM entry WHERE puzzle_id = ? "
                + "ORDER BY direction, start_row, start_col";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, puzzle.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    puzzle.addEntry(new Entry(
                            rs.getInt("id"),
                            rs.getInt("puzzle_id"),
                            rs.getString("word"),
                            rs.getString("clue"),
                            rs.getInt("start_row"),
                            rs.getInt("start_col"),
                            Direction.valueOf(rs.getString("direction"))));
                }
            }
        }
    }

    private Puzzle toPuzzle(ResultSet rs) throws SQLException {
        return new Puzzle(
                rs.getInt("id"),
                rs.getString("title"),
                Difficulty.valueOf(rs.getString("difficulty")));
    }
}