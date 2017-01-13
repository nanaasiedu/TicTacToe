package app;

import app.controllers.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        mainWindow();
    }

    public void mainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/app/views/mainView.fxml"));
            AnchorPane pane = loader.load();

            MainWindowController mainWindowController = loader.getController();

            Scene scene = new Scene(pane);

            primaryStage.setScene(scene);

            primaryStage.show();

        } catch (IOException e) {

        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
