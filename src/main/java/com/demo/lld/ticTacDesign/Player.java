package com.demo.lld.ticTacDesign;

public class Player {
    String name;
    

    PlayingPiece playingPiece;

    public Player(String name, PlayingPiece playingPiece) {
        this.name = name;
        this.playingPiece = playingPiece;
    }
    

    public PlayingPiece getPlayingPiece() {
        return playingPiece;
    }

    public void setPlayingPiece(PlayingPiece playingPiece) {
        this.playingPiece = playingPiece;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }
    

}
