package com.demo.lld.atmMachine.atmStates;

import com.demo.lld.atmMachine.Atm;
import com.demo.lld.atmMachine.BankAccount;

public class CheckBalanceState extends AtmState {

    @Override
    public void checkBalance(Atm atm, BankAccount bankAccount) {
        System.out.println("Current balance: " + bankAccount.getBalance());
        exit(atm);
    }

    private void exit(Atm atm) {
        atm.setAtmState(new IdleState());
        System.out.println("Exiting the ATM. Please take your card.");
    }

}
