package com.zoli.survivor.internal;

import com.zoli.survivor.state.Cell;
import com.zoli.survivor.state.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public abstract class ShortestPath {

    public static int getDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x2 - x1) + Math.abs(y2 - y1);
    }

    /**
     * Get the shortest path from the shark to the player.
     */
    public static List<CellWithPosition> get(boolean allowAllCell, World world, int srcRow, int srcCol, int dstRow, int dstCol) {
        if (srcRow >= 0 && srcRow < World.ROWS && srcCol >= 0 && srcCol < World.COLS
                && dstRow >= 0 && dstRow < World.ROWS && dstCol >= 0 && dstCol < World.COLS) {
            Entry[][] matrix = getMatrix(allowAllCell, world);
            if ((srcRow == dstRow && srcCol == dstCol) || matrix[dstRow][dstCol].distanceTravelled < 0) {
                return new ArrayList<>();
            } else {
                aStar(matrix, srcRow, srcCol, dstRow, dstCol);
                return trackBackPath(matrix, srcRow, srcCol, dstRow, dstCol);
            }
        } else {
            return new ArrayList<>();
        }
    }

    private static Entry[][] getMatrix(boolean allowAllCell, World world) {
        Cell[][] cells = world.getCells();
        Entry[][] entries = new Entry[World.ROWS][World.COLS];
        for (int i = 0; i < World.ROWS; i++) {
            for (int j = 0; j < World.COLS; j++) {
                Cell cell = cells[i][j];
                int initialValue = allowAllCell || null == cell || cell.getSurfaceType().isResource() ? Integer.MAX_VALUE : -1;
                entries[i][j] = new Entry(i, j, initialValue);
            }
        }
        return entries;
    }

    private static int aStar(Entry[][] matrix, int srcRow, int srcCol, int dstRow, int dstCol) {
        PriorityQueue<Entry> queue = new PriorityQueue<>();
        int best = Integer.MAX_VALUE;
        matrix[srcRow][srcCol].distanceTravelled = 0;
        matrix[srcRow][srcCol].distanceRemained = getDistance(srcRow, srcCol, dstRow, dstCol);
        queue.add(matrix[srcRow][srcCol]);

        while (!queue.isEmpty()) {
            Entry entry = queue.poll();
            if (entry.distanceRemained >= best) {
                break;
            }
            if (entry.row == dstRow && entry.col == dstCol) {
                if (entry.distanceTravelled < best) {
                    best = entry.distanceTravelled;
                }
            }
            addEntry(queue, matrix, entry, entry.row, entry.col - 1, best, dstRow, dstCol);
            addEntry(queue, matrix, entry, entry.row - 1, entry.col, best, dstRow, dstCol);
            addEntry(queue, matrix, entry, entry.row, entry.col + 1, best, dstRow, dstCol);
            addEntry(queue, matrix, entry, entry.row + 1, entry.col, best, dstRow, dstCol);
        }

        return best == Integer.MAX_VALUE ? -1 : best;
    }

    private static void addEntry(PriorityQueue<Entry> queue, Entry[][] matrix, Entry entry, int row, int col, int best, int dstRow, int dstCol) {
        if (row >= 0 && row < World.ROWS && col >= 0 && col < World.COLS) {
            Entry newEntry = matrix[row][col];
            int newDistance = entry.distanceTravelled + 1;
            if (newEntry.distanceTravelled > 0 && newDistance < newEntry.distanceTravelled) {
                newEntry.fromRow = entry.row;
                newEntry.fromCol = entry.col;
                newEntry.distanceTravelled = newDistance;
                newEntry.distanceRemained = getDistance(row, col, dstRow, dstCol);
                if (newEntry.distanceTravelled + newEntry.distanceRemained < best) {
                    queue.add(newEntry);
                }
            }
        }
    }

    private static List<CellWithPosition> trackBackPath(Entry[][] matrix, int srcRow, int srcCol, int dstRow, int dstCol) {
        Entry targetEntry = matrix[dstRow][dstCol];
        List<CellWithPosition> path = new ArrayList<>();
        int row = dstRow;
        int col = dstCol;
        if (Integer.MAX_VALUE != targetEntry.distanceTravelled) {
            while (row != srcRow || col != srcCol) {
                path.add(new CellWithPosition(null, row, col));
                Entry entry = matrix[row][col];
                row = entry.fromRow;
                col = entry.fromCol;
            }
        }
        Collections.reverse(path);
        return path;
    }

    private static class Entry implements Comparable<Entry> {

        public int row;
        public int col;
        public int fromRow;
        public int fromCol;
        public int distanceTravelled;
        public int distanceRemained;

        public Entry(int row, int col, int distanceTravelled) {
            this.row = row;
            this.col = col;
            this.distanceTravelled = distanceTravelled;
            this.distanceRemained = Integer.MAX_VALUE;
        }

        @Override
        public int compareTo(Entry o) {
            return Integer.compare(distanceRemained, o.distanceRemained);
        }

    }

}
