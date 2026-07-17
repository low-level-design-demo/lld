package com.demo.lld.ticTacDesign;

public class TicTacDemo {
    public static void main(String[] args) {

        TicTacGame game = new TicTacGame(3);

        System.out.println("Player 1 has been assigned X, Player 2 has been assigned O.");
        System.out.println("Enter moves as: row,col (0-indexed). Example: 1,2");

        String winner = game.startGame();

        if ("tie".equals(winner)) {
            System.out.println("Game over — it's a tie!");
        } else {
            System.out.println("Game over — Winner is: " + winner);
        }
    }
}
