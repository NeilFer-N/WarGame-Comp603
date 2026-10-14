/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wargamegui;

/**
 * A single playing card: a suit and a rank. Immutable.
 *
 * @author neilf
 */
public class Card {
    private final Suit suit;
    private final Rank rank;

    /**
     * Creates a card.
     *
     * @param suit the card's suit
     * @param rank the card's rank
     */
    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    /** @return the card's rank */
    public Rank getRank() {
        return rank;
    }

    /** @return the card's suit */
    public Suit getSuit() {
        return suit;
    }

    /** @return the card as text, e.g. "TEN of SPADES" */
    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}
