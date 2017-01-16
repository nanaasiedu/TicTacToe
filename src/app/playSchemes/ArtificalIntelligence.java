package app.playSchemes;

import app.Coordinate;
import app.Icon;
import app.TicTacToeModel;

public class ArtificalIntelligence extends Player {
    public ArtificalIntelligence(Icon icon) {
        super(icon);
    }

    @Override
    public Coordinate makeMove(TicTacToeModel model) {
        return new Coordinate(1,1);
    }
}
