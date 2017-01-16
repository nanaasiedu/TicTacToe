package app.controllers;

import app.TicTacToeModel;
import javafx.scene.Node;

/**
 * Created by nman on 13/01/2017.
 */
public abstract class AbstractController {
    protected TicTacToeModel model;

    public void setModel(TicTacToeModel model) {
        this.model = model;
    }

}
