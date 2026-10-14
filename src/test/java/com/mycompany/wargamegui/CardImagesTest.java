package com.mycompany.wargamegui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class CardImagesTest {

    @Test
    void fileNameFor_mapsRanksAndSuitsToTheKenneyFileNames() {
        assertEquals("card_spades_10.png", CardImages.fileNameFor(new Card(Suit.SPADES, Rank.TEN)));
        assertEquals("card_hearts_02.png", CardImages.fileNameFor(new Card(Suit.HEARTS, Rank.TWO)));
        assertEquals("card_clubs_A.png", CardImages.fileNameFor(new Card(Suit.CLUBS, Rank.ACE)));
    }

    @Test
    void everyCardInADeck_hasAnImageFileInTheResourcesFolder() {
        Deck deck = new Deck();
        for (int i = 0; i < 52; i++) {
            String file = CardImages.fileNameFor(deck.dealCard());
            assertNotNull(CardImages.class.getResource("/cards/" + file), "missing image " + file);
        }
        assertNotNull(CardImages.class.getResource("/cards/card_back.png"), "missing card back");
    }
}
