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

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getCpuName() {
        return cpuName;
    }

    public String getPlayerCard() {
        return playerCard;
    }

    public String getCpuCard() {
        return cpuCard;
    }

    public String getResult() {
        return result;
    }

    public int getPlayerScoreAfter() {
        return playerScoreAfter;
    }

    public int getCpuScoreAfter() {
        return cpuScoreAfter;
    }
}
