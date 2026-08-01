package com.demo.lld.atmMachine.withdrawProcessors;

import com.demo.lld.atmMachine.Atm;

public class OneHundredNotesWithdrawProcessor extends WithdrawProcessor {
    public OneHundredNotesWithdrawProcessor(WithdrawProcessor withdrawProcessor) {
        super(withdrawProcessor);
    }

    @Override
    public void processWithdraw(Atm atm, double amount) {
        int requiredNotesCount = (int) (amount / 100);
        int balance=(int)(amount%100);
        if(requiredNotesCount<=atm.getNumberOfOneHundredNotes()){
           atm.deductOneHundredNotes(requiredNotesCount);
        }else if(requiredNotesCount>atm.getNumberOfOneHundredNotes()){
            int notesDispensed=atm.getNumberOfOneHundredNotes();
            atm.deductOneHundredNotes(notesDispensed);
            balance=balance+(requiredNotesCount-(atm.getNumberOfOneHundredNotes()*100));
        }
    
        if (balance!=0) {
            super.processWithdraw(atm, amount);
        }
    }

}
