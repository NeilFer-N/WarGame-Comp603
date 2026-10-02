/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wargamegui;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author hdn0310
 */
public class Deck {
    private final List<Card> cards;
    private final List<Card> discardPile;

    public Deck(){
        cards = new ArrayList<>();
        discardPile = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
    }

    public void shuffle(){
        Collections.shuffle(cards);
    }

    public Card dealCard() {
        if (cards.isEmpty()) {
            refillFromDiscard();
        }
        if (cards.isEmpty()) {
            throw new EmptyDeckException("Cannot deal from an empty deck.");
        }
        return cards.remove(cards.size() - 1);
    }

    public void discard(Card card) {
        discardPile.add(card);
    }

    private void refillFromDiscard() {
        if (discardPile.isEmpty()) {
            return;
        }
        cards.addAll(discardPile);
        discardPile.clear();
        shuffle();
    }


    public int remainingCards() {
        return cards.size() + discardPile.size();
    }
}