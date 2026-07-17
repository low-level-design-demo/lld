package com.demo.lld.ticTacDesign;

public class Board {

    private int size;
    private PlayingPiece[][] board;

    

    public Board(int size) {
        this.size = size;
        this.board = new PlayingPiece[size][size];
        
    }



    public void printBoard() {
        for (PlayingPiece[] row : board) {
            System.out.print("| ");
            for (PlayingPiece col : row) {
                System.out.print((col != null ? col.getPieceType() : " ") + " | ");
            }
            System.out.println();
        }
    }

    public int getSize() {
        return size;
    }

    public PlayingPiece[][] getBoard() {
        return board;
    }



    public int getFreeCellCount() {
        int count = 0;
        for (PlayingPiece[] row : board) {
            for (PlayingPiece cell : row) {
                if (cell == null) count++;
            }
        }
        return count;
    }



    public boolean addPiece(int x, int y, PlayingPiece currentPlayerPiece) {
        if (board[x][y] != null) {
            return false;
        }
        board[x][y] = currentPlayerPiece;
        return true;
    }

    

}
