package com.demo.lld.atmMachine.atmStates;

import com.demo.lld.atmMachine.Atm;
import com.demo.lld.atmMachine.BankAccount;
import com.demo.lld.atmMachine.Card;
import com.demo.lld.atmMachine.TransactionType;

public abstract class AtmState {

    public AtmState() {
        
        
    }

    public void insertCard(Atm atm, Card card) {
        System.out.println("Card inserted: " + card.getCardNumber());
    }
    public void authenticate(Atm atm, Card card,String enteredPin) {
        System.out.println("Card authenticated: " + card.getCardNumber());
    }
    public void selectOperation(Atm atm,Card card, TransactionType operation) {
        System.out.println("Operation selected");
    }
   
    public void withdrawCash(Atm atm,Card card, double withdrawAmount) {
        System.out.println("Cash withdrawn: " + withdrawAmount);
    }
    
    public void checkBalance(Atm atm,BankAccount bankAccount) {
        System.out.println("Current balance: " + bankAccount.getBalance());
    }
    public void displayMessage(String message) {
        System.out.println("Message: " + message);
    }
}
