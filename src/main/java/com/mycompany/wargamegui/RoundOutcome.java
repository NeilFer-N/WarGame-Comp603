package com.mycompany.wargamegui;

/**
 * Who won a single round. Decided once, in {@link WarGame#playRound()}, and then reused by
 * the GUI and the database so the rule is never written out in two places.
 */
public enum RoundOutcome {
    PLAYER_WIN, CPU_WIN, TIE
}
