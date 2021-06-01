package com.zoli.survivor.internal;

import com.zoli.survivor.state.Cell;

public final class CellWithPosition {

    public final Cell cell;
    public final int row;
    public final int col;

    public CellWithPosition(Cell cell, int row, int col) {
        this.cell = cell;
        this.row = row;
        this.col = col;
    }

    @Override
    public String toString() {
        return "CellWithPosition{" +
                "row=" + row +
                ", col=" + col +
                ", cell=" + cell +
                '}';
    }

}
