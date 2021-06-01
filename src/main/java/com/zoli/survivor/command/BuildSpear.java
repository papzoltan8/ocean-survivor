package com.zoli.survivor.command;

import com.zoli.survivor.enumeration.InventoryItem;
import com.zoli.survivor.state.GameState;
import com.zoli.survivor.state.Inventory;
import com.zoli.survivor.state.Player;

public final class BuildSpear extends Command {

    public static final Inventory requirements = Inventory.of(4, InventoryItem.PLANK).put(4, InventoryItem.LEAF).put(4, InventoryItem.WASTE);

    @Override
    public boolean execute(GameState gs) {
        Player player = gs.getWorld().getPlayer();
        if (player.getInventory().take(requirements)) {
            player.addSharkDefence();
            return true;
        }
        return false;
    }

}
