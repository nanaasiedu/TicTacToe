package app.playSchemes;

import app.Coordinate;
import app.Icon;
import app.TicTacToeModel;

public class Human extends Player {
    public Human(Icon icon) {
        super(icon);
    }

    @Override
    public synchronized Coordinate makeMove(TicTacToeModel model) {
        // Coordinate of square the user clicks
        Coordinate clickedCoordinate;

        do {
            // The buttons are activated to allow the model to detect button clicks
            model.allowClicks();
            // The game thread sleeps on a signal. It is woken up by a button click event and provided with the
            // registered coordinates of the clicked square
            clickedCoordinate = model.waitOnSignal();

        } while (clickedCoordinate == null &&
                 !model.isMoveValid(clickedCoordinate.getRow(), clickedCoordinate.getCol()));

        return clickedCoordinate;
    }
}
