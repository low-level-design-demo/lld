package com.demo.lld.snakeAndLadder;

import java.util.concurrent.ThreadLocalRandom;

public class Board {
    private Cell[][] cells;
    private int snakeCount;
    private int ladderCount;
    private int size;

    public Board(int size, int snakeCount, int ladderCount) {
        this.size = size;
        this.snakeCount = snakeCount;
        this.ladderCount = ladderCount;
        cells = new Cell[this.size][this.size];
        initializeCells();
        putSnakeAndLadder();
    }

    private void initializeCells() {
            for(int i=0;i<size;i++){
                for(int j=0;j<size;j++){
                    Cell cell=new Cell();
                    cells[i][j]=cell;
                }
            }
    }

    private void putSnakeAndLadder() {

        while (snakeCount > 0) {

            int start = ThreadLocalRandom.current().nextInt(1, (cells.length*cells.length)-1);
            int end = ThreadLocalRandom.current().nextInt(1, (cells.length*cells.length)-1);
            if(start<=end){
                continue;
            }

            
            int row = start / cells.length;
            int col = start % cells.length;
            if (cells[row][col].getJump() != null) continue;
            Jump jump=new Jump(start,end);
            cells[row][col]=new Cell(jump);
            snakeCount--;
        }

        while (ladderCount > 0) {

            int start = ThreadLocalRandom.current().nextInt(1, (cells.length*cells.length)-1);
            int end = ThreadLocalRandom.current().nextInt(1, (cells.length*cells.length)-1);
            
            if(start>=end){
                continue;
            }

            int row = start / cells.length;
            int col = start % cells.length;
            if (cells[row][col].getJump() != null) continue;
            Jump jump=new Jump(start,end);
            cells[row][col]=new Cell(jump);
            ladderCount--;
        }

    }


    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }

    public int getSnakeCount() {
        return snakeCount;
    }

    public void setSnakeCount(int snakeCount) {
        this.snakeCount = snakeCount;
    }

    public int getLadderCount() {
        return ladderCount;
    }

    public void setLadderCount(int ladderCount) {
        this.ladderCount = ladderCount;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    

}
