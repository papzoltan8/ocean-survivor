package com.zoli.survivor.state;

import com.zoli.survivor.enumeration.ItemState;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class WaterFilter extends RaftBuiltItem {

    private transient static final Logger logger = LogManager.getLogger(WaterFilter.class);
    private static final int MAX_TURNS = 25;

    private boolean isFull;
    private int turn;

    public WaterFilter(int row, int col) {
        super(row, col);
    }

    @Override
    public void turn() {
        if (!isFull) {
            turn++;
            if (MAX_TURNS == turn) {
                turn = 0;
                isFull = true;
                logger.debug("Water filer is full.");
            }
        }
    }

    public boolean use(World world) {
        if (isFull) {
            world.getPlayer().decreaseThirst(20);
            isFull = false;
            return true;
        } else {
            return false;
        }
    }

    public void draw(GraphicsContext gc, int x, int y) {
        gc.setStroke(Color.BLACK);
        if (isFull) {
            gc.setFill(Color.LIGHTBLUE);
            gc.fillRect(x + 12, y + 6, 8, 20);
        } else {
            gc.setFill(Color.LIGHTBLUE);
            int offset = 20 - (int) Math.floor(20 * (double) turn / (double) MAX_TURNS);
            gc.fillRect(x + 12, y + 6 + offset, 8, 20 - offset);
        }
        gc.strokeRect(x + 12, y + 6, 8, 20);
        gc.strokeLine(x + 6, y + 26, x + 26, y + 26);
    }

    @Override
    public ItemState getState() {
        return isFull ? ItemState.FINISHED : ItemState.WORKING;
    }

    @Override
    public String toString() {
        return "WaterFilter{" +
                "turn=" + turn +
                ", state=" + getState() +
                '}';
    }

}
