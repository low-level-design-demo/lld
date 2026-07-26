package com.demo.lld.snakeAndLadder;

import java.util.concurrent.ThreadLocalRandom;

public class Dice {

    int currNumber;
    int diceCount;
    private int min;
    private int max;
    public int getCurrNumber() {
        return currNumber;
    }
    
    public void setCurrNumber(int currNumber) {
        this.currNumber = currNumber;
    }
    public int getMin() {
        return min;
    }
    public void setMin(int min) {
        this.min = min;
    }
    public int getMax() {
        return max;
    }
    public void setMax(int max) {
        this.max = max;
    }
    public int throwDice() {
        int diceNumber= ThreadLocalRandom.current().nextInt(1, 7);
        return diceNumber;
       
    }

    public int getDiceCount() {
        return diceCount;
    }

    public void setDiceCount(int diceCount) {
        this.diceCount = diceCount;
    }

    

}
