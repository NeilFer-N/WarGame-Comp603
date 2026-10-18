package com.mycompany.wargamegui;

/**
 * One row of the PLAYER_STATS table: a player's name with their win/loss totals. Immutable.
 */
public class PlayerStats {

    private final String name;
    private final int wins;
    private final int losses;

    /**
     * Creates a stats row.
     *
     * @param name   the player's name
     * @param wins   games won
     * @param losses games lost
     */
    public PlayerStats(String name, int wins, int losses) {
        this.name = name;
        this.wins = wins;
        this.losses = losses;
    }

    /** @return the player's name */
    public String getName() {
        return name;
    }

    /** @return games won */
    public int getWins() {
        return wins;
    }

    /** @return games lost */
    public int getLosses() {
        return losses;
    }
}
