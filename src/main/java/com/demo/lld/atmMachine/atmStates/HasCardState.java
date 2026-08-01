package com.demo.lld.atmMachine.atmStates;

import com.demo.lld.atmMachine.Atm;
import com.demo.lld.atmMachine.Card;

public class HasCardState extends AtmState {
    public HasCardState() {
        System.out.println("Enter your PIN to authenticate the card.");

    }

    @Override
    public void authenticate(Atm atm, Card card,String enteredPin) {
        boolean cardAuthenticated=isCardAuthenticated(enteredPin, card);
        if(cardAuthenticated){
            System.out.println("Card authenticated: " + card.getCardNumber());
            
        atm.setAtmState(new SelectOperationState());
        }else{
            System.out.println("Card authentication failed: " + card.getCardNumber());
            atm.setAtmState(new IdleState());
            exit(atm);
        }
    }

    private void exit(Atm atm) {
        atm.setAtmState(new IdleState());
        System.out.println("Exiting the ATM. Please take your card.");
    }

    private boolean isCardAuthenticated(String enteredPin, Card card) {
        return enteredPin.equals(card.getPin());
        
    }

}

