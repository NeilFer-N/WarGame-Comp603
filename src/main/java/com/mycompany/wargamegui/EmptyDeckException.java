package com.mycompany.wargamegui;

/**
 * Thrown when a card is dealt but the deck and its discard pile are both empty.
 */
public class EmptyDeckException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param message what went wrong
     */
    public EmptyDeckException(String message) {
        super(message);
    }
}
