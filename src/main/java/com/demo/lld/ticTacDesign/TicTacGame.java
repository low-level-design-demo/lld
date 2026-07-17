package com.demo.lld.ticTacDesign;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Scanner;

public class TicTacGame {

    Deque<Player> dq;
    Board board;

    public TicTacGame(int size) {
        initializeBoard(size);
    }

    public void initializeBoard(int size) {

        dq = new LinkedList<>();

        Player player1 = new Player("Player 1", new PlayingPieceX());

        Player player2 = new Player("Player 2", new PlayingPieceO());

        dq.add(player1);
        dq.add(player2);
        board = new Board(size);

    }

    public String startGame() {

        boolean isGameOver = false;

        while (!isGameOver) {

            Player currentPlayer = dq.removeFirst();

            board.printBoard();

            PlayingPiece currentPlayerPiece = currentPlayer.getPlayingPiece();
            System.out.println("Turn of user: " + currentPlayer.getName() + ". Enter x and y coordinates to keep the piece");
            Scanner inputScanner = new Scanner(System.in);
            String s = inputScanner.nextLine();
            String[] values = s.split(",");

            int x = Integer.valueOf(values[0]);
            int y = Integer.valueOf(values[1]);

            boolean isPlacedSuccessfullly = board.addPiece(x, y, currentPlayerPiece);

            if (!isPlacedSuccessfullly) {
                System.out.println("Already piece is kept at " + x + "," + y + " . Try again");
                dq.addFirst(currentPlayer);
                continue;
            }

            board.printBoard();

            dq.addLast(currentPlayer);
            boolean isWinner = isThereWinner(x, y, currentPlayerPiece.pieceType);
            if (isWinner) {
                return currentPlayer.getName();
            }

            if (board.getFreeCellCount() == 0) {
                isGameOver = true;
            }
        }
        return "tie";

    }

    private boolean isThereWinner(int x, int y, PieceType pieceType) {
        int size = board.getSize();
        PlayingPiece[][] grid = board.getBoard();

        // check row
        boolean rowWin = true;
        for (int col = 0; col < size; col++) {
            if (grid[x][col] == null || grid[x][col].pieceType != pieceType) {
                rowWin = false;
                break;
            }
        }
        if (rowWin) return true;

        // check column
        boolean colWin = true;
        for (int row = 0; row < size; row++) {
            if (grid[row][y] == null || grid[row][y].pieceType != pieceType) {
                colWin = false;
                break;
            }
        }
        if (colWin) return true;

        // check main diagonal (top-left to bottom-right)
        if (x == y) {
            boolean diagWin = true;
            for (int i = 0; i < size; i++) {
                if (grid[i][i] == null || grid[i][i].pieceType != pieceType) {
                    diagWin = false;
                    break;
                }
            }
            if (diagWin) return true;
        }

        // check anti-diagonal (top-right to bottom-left)
        if (x + y == size - 1) {
            boolean antiDiagWin = true;
            for (int i = 0; i < size; i++) {
                if (grid[i][size - 1 - i] == null || grid[i][size - 1 - i].pieceType != pieceType) {
                    antiDiagWin = false;
                    break;
                }
            }
            if (antiDiagWin) return true;
        }

        return false;
    }

}
