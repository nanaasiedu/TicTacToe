package app.controllers;
import app.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainWindowController extends AbstractController {

    @FXML
    public void openPlayerSelectScene(ActionEvent event) {
        String mode = ((Button)event.getSource()).getId();

        model.setPlayer1(mode.charAt(0) == 'H');
        model.setPlayer2(mode.charAt(1) == 'H');

        Main.getInstanece().openPlayerSelectWindow();
    }

}
