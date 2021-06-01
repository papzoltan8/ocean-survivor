package com.zoli.survivor.command;

import com.zoli.survivor.enumeration.InventoryItem;
import com.zoli.survivor.state.*;

public final class BuildWaterFilter extends Command {

    public static final Inventory requirements = Inventory.of(2, InventoryItem.LEAF).put(4, InventoryItem.WASTE);

    @Override
    public boolean execute(GameState gs) {
        World world = gs.getWorld();
        Cell cell = world.getCellOfPlayer();
        Player player = world.getPlayer();
        if (null != cell && cell.isFreeToBuild()
                && (player.getInventory().take(1, InventoryItem.WATER_FILTER) || player.getInventory().take(requirements))) {
            return world.setBuiltItem(new WaterFilter(player.getRow(), player.getCol()));
        } else {
            return false;
        }
    }

}
