package com.zoli.survivor.state;

import javafx.scene.canvas.GraphicsContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;

public final class GameState implements Serializable {

    public static final int MAX_TURNS = 999;
    private transient static final Logger logger = LogManager.getLogger(GameState.class);

    private final World world = new World();
    private boolean isGameOver;
    private String gameOverReason;
    private int turns;

    public void turn() {
        turns++;
        logger.info(turns + ". turn");
        world.turn();
        if (world.getPlayer().isDiedFromShark()) {
            setGameOver("shark");
        } else if (world.getPlayer().isDiedFromHunger()) {
            setGameOver("hunger");
        } else if (world.getPlayer().isDiedFromThirst()) {
            setGameOver("thirst");
        } else if (MAX_TURNS == turns) {
            setGameOver("winning");
        }
    }

    public void draw(GraphicsContext gc) {
        world.draw(gc);
    }

    public int getTurn() {
        return turns;
    }

    public World getWorld() {
        return world;
    }

    public void reload() {
        world.reload();
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    private void setGameOver(String gameOverReason) {
        isGameOver = true;
        this.gameOverReason = gameOverReason;
    }

    public String getGameOverReason() {
        return gameOverReason;
    }

}
