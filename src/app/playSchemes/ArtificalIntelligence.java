package app.playSchemes;

import app.BoardSpace;
import app.Coordinate;
import app.Icon;
import app.TicTacToeModel;
import java.util.ArrayList;
import java.util.Arrays;

public class ArtificalIntelligence extends Player {
    private static int SLEEP_TIME = 2500;
    private static int WIN_BONUS = 1;
    private static int DRAW_BONUS = 0;
    private static int LOSE_BONUS = -1;

    public ArtificalIntelligence(Icon icon) {
        super(icon);
    }

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public Coordinate makeMove(TicTacToeModel model) {
        ai_think();
        BoardSpace[][] board = model.getBoard();
        PossibleBoard possibleBoard = initPossibleBoard(board, getIcon());

        // Uses basic first move heuristics to determine the optimal first move of an AI
        // If the AI is playing its second move then firstMove = null
        Coordinate firstMove = firstMoveHeuristics(model, possibleBoard);
        if (firstMove != null) return firstMove;

        return minMax(possibleBoard, getIcon(), 0).moveToMake;
    }

    // Translates the board into a PossibleBoard class
    private PossibleBoard initPossibleBoard(BoardSpace[][] board, Icon icon) {
        PossibleBoard possibleBoard = new PossibleBoard();
        possibleBoard.board = board;

        for (int row = 0; row < TicTacToeModel.BOARD_DIMENSION; row++) {
            for (int col = 0; col < TicTacToeModel.BOARD_DIMENSION; col++) {
                if (board[row][col].getIcon() == icon) {
                  possibleBoard.rowScore[row]++;
                  possibleBoard.colScore[col]++;
                  if (row == col) possibleBoard.leftDiagScore++;
                  if (row + col == TicTacToeModel.BOARD_DIMENSION - 1) possibleBoard.rightDiagScore++;

                } else if (board[row][col].getIcon() != Icon.EMPTY) {
                    possibleBoard.rowScore[row]--;
                    possibleBoard.colScore[col]--;
                    if (row == col) possibleBoard.leftDiagScore--;
                    if (row + col == TicTacToeModel.BOARD_DIMENSION - 1) possibleBoard.rightDiagScore--;

                }

                if (board[row][col].getIcon() != Icon.EMPTY) possibleBoard.freeSpace--;
            }
        }

        return possibleBoard;
    }

    // Used to speed up the game by allowing the AI to choose optimal first/second moves that will ensure it will never
    // lose
    private Coordinate firstMoveHeuristics(TicTacToeModel model, PossibleBoard possibleBoard) {
        BoardSpace[][] board = possibleBoard.board;

        if (possibleBoard.freeSpace == TicTacToeModel.BOARD_DIMENSION*TicTacToeModel.BOARD_DIMENSION) {
            if (board[0][0].getIcon() == Icon.EMPTY) {
                return new Coordinate(0,0);
            } else {
                return new Coordinate(0, TicTacToeModel.BOARD_DIMENSION - 1);
            }
        } else if (possibleBoard.freeSpace == TicTacToeModel.BOARD_DIMENSION*TicTacToeModel.BOARD_DIMENSION - 1) {
            if (model.cornerTaken()) {
                return new Coordinate(TicTacToeModel.BOARD_DIMENSION/2, TicTacToeModel.BOARD_DIMENSION/2);
            } else if (board[TicTacToeModel.BOARD_DIMENSION/2][TicTacToeModel.BOARD_DIMENSION/2].getIcon() != Icon.EMPTY) {
                return new Coordinate(0,0);
            } else {
                return model.getCornerNextToEdge();
            }
        }

        return null;
    }

