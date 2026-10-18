package com.mycompany.wargamegui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A deck of 52 cards with a discard pile. When the deck runs out, the discard pile is
 * shuffled back in.
 *
 * @author hdn0310
 */
public class Deck {
    private final List<Card> cards;
    private final List<Card> discardPile;

    /** Creates a full, unshuffled 52-card deck. */
    public Deck() {
        cards = new ArrayList<>();
        discardPile = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
    }

    /**
     * Creates a deck from a ready-made list of cards, for tests that need a known order.
     * Cards are dealt from the END of the list.
     *
     * @param stackedCards the cards in the deck, last card is dealt first
     */
    public Deck(List<Card> stackedCards) {
        cards = new ArrayList<>(stackedCards);
        discardPile = new ArrayList<>();
    }

    /** Shuffles the cards still in the deck. */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Deals the top card, first refilling the deck from the discard pile if it is empty.
     *
     * @return the dealt card
     * @throws EmptyDeckException if there are no cards left anywhere
     */
    public Card dealCard() {
        if (cards.isEmpty()) {
            refillFromDiscard();
        }
        if (cards.isEmpty()) {
            throw new EmptyDeckException("Cannot deal from an empty deck.");
        }
        return cards.remove(cards.size() - 1);
    }

    /**
     * Puts a played card on the discard pile.
     *
     * @param card the card to discard
     */
    public void discard(Card card) {
        discardPile.add(card);
    }

    /** @return how many cards remain, counting the discard pile */
    public int remainingCards() {
        return cards.size() + discardPile.size();
    }

    private void refillFromDiscard() {
        if (discardPile.isEmpty()) {
            return;
        }
        cards.addAll(discardPile);
        discardPile.clear();
        shuffle();
    }
}
