package com.minibot.api.method;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.Item.Source;
import com.minibot.api.wrapper.WidgetComponent;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class Inventory {

    public static final int INVENTORY_PARENT = 149, BANK_PARENT = 15;
    public static final int INVENTORY_CONTAINER = 0, BANK_CONTAINER = 3;

    public static boolean viewing() {
        return GameTab.INVENTORY.viewing();
    }

    public static Item[] items(Filter<Item> filter) {
        boolean bank = Bank.viewing();
        WidgetComponent inventory = Widgets.get(bank ? BANK_PARENT : INVENTORY_PARENT, bank ? BANK_CONTAINER : INVENTORY_CONTAINER);
        if (inventory == null)
            return new Item[0];
        Item[] items = new Item[inventory.itemStackSizes().length];
        if (inventory.validate()) {
            if (bank) {
                int index = 0;
                for (WidgetComponent slot : inventory.children()) {
                    int id = slot.itemId();
                    int stack = slot.itemAmount();
                    if (id > 0 && stack > 0) {
                        Item item = new Item(slot, Source.INVENTORY, index);
                        if (!filter.accept(item))
                            continue;
                        items[index] = item;
                    }
                    index++;
                }
            } else {
                int[] itemIds = inventory.itemIds();
                int[] itemAmounts = inventory.itemStackSizes();
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
                            items[i] = item;
                            items[i].setComponent(inventory);
                        }
                    }
                }
            }
        }
        return items;
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

    public static Item first(Filter<Item> filter) {
        for (Item item : items()) {
            if (item != null && filter.accept(item))
                return item;
        }
        return null;
    }

    public static boolean dropAll(Filter<Item> filter) {
        boolean bank = Bank.viewing();
        WidgetComponent container = Widgets.get(bank ? BANK_PARENT : INVENTORY_PARENT, bank ?
                BANK_CONTAINER : INVENTORY_CONTAINER);
        int widgetUid = container.hash();
        for (Item item : Inventory.items(filter)) {
            if(item==null)continue;
            Point p = item.point();
            if (p == null)
                continue;
            item.processAction(ActionOpcodes.ITEM_ACTION_4, "Drop");
        }
        return items(filter).length == 0;
    }

    public static boolean dropAllExcept(Filter<Item> filter) {
        boolean bank = Bank.viewing();
        WidgetComponent container = Widgets.get(bank ? BANK_PARENT : INVENTORY_PARENT, bank ?
                BANK_CONTAINER : INVENTORY_CONTAINER);
        int widgetUid = container.hash();
        for (Item item : Inventory.items(Filter.not(filter))) {
            Point p = item.point();
            if (p == null)
                continue;
            item.processAction(ActionOpcodes.ITEM_ACTION_4, "Drop");
        }
        return 28 - items(filter).length == count();
    }
}
