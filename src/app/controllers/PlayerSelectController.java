package app.controllers;

import app.Main;
import javafx.fxml.FXML;

public class PlayerSelectController extends AbstractController {
    @FXML
    public void selectPlayer1() {
        model.setTurn(true);
        Main.getInstanece().GameWindow();
    }

    @FXML
    public void selectPlayer2() {
        model.setTurn(false);
        Main.getInstanece().GameWindow();
    }

}
