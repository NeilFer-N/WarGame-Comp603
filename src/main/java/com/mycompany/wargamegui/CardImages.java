package com.mycompany.wargamegui;

import java.awt.Image;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 * Loads the card pictures from the resources folder. This is a user-interface concern, so it
 * lives here and not in the {@link Card} model class.
 */
public final class CardImages {

    /** Width every card picture is scaled to. */
    public static final int CARD_WIDTH = 100;

    /** Height every card picture is scaled to (keeps the cards' 5:7 shape). */
    public static final int CARD_HEIGHT = 145;

    private static final Logger LOGGER = Logger.getLogger(CardImages.class.getName());
    private static final String FOLDER = "/cards/";
    private static final String BACK_FILE = "card_back.png";

    // scaled icons are kept so each picture is only read and resized once
    private static final Map<String, ImageIcon> CACHE = new HashMap<>();

    private CardImages() {
        // static helpers only
    }

    /**
     * Returns the picture for a card.
     *
     * @param card the card to show
     * @return its scaled icon, or null if the image file is missing
     */
    public static ImageIcon iconFor(Card card) {
        return load(fileNameFor(card));
    }

    /**
     * Returns the picture of a face-down card.
     *
     * @return the scaled card back, or null if the image file is missing
     */
    public static ImageIcon backIcon() {
        return load(BACK_FILE);
    }

    // e.g. TEN of SPADES -> card_spades_10.png, ACE of CLUBS -> card_clubs_A.png
    static String fileNameFor(Card card) {
        Rank rank = card.getRank();
        String rankPart = switch (rank) {
            case JACK  -> "J";
            case QUEEN -> "Q";
            case KING  -> "K";
            case ACE   -> "A";
            default    -> String.format("%02d", rank.getWarValue());
        };
        return "card_" + card.getSuit().name().toLowerCase() + "_" + rankPart + ".png";
    }

    private static ImageIcon load(String fileName) {
        ImageIcon cached = CACHE.get(fileName);
        if (cached != null) {
            return cached;
        }

        URL url = CardImages.class.getResource(FOLDER + fileName);
        if (url == null) {
            LOGGER.warning("Missing card image: " + fileName);
            return null;
        }
        Image scaled = new ImageIcon(url).getImage()
                .getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(scaled);
        CACHE.put(fileName, icon);
        return icon;
    }
}
