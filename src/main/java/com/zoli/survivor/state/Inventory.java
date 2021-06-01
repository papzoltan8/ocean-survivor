package com.zoli.survivor.state;

import com.zoli.survivor.command.*;
import com.zoli.survivor.enumeration.InventoryItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class Inventory implements Serializable {

    private final EnumMap<InventoryItem, Integer> items = new EnumMap<>(InventoryItem.class);

    public static Inventory of(int count, InventoryItem inventoryItem) {
        Inventory inventory = new Inventory();
        inventory.items.put(inventoryItem, count);
        return inventory;
    }

    public boolean has(int count, InventoryItem inventoryItem) {
        Integer value = items.get(inventoryItem);
        return null != value && value >= count;
    }

    public boolean has(Inventory inventory) {
        for (Map.Entry<InventoryItem, Integer> entry : inventory.items.entrySet()) {
            Integer requested = entry.getValue();
            if (requested > 0) {
                Integer current = items.get(entry.getKey());
                if (null == current) {
                    return false;
                } else if (requested > current) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean take(int count, InventoryItem inventoryItem) {
        if (has(count, inventoryItem)) {
            items.merge(inventoryItem, count, (oldValue, value) -> oldValue - value);
            return true;
        } else {
            return false;
        }
    }

    public boolean take(Inventory inventory) {
        if (has(inventory)) {
            inventory.items.forEach((key, value1) -> items.merge(key, value1, (oldValue, value) -> oldValue - value));
            return true;
        } else {
            return false;
        }
    }

    public Inventory put(int count, InventoryItem inventoryItem) {
        items.merge(inventoryItem, count, Integer::sum);
        return this;
    }

    public Inventory put(Inventory inventory) {
        inventory.items.forEach((key, value) -> items.merge(key, value, Integer::sum));
        return this;
    }

    @Override
    public String toString() {
        String inventory = items.isEmpty()
                ? "You don't have anything in your inventory."
                : items.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey().name().replace("_", "") + ": " + entry.getValue())
                .collect(Collectors.joining("\n"));
        List<String> canBuild = new ArrayList<>();
        if (has(BuildFireplace.requirements)) {
            canBuild.add("fireplace");
        }
        if (has(BuildWaterFilter.requirements)) {
            canBuild.add("water filter");
        }
        if (has(BuildSpear.requirements)) {
            canBuild.add("spear");
        }
        if (has(BuildNet.requirements)) {
            canBuild.add("net");
        }
        if (has(BuildRaft.requirements)) {
            canBuild.add("raft");
        }
        return inventory + (canBuild.size() > 0 ? "\nYou can build: " + String.join(", ", canBuild) : "");
    }

    public boolean isEmpty() {
        return items.isEmpty() || items.values().stream().mapToInt(Integer::intValue).sum() == 0;
    }

}
