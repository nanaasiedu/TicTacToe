package app.playSchemes;

import app.Coordinate;
import app.Icon;
import app.TicTacToeModel;

/**
 * Created by nman on 13/01/2017.
 */
public abstract class Player {
    private Icon icon;

    public Player(Icon icon) {
        this.icon = icon;
    }

    abstract public Coordinate makeMove(TicTacToeModel model);

    abstract public boolean isHuman();

    public Icon getIcon() {
        return icon;
    }
}
