package com.zoli.survivor.state;

import com.zoli.survivor.enumeration.SurfaceItem;
import com.zoli.survivor.game.Flow;
import com.zoli.survivor.internal.CellWithPosition;
import com.zoli.survivor.internal.ImageCache;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class World implements Serializable {

    public static final int CELL_SIZE = 32;

    public static final int CELL_LAST_PIXEL = 31;
    public static final int ROWS = 24;
    public static final int COLS = 36;
    private static final int WIDTH = COLS * CELL_SIZE;
    private static final int HEIGHT = ROWS * CELL_SIZE;
    private transient static final Logger logger = LogManager.getLogger(World.class);
    private final Cell[][] cells;
    private final Player player;
    private final Shark shark;
    private final List<RaftBuiltItem> raftBuiltItemList = new ArrayList<>();
    private final List<Cell> nets = new ArrayList<>();
    private final List<Cell> rafts = new ArrayList<>();

    public World() {
        int centerCol = COLS / 2;
        int centerRow = ROWS / 2;
        cells = new Cell[ROWS][COLS];
        cells[centerRow][centerCol] = new Cell(SurfaceItem.RAFT);
        rafts.add(cells[centerRow][centerCol]);
        cells[centerRow + 1][centerCol] = new Cell(SurfaceItem.RAFT);
        rafts.add(cells[centerRow + 1][centerCol]);
        cells[centerRow][centerCol + 1] = new Cell(SurfaceItem.RAFT);
        rafts.add(cells[centerRow][centerCol + 1]);
        cells[centerRow + 1][centerCol + 1] = new Cell(SurfaceItem.RAFT);
        rafts.add(cells[centerRow + 1][centerCol + 1]);
        player = new Player(centerRow, centerCol);
        Random rnd = new Random();
        int sign1 = rnd.nextDouble() < 0.5 ? -1 : 1;
        int sign2 = rnd.nextDouble() < 0.5 ? -1 : 1;
        shark = new Shark(centerRow + sign1 * (rnd.nextInt(centerRow / 2) + 2), centerCol + sign2 * (rnd.nextInt(centerCol / 2) + 2));
        for (int i = 0; i < ROWS; i++) {
            Flow.flow(cells, player);
        }
    }

    public void turn() {
        for (RaftBuiltItem raftBuiltItem : raftBuiltItemList) {
            raftBuiltItem.turn();
        }
        for (Cell net : nets) {
            net.turn(player.getInventory());
        }
        // shark turns before player
        shark.turn(this);
        player.turn(shark);
        Flow.flow(cells, player);
    }

    public Cell getCell(int row, int col) {
        return (row >= 0 && col >= 0 && row < ROWS && col < COLS) ? cells[row][col] : null;
    }

    public Cell getCellOfPlayer() {
        return cells[player.getRow()][player.getCol()];
    }

    public Cell[][] getCells() {
        return cells;
    }

    public boolean createCellUnderPlayer(SurfaceItem surfaceItem) {
        if (SurfaceItem.RAFT == surfaceItem || SurfaceItem.NET == surfaceItem) {
            int row = player.getRow();
            int col = player.getCol();
            if (null == cells[row][col]) {
                List<CellWithPosition> neighbourCells = getNeighbourCells();
                if ((SurfaceItem.RAFT == surfaceItem && rafts.isEmpty()) || neighbourCells.stream().anyMatch(c -> SurfaceItem.RAFT == c.cell.getSurfaceType())) {
                    cells[row][col] = new Cell(surfaceItem);
                    if (SurfaceItem.RAFT == surfaceItem) {
                        rafts.add(cells[row][col]);
                    } else if (SurfaceItem.NET == surfaceItem) {
                        nets.add(cells[row][col]);
                    }
                    logger.info(surfaceItem.name() + " built at " + row + ", " + col);
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public void deleteCell(int row, int col) {
        Cell cell = getCell(row, col);
        if (null != cell) {
            if (null != cell.getBuiltItem()) {
                raftBuiltItemList.remove(cell.getBuiltItem());
            }
            rafts.remove(cell);
            nets.remove(cell);
            cells[row][col] = null;
        }
    }

    public void draw(GraphicsContext gc) {

        // draw cells
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                int x = j * CELL_SIZE;
                int y = i * CELL_SIZE;
                gc.drawImage(ImageCache.get("/water.png"), x, y);
                if (null != cells[i][j]) {
                    cells[i][j].draw(gc, x, y);
                }
            }
        }

        player.draw(gc);
        shark.draw(gc);

        // draw grid
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);
        for (int i = 0; i <= ROWS + 1; i++) {
            int row = i * CELL_SIZE;
            gc.strokeLine(0, row, WIDTH - 1, row - 1);
        }
        for (int i = 0; i <= COLS + 1; i++) {
            int col = i * CELL_SIZE;
            gc.strokeLine(col, 0, col - 1, HEIGHT - 1);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public Shark getShark() {
        return shark;
    }

    public void reload() {
        player.reload();
        shark.reload();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (null != cells[i][j]) {
                    cells[i][j].reload();
                }
            }
        }
    }

    public boolean setBuiltItem(RaftBuiltItem raftBuiltItem) {
        Cell cell = getCellOfPlayer();
        if (null != cell && cell.setBuiltItem(raftBuiltItem)) {
            raftBuiltItemList.add(raftBuiltItem);
            logger.debug(raftBuiltItem.getClass().getSimpleName() + " built on raft.");
            return true;
        } else {
            return false;
        }
    }

    public List<CellWithPosition> getNeighbourCells() {
        int row = player.getRow();
        int col = player.getCol();
        List<CellWithPosition> cells = new ArrayList<>(9);
        add(cells, row, col);

        add(cells, row - 1, col - 1);
        add(cells, row - 1, col);
        add(cells, row - 1, col + 1);

        add(cells, row, col - 1);
        add(cells, row, col + 1);

        add(cells, row + 1, col - 1);
        add(cells, row + 1, col);
        add(cells, row + 1, col + 1);

        return cells;
    }

    public boolean isPlayerNotUnderShark() {
        return player.getCol() != shark.getCol() || player.getRow() != shark.getRow();
    }

    public boolean isPlayerInWater() {
        Cell cell = cells[player.getRow()][player.getCol()];
        return null == cell || cell.getSurfaceType().isResource();
    }

    private void add(List<CellWithPosition> cells, int row, int col) {
        Cell cell = getCell(row, col);
        if (null != cell) {
            cells.add(new CellWithPosition(cell, row, col));
        }
    }

    public boolean isFixedCell(int row, int col) {
        Cell cell = getCell(row, col);
        return null != cell && cell.getSurfaceType().isFixed();
    }

    public RaftBuiltItem getBuiltItem(Class<? extends RaftBuiltItem> builtItemClass) {
        for (RaftBuiltItem raftBuiltItem : raftBuiltItemList) {
            if (raftBuiltItem.getClass() == builtItemClass) {
                return raftBuiltItem;
            }
        }
        return null;
    }

    public List<Cell> getNets() {
        return nets;
    }

    public List<Cell> getRafts() {
        return rafts;
    }

}
