package com.mycompany.wargamegui;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class PlayerNamesTest {

    @Test
    void validate_withEmptyName_returnsAMessage() {
        assertNotNull(PlayerNames.validate(""));
        assertNotNull(PlayerNames.validate(null));
    }

    @Test
    void validate_withNameOverFiftyCharacters_returnsAMessage() {
        assertNotNull(PlayerNames.validate("x".repeat(PlayerNames.MAX_LENGTH + 1)));
    }

    @Test
    void validate_withExactlyFiftyCharacters_isAccepted() {
        assertNull(PlayerNames.validate("x".repeat(PlayerNames.MAX_LENGTH)));
    }

    @Test
    void validate_withTheComputersName_isRejectedIgnoringCase() {
        assertNotNull(PlayerNames.validate("computer"));
    }

    @Test
    void validate_withOrdinaryName_returnsNull() {
        assertNull(PlayerNames.validate("O'Brien"));
    }
}
