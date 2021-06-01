package com.zoli.survivor.state;

import com.zoli.survivor.enumeration.InventoryItem;
import com.zoli.survivor.enumeration.SurfaceItem;
import com.zoli.survivor.internal.ImageCache;
import com.zoli.survivor.internal.Util;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.Serializable;
import java.util.Random;

public final class Cell implements Serializable {

    private SurfaceItem surfaceItem;
    private SurfaceItem subSurfaceItem;
    private RaftBuiltItem builtItem;
    private Inventory inventory;
    private transient Image image;
    private Random rnd;

    public Cell(SurfaceItem surfaceItem) {
        this.surfaceItem = surfaceItem;
        if (SurfaceItem.NET == surfaceItem) {
            rnd = new Random();
        }
        reload();
    }

    public Cell(SurfaceItem surfaceItem, Inventory inventory) {
        this(surfaceItem);
        this.inventory = inventory;
    }

    public boolean use(World world, int row, int col) {
        if (null != inventory && !inventory.isEmpty() && null == subSurfaceItem) {
            world.getPlayer().getInventory().put(inventory);
            world.deleteCell(row, col);
            return true;
        }
        return null != builtItem && builtItem.use(world);
    }

    public void reload() {
        if (SurfaceItem.RAFT != surfaceItem && SurfaceItem.NET != surfaceItem) {
            image = ImageCache.get("/" + surfaceItem.name() + ".png");
        }
    }

    public void draw(GraphicsContext gc, int x, int y) {
        if (SurfaceItem.RAFT == surfaceItem) {
            gc.drawImage(ImageCache.get("/raft.png"), x, y);
            if (null != builtItem) {
                builtItem.draw(gc, x, y);
            }
        } else if (SurfaceItem.NET == surfaceItem) {
            gc.setStroke(Color.rgb(180, 160, 110));
            Util.drawGridPattern(gc, x, y);
            Util.drawPlusSign(gc, x, y);
            gc.strokeRect(x + 1, y + 1, World.CELL_LAST_PIXEL - 1, World.CELL_LAST_PIXEL - 1);
            if (null != subSurfaceItem && subSurfaceItem.isResource()) {
                gc.drawImage(ImageCache.get("/" + subSurfaceItem.name() + ".png"), x, y);
            }
        } else if (null != image) {
            gc.drawImage(image, x, y);
        }
    }

    public void putTransferable(Cell cell) {
        subSurfaceItem = cell.surfaceItem;
        inventory = cell.inventory;
    }

    public void transferToAndClear(Cell cell) {
        cell.subSurfaceItem = subSurfaceItem;
        cell.inventory = inventory;
        subSurfaceItem = null;
        inventory = null;
    }

    public void transferToAndClear(Inventory inventory) {
        if (null != this.inventory) {
            inventory.put(this.inventory);
        }
        subSurfaceItem = null;
        this.inventory = null;
    }

    public Cell getAndClearTransferable() {
        Cell cell = null;
        if (null != subSurfaceItem) {
            cell = new Cell(subSurfaceItem, inventory);
            subSurfaceItem = null;
            inventory = null;
        }
        return cell;
    }

    public boolean isFixed() {
        return SurfaceItem.RAFT == surfaceItem || SurfaceItem.NET == surfaceItem;
    }

    public boolean isFreeToBuild() {
        return SurfaceItem.RAFT == surfaceItem && null == builtItem;
    }

    public RaftBuiltItem getBuiltItem() {
        return builtItem;
    }

    public boolean setBuiltItem(RaftBuiltItem builtItem) {
        if (isFreeToBuild()) {
            this.builtItem = builtItem;
            return true;
        } else {
            return false;
        }
    }

    public boolean pickup(World world, int row, int col) {
        Inventory inventory = world.getPlayer().getInventory();
        if (SurfaceItem.NET == surfaceItem) {
            inventory.put(1, InventoryItem.NET);
            world.deleteCell(row, col);
            return true;
        } else if (SurfaceItem.RAFT == surfaceItem && world.getCell(row, col) != null) {
            inventory.put(1, InventoryItem.RAFT);
            RaftBuiltItem builtItem = world.getCell(row, col).getBuiltItem();
            if (builtItem instanceof WaterFilter) {
                inventory.put(1, InventoryItem.WATER_FILTER);
            } else if (builtItem instanceof Fireplace) {
                inventory.put(1, InventoryItem.FIREPLACE);
            }
            world.deleteCell(row, col);
            return true;
        } else {
            return false;
        }
    }

    public boolean turnInto(SurfaceItem surfaceItem) {
        if (surfaceItem.isResource()) {
            subSurfaceItem = this.surfaceItem;
            this.surfaceItem = surfaceItem;
            return true;
        } else {
            return false;
        }
    }

    public SurfaceItem getSurfaceType() {
        return surfaceItem;
    }


    public void setSubsurface(SurfaceItem type, Inventory inventory) {
        subSurfaceItem = type;
        if (null == this.inventory) {
            this.inventory = new Inventory();
        }
        this.inventory.put(inventory);
    }

    public void turn(Inventory playerInventory) {
        if (SurfaceItem.NET == surfaceItem && null != rnd && rnd.nextDouble() < 0.005) {
            playerInventory.put(1, InventoryItem.FISH);
        }
    }

    @Override
    public String toString() {
        return "Cell{" +
                "surfaceItem=" + surfaceItem +
                ", subSurfaceItem=" + subSurfaceItem +
                ", builtItem=" + builtItem +
                ", inventory=" + inventory +
                '}';
    }

}
