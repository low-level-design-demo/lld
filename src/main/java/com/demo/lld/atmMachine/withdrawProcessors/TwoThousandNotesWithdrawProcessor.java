package com.demo.lld.atmMachine.withdrawProcessors;

import com.demo.lld.atmMachine.Atm;

public class TwoThousandNotesWithdrawProcessor extends WithdrawProcessor {

    

    public TwoThousandNotesWithdrawProcessor(WithdrawProcessor withdrawProcessor) {
        super(withdrawProcessor);

    }

    public void processWithdraw(Atm atm, double amount) {
        int requiredNotesCount = (int) (amount / 2000);
        int balance=(int)(amount%2000);
        if(requiredNotesCount<=atm.getTwoThousandNotesCount()){
           atm.deductTwoThousandNotes(requiredNotesCount);
        }else if(requiredNotesCount>atm.getTwoThousandNotesCount()){
            int notesDispensed=atm.getTwoThousandNotesCount();
            atm.deductTwoThousandNotes(notesDispensed);
            balance=balance+(requiredNotesCount-(atm.getTwoThousandNotesCount()*2000));
        }
        if(balance!=0){
            super.processWithdraw(atm, balance);
        }
       
    }

}
