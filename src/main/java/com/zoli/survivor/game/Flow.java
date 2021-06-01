package com.zoli.survivor.game;

import com.zoli.survivor.enumeration.InventoryItem;
import com.zoli.survivor.enumeration.SurfaceItem;
import com.zoli.survivor.state.Cell;
import com.zoli.survivor.state.Inventory;
import com.zoli.survivor.state.Player;
import com.zoli.survivor.state.World;

import java.util.Random;

public abstract class Flow {

    public static void flow(Cell[][] cells, Player player) {
        // clear last row
        for (int i = 0; i < World.COLS; i++) {
            Cell cell = cells[World.ROWS - 1][i];
            if (cell != null && !cell.isFixed()) {
                cells[World.ROWS - 1][i] = null;
            }
        }
        // move rows
        for (int i = World.ROWS - 2; i >= 0; i--) {
            for (int j = World.COLS - 1; j >= 0; j--) {
                Cell sourceCell = cells[i][j];
                if (null != sourceCell) {
                    SurfaceItem surfaceType = sourceCell.getSurfaceType();
                    if (SurfaceItem.RAFT == surfaceType) {
                        if (null == cells[i + 1][j]) {
                            cells[i + 1][j] = sourceCell.getAndClearTransferable();
                        } else if (cells[i + 1][j].isFixed()) {
                            sourceCell.transferToAndClear(cells[i + 1][j]);
                        } else {
                            cells[i + 1][j] = sourceCell.getAndClearTransferable();
                        }
                    } else if (SurfaceItem.NET == surfaceType) {
                        sourceCell.transferToAndClear(player.getInventory());
                    } else {
                        if (null == cells[i + 1][j]) {
                            cells[i + 1][j] = sourceCell;
                        } else {
                            cells[i + 1][j].putTransferable(sourceCell);
                        }
                        cells[i][j] = null;
                    }
                }
            }
        }
        // create new first row
        Random rnd = new Random();
        int newResources = rnd.nextInt(4);
        for (int i = 0; i < newResources; i++) {
            Inventory inventory = new Inventory();
            SurfaceItem type = null;
            InventoryItem inventoryItem = null;
            double d = rnd.nextDouble();
            if (d <= 0.4) {
                type = SurfaceItem.LEAF;
                inventoryItem = InventoryItem.LEAF;
            } else if (d > 0.4 && d <= 0.68) {
                type = SurfaceItem.PLANK;
                inventoryItem = InventoryItem.PLANK;
            } else if (d > 0.68 && d <= 0.95) {
                type = SurfaceItem.WASTE;
                inventoryItem = InventoryItem.WASTE;
            } else if (d > 0.95) {
                type = SurfaceItem.BARREL;
                for (int j = 0; j < 5; j++) {
                    d = rnd.nextDouble();
                    if (d <= 0.25) {
                        inventory.put(1, InventoryItem.PLANK);
                    } else if (d > 0.25 && d <= 0.5) {
                        inventory.put(1, InventoryItem.LEAF);
                    } else if (d > 0.5 && d <= 0.75) {
                        inventory.put(1, InventoryItem.WASTE);
                    } else if (d > 0.75) {
                        inventory.put(1, InventoryItem.POTATO);
                    }
                }
            }
            if (inventory.isEmpty()) {
                inventory.put(1, inventoryItem);
            }
            int column = rnd.nextInt(World.COLS);
            Cell cell = cells[0][column];
            if (null != cell && cell.isFixed()) {
                cells[0][column].setSubsurface(type, inventory);
            } else {
                cells[0][column] = new Cell(type, inventory);
            }
        }
    }

}
