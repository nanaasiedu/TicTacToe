package app;

import app.controllers.GameController;
import app.playSchemes.*;
import javafx.application.Platform;

public class TicTacToeModel implements Runnable {
    public static int BOARD_DIMENSION = 3;
    private int numMovesMade = 0;
    private BoardSpace[][] board;

    // Optimisation that speeds up the isGameOver method at the cost of space
    // Stores the number of icons per row/ column/ diagonal for both players
    // (Note: space can be saved by useing only 4 variables rather than 8)
    private int[] player1RowCount = new int[BOARD_DIMENSION];
    private int[] player1ColCount = new int[BOARD_DIMENSION];
    private int player1LeftDiagCount = 0;
    private int player1RightDiagCount = 0;
    private int[] player2RowCount = new int[BOARD_DIMENSION];
    private int[] player2ColCount = new int[BOARD_DIMENSION];
    private int player2LeftDiagCount = 0;
    private int player2RightDiagCount = 0;

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

        clearBoard();

    }

    private void clearBoard() {
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

    private boolean movesExhausted() {
        return numMovesMade >= BOARD_DIMENSION*BOARD_DIMENSION;
    }

    private void performMove(int row, int col) {
        assert(!movesExhausted());
        assert(isMoveValid(row, col));

        validMove = true;
        Icon icon = (isPlayer1turn ? player1.getIcon() : player2.getIcon());
        board[row][col].setIcon(icon);

        if (isPlayer1turn) {
            player1ColCount[col]++;
            player1RowCount[row]++;

            if (row == col) player1LeftDiagCount++;
            if (row + col == BOARD_DIMENSION - 1) player1RightDiagCount++;

        } else {
            player2ColCount[col]++;
            player2RowCount[row]++;

            if (row == col) player2LeftDiagCount++;
            if (row + col == BOARD_DIMENSION - 1) player2RightDiagCount++;

        }

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
        Player winner = null;

        while (!movesExhausted()) {
            Coordinate coor;
            Player currentPlayer = getCurrentPlayer();

            setGameText((isPlayer1turn ? "Player 1 " : "Player 2 ") +
                                   (currentPlayer.isHuman() ? GameController.HUMAN_TEXT : GameController.AI_TEXT));

            coor = currentPlayer.makeMove(this);
            performMove(coor.getRow(), coor.getCol());

            winner = isGameOver(coor.getRow(), coor.getCol());
            if (winner != null) break;

            switchTurns();
        }

        if (winner != null) {
            setGameText(GameController.ENG_GAME_TEXT + winner);
        } else {
            setGameText(GameController.DRAW_TEXT);
        }
    }

    private Player isGameOver(int row, int col) {
            if (isPlayer1turn) {
                if (player1RowCount[row] == BOARD_DIMENSION || player1ColCount[col] == BOARD_DIMENSION
                        || player1LeftDiagCount == BOARD_DIMENSION || player1RightDiagCount == BOARD_DIMENSION){
                    return player1;
                }

            } else {
                if (player2RowCount[row] == BOARD_DIMENSION || player2ColCount[col] == BOARD_DIMENSION
                        || player2LeftDiagCount == BOARD_DIMENSION || player2RightDiagCount == BOARD_DIMENSION){
                    return player2;
                }
            }

            return null;
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

    private void notifyPlayers() {
        synchronized (clickSignal) {
            clickSignal.notifyAll();
        }
    }

    public void setGameText(String text) {
        controller.setGameText(text);
    }

    public boolean cornerTaken() {
        return board[0][0].isEmpty() || board[0][BOARD_DIMENSION-1].isEmpty() ||
               board[BOARD_DIMENSION-1][0].isEmpty() || board[BOARD_DIMENSION-1][BOARD_DIMENSION-1].isEmpty();
    }

    public Coordinate getCornerNextToEdge() {
        if (!board[0][BOARD_DIMENSION/2].isEmpty() || !board[BOARD_DIMENSION/2][0].isEmpty())
            return new Coordinate(0,0);
        if (!board[0][BOARD_DIMENSION/2].isEmpty() || !board[BOARD_DIMENSION/2][0].isEmpty())
            return new Coordinate(BOARD_DIMENSION, BOARD_DIMENSION);

        return null;
    }
}
