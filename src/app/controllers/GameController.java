package app.controllers;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class GameController extends AbstractController {
    public static int GRID_SPAN = 1;
    public static String HUMAN_TEXT = "(Human) pick a square";
    public static String AI_TEXT = "(AI) is thinking...";
    public static String WRONG_MOVE_TEXT = "Please pick a empty square!";
    public static String ENG_GAME_TEXT = "GAME FINISHED! AND THE WINNER IS : ";
    public static String DRAW_TEXT = "GAME IS A DRAW!!!";

    private Label gameLabel;

    public void setGameText(String text) {
        if (gameLabel == null) return;

        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                gameLabel.setText(text);
            }
        });

    }

    public void setGameLabel(Label label) {
        this.gameLabel = label;
    };

}
