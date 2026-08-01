package com.demo.lld.atmMachine.atmStates;

import com.demo.lld.atmMachine.Atm;
import com.demo.lld.atmMachine.Card;
import com.demo.lld.atmMachine.TransactionType;

public class SelectOperationState extends AtmState {


    @Override
    public void selectOperation(Atm atm, Card card, TransactionType transactionType) {
        switch (transactionType) {
            case WITHDRAW:
                atm.setAtmState(new WithdrawState());
                System.out.println("Withdraw operation selected");
                break;
            case CHECK_BALANCE:
                atm.setAtmState(new CheckBalanceState());
                System.out.println("Check balance operation selected");
                break;
            default:
                System.out.println("Invalid operation selected");
                exit(atm);
        }

    }

    public void exit(Atm atm) {
        atm.setAtmState(new IdleState());
        System.out.println("Exiting the ATM. Please take your card.");
    }

}
