package com.minibot.macros.clue.structure;

import com.minibot.api.method.Inventory;
import com.minibot.api.wrapper.Item;

/**
 * @author Tyler Sedlar
 * @since 7/11/2015
 */
public class ClueScroll {

    public final int id;

    public ClueScroll(int id) {
        this.id = id;
    }

    public static Item findInventoryItem() {
        return Inventory.first(i -> {
            String name = i.name();
            return name != null && name.contains("Clue scroll");
        });
    }
}
