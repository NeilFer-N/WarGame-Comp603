package com.mycompany.wargamegui;

/**
 * One row of the PLAYER_STATS table: a player's name with their win/loss totals.
 */
public class PlayerStats {

    private final String name;
    private final int wins;
    private final int losses;

    public PlayerStats(String name, int wins, int losses) {
        this.name = name;
        this.wins = wins;
        this.losses = losses;
    }

    public String getName() {
        return name;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }
}
