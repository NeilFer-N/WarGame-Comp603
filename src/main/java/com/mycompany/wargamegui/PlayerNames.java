package com.mycompany.wargamegui;

/**
 * The rules for a player's name, in one place so the GUI and the database agree.
 * The database column is VARCHAR(50), so a longer name would cause an SQL error.
 */
public final class PlayerNames {

    /** Longest name the PLAYER_NAME column can hold. */
    public static final int MAX_LENGTH = 50;

    /** Name used until the player types their own. */
    public static final String DEFAULT_NAME = "Player";

    /** The computer opponent's name; a human may not take it or the stats would collide. */
    public static final String CPU_NAME = "Computer";

    private PlayerNames() {
        // static helpers only
    }

    /**
     * Checks a name that has already been trimmed.
     *
     * @param name the trimmed name typed by the user (may be null)
     * @return a short message explaining the problem, or null if the name is fine
     */
    public static String validate(String name) {
        if (name == null || name.isEmpty()) {
            return "Please enter a name.";
        }
        if (name.length() > MAX_LENGTH) {
            return "Please use " + MAX_LENGTH + " characters or fewer.";
        }
        if (name.equalsIgnoreCase(CPU_NAME)) {
            return "\"" + CPU_NAME + "\" is the computer's name. Please choose another.";
        }
        return null;
    }
}
