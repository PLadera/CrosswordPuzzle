package crossword.app;

import crossword.dao.Db;
import crossword.exception.DatabaseException;

import java.sql.Connection;
import java.sql.SQLException;

public class DbTest {
    public static void main(String[] args) {
        try (Connection conn = Db.getConnection()) {
            System.out.println("Connected through the Db class!");
        } catch (DatabaseException e) {
            System.out.println("Friendly error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Close failed: " + e.getMessage());
        }
    }
}