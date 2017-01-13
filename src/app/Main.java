package app;

import app.controllers.MainWindowController;
import app.controllers.PlayerSelectController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

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
        this.model = new TicTacToeModel();

        mainWindow();
    }

    public void mainWindow() {
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

    public void playerSelectWindow() {
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

    public static void main(String[] args) {
        launch(args);
    }
}