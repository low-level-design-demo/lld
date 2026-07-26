package com.demo.lld.snakeAndLadder;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class Game {

    private static final Logger logger = Logger.getLogger(Game.class.getName());

    Board board;
    Deque<Player> players;
    List<Dice> dices;

    public Game() {
        initializeGame();
    }

    private void initializeGame() {
        board = new Board(10, 5, 5);
        initializePlayers();
        initializeDices();

    }

    private void initializeDices() {
        dices = new ArrayList<>();
        Dice dice = new Dice();
        dices.add(dice);
    }

    private void initializePlayers() {
        Player player1 = new Player("p1");
        player1.setCurrentPosition(0);
        Player player2 = new Player("p2");
        player2.setCurrentPosition(0);

        players = new LinkedList<>();
        players.offer(player1);
        players.offer(player2);

    }

    public String start() {
        String winner = null;
        while (winner == null) {

            Player player = players.poll();
            
            players.offer(player);

            Dice dice = dices.get(0);
            int diceNumber = dice.throwDice();
            logger.info("Turn of player: " + player.getId() + " | Dice: " + diceNumber + " | Position: " + player.getCurrentPosition());
            int totalSteps = player.getCurrentPosition() + diceNumber;
            int lastCell = board.getSize() * board.getSize() - 1;
            if (totalSteps > lastCell) {
                // must land exactly on last cell; overshoot means turn is skipped
                logger.info("Player " + player.getId() + " overshot — stays at " + player.getCurrentPosition());
                continue;
            }
            totalSteps = updateStepsIfSnakeOrLadder(totalSteps, player);
            player.setCurrentPosition(totalSteps);
            if (totalSteps == lastCell) {
                winner = player.getId();
                logger.info("Player " + winner + " wins!");
                return winner;
            }

        }
        return winner;
    }

    private int updateStepsIfSnakeOrLadder(int totalSteps, Player player) {
        int size = board.getSize();

        int row = totalSteps / size;
        int col = totalSteps % size;
        Cell cell = board.getCells()[row][col];
        Jump jump = cell.getJump();
        if (jump != null) {
            if (jump.getStart() < jump.getEnd()) { // ladder: start < end, moves up
                logger.info("Player " + player.getId() + " climbed a ladder: " + jump.getStart() + " -> " + jump.getEnd());
                return jump.getEnd();

            } else { // snake: start > end, moves down
                logger.info("Player " + player.getId() + " bitten by a snake: " + jump.getStart() + " -> " + jump.getEnd());
                return jump.getEnd();

            }

        }
        return totalSteps;

    }

}
