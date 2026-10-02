/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wargamegui;

/**
 *
 * @author neilf
 */
public class Card {
    private final Suit suit;
    private final Rank rank;
    
    public Card(Suit suit, Rank rank){
        this.suit = suit;
        this.rank = rank;
    }
    public Rank getRank(){
        return rank;
    }
    public Suit getSuits(){
        return suit;
    }
    @Override
    public String toString(){
        return rank + " of " + suit;
    }
}
