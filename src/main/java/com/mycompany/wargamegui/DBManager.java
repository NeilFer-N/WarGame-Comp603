package com.mycompany.wargamegui;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton that owns the one embedded Derby connection for the whole program.
 * Everything else gets the connection through DBManager.getInstance().
 */
public class DBManager {

    // the one shared copy
    private static DBManager instance;

    private final Connection connection;

    // private: nobody outside this class can call "new DBManager()"
    private DBManager() throws SQLException {
        // embedded mode: the database lives in a "warGameDB" folder next to the program
        connection = DriverManager.getConnection("jdbc:derby:warGameDB;create=true");
        createTablesIfNeeded();
    }

    // the ONLY way to get a DBManager: created on first call, reused after that
    public static synchronized DBManager getInstance() throws SQLException {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    // creates PLAYER_STATS and ROUND_HISTORY the first time the program runs.
    // Derby has no "CREATE TABLE IF NOT EXISTS", so we ask the metadata first.
    private void createTablesIfNeeded() throws SQLException {
        if (!tableExists("PLAYER_STATS")) {
            try (Statement st = connection.createStatement()) {
                st.execute("CREATE TABLE PLAYER_STATS ("
                        + "PLAYER_NAME VARCHAR(50) PRIMARY KEY, "
                        + "WINS INT, "
                        + "LOSSES INT)");
            }
        }

        if (!tableExists("ROUND_HISTORY")) {
            try (Statement st = connection.createStatement()) {
                st.execute("CREATE TABLE ROUND_HISTORY ("
                        + "ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, "
                        + "PLAYED_AT TIMESTAMP, "
                        + "PLAYER_NAME VARCHAR(50), "
                        + "CPU_NAME VARCHAR(50), "
                        + "PLAYER_CARD VARCHAR(30), "
                        + "CPU_CARD VARCHAR(30), "
                        + "RESULT VARCHAR(20), "
                        + "PLAYER_SCORE_AFTER INT, "
                        + "CPU_SCORE_AFTER INT)");
            }
        }
    }

    // Derby stores unquoted table names in UPPERCASE, so pass the name in uppercase
    private boolean tableExists(String tableName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, tableName, new String[]{"TABLE"})) {
            return rs.next();   // true if at least one matching table came back
        }
    }
}
