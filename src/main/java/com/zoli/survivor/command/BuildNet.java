package com.zoli.survivor.command;

import com.zoli.survivor.enumeration.InventoryItem;
import com.zoli.survivor.enumeration.SurfaceItem;
import com.zoli.survivor.state.Cell;
import com.zoli.survivor.state.GameState;
import com.zoli.survivor.state.Inventory;
import com.zoli.survivor.state.World;

public final class BuildNet extends Command {

    public static final Inventory requirements = Inventory.of(2, InventoryItem.PLANK).put(6, InventoryItem.LEAF);

    @Override
    public boolean execute(GameState gs) {
        World world = gs.getWorld();
        Inventory inventory = world.getPlayer().getInventory();
        if (inventory.has(1, InventoryItem.NET) || inventory.has(requirements)) {
            Cell cell = world.getCellOfPlayer();
            if (null == cell) {
                if (world.createCellUnderPlayer(SurfaceItem.NET)) {
                    if (!inventory.take(1, InventoryItem.NET)) {
                        inventory.take(requirements);
                    }
                    return true;
                }
            } else if (cell.getSurfaceType().isResource() && world.isPlayerNotUnderShark()) {
                if (!inventory.take(1, InventoryItem.NET)) {
                    inventory.take(requirements);
                }
                return cell.turnInto(SurfaceItem.NET);
            }
        }

        return false;
    }
}
