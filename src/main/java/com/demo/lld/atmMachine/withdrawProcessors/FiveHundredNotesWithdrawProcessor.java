package com.demo.lld.atmMachine.withdrawProcessors;

import com.demo.lld.atmMachine.Atm;

public class FiveHundredNotesWithdrawProcessor extends WithdrawProcessor {

    

    public FiveHundredNotesWithdrawProcessor(WithdrawProcessor withdrawProcessor) {
        super(withdrawProcessor);
        

    }
    @Override
    public void processWithdraw(Atm atm, double remainingAmount) {
        int requiredNotesCount = (int) (remainingAmount / 500);
        int balance=(int)(remainingAmount%500);
        if(requiredNotesCount<=atm.getFiveHundredNotesCount()){
           atm.deductFiveHundredNotes(requiredNotesCount);
        }else if(requiredNotesCount>atm.getFiveHundredNotesCount()){
            int notesDispensed=atm.getFiveHundredNotesCount();
            atm.deductFiveHundredNotes(notesDispensed);
            balance=balance+(requiredNotesCount-(atm.getFiveHundredNotesCount()*500));
        }
        if(balance!=0){
            super.processWithdraw(atm, balance);
        }
    }
  

}
