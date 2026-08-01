package com.demo.lld.atmMachine.withdrawProcessors;

import com.demo.lld.atmMachine.Atm;

/**
 * WithdrawProcessor
 */
public abstract class WithdrawProcessor {

    WithdrawProcessor nextWithdrawProcessor;
  
    public WithdrawProcessor() {
    }
    public WithdrawProcessor(WithdrawProcessor withdrawProcessor) {
        this.nextWithdrawProcessor = withdrawProcessor;
    }

    public  void processWithdraw(Atm atm, double amount){
        
        if (nextWithdrawProcessor != null) {
            nextWithdrawProcessor.processWithdraw(atm, amount);
        }
    }

}
