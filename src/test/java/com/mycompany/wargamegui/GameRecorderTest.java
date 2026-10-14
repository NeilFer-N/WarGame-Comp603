package com.mycompany.wargamegui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class GameRecorderTest extends DatabaseTestBase {

    private final GameDAO dao = new GameDAO();
    private final GameRecorder recorder = new GameRecorder(dao);

    // a game whose deck deals these ranks in order: player, cpu, player, cpu...
    private static WarGame gameDealing(Rank... ranksInDealOrder) {
        List<Card> cards = new ArrayList<>();
        for (Rank rank : ranksInDealOrder) {
            cards.add(new Card(Suit.SPADES, rank));
        }
        Collections.reverse(cards);
        return new WarGame(new Deck(cards), new HumanPlayer("Neil"), new CPU("Computer"));
    }

    @Test
    void record_forARoundThatDoesNotEndTheGame_savesTheRoundButNoStats() throws SQLException {
        WarGame game = gameDealing(Rank.ACE, Rank.TWO);

        recorder.record(game, game.playRound());

        List<RoundRecord> history = dao.getRecentRounds(10);
        assertEquals(1, history.size());
        assertEquals("PLAYER_WIN", history.get(0).getResult());
        assertNull(dao.getStats("Neil"));
    }

    @Test
    void record_whenTheGameEnds_addsAWinToTheWinnerAndALossToTheLoser() throws SQLException {
        WarGame game = gameDealing(Rank.ACE, Rank.TWO, Rank.KING, Rank.THREE, Rank.QUEEN, Rank.FOUR);

        for (int i = 0; i < 3; i++) {
            recorder.record(game, game.playRound());
        }

        assertEquals(3, dao.getRecentRounds(10).size());
        assertEquals(1, dao.getStats("Neil").getWins());
        assertEquals(1, dao.getStats("Computer").getLosses());
    }
}
