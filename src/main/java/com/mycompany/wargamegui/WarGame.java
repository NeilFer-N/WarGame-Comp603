/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wargamegui;

/**
 *
 * @author neilf
 */
public class WarGame {
    private static final int TARGET_ROUND_WINS = 3; // first to 3 round wins takes the game

    private final Deck deck;
    private final Player player;
    private final CPU cpu;


    public WarGame(Deck deck, Player player, CPU cpu) {
        this.deck = deck;
        this.player = player;
        this.cpu = cpu;
    }

    public Player play() {
        int playerScore = 0;
        int cpuScore = 0;
        int round = 0;

        //if (logger != null) {
            //logger.startSession(player.getName(), cpu.getName());
        //}

        while (playerScore < TARGET_ROUND_WINS &&
               cpuScore < TARGET_ROUND_WINS) {

            round++;

            player.takeTurnPrompt();
            cpu.takeTurnPrompt();

            Card card1 = deck.dealCard();
            Card card2 = deck.dealCard();

            System.out.println(player.getName() + " draws " + card1);
            System.out.println(cpu.getName() + " draws " + card2);

            int value1 = card1.getRank().getWarValue();
            int value2 = card2.getRank().getWarValue();

            String result;
            if (value1 > value2) {
                playerScore++;
                result = player.getName() + " wins the round";
                System.out.println(player.getName() + " wins the round! Score: " + playerScore + "-" + cpuScore);
            } else if (value2 > value1) {
                cpuScore++;
                result = cpu.getName() + " wins the round";
                System.out.println(cpu.getName() + " wins the round! Score: " + playerScore + "-" + cpuScore);
            } else {
                result = "Tie, no points awarded";
                System.out.println("Tie! No points awarded.");
            }

            //if (logger != null) {
                //logger.logRound(round, player.getName(), card1, cpu.getName(), card2, result);
            //}


            deck.discard(card1);
            deck.discard(card2);
        }

        Player winner = (playerScore >= TARGET_ROUND_WINS) ? player : cpu;

        //if (logger != null) {
            //logger.endSession(winner.getName());
        //}

        return winner;
    }
}
