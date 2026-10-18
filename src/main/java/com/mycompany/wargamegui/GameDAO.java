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

    // the columns every history query selects; mapRound reads them back by name
    private static final String ROUND_COLUMNS =
            "PLAYED_AT, PLAYER_NAME, CPU_NAME, PLAYER_CARD, CPU_CARD, RESULT, "
            + "PLAYER_SCORE_AFTER, CPU_SCORE_AFTER";

    // ---------------------------------------------------------------- stats

    /**
     * Adds one win for this player, creating their row the first time.
     *
     * @param name the player's name
     * @throws SQLException if the database write fails
     */
    public void incrementWin(String name) throws SQLException {
        addToStats(name, 1, 0);
    }

    /**
     * Adds one loss for this player, creating their row the first time.
     *
     * @param name the player's name
     * @throws SQLException if the database write fails
     */
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

    /**
     * Looks up one player's totals.
     *
     * @param name the player's name
     * @return the totals, or null if the player has no row yet
     * @throws SQLException if the query fails
     */
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

    /**
     * Lists every player, most wins first (fewest losses, then name, break ties).
     *
     * @return the leaderboard rows, best first
     * @throws SQLException if the query fails
     */
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

    /**
     * Writes one row into ROUND_HISTORY; Derby numbers the ID itself.
     *
     * @param round the round to save
     * @throws SQLException if the database write fails
     */
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

    /**
     * Reads the newest rounds first.
     *
     * @param limit the most rounds to return; below 1 returns an empty list
     * @return the rounds, newest first
     * @throws SQLException if the query fails
     */
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

    /**
     * Reads the newest rounds played by one player.
     *
     * @param name  the player's name
     * @param limit the most rounds to return; below 1 returns an empty list
     * @return that player's rounds, newest first
     * @throws SQLException if the query fails
     */
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

    /**
     * Deletes every row of ROUND_HISTORY. Destructive, so the caller should confirm first.
     *
     * @throws SQLException if the delete fails
     */
    public void clearHistory() throws SQLException {
        Connection conn = DBManager.getInstance().getConnection();

        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM ROUND_HISTORY");
        }
    }

    // ------------------------------------------------------------ helpers

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
