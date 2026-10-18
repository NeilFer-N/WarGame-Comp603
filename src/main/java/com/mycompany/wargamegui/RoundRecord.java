package com.mycompany.wargamegui;

import java.time.LocalDateTime;

/**
 * One row of the ROUND_HISTORY table: everything about a single round.
 * Immutable, like PlayerStats.
 */
public class RoundRecord {

    private final LocalDateTime playedAt;
    private final String playerName;
    private final String cpuName;
    private final String playerCard;
    private final String cpuCard;
    private final String result;            // "PLAYER_WIN", "CPU_WIN" or "TIE"
    private final int playerScoreAfter;
    private final int cpuScoreAfter;

    /**
     * Creates a history row.
     *
     * @param playedAt         when the round was played
     * @param playerName       the human player's name
     * @param cpuName          the computer's name
     * @param playerCard       the card the player drew, as text
     * @param cpuCard          the card the computer drew, as text
     * @param result           the {@link RoundOutcome} name: PLAYER_WIN, CPU_WIN or TIE
     * @param playerScoreAfter the player's round wins after this round
     * @param cpuScoreAfter    the computer's round wins after this round
     */
    public RoundRecord(LocalDateTime playedAt, String playerName, String cpuName,
                       String playerCard, String cpuCard, String result,
                       int playerScoreAfter, int cpuScoreAfter) {
        this.playedAt = playedAt;
        this.playerName = playerName;
        this.cpuName = cpuName;
        this.playerCard = playerCard;
        this.cpuCard = cpuCard;
        this.result = result;
        this.playerScoreAfter = playerScoreAfter;
        this.cpuScoreAfter = cpuScoreAfter;
    }

    /** @return when the round was played */
    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    /** @return the human player's name */
    public String getPlayerName() {
        return playerName;
    }

    /** @return the computer's name */
    public String getCpuName() {
        return cpuName;
    }

    /** @return the card the player drew, as text */
    public String getPlayerCard() {
        return playerCard;
    }

    /** @return the card the computer drew, as text */
    public String getCpuCard() {
        return cpuCard;
    }

    /** @return PLAYER_WIN, CPU_WIN or TIE */
    public String getResult() {
        return result;
    }

    /** @return the player's round wins after this round */
    public int getPlayerScoreAfter() {
        return playerScoreAfter;
    }

    /** @return the computer's round wins after this round */
    public int getCpuScoreAfter() {
        return cpuScoreAfter;
    }
}
