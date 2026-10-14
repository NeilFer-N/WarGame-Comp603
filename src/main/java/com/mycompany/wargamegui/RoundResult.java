/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wargamegui;

/**
 *
 * @author neilf
 */
public final class RoundResult {

    private final Card playerCard;
    private final Card cpuCard;
    private final int playerScore;
    private final int cpuScore;
    private final boolean gameOver;
    private final Player winner;

    public RoundResult(Card playerCard, Card cpuCard,
                       int playerScore, int cpuScore,
                       boolean gameOver, Player winner) {

        this.playerCard = playerCard;
        this.cpuCard = cpuCard;
        this.playerScore = playerScore;
        this.cpuScore = cpuScore;
        this.gameOver = gameOver;
        this.winner = winner;
    }

    public Card getPlayerCard() {
        return playerCard;
    }

    public Card getCpuCard() {
        return cpuCard;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public int getCpuScore() {
        return cpuScore;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public Player getWinner() {
        return winner;
    }
}
