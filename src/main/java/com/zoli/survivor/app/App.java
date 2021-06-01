package com.zoli.survivor.app;

import com.zoli.survivor.ui.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * JavaFX App
 */
public final class App extends Application {

    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        logger.info("Starting application...");
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        MainController mainController = fxmlLoader.getController();
        mainController.setScene(scene);
        stage.setTitle("Ocean survivor game");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        logger.info("Stopping application...");
    }

}