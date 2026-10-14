package com.mycompany.wargamegui;

/**
 * The thirteen ranks of a standard deck, each with the value used to win a round of War
 * (Ace is highest).
 *
 * @author neilf
 */
public enum Rank {
    TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10),
    JACK(11), QUEEN(12), KING(13), ACE(14);

    private final int value;

    Rank(int value) {
        this.value = value;
    }

    /** @return this rank's value in War, from 2 (Two) up to 14 (Ace) */
    public int getWarValue() {
        return value;
    }
}
