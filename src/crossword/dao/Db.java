package crossword.dao;

import crossword.exception.DatabaseException;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class Db {
    private static final String CONFIG_FILE = "db.properties";
    private static Properties config;

    private Db() { }

    public static Connection getConnection() throws DatabaseException {
        Properties p = loadConfig();
        try {
            return DriverManager.getConnection(
                    p.getProperty("db.url"),
                    p.getProperty("db.user"),
                    p.getProperty("db.password"));
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Cannot connect to the database. Is MySQL running?", e);
        }
    }

    private static synchronized Properties loadConfig() throws DatabaseException {
        if (config == null) {
            Properties p = new Properties();
            try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
                p.load(in);
            } catch (IOException e) {
                throw new DatabaseException("Cannot read " + CONFIG_FILE
                        + ". Copy db.properties.example to db.properties "
                        + "and fill in your password.", e);
            }
            config = p;
        }
        return config;
    }
}