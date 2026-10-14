package com.mycompany.wargamegui;

/**
 * Someone who plays War: has a name and nothing else, so human and computer players share it.
 *
 * @author neilf
 */
public abstract class Player {
    protected final String name;

    /**
     * Creates a player.
     *
     * @param name the player's name
     */
    protected Player(String name) {
        this.name = name;
    }

    /** @return the player's name */
    public String getName() {
        return name;
    }

    /** @return the player's name */
    @Override
    public String toString() {
        return name;
    }
}
