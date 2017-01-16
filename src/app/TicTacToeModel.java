package app;

import app.controllers.GameController;
import app.playSchemes.*;
import javafx.application.Platform;

public class TicTacToeModel implements Runnable {
    public static int BOARD_DIMENSION = 3;
    private int numMovesMade = 0;
    private BoardSpace[][] board;
    private Player player1;
    private Player player2;

    private boolean isPlayer1turn = true;
    private boolean allowedToMove = false;
    private GameController controller;
    private Object clickSignal = new Object();
    private boolean validMove = false; // used for iconSetSignal
    private Coordinate clickedCoordinate;

    public TicTacToeModel() {
        board = new BoardSpace[BOARD_DIMENSION][BOARD_DIMENSION];

        for (int row = 0; row < BOARD_DIMENSION; row++) {
            for (int col = 0; col < BOARD_DIMENSION; col++) {
                board[row][col] = new BoardSpace(Icon.EMPTY, row, col);

                board[row][col].setOnAction(event -> {
                    if (!allowedToMove) return;
                    BoardSpace space = (BoardSpace)event.getSource();
                    disableClicks();

                    clickedCoordinate = new Coordinate(space.getRow(), space.getCol());
                    notifyPlayers();
                });
            }
        }

    }

    private void notifyPlayers() {
        synchronized (clickSignal) {
            clickSignal.notifyAll();
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
        assert(!gameOver());
        assert(isMoveValid(row, col));

        validMove = true;
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                Icon icon = (isPlayer1turn ? player1.getIcon() : player2.getIcon());
                board[row][col].setIcon(icon);
            }
        });

        numMovesMade++;
    }

    private Player getCurrentPlayer() {
        return (isPlayer1turn ? player1 : player2);
    }

    public boolean isMoveValid(int row, int col) {
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

    private void disableClicks() {
        allowedToMove = false;
    }

    public void run() {
        assert (isGameSet());
        while (!gameOver()) {
            Coordinate coor;
            Player currentPlayer = getCurrentPlayer();

            coor = currentPlayer.makeMove(this);
            performMove(coor.getRow(), coor.getCol());

            switchTurns();
        }

        controller.setGameText(GameController.ENG_GAME_TEXT);
    }

    public Coordinate waitOnSignal() {
        synchronized (clickSignal) {
            try {
                clickSignal.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return clickedCoordinate;
    }
}
