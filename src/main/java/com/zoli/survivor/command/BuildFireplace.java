package com.zoli.survivor.command;

import com.zoli.survivor.enumeration.InventoryItem;
import com.zoli.survivor.state.*;

public final class BuildFireplace extends Command {

    public static final Inventory requirements = Inventory.of(2, InventoryItem.PLANK).put(4, InventoryItem.LEAF).put(3, InventoryItem.WASTE);

    @Override
    public boolean execute(GameState gs) {
        World world = gs.getWorld();
        Cell cell = world.getCellOfPlayer();
        Player player = world.getPlayer();
        Inventory inventory = player.getInventory();
        if (null != cell && cell.isFreeToBuild()
                && (inventory.take(1, InventoryItem.FIREPLACE) || inventory.take(requirements))) {
            return world.setBuiltItem(new Fireplace(player.getRow(), player.getCol()));
        } else {
            return false;
        }
    }

}
