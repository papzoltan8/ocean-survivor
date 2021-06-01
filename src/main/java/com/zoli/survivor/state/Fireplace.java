package com.zoli.survivor.state;

import com.zoli.survivor.enumeration.InventoryItem;
import com.zoli.survivor.enumeration.ItemState;
import com.zoli.survivor.internal.ImageCache;
import com.zoli.survivor.internal.Util;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Fireplace extends RaftBuiltItem {

    private transient static final Logger logger = LogManager.getLogger(Fireplace.class);
    private static final int MAX_TURNS = 15;

    private InventoryItem food;
    private boolean hasRawFood;
    private boolean hasCookedFood;
    private int turn;

    public Fireplace(int row, int col) {
        super(row, col);
    }

    @Override
    public void turn() {
        if (hasRawFood) {
            turn++;
            if (MAX_TURNS == turn) {
                turn = 0;
                hasRawFood = false;
                hasCookedFood = true;
                logger.debug(food.name() + " is ready.");
            }
        }
    }

    public boolean use(World world) {
        if (!hasRawFood && !hasCookedFood) {
            Inventory inventory = world.getPlayer().getInventory();
            if (inventory.take(1, InventoryItem.FISH)) {
                food = InventoryItem.FISH;
                hasRawFood = true;
                return true;
            } else if (inventory.take(1, InventoryItem.POTATO)) {
                food = InventoryItem.POTATO;
                hasRawFood = true;
                return true;
            } else {
                return false;
            }
        } else if (hasCookedFood) {
            world.getPlayer().decreaseHunger(30);
            hasCookedFood = false;
            return true;
        } else {
            return false;
        }
    }

    public void draw(GraphicsContext gc, int x, int y) {
        if (hasRawFood) {
            gc.setStroke(Color.RED);
            double middle = World.CELL_SIZE / 2.0;
            gc.strokeLine(x, y + middle, x + World.CELL_LAST_PIXEL, y + middle);
            gc.strokeLine(x + middle, y, x + middle, y + World.CELL_LAST_PIXEL);
            gc.strokeLine(x, y, x + World.CELL_LAST_PIXEL, y + World.CELL_LAST_PIXEL);
            gc.strokeLine(x + World.CELL_LAST_PIXEL, y, x, y + World.CELL_LAST_PIXEL);
            gc.setStroke(Color.BLACK);
            Util.drawStripes(gc, x, y);
            gc.drawImage(ImageCache.get("/" + food.name() + "-raw.png"), x, y);
        } else if (hasCookedFood) {
            gc.setStroke(Color.BLACK);
            Util.drawStripes(gc, x, y);
            gc.drawImage(ImageCache.get("/" + food.name() + "-cooked.png"), x, y);
        } else {
            gc.setStroke(Color.BLACK);
            Util.drawStripes(gc, x, y);
        }
    }

    @Override
    public ItemState getState() {
        if (!hasRawFood && !hasCookedFood) {
            return ItemState.EMPTY;
        } else if (hasRawFood) {
            return ItemState.WORKING;
        } else if (hasCookedFood) {
            return ItemState.FINISHED;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "Fireplace{" +
                "turn=" + turn +
                ", food=" + food +
                ", state=" + getState() +
                '}';
    }

}
