package com.zoli.survivor.state;

import com.zoli.survivor.enumeration.ItemState;
import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;

public abstract class RaftBuiltItem implements Serializable {

    public final int row;
    public final int col;

    public RaftBuiltItem(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public abstract void turn();

    public abstract boolean use(World world);

    public abstract void draw(GraphicsContext gc, int x, int y);

    public abstract ItemState getState();

}
