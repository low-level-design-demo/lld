package com.demo.lld.snakeAndLadder;

/**
 * Player
 */
public class Player {
    private String id;
    private int currentPosition;
    public Player(String id){
        this.id=id;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public int getCurrentPosition() {
        return currentPosition;
    }
    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    


}
