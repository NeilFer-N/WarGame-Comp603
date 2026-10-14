package com.mycompany.wargamegui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class GameDAOTest extends DatabaseTestBase {

    private final GameDAO dao = new GameDAO();

    private static RoundRecord round(String playerName, int playerScoreAfter) {
        return new RoundRecord(LocalDateTime.now(), playerName, "Computer",
                "ACE of SPADES", "TWO of HEARTS", "PLAYER_WIN", playerScoreAfter, 0);
    }

    @Test
    void incrementWin_calledTwice_accumulatesWins() throws SQLException {
        dao.incrementWin("Neil");
        dao.incrementWin("Neil");

        PlayerStats stats = dao.getStats("Neil");
        assertEquals(2, stats.getWins());
        assertEquals(0, stats.getLosses());
    }

    @Test
    void incrementLoss_forNewPlayer_createsRowWithOneLoss() throws SQLException {
        dao.incrementLoss("Computer");

        PlayerStats stats = dao.getStats("Computer");
        assertEquals(0, stats.getWins());
        assertEquals(1, stats.getLosses());
    }

    @Test
    void getStats_forMissingPlayer_returnsNull() throws SQLException {
        assertNull(dao.getStats("Nobody"));
    }

    @Test
    void incrementWin_withApostropheInName_isStoredCorrectly() throws SQLException {
        dao.incrementWin("O'Brien");

        assertEquals(1, dao.getStats("O'Brien").getWins());
    }

    @Test
    void getLeaderboard_ordersByWinsThenFewestLosses() throws SQLException {
        dao.incrementWin("Bob");
        dao.incrementWin("Amy");
        dao.incrementWin("Amy");
        dao.incrementLoss("Amy");
        dao.incrementWin("Zed");
        dao.incrementWin("Zed");
        dao.incrementLoss("Computer");

        List<PlayerStats> board = dao.getLeaderboard();

        assertEquals(4, board.size());
        assertEquals("Zed", board.get(0).getName());        // 2 wins, 0 losses
        assertEquals("Amy", board.get(1).getName());        // 2 wins, 1 loss
        assertEquals("Bob", board.get(2).getName());        // 1 win
        assertEquals("Computer", board.get(3).getName());   // 0 wins
    }

    @Test
    void getRecentRounds_returnsNewestFirstAndRespectsTheLimit() throws SQLException {
        for (int score = 1; score <= 5; score++) {
            dao.insertRound(round("Neil", score));
        }

        List<RoundRecord> recent = dao.getRecentRounds(3);

        assertEquals(3, recent.size());
        assertEquals(5, recent.get(0).getPlayerScoreAfter());
        assertEquals(4, recent.get(1).getPlayerScoreAfter());
        assertEquals(3, recent.get(2).getPlayerScoreAfter());
    }

    @Test
    void getRecentRounds_withLimitBelowOne_returnsAnEmptyList() throws SQLException {
        dao.insertRound(round("Neil", 1));

        assertTrue(dao.getRecentRounds(0).isEmpty());
    }

    @Test
    void insertRound_thenRead_keepsEveryField() throws SQLException {
        dao.insertRound(round("Neil", 2));

        RoundRecord saved = dao.getRecentRounds(1).get(0);
        assertNotNull(saved.getPlayedAt());
        assertEquals("Neil", saved.getPlayerName());
        assertEquals("Computer", saved.getCpuName());
        assertEquals("ACE of SPADES", saved.getPlayerCard());
        assertEquals("TWO of HEARTS", saved.getCpuCard());
        assertEquals("PLAYER_WIN", saved.getResult());
        assertEquals(2, saved.getPlayerScoreAfter());
    }

    @Test
    void getRoundsForPlayer_returnsOnlyThatPlayersRounds() throws SQLException {
        dao.insertRound(round("Neil", 1));
        dao.insertRound(round("Amy", 1));
        dao.insertRound(round("Neil", 2));

        List<RoundRecord> neils = dao.getRoundsForPlayer("Neil", 10);

        assertEquals(2, neils.size());
        for (RoundRecord r : neils) {
            assertEquals("Neil", r.getPlayerName());
        }
    }

    @Test
    void clearHistory_removesEveryRoundButKeepsTheStats() throws SQLException {
        dao.insertRound(round("Neil", 1));
        dao.incrementWin("Neil");

        dao.clearHistory();

        assertTrue(dao.getRecentRounds(10).isEmpty());
        assertEquals(1, dao.getStats("Neil").getWins());
    }
}
