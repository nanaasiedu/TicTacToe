package app;

import app.controllers.GameController;
import app.playSchemes.*;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;

public class TicTacToeModel implements Runnable {
    public static int BOARD_DIMENSION = 3;
    private int numMovesMade = 0;
    private BoardSpace[][] board;
    private Player player1;
    private Player player2;

    private boolean isPlayer1turn = true;
    private boolean allowedToMove = false;
    private GameController controller;
    private Object signal = new Object();
    private Coordinate clickedCoordinate;

    public TicTacToeModel() {
        board = new BoardSpace[BOARD_DIMENSION][BOARD_DIMENSION];

        for (int row = 0; row < BOARD_DIMENSION; row++) {
            for (int col = 0; col < BOARD_DIMENSION; col++) {
                board[row][col] = new BoardSpace(Icon.EMPTY, row, col);

                board[row][col].setOnAction(event -> {
                    if (!allowedToMove) return;
                    BoardSpace space = (BoardSpace)event.getSource();
                    allowedToMove = false;

                    clickedCoordinate = new Coordinate(space.getRow(), space.getCol());
                    notifyPlayers();
                });
            }
        }

    }

    private void notifyPlayers() {
        synchronized (signal) {
            signal.notifyAll();
        }
    }

    public BoardSpace[][] getBoard() {
        return board;
    }

    // Player 1 automatically plays with NOUGHTS while player 2 plays with CROSSES
    public void setPlayer1(boolean isHuman) {
        player1 = (isHuman ? new Human(Icon.NOUGHTS) : new ArtificalIntelligence(Icon.NOUGHTS));
    }

    public void setPlayer2(boolean isHuman) {
        player2 = (isHuman ? new Human(Icon.CROSSES) : new ArtificalIntelligence(Icon.CROSSES));
    }

    public void setTurn(boolean isPlayer1turn) {
        this.isPlayer1turn = isPlayer1turn;
    }

    private boolean isGameSet() {
        return player1 != null && player2 != null && controller != null;
    }

    private boolean gameOver() {
        return numMovesMade >= BOARD_DIMENSION*BOARD_DIMENSION;
    }

    private void performMove(int row, int col) {
        //assert(!gameOver());
        //assert(isMoveValid(row, col));

        numMovesMade++;
    }

    private boolean isMoveValid(int row, int col) {
        return board[row][col].getIcon() == Icon.EMPTY;
    }

    private void switchTurns() {
        isPlayer1turn = !isPlayer1turn;
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public void allowClicks() {
        allowedToMove = true;
    }

    public void run() {
        assert (isGameSet());
        while (!gameOver()) {
            Coordinate coor;

            if (isPlayer1turn) {
                coor = player1.makeMove(this);
            } else {
                coor = player2.makeMove(this);
            }

            performMove(1, 1); // TODO: CHANGE TO COOR

            switchTurns();
        }
    }

    public Coordinate getClickedCoordinates() {
        return clickedCoordinate;
    }

    public void waitOnSignal() {
        synchronized (signal) {
            try {
                signal.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
