/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wargamegui;

/**
 * Immutable snapshot of one played round: the cards, who won it, and the score afterwards.
 *
 * @author neilf
 */
public final class RoundResult {

    private final Card playerCard;
    private final Card cpuCard;
    private final RoundOutcome outcome;
    private final int playerScore;
    private final int cpuScore;
    private final boolean gameOver;
    private final Player winner;

    /**
     * Creates a round result.
     *
     * @param playerCard  the card the human player drew
     * @param cpuCard     the card the computer drew
     * @param outcome     who won this round
     * @param playerScore the player's round wins after this round
     * @param cpuScore    the computer's round wins after this round
     * @param gameOver    true if this round ended the game
     * @param winner      the game winner, or null while the game is still going
     */
    public RoundResult(Card playerCard, Card cpuCard, RoundOutcome outcome,
                       int playerScore, int cpuScore,
                       boolean gameOver, Player winner) {

        this.playerCard = playerCard;
        this.cpuCard = cpuCard;
        this.outcome = outcome;
        this.playerScore = playerScore;
        this.cpuScore = cpuScore;
        this.gameOver = gameOver;
        this.winner = winner;
    }

    /** @return the card the human player drew */
    public Card getPlayerCard() {
        return playerCard;
    }

    /** @return the card the computer drew */
    public Card getCpuCard() {
        return cpuCard;
    }

    /** @return who won this round (or a tie) */
    public RoundOutcome getOutcome() {
        return outcome;
    }

    /** @return the player's round wins after this round */
    public int getPlayerScore() {
        return playerScore;
    }

    /** @return the computer's round wins after this round */
    public int getCpuScore() {
        return cpuScore;
    }

    /** @return true if this round ended the game */
    public boolean isGameOver() {
        return gameOver;
    }

    /** @return the game winner, or null while the game is still going */
    public Player getWinner() {
        return winner;
    }
}
