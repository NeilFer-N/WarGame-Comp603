/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wargamegui;

/**
 * One game of War: the first side to win {@value #TARGET_ROUND_WINS} rounds wins the game.
 *
 * @author neilf
 */
public class WarGame {
    private static final int TARGET_ROUND_WINS = 3;

    private final Deck deck;
    private final Player player;
    private final CPU cpu;
    private int playerScore;
    private int cpuScore;

    /**
     * Starts a new game.
     *
     * @param deck   the deck both sides draw from
     * @param player the human player
     * @param cpu    the computer opponent
     */
    public WarGame(Deck deck, Player player, CPU cpu) {
        this.deck = deck;
        this.player = player;
        this.cpu = cpu;
        this.playerScore = 0;
        this.cpuScore = 0;
    }

    /** @return the human player */
    public Player getPlayer() {
        return player;
    }

    /** @return the computer opponent */
    public CPU getCpu() {
        return cpu;
    }

    /**
     * Plays one round: each side draws a card, the higher rank wins the round, and the
     * outcome is decided here once so nobody else has to compare cards.
     *
     * @return the cards drawn, who won, the new scores and whether the game is over
     * @throws EmptyDeckException if there are no cards left to draw
     */
    // Generated with assistance from Claude (Anthropic)
    public RoundResult playRound() {
        Card card1 = deck.dealCard();
        Card card2 = deck.dealCard();

        RoundOutcome outcome = decideOutcome(card1, card2);
        if (outcome == RoundOutcome.PLAYER_WIN) {
            playerScore++;
        } else if (outcome == RoundOutcome.CPU_WIN) {
            cpuScore++;
        }

        boolean gameOver = (playerScore >= TARGET_ROUND_WINS)
                || (cpuScore >= TARGET_ROUND_WINS);

        Player winner = gameOver
                ? (playerScore >= TARGET_ROUND_WINS ? player : cpu)
                : null;

        deck.discard(card1);
        deck.discard(card2);

        return new RoundResult(
                card1,
                card2,
                outcome,
                playerScore,
                cpuScore,
                gameOver,
                winner
        );
    }

    // higher war value wins; equal values tie
    private RoundOutcome decideOutcome(Card playerCard, Card cpuCard) {
        int comparison = Integer.compare(playerCard.getRank().getWarValue(),
                cpuCard.getRank().getWarValue());
        if (comparison > 0) {
            return RoundOutcome.PLAYER_WIN;
        } else if (comparison < 0) {
            return RoundOutcome.CPU_WIN;
        }
        return RoundOutcome.TIE;
    }
}
