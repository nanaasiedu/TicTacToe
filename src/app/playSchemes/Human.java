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
        model.allowClicks();
        model.waitOnSignal();

        System.out.println("IM FREEE");
        return null;
    }
}
