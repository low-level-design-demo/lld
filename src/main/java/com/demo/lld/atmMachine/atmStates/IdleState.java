package com.demo.lld.atmMachine.atmStates;

import com.demo.lld.atmMachine.Atm;

public class IdleState extends AtmState {
    public IdleState() {
        System.out.println("ATM is in idle state. Please insert your card to proceed.");
    }

    public void insertCard(Atm atm) {
        System.out.println("Card inserted");
        atm.setAtmState(new HasCardState());
    }

    

}
