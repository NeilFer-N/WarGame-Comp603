package com.mycompany.wargamegui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object: the only class that knows SQL. The rest of the program
 * calls these plain Java methods and never touches a Connection.
 */
public class GameDAO {

    // ---------------------------------------------------------------- stats

    /** Adds one win for this player, creating their row the first time. */
    public void incrementWin(String name) throws SQLException {
        addToStats(name, 1, 0);
    }

    /** Adds one loss for this player, creating their row the first time. */
    public void incrementLoss(String name) throws SQLException {
        addToStats(name, 0, 1);
    }

    // Update first; if no row was changed the player is new, so insert instead.
    // (Not atomic, but fine for one connection in a single-user desktop app.)
    private void addToStats(String name, int winsToAdd, int lossesToAdd) throws SQLException {
        Connection conn = DBManager.getInstance().getConnection();

        try (PreparedStatement update = conn.prepareStatement(
                "UPDATE PLAYER_STATS SET WINS = WINS + ?, LOSSES = LOSSES + ? "
                + "WHERE PLAYER_NAME = ?")) {
            update.setInt(1, winsToAdd);
            update.setInt(2, lossesToAdd);
            update.setString(3, name);

            if (update.executeUpdate() == 0) {
                try (PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO PLAYER_STATS (PLAYER_NAME, WINS, LOSSES) VALUES (?, ?, ?)")) {
                    insert.setString(1, name);
                    insert.setInt(2, winsToAdd);
                    insert.setInt(3, lossesToAdd);
                    insert.executeUpdate();
                }
            }
        }
    }

    /** Returns this player's totals, or null if they have no row yet. */
    public PlayerStats getStats(String name) throws SQLException {
        Connection conn = DBManager.getInstance().getConnection();

        try (PreparedStatement query = conn.prepareStatement(
                "SELECT PLAYER_NAME, WINS, LOSSES FROM PLAYER_STATS WHERE PLAYER_NAME = ?")) {
            query.setString(1, name);
            try (ResultSet rs = query.executeQuery()) {
                if (rs.next()) {
                    return new PlayerStats(rs.getString("PLAYER_NAME"),
                            rs.getInt("WINS"), rs.getInt("LOSSES"));
                }
                return null;
            }
        }
    }

    /** Every player, most wins first (fewest losses, then name, break ties). */
    public List<PlayerStats> getLeaderboard() throws SQLException {
        Connection conn = DBManager.getInstance().getConnection();
        List<PlayerStats> board = new ArrayList<>();

        try (PreparedStatement query = conn.prepareStatement(
                "SELECT PLAYER_NAME, WINS, LOSSES FROM PLAYER_STATS "
                + "ORDER BY WINS DESC, LOSSES ASC, PLAYER_NAME ASC");
             ResultSet rs = query.executeQuery()) {
            while (rs.next()) {
                board.add(new PlayerStats(rs.getString("PLAYER_NAME"),
                        rs.getInt("WINS"), rs.getInt("LOSSES")));
            }
        }
        return board;
    }

    // -------------------------------------------------------------- history

    /** Writes one row into ROUND_HISTORY; the ID is numbered by Derby. */
    public void insertRound(RoundRecord round) throws SQLException {
        Connection conn = DBManager.getInstance().getConnection();

        try (PreparedStatement insert = conn.prepareStatement(
                "INSERT INTO ROUND_HISTORY (PLAYED_AT, PLAYER_NAME, CPU_NAME, PLAYER_CARD, "
                + "CPU_CARD, RESULT, PLAYER_SCORE_AFTER, CPU_SCORE_AFTER) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            insert.setTimestamp(1, Timestamp.valueOf(round.getPlayedAt()));
            insert.setString(2, round.getPlayerName());
            insert.setString(3, round.getCpuName());
            insert.setString(4, round.getPlayerCard());
            insert.setString(5, round.getCpuCard());
            insert.setString(6, round.getResult());
            insert.setInt(7, round.getPlayerScoreAfter());
            insert.setInt(8, round.getCpuScoreAfter());
            insert.executeUpdate();
        }
    }

    /** The newest rounds first, at most {@code limit} of them. */
    public List<RoundRecord> getRecentRounds(int limit) throws SQLException {
        List<RoundRecord> rounds = new ArrayList<>();
        if (limit < 1) {        // FETCH FIRST needs at least 1 row, so answer "none" ourselves
            return rounds;
        }

        Connection conn = DBManager.getInstance().getConnection();
        try (PreparedStatement query = conn.prepareStatement(
                "SELECT " + ROUND_COLUMNS + " FROM ROUND_HISTORY "
                + "ORDER BY ID DESC FETCH FIRST ? ROWS ONLY")) {
            query.setInt(1, limit);
            try (ResultSet rs = query.executeQuery()) {
                while (rs.next()) {
                    rounds.add(mapRound(rs));
                }
            }
        }
        return rounds;
    }

    /** The newest rounds in which this player took part, at most {@code limit} of them. */
    public List<RoundRecord> getRoundsForPlayer(String name, int limit) throws SQLException {
        List<RoundRecord> rounds = new ArrayList<>();
        if (limit < 1) {
            return rounds;
        }

        Connection conn = DBManager.getInstance().getConnection();
        try (PreparedStatement query = conn.prepareStatement(
                "SELECT " + ROUND_COLUMNS + " FROM ROUND_HISTORY WHERE PLAYER_NAME = ? "
                + "ORDER BY ID DESC FETCH FIRST ? ROWS ONLY")) {
            query.setString(1, name);
            query.setInt(2, limit);
            try (ResultSet rs = query.executeQuery()) {
                while (rs.next()) {
                    rounds.add(mapRound(rs));
                }
            }
        }
        return rounds;
    }

    /** Deletes every row of ROUND_HISTORY. Destructive: the caller should confirm first. */
    public void clearHistory() throws SQLException {
        Connection conn = DBManager.getInstance().getConnection();

        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM ROUND_HISTORY");
        }
    }

    // ------------------------------------------------------------ helpers

    // the columns every history query selects, in the order mapRound reads them by name
    private static final String ROUND_COLUMNS =
            "PLAYED_AT, PLAYER_NAME, CPU_NAME, PLAYER_CARD, CPU_CARD, RESULT, "
            + "PLAYER_SCORE_AFTER, CPU_SCORE_AFTER";

    // turns the ResultSet's current row into a RoundRecord
    private RoundRecord mapRound(ResultSet rs) throws SQLException {
        return new RoundRecord(
                rs.getTimestamp("PLAYED_AT").toLocalDateTime(),
                rs.getString("PLAYER_NAME"),
                rs.getString("CPU_NAME"),
                rs.getString("PLAYER_CARD"),
                rs.getString("CPU_CARD"),
                rs.getString("RESULT"),
                rs.getInt("PLAYER_SCORE_AFTER"),
                rs.getInt("CPU_SCORE_AFTER"));
    }
}
