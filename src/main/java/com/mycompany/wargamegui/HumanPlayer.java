/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wargamegui;
import java.util.Scanner;
/**
 *
 * @author neilf
 */
public class HumanPlayer extends Player {
    private final Scanner scanner;

    public HumanPlayer(String name, Scanner scanner) {
        super(name);
        this.scanner = scanner; 
    }
    
    @Override
    public void takeTurnPrompt() {
        System.out.print(name + ", press Enter to draw your card...");
        scanner.nextLine();
    }
}
