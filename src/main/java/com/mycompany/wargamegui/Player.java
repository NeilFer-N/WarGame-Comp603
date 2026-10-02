/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wargamegui;

/**
 *
 * @author neilf
 */
public abstract class Player {
    protected final String name;

    @Override
    public String toString() {
        return name;
    }
    
    public Player(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }
    public abstract void takeTurnPrompt();
}
