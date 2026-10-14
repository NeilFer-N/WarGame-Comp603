package com.mycompany.wargamegui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DeckTest {

    @Test
    void newDeck_hasFiftyTwoCards() {
        assertEquals(52, new Deck().remainingCards());
    }

    @Test
    void newDeck_containsNoDuplicateCards() {
        Deck deck = new Deck();
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 52; i++) {
            seen.add(deck.dealCard().toString());
        }
        assertEquals(52, seen.size());
    }

    @Test
    void dealCard_removesOneCardFromTheDeck() {
        Deck deck = new Deck();
        deck.dealCard();
        assertEquals(51, deck.remainingCards());
    }

    @Test
    void dealCard_onEmptyDeck_throwsEmptyDeckException() {
        Deck empty = new Deck(new ArrayList<>());
        assertThrows(EmptyDeckException.class, empty::dealCard);
    }

    @Test
    void dealCard_whenDeckIsEmptyButDiscardHasCards_refillsFromDiscard() {
        Card only = new Card(Suit.HEARTS, Rank.ACE);
        Deck deck = new Deck(List.of(only));

        Card dealt = deck.dealCard();
        deck.discard(dealt);

        assertSame(only, deck.dealCard());
    }
}
