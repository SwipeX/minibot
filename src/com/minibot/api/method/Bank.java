package com.minibot.api.method;

import com.minibot.api.util.Array;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.WidgetComponent;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class Bank {

    private static final int BANK_PARENT = 12;
    private static final int SLOT_CONTAINER = 6;

    public static boolean viewing() {
        WidgetComponent component = Widgets.get(BANK_PARENT, 0);
        return component != null && component.visible();
    }

    public static Item[] items() {
        WidgetComponent[] children = Widgets.childrenFor(BANK_PARENT);
        WidgetComponent container = null;
        for (WidgetComponent component : children) {
            if (component != null && component.width() == 374) {
                container = component;
                break;
            }
        }
        if (container != null) {
            Item[] array = new Item[0];
            WidgetComponent[] slots = container.children();
            for (WidgetComponent slot : slots) {
                int id = slot.itemId();
                int stack = slot.itemAmount();
                if (id > 0 && stack > 0)
                    array = Array.add(array, (new Item(slot, Item.ItemType.BANK, slot.index())));
            }
            return array;
        }
        return new Item[0];
    }

    public static Item findByFilter(Filter<Item> filter) {
        for (Item item : items()) {
            if (item != null && filter.accept(item))
                return item;
        }
        return null;
    }
}
