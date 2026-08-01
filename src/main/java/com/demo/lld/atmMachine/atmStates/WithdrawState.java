package com.demo.lld.atmMachine.atmStates;

import com.demo.lld.atmMachine.Atm;
import com.demo.lld.atmMachine.Card;
import com.demo.lld.atmMachine.TransactionType;
import com.demo.lld.atmMachine.withdrawProcessors.FiveHundredNotesWithdrawProcessor;
import com.demo.lld.atmMachine.withdrawProcessors.OneHundredNotesWithdrawProcessor;
import com.demo.lld.atmMachine.withdrawProcessors.TwoThousandNotesWithdrawProcessor;
import com.demo.lld.atmMachine.withdrawProcessors.WithdrawProcessor;

public class WithdrawState extends AtmState {

    


    @Override
    public void withdrawCash(Atm atm,Card card, double withdrawAmount) {

        if(atm.getAtmBalance() < withdrawAmount) {
            System.out.println("Insufficient funds in ATM.");
            exit(atm);
            
        }else if(card.getBalance() < withdrawAmount) {
            System.out.println("Insufficient funds in your account.");
            exit(atm);
            
        }else{
            card.deductBalance(withdrawAmount);
            atm.deductAtmBalance(withdrawAmount);
            
            WithdrawProcessor withdrawProcessor=new TwoThousandNotesWithdrawProcessor(new FiveHundredNotesWithdrawProcessor(new OneHundredNotesWithdrawProcessor(null)));
            withdrawProcessor.processWithdraw(atm, withdrawAmount);
            exit(atm);
        }
        System.out.println("Cash withdrawn: " + withdrawAmount);
    }

    private void exit(Atm atm) {
        atm.setAtmState(new IdleState());
        System.out.println("Exiting the ATM. Please take your card.");
    }

}
