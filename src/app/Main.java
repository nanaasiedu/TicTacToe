package app;

import app.controllers.GameController;
import app.controllers.MainWindowController;
import app.controllers.PlayerSelectController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class Main extends Application {

    private Stage primaryStage;
    private TicTacToeModel model;
    public static Main instance;

    public Main() {
        instance = this;
    }

    public static Main getInstanece() {
        return instance;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

        this.model = new TicTacToeModel();

        openMainWindow();
    }

    public void openMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/app/views/mainView.fxml"));
            AnchorPane pane = loader.load();

            MainWindowController mainWindowController = loader.getController();
            mainWindowController.setModel(model);

            Scene scene = new Scene(pane);

            primaryStage.setScene(scene);

            primaryStage.show();

        } catch (IOException e) {

        }
    }

    public void openPlayerSelectWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/app/views/playerSelectView.fxml"));
            AnchorPane pane = loader.load();

            PlayerSelectController psController = loader.getController();
            psController.setModel(model);

            Scene scene = new Scene(pane);

            primaryStage.close();
            primaryStage.setScene(scene);

            primaryStage.show();

        } catch (IOException e) {

        }
    }

    public void openGameWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/app/views/GameView.fxml"));
            AnchorPane pane = loader.load();
            GridPane gridPane = (GridPane) pane.lookup("#buttonGrid");
            BoardSpace[][] board = model.getBoard();

            for (int row = 0; row < board.length; row++) {
                for (int col = 0; col < board[0].length; col++) {
                    gridPane.add(board[row][col], col, row, GameController.GRID_SPAN, GameController.GRID_SPAN);
                }
            }

            GameController gameController = loader.getController();
            gameController.setModel(model);
            gameController.setGameLabel((Label)pane.lookup("#GameDescription_txt"));
            model.setController(gameController);

            Scene scene = new Scene(pane);
            primaryStage.close();
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {}

        // Begin game
        (new Thread(model)).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
