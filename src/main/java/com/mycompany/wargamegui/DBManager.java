package com.mycompany.wargamegui;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton that owns the one embedded Derby connection for the whole program.
 * Everything else gets the connection through DBManager.getInstance().
 */
public class DBManager {

    private static final Logger LOGGER = Logger.getLogger(DBManager.class.getName());

    /** The real game database: a "warGameDB" folder in the program's working directory. */
    private static final String DEFAULT_URL = "jdbc:derby:warGameDB;create=true";

    /** Derby reports a successful whole-engine shutdown with this SQLState. */
    private static final String ENGINE_SHUTDOWN_STATE = "XJ015";

    private static String url = DEFAULT_URL;

    // the one shared copy
    private static DBManager instance;

    private final Connection connection;

    // private: nobody outside this class can call "new DBManager()"
    private DBManager() throws SQLException {
        connection = DriverManager.getConnection(url);
        createTablesIfNeeded();
    }

    /**
     * Returns the shared DBManager, opening the database the first time it is called.
     *
     * @return the single DBManager instance
     * @throws SQLException if the database cannot be opened (for example because another
     *                      copy of the game already has it open)
     */
    public static synchronized DBManager getInstance() throws SQLException {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    /**
     * Chooses which database to open, so tests can use an in-memory database instead of the
     * real one. Must be called before the first {@link #getInstance()}.
     *
     * @param newUrl a Derby JDBC URL, e.g. "jdbc:derby:memory:testDB;create=true"
     * @throws IllegalStateException if the database has already been opened
     */
    public static synchronized void setUrl(String newUrl) {
        if (instance != null) {
            throw new IllegalStateException("The database is already open.");
        }
        url = newUrl;
    }

    /**
     * Closes the connection and shuts the embedded Derby engine down cleanly, so no
     * identity numbers are lost and the database folder is left tidy. Safe to call when the
     * database was never opened.
     */
    public static synchronized void shutdown() {
        if (instance == null) {
            return;
        }
        try {
            instance.connection.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Could not close the database connection", ex);
        }
        instance = null;

        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException ex) {
            // Derby signals a successful engine shutdown by throwing XJ015, so that one is expected
            if (!ENGINE_SHUTDOWN_STATE.equals(ex.getSQLState())) {
                LOGGER.log(Level.WARNING, "Unexpected error while shutting Derby down", ex);
            }
        }
    }

    /**
     * Returns the shared connection.
     *
     * @return the open database connection
     */
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
