package com.zoli.survivor.state;

import com.zoli.survivor.internal.CellWithPosition;
import com.zoli.survivor.internal.ImageCache;
import com.zoli.survivor.internal.ShortestPath;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

public final class Shark implements Serializable {

    private transient static final Logger logger = LogManager.getLogger(Shark.class);

    private final Random rnd = new Random();
    private int row;
    private int col;
    private int rowOffset = 99;
    private int colOffset = 99;
    private int continuousSwimming;
    private transient Image image;

    public Shark() {
        reload();
    }

    public Shark(int row, int col) {
        this.row = row;
        this.col = col;
        reload();
    }

    public void draw(GraphicsContext gc) {
        if (null != image) {
            gc.drawImage(image, col * World.CELL_SIZE, row * World.CELL_SIZE);
        }
    }

    public void reload() {
        image = ImageCache.get("/shark_0_1.png");
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void turn(World world) {
        int sharkRow = world.getShark().getRow();
        int sharkCol = world.getShark().getCol();
        int playerRow = world.getPlayer().getRow();
        int playerCol = world.getPlayer().getCol();
        if (sharkRow == playerRow && sharkCol == playerCol) {
            return;
        }

        int newRow = -1;
        int newCol = -1;

        if (world.isPlayerInWater()) {
            List<CellWithPosition> path = ShortestPath.get(false, world, sharkRow, sharkCol, playerRow, playerCol);
            if (path.size() > 0) {
                row = newRow = path.get(0).row;
                col = newCol = path.get(0).col;
                logger.debug(String.format("%d cells left until the player.", path.size() - 1));
            }
        }

        if (-1 == newRow || -1 == newCol) {
            if (rowOffset > 1) {
                getNewOffset(rnd);
            }
            int attempts = 0;
            while (true) {
                newRow = row + rowOffset;
                newCol = col + colOffset;
                if ((0 != rowOffset || 0 != colOffset) && Math.abs(rowOffset) != Math.abs(colOffset) && newCol >= 0 && newCol < World.COLS && newRow >= 0 && newRow < World.ROWS) {
                    if (world.isFixedCell(newRow, newCol)) {
                        getNewOffset(rnd);
                    } else {
                        row = newRow;
                        col = newCol;
                        image = ImageCache.get("/shark_" + rowOffset + "_" + colOffset + ".png");
                        continuousSwimming++;
                        if (continuousSwimming > 10) {
                            getNewOffset(rnd);
                        }
                        break;
                    }
                } else {
                    getNewOffset(rnd);
                }

                attempts++;
                if (attempts > 20) {
                    break;
                }
            }
        }
    }

    private void getNewOffset(Random rnd) {
        continuousSwimming = 0;
        rowOffset = rnd.nextInt(3) - 1;
        colOffset = rnd.nextInt(3) - 1;
    }
}
