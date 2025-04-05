package com.dyma.game;

import java.util.Arrays;

import static com.dyma.game.StringConstant.LINE_SEPARATOR;
import static com.dyma.game.StringConstant.SPACE;

public class TicTacToe {
    private char[][] grid = new char[][] {
            {'.','.','.'},
            {'.','.','.'},
            {'.','.','.'}
    };

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Tic-Tac-Toe Grid: ").append(LINE_SEPARATOR);
        for(char[] line : grid){
            for(char cell: line){
                builder.append(SPACE).append(cell).append(SPACE);
            }
            builder.append(LINE_SEPARATOR);
        }
        return builder.toString();
    }

    public void processInput(Player player, int playerInput) {
        final var row = (playerInput - 1) / 3;
        final var colum = (playerInput - 1) % 3;
        if(grid[row][colum] == '.'){
            if(player.equals(Player.FIRST)){
                grid[row][colum] = 'X';
            }else {
                grid[row][colum] = 'O';
            }
        }
    }

    public boolean checkWin() {
        // Check diagonals
        if (grid[0][0] != '.' && grid[0][0] == grid[1][1] && grid[1][1] == grid[2][2]) {
            return true;
        }
        if (grid[0][2] != '.' && grid[0][2] == grid[1][1] && grid[1][1] == grid[2][0]) {
            return true;
        }

        // Check rows and columns
        for (int i = 0; i < 3; i++) {
            // Check row
            if (grid[i][0] != '.' && grid[i][0] == grid[i][1] && grid[i][1] == grid[i][2]) {
                return true;
            }
            // Check column
            if (grid[0][i] != '.' && grid[0][i] == grid[1][i] && grid[1][i] == grid[2][i]) {
                return true;
            }
        }

        return false;
    }
}
