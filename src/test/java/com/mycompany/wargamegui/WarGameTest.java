package com.mycompany.wargamegui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WarGameTest {

    private Player player;
    private CPU cpu;

    @BeforeEach
    void createPlayers() {
        player = new HumanPlayer("Neil");
        cpu = new CPU("Computer");
    }

    // builds a deck that deals the given ranks in exactly this order (player, cpu, player, cpu...)
    private WarGame gameDealing(Rank... ranksInDealOrder) {
        List<Card> cards = new ArrayList<>();
        for (Rank rank : ranksInDealOrder) {
            cards.add(new Card(Suit.SPADES, rank));
        }
        Collections.reverse(cards);     // Deck deals from the end of the list
        return new WarGame(new Deck(cards), player, cpu);
    }

    @Test
    void playRound_whenPlayerCardIsHigher_returnsPlayerWin() {
        RoundResult result = gameDealing(Rank.KING, Rank.FIVE).playRound();

        assertEquals(RoundOutcome.PLAYER_WIN, result.getOutcome());
        assertEquals(1, result.getPlayerScore());
        assertEquals(0, result.getCpuScore());
    }

    @Test
    void playRound_whenCpuCardIsHigher_returnsCpuWin() {
        RoundResult result = gameDealing(Rank.FIVE, Rank.KING).playRound();

        assertEquals(RoundOutcome.CPU_WIN, result.getOutcome());
        assertEquals(0, result.getPlayerScore());
        assertEquals(1, result.getCpuScore());
    }

    @Test
    void playRound_whenRanksAreEqual_returnsTieAndScoresStayAtZero() {
        RoundResult result = gameDealing(Rank.NINE, Rank.NINE).playRound();

        assertEquals(RoundOutcome.TIE, result.getOutcome());
        assertEquals(0, result.getPlayerScore());
        assertEquals(0, result.getCpuScore());
    }

    @Test
    void playRound_beforeThreeWins_gameIsNotOverAndHasNoWinner() {
        WarGame game = gameDealing(Rank.ACE, Rank.TWO, Rank.ACE, Rank.TWO);

        game.playRound();
        RoundResult second = game.playRound();

        assertFalse(second.isGameOver());
        assertNull(second.getWinner());
    }

    @Test
    void playRound_afterThreePlayerWins_gameOverWithPlayerAsWinner() {
        WarGame game = gameDealing(Rank.ACE, Rank.TWO, Rank.KING, Rank.THREE, Rank.QUEEN, Rank.FOUR);

        game.playRound();
        game.playRound();
        RoundResult third = game.playRound();

        assertTrue(third.isGameOver());
        assertSame(player, third.getWinner());
        assertEquals(3, third.getPlayerScore());
    }

    @Test
    void playRound_afterThreeCpuWins_gameOverWithCpuAsWinner() {
        WarGame game = gameDealing(Rank.TWO, Rank.ACE, Rank.THREE, Rank.KING, Rank.FOUR, Rank.QUEEN);

        game.playRound();
        game.playRound();
        RoundResult third = game.playRound();

        assertTrue(third.isGameOver());
        assertSame(cpu, third.getWinner());
        assertEquals(3, third.getCpuScore());
    }
}