    // Returns all possible boards that can occur out of all possible moves on possibleBoard
    private ArrayList<PossibleBoard> getPossibleNextBoards(PossibleBoard possibleBoard, Icon icon) {
        ArrayList<PossibleBoard> possibleNextBoards = new ArrayList<PossibleBoard>();
        BoardSpace[][] board = possibleBoard.board;

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                if (board[row][col].getIcon() == Icon.EMPTY) {
                    PossibleBoard newPosBoard = new PossibleBoard();
                    BoardSpace[][] newBoard;
                    newPosBoard.coor = new Coordinate(row, col);
                    
                    // Cloning
                    newBoard = boardClone(board);
                    newPosBoard.board = newBoard;

                    newBoard[row][col].setIcon(icon, false);

                    // If the icon used is the players icon, then we get a bonus to our score
                    // If the icon belongs to the opposing player, then we subtract the bonus
                    int scoreBonus = (icon == getIcon() ? 1 : -1);
                    for (int r = 0; r < TicTacToeModel.BOARD_DIMENSION; r++) {
                        newPosBoard.rowScore[r] = possibleBoard.rowScore[r];
                        if (row == r) newPosBoard.rowScore[row] += scoreBonus;
                    }

                    for (int c = 0; c < TicTacToeModel.BOARD_DIMENSION; c++) {
                        newPosBoard.colScore[c] = possibleBoard.colScore[c];
                        if (col == c) newPosBoard.colScore[col] += scoreBonus;;
                    }

                    newPosBoard.leftDiagScore = possibleBoard.leftDiagScore;
                    if (row == col) newPosBoard.leftDiagScore += scoreBonus;
                    newPosBoard.rightDiagScore = possibleBoard.rightDiagScore;
                    if (row + col == TicTacToeModel.BOARD_DIMENSION - 1) newPosBoard.rightDiagScore += scoreBonus;
                    newPosBoard.freeSpace = possibleBoard.freeSpace - 1;

                    possibleNextBoards.add(newPosBoard);

                }
            }
        }

        return possibleNextBoards;
    }

    // Returns clone of board
    private BoardSpace[][] boardClone(BoardSpace[][] board) {
        BoardSpace[][] newBoard = new BoardSpace[board.length][board[0].length];

        for (int row = 0; row < TicTacToeModel.BOARD_DIMENSION; row++) {
            for (int col = 0; col < TicTacToeModel.BOARD_DIMENSION; col++) {
                newBoard[row][col] = board[row][col].clone();
            }
        }

        return newBoard;
    }

    // Returns the coordinates of the best possible move that minimises or maximises the players score depending
    // on the provided icon
    private Outcome minMax(PossibleBoard posBoard, Icon icon, int depth) {
        Outcome outcome = new Outcome();
        Result result = posBoard.determineOutcome();
        boolean maximiseWin = icon == getIcon();

        // BASE CASE
        if (result.gameComplete()) {
            outcome.score = result.getBonus();
            outcome.moveToMake = posBoard.coor;
            outcome.depth = depth;
            return outcome;
        }

        ArrayList<PossibleBoard> posNextBoards = getPossibleNextBoards(posBoard, icon);
        int optimalScore = (maximiseWin ? Integer.MIN_VALUE : Integer.MAX_VALUE);
        Coordinate optimalCoor = new Coordinate(0,0);
        int optimalDepth = Integer.MAX_VALUE;

        for (PossibleBoard posNextBoard : posNextBoards) {
            Outcome nextOutcome = minMax(posNextBoard, (icon == Icon.CROSSES ? Icon.NOUGHTS : Icon.CROSSES), depth + 1);

            if (maximiseWin && nextOutcome.score >= optimalScore) {
                if (nextOutcome.score == optimalScore && nextOutcome.depth < optimalDepth) continue;
                optimalScore = nextOutcome.score;
                optimalCoor = posNextBoard.coor;
                optimalDepth = nextOutcome.depth;

            } else if (!maximiseWin && nextOutcome.score <= optimalScore){
                if (nextOutcome.score == optimalScore && nextOutcome.depth < optimalDepth) continue;
                optimalScore = nextOutcome.score;
                optimalCoor = posNextBoard.coor;
                optimalDepth = nextOutcome.depth;

            }
        }

        outcome.moveToMake = optimalCoor;
        outcome.score = optimalScore;
        outcome.depth = optimalDepth;
        return outcome;
    }

    // Optimisation to avoid O(n^2) algorithm calculating board score
    // Stores data about the state of the board
    private class PossibleBoard {
        private BoardSpace[][] board;
        // Used to determine the number of squares per row/ column/ diagonal the AI and its oponent occupys
        // Helps determine if a row/ column/ diagonal show a winner
        private int[] rowScore = new int[TicTacToeModel.BOARD_DIMENSION];
        private int[] colScore = new int[TicTacToeModel.BOARD_DIMENSION];
        private int leftDiagScore = 0;
        private int rightDiagScore = 0;
        private int freeSpace = TicTacToeModel.BOARD_DIMENSION*TicTacToeModel.BOARD_DIMENSION;
        private Coordinate coor; // Coordinate of move made to obtain this board

        // Returns result of board
        public Result determineOutcome() {
            for (int row = 0; row < TicTacToeModel.BOARD_DIMENSION; row++) {
                if (rowScore[row] == TicTacToeModel.BOARD_DIMENSION){
                    return Result.WIN;
                } else if (rowScore[row] == -TicTacToeModel.BOARD_DIMENSION) {
                    return Result.LOSE;
                }
            }

            for (int col = 0; col < TicTacToeModel.BOARD_DIMENSION; col++) {
                if (colScore[col] == TicTacToeModel.BOARD_DIMENSION){
                    return Result.WIN;
                } else if (colScore[col] == -TicTacToeModel.BOARD_DIMENSION) {
                    return Result.LOSE;
                }
            }

            if (leftDiagScore == TicTacToeModel.BOARD_DIMENSION) return Result.WIN;
            if (rightDiagScore == TicTacToeModel.BOARD_DIMENSION) return Result.WIN;
            if (leftDiagScore == -TicTacToeModel.BOARD_DIMENSION) return Result.LOSE;
            if (rightDiagScore == -TicTacToeModel.BOARD_DIMENSION) return Result.LOSE;

            if (freeSpace == 0) return Result.DRAW;

            return Result.INCOMPLETE;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();

            for (BoardSpace[] row : board) {
                sb.append(Arrays.toString(row));
                sb.append('\n');
            }

            return sb.toString();
        }
    }

    // Describes the outcome of a specific move
    private class Outcome {
        private Coordinate moveToMake; // Coordinate of move that will produce the outcome
        private int score;             // Score obtained from performing the move that results in the outcome
        private int depth;             // How deep into the minMax recursion this outcome will occur (i.e. number of turns
                                       // till outcome can occur)
    }

    // Enum that describes the result of a game
    private enum Result {
        WIN(WIN_BONUS), LOSE(LOSE_BONUS), DRAW(DRAW_BONUS), INCOMPLETE(DRAW_BONUS);

        private int bonus;

        Result(int bonus) {
            this.bonus = bonus;
        }

        public int getBonus() {
            return bonus;
        }

        public boolean gameComplete() {
            return this != INCOMPLETE;
        }
    }

    // Makes the AI sleep for a certain amount of time
    private void ai_think() {
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
