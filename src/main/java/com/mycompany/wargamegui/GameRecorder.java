package com.mycompany.wargamegui;

import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Turns what happened in a game into database rows, so the window never builds
 * {@link RoundRecord}s or decides who gets a win or a loss itself.
 */
public class GameRecorder {

    private final GameDAO dao;

    /**
     * Creates a recorder that saves through the given DAO.
     *
     * @param dao the data access object to write with
     */
    public GameRecorder(GameDAO dao) {
        this.dao = dao;
    }

    /**
     * Saves one round to the history. If the round ended the game, also adds a win to the
     * winner's totals and a loss to the loser's.
     *
     * @param game   the game the round was played in (supplies both names)
     * @param result what happened in the round
     * @throws SQLException if the database write fails
     */
    public void record(WarGame game, RoundResult result) throws SQLException {
        String playerName = game.getPlayer().getName();
        String cpuName = game.getCpu().getName();

        dao.insertRound(new RoundRecord(
                LocalDateTime.now(),
                playerName,
                cpuName,
                result.getPlayerCard().toString(),
                result.getCpuCard().toString(),
                result.getOutcome().name(),
                result.getPlayerScore(),
                result.getCpuScore()));

        if (result.isGameOver()) {
            boolean playerWon = result.getWinner() == game.getPlayer();
            dao.incrementWin(playerWon ? playerName : cpuName);
            dao.incrementLoss(playerWon ? cpuName : playerName);
        }
    }
}
