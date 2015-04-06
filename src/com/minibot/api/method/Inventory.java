package com.minibot.api.method;

import com.minibot.api.util.Array;
import com.minibot.api.util.Filter;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.WidgetComponent;

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

    public static Item[] items() {
        boolean bank = Bank.viewing();
        WidgetComponent inventory = Widgets.get(bank ? BANK_PARENT : INVENTORY_PARENT, bank ? BANK_CHILD : INVENTORY_CHILD);
        Item[] array = new Item[0];
        if (inventory != null && inventory.valid()) {
            if (bank) {
                int index = 0;
                for (WidgetComponent slot : inventory.children()) {
                    int id = slot.itemId();
                    int stack = slot.itemAmount();
                    if (id > 0 && stack > 0)
                        array = Array.add(array, (new Item(slot, Item.ItemType.INVENTORY, index)));
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
                        if (id > 0 && stack > 0)
                            array = Array.add(array, (new Item(id - 1, stack, i)));
                    }
                }
            }
        }
        return array;
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
}
