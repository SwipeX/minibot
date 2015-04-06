package com.minibot.api.method;

import com.minibot.api.util.Array;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.WidgetComponent;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class Inventory {

    private static final int INVENTORY_PARENT = 149, BANK_PARENT = 15;
    private static final int INVENTORY_CHILD = 0, BANK_CHILD = 3;

    public static boolean viewing() {
        return GameTab.INVENTORY.viewing();
    }

    public static Item[] items(Filter<Item> filter) {
        boolean bank = Bank.viewing();
        WidgetComponent inventory = Widgets.get(bank ? BANK_PARENT : INVENTORY_PARENT, bank ? BANK_CHILD : INVENTORY_CHILD);
        Item[] array = new Item[0];
        if (inventory != null && inventory.valid()) {
            if (bank) {
                int index = 0;
                for (WidgetComponent slot : inventory.children()) {
                    int id = slot.itemId();
                    int stack = slot.itemAmount();
                    if (id > 0 && stack > 0) {
                        Item item = new Item(slot, Item.ItemType.INVENTORY, index);
                        if (!filter.accept(item))
                            continue;
                        array = Array.add(array, item);
                    }
                    index++;
                }
            } else {
                int[] itemIds = inventory.itemIds();
                int[] itemAmounts = inventory.stackSizes();
                if (itemIds != null && itemAmounts != null) {
                    if (itemIds.length != itemAmounts.length)
                        throw new IllegalStateException("Internal Data Mismatch");
                    for (int i = 0; i < itemIds.length; i++) {
                        int id = itemIds[i];
                        int stack = itemAmounts[i];
                        if (id > 0 && stack > 0) {
                            Item item = new Item(id - 1, stack, i);
                            if (!filter.accept(item))
                                continue;
                            array = Array.add(array, item);
                        }
                    }
                }
            }
        }
        return array;
    }

    public static Item[] items() {
        return items(Filter.always());
    }

    public static int count() {
        return items().length;
    }

    public static boolean full() {
        return count() == 28;
    }

    public static boolean empty() {
        return count() == 0;
    }

    public static Item findByFilter(Filter<Item> filter) {
        for (Item item : items()) {
            if (item != null && filter.accept(item))
                return item;
        }
        return null;
    }

    public static boolean dropAll(Filter<Item> filter) {
        for (Item item : Inventory.items(filter)) {
            Point p = item.screen();
            if (p == null)
                continue;
            RuneScape.doAction(item.index(), 9764864, 37, 436, "Drop", item.name(), p.x, p.y);
        }
        return empty();
    }
}
