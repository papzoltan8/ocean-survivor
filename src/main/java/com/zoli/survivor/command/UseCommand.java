package com.zoli.survivor.command;

import com.zoli.survivor.enumeration.InventoryItem;
import com.zoli.survivor.internal.CellWithPosition;
import com.zoli.survivor.state.GameState;
import com.zoli.survivor.state.World;

import java.util.List;
import java.util.Random;

public final class UseCommand extends Command {

    @Override
    public boolean execute(GameState gs) {
        World world = gs.getWorld();
        List<CellWithPosition> cells = world.getNeighbourCells();
        for (CellWithPosition cell : cells) {
            if (cell.cell.use(world, cell.row, cell.col)) {
                return true;
            }
        }

        // fishing
        if (null == world.getCellOfPlayer()) {
            Random rnd = new Random();
            if (rnd.nextDouble() <= 0.20) {
                world.getPlayer().getInventory().put(1, InventoryItem.FISH);
            }
            return true;
        }

        return false;
    }

}
