/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wargamegui;

/**
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

    public WarGame(Deck deck, Player player, CPU cpu) {
        this.deck = deck;
        this.player = player;
        this.cpu = cpu;
        this.playerScore = 0;
        this.cpuScore = 0;
    }

    // Generated with assistance from Claude (Anthropic)
    public RoundResult playRound() {
        Card card1 = deck.dealCard();
        Card card2 = deck.dealCard();

        int value1 = card1.getRank().getWarValue();
        int value2 = card2.getRank().getWarValue();

        if (value1 > value2) {
            playerScore++;
        } else if (value2 > value1) {
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
                playerScore,
                cpuScore,
                gameOver,
                winner
        );
    }
}