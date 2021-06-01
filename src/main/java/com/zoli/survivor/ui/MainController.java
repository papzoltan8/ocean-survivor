package com.zoli.survivor.ui;

import com.zoli.survivor.command.Command;
import com.zoli.survivor.game.AI;
import com.zoli.survivor.game.GameMain;
import com.zoli.survivor.internal.Util;
import com.zoli.survivor.state.GameState;
import com.zoli.survivor.state.Player;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainController {

    private static final Logger logger = LogManager.getLogger(MainController.class);

    private final FileChooser fileChooser;

    @FXML
    private Label turns;
    @FXML
    private Label hunger;
    @FXML
    private Label thirst;
    @FXML
    private Label sharkDefence;
    @FXML
    private TextArea details;
    @FXML
    private TextArea instructions;
    @FXML
    private Canvas canvas;
    @FXML
    private Button startStopAI;

    private Timer timer;
    private Scene scene;
    private GameMain gameMain;
    private AI ai;

    public MainController() {
        fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Save game", "*.sav")
        );
        String home = System.getProperty("user.home");
        String os = System.getProperty("os.name");
        File path;
        if (os.toLowerCase(Locale.ROOT).contains("windows")) {
            path = Paths.get(home, "documents").toFile();
        } else {
            path = new File(home);
        }
        fileChooser.setInitialDirectory(path);
    }

    public void setScene(Scene scene) {
        this.scene = scene;
        scene.setOnKeyPressed(e -> {
            if (null != timer || e.isAltDown() || e.isControlDown()) {
                return;
            }

            String input;
            if (KeyCode.LEFT == e.getCode() || KeyCode.NUMPAD4 == e.getCode()) {
                input = "move-west";
            } else if (KeyCode.UP == e.getCode() || KeyCode.NUMPAD8 == e.getCode()) {
                input = "move-north";
            } else if (KeyCode.RIGHT == e.getCode() || KeyCode.NUMPAD6 == e.getCode()) {
                input = "move-east";
            } else if (KeyCode.DOWN == e.getCode() || KeyCode.NUMPAD2 == e.getCode()) {
                input = "move-south";
            } else if (KeyCode.SPACE == e.getCode()) {
                input = "do-nothing";
            } else {
                input = e.getText();
            }
            gameMain.turn(input);
            gameMain.draw(canvas.getGraphicsContext2D());
        });
    }

    @FXML
    private void initialize() {
        canvas.setWidth(1152);
        canvas.setHeight(768);

        instructions.setText("Survive " + GameState.MAX_TURNS + " turns!" +
                "\nMovement: arrows or numpad\nUse things, fishing: e" +
                "\nBuild a fireplace: f\nBuild a water filter: w\nBuild a net: n\nBuild a raft: r\nBuild a spear: s" +
                "\nPick up raft, net: p\nDo nothing: space");
        UIUpdate UIUpdate = gs -> {
            turns.setText("Turns: " + gs.getTurn());
            Player player = gs.getWorld().getPlayer();
            double hunger = player.getHunger();
            this.hunger.setText("Hunger: " + hunger + " %");
            this.hunger.setTextFill(hunger >= 60.0 ? Color.RED : Color.BLACK);
            double thirst = player.getThirst();
            this.thirst.setText("Thirst: " + thirst + " %");
            this.thirst.setTextFill(thirst >= 60.0 ? Color.RED : Color.BLACK);
            if (thirst == 60.0 || thirst == 80.0 || thirst == 90.0 || hunger == 60.0 || hunger == 80.0 || hunger == 90.0) {
                Runnable runnable = (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
                if (runnable != null) {
                    runnable.run();
                }
            }
            sharkDefence.setText("Shark defence: " + player.getSharkDefence());
            details.setText(player.getInventory().toString());
            if (gs.isGameOver()) {
                String message = switch (gs.getGameOverReason()) {
                    case "winning" -> "Congratulation, you win!";
                    case "hunger" -> "You have died from hunger.";
                    case "thirst" -> "You have died from thirst.";
                    case "shark" -> "You have died in shark attack.";
                    default -> "Unknown end condition: " + gs.getGameOverReason();
                };
                Util.showInfoMessage(message);
            }
        };

        gameMain = new GameMain(UIUpdate);
        gameMain.draw(canvas.getGraphicsContext2D());
    }

    @FXML
    private void load() {
        fileChooser.setTitle("Open saved game");
        File file = fileChooser.showOpenDialog(scene.getWindow());
        if (null != file) {
            gameMain.load(file);
            gameMain.draw(canvas.getGraphicsContext2D());
        }
        canvas.requestFocus();
    }

    @FXML
    private void save() {
        fileChooser.setTitle("Save game");
        File file = fileChooser.showSaveDialog(scene.getWindow());
        if (null != file) {
            gameMain.save(file);
        }
    }

    @FXML
    private void handleInputAgain() {
        canvas.requestFocus();
    }

    @FXML
    private void newGame() {
        stopAI();
        gameMain.newGame();
        gameMain.draw(canvas.getGraphicsContext2D());
        canvas.requestFocus();
    }

    @FXML
    private void startStopAI() {
        if (null == timer) {
            ai = new AI(gameMain.getGameState());
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        Command command = ai.getCommand();
                        if (null == command) {
                            stopAI();
                        } else {
                            if (!gameMain.execute(command)) {
                                gameMain.execute(new Command());
                            }
                            gameMain.draw(canvas.getGraphicsContext2D());
                        }
                    });
                }
            }, 0, 250);
            startStopAI.setText("Stop AI");
            logger.info("AI started.");
        } else {
            stopAI();
        }
    }

    private void stopAI() {
        if (null != timer) {
            timer.cancel();
        }
        timer = null;
        ai = null;
        startStopAI.setText("Start AI");
        logger.info("AI stopped.");
    }

}
