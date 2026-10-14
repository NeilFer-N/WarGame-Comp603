package com.mycompany.wargamegui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RankTest {

    @Test
    void ace_hasTheHighestWarValue() {
        for (Rank rank : Rank.values()) {
            if (rank != Rank.ACE) {
                assertTrue(rank.getWarValue() < Rank.ACE.getWarValue(),
                        rank + " should be lower than ACE");
            }
        }
    }

    @Test
    void numberRanks_haveTheirFaceValue() {
        assertEquals(2, Rank.TWO.getWarValue());
        assertEquals(10, Rank.TEN.getWarValue());
    }
}
