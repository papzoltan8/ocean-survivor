package com.zoli.survivor.state;

import com.zoli.survivor.internal.ImageCache;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;

public final class Player implements Serializable {

    private transient static final Logger logger = LogManager.getLogger(Player.class);
    private static final int SHARK_DEFENCE_PER_SPEAR = 5;

    private final Inventory inventory = new Inventory();
    private int row;
    private int col;
    private double hunger;
    private double thirst;
    private int sharkDefenceLeft;
    private boolean isDiedFromShark;
    private transient Image image;

    public Player(int row, int col) {
        this.row = row;
        this.col = col;
        reload();
/*
        inventory.put(50, InventoryItem.FISH);
        inventory.put(50, InventoryItem.LEAF);
        inventory.put(50, InventoryItem.WASTE);
        inventory.put(50, InventoryItem.PLANK);
*/
    }

    public void turn(Shark shark) {
        hunger += 0.5;
        thirst += 0.5;
        if (row == shark.getRow() && col == shark.getCol()) {
            if (sharkDefenceLeft > 0) {
                sharkDefenceLeft--;
            } else {
                isDiedFromShark = true;
            }
            logger.debug("Under shark attack! Defence left: " + sharkDefenceLeft);
        }
    }

    public boolean moveWithOffset(int rowOffset, int colOffset) {
        int newRow = row + rowOffset;
        int newCol = col + colOffset;
        if (newRow >= 0 && newRow < World.ROWS && newCol >= 0 && newCol < World.COLS) {
            row = newRow;
            col = newCol;
            return true;
        } else {
            return false;
        }
    }

    public void draw(GraphicsContext gc) {
        if (null != image) {
            gc.drawImage(image, col * World.CELL_SIZE, row * World.CELL_SIZE);
        }
    }

    public boolean isDiedFromHunger() {
        return hunger >= 100;
    }

    public boolean isDiedFromThirst() {
        return thirst >= 100;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public double getHunger() {
        return hunger;
    }

    public double getThirst() {
        return thirst;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void reload() {
        image = ImageCache.get("/player.png");
    }

    public void decreaseHunger(int value) {
        hunger = Math.max(0, hunger - value);
    }

    public void decreaseThirst(int value) {
        thirst = Math.max(0, thirst - value);
    }

    public void addSharkDefence() {
        sharkDefenceLeft += SHARK_DEFENCE_PER_SPEAR;
    }

    public boolean isDiedFromShark() {
        return isDiedFromShark;
    }

    public int getSharkDefence() {
        return sharkDefenceLeft;
    }

}
