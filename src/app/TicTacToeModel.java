package app;

import app.playSchemes.*;

public class TicTacToeModel {
    //TODO: 2D array board
    Player player1;
    Player player2;
    boolean isPlayer1turn = true;

    public void setPlayer1(boolean isHuman) {
        player1 = (isHuman ? new Human() : new ArtificalIntelligence());
    }

    public void setPlayer2(boolean isHuman) {
        player2 = (isHuman ? new Human() : new ArtificalIntelligence());
    }

        this.isPlayer1turn = isPlayer1turn;
    }

    private boolean gameSet() {
        return player1 != null && player2 != null;
    }
}
