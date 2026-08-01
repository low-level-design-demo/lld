package com.demo.lld.atmMachine;

import com.demo.lld.atmMachine.atmStates.AtmState;
import com.demo.lld.atmMachine.atmStates.IdleState;

public class Atm {
    private static Atm atm = new Atm();
    AtmState atmState;
    private double atmBalance;
    int numberOfTwoThousandNotes;
    int numberOfFiveHundredNotes;
    int numberOfOneHundredNotes;

    public Atm() {
        

    }

    

    public AtmState getAtmState() {
        setAtmState(new IdleState());
        return atmState;
    }

    public void setAtmState(AtmState atmState) {
        this.atmState = atmState;
    }

    public double getAtmBalance() {
        return atmBalance;
    }

    public void setAtmBalance(double atmBalance, int numberOfTwoThousandNotes, int numberOfFiveHundredNotes,
            int numberOfOneHundredNotes) {

        this.atmBalance = atmBalance;
        this.numberOfTwoThousandNotes = numberOfTwoThousandNotes;
        this.numberOfFiveHundredNotes = numberOfFiveHundredNotes;
        this.numberOfOneHundredNotes = numberOfOneHundredNotes;
    }

    public void deductAtmBalance(double withdrawAmount) {
        this.atmBalance -= withdrawAmount;
    }

    public static Atm getAtm() {
        return atm;
    }

    public static void setAtm(Atm atm) {
        Atm.atm = atm;
    }

  

    public void setAtmBalance(double atmBalance) {
        this.atmBalance = atmBalance;
    }

    public int getNumberOfTwoThousandNotes() {
        return numberOfTwoThousandNotes;
    }

    public void setNumberOfTwoThousandNotes(int numberOfTwoThousandNotes) {
        this.numberOfTwoThousandNotes = numberOfTwoThousandNotes;
    }

    public int getNumberOfFiveHundredNotes() {
        return numberOfFiveHundredNotes;
    }

    public void setNumberOfFiveHundredNotes(int numberOfFiveHundredNotes) {
        this.numberOfFiveHundredNotes = numberOfFiveHundredNotes;
    }

    public int getNumberOfOneHundredNotes() {
        return numberOfOneHundredNotes;
    }

    public void setNumberOfOneHundredNotes(int numberOfOneHundredNotes) {
        this.numberOfOneHundredNotes = numberOfOneHundredNotes;
    }

    public void deductFiveHundredNotes(int requiredNotesCount) {
        this.numberOfFiveHundredNotes -= requiredNotesCount;
    }

    public int getFiveHundredNotesCount() {
        return numberOfFiveHundredNotes;
    }

    public void deductOneHundredNotes(int requiredNotesCount) {
        this.numberOfOneHundredNotes -= requiredNotesCount;
    }

    public void deductTwoThousandNotes(int requiredNotesCount) {
        this.numberOfTwoThousandNotes -= requiredNotesCount;
    }

    public int getTwoThousandNotesCount() {
        return numberOfTwoThousandNotes;
    }

    public void printCurrentATMStatus() {
        System.out.println("Current ATM Status:");
        System.out.println("ATM Balance: " + atmBalance);
        System.out.println("Two Thousand Notes: " + numberOfTwoThousandNotes);
        System.out.println("Five Hundred Notes: " + numberOfFiveHundredNotes);
        System.out.println("One Hundred Notes: " + numberOfOneHundredNotes);
    }

}
