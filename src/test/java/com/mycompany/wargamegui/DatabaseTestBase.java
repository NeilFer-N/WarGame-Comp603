package com.mycompany.wargamegui;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

/**
 * Shared set-up for tests that need a database. DBManager is a Singleton, so every test class
 * shares one in-memory Derby database (never the real warGameDB) and each test starts with
 * both tables empty.
 */
abstract class DatabaseTestBase {

    private static boolean configured;

    @BeforeAll
    static void useInMemoryDatabase() {
        if (!configured) {      // setUrl may only be called once, before the database is opened
            DBManager.setUrl("jdbc:derby:memory:warGameTestDB;create=true");
            configured = true;
        }
    }

    @BeforeEach
    void clearTables() throws SQLException {
        Connection conn = DBManager.getInstance().getConnection();
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM ROUND_HISTORY");
            st.executeUpdate("DELETE FROM PLAYER_STATS");
        }
    }
}
