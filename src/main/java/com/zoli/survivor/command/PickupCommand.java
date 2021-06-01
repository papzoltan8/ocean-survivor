package com.zoli.survivor.command;

import com.zoli.survivor.state.Cell;
import com.zoli.survivor.state.GameState;
import com.zoli.survivor.state.World;

public final class PickupCommand extends Command {

    public boolean execute(GameState gs) {
        World world = gs.getWorld();
        Cell cell = world.getCellOfPlayer();
        if (null != cell) {
            return cell.pickup(world, world.getPlayer().getRow(), world.getPlayer().getCol());
        }
        return false;
    }

}
