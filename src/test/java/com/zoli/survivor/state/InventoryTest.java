package com.zoli.survivor.state;

import com.zoli.survivor.enumeration.InventoryItem;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class InventoryTest {

    @Test
    public void testIsEmpty() {
        Inventory inventory = new Inventory();
        assertTrue(inventory.isEmpty());
    }

    @Test
    public void testOfAndHas() {
        Inventory inventory = Inventory.of(3, InventoryItem.FISH);
        assertTrue(inventory.has(3, InventoryItem.FISH));
        assertTrue(inventory.has(Inventory.of(3, InventoryItem.FISH)));
    }

    @Test
    public void testPutAndTake() {
        Inventory inventory = Inventory.of(3, InventoryItem.FISH);
        assertTrue(inventory.has(3, InventoryItem.FISH));
        assertTrue(inventory.take(3, InventoryItem.FISH));
        assertTrue(inventory.isEmpty());
        inventory.put(3, InventoryItem.FISH);
        inventory.put(Inventory.of(3, InventoryItem.FISH));
        assertTrue(inventory.take(Inventory.of(6, InventoryItem.FISH)));
        assertTrue(inventory.isEmpty());
    }

}