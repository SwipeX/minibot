package com.minibot.api.method;

import com.minibot.api.action.tree.Action;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.Item.Source;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.GroundItem;
import com.minibot.client.natives.RSItemDefinition;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class Inventory {

    public static final Filter<Item> FOOD_FILTER = (i -> {
        RSItemDefinition def = i.definition();
        return def != null && Action.indexOf(def.getActions(), "Eat") >= 0;
    });

    public static final int INVENTORY_PARENT = 149, BANK_PARENT = 15;
    public static final int INVENTORY_CONTAINER = 0, BANK_CONTAINER = 3;

    public static boolean viewing() {
        return GameTab.INVENTORY.viewing();
    }

    public static List<Item> items(Filter<Item> filter) {
        boolean bank = Bank.viewing();
        WidgetComponent inventory = Widgets.get(bank ? BANK_PARENT : INVENTORY_PARENT, bank ? BANK_CONTAINER : INVENTORY_CONTAINER);
        List<Item> items = new LinkedList<>();
        if (inventory == null) {
            return items;
        }
        if (inventory.validate()) {
            if (bank) {
                int index = 0;
                for (WidgetComponent slot : inventory.children()) {
                    int id = slot.itemId();
                    int stack = slot.itemAmount();
                    if (id > 0 && stack > 0) {
                        Item item = new Item(slot, Source.INVENTORY, index);
                        if (!filter.accept(item)) {
                            continue;
                        }
                        items.add(item);
                    }
                    index++;
                }
            } else {
                int[] itemIds = inventory.itemIds();
                int[] itemAmounts = inventory.itemStackSizes();
                if (itemIds != null && itemAmounts != null) {
                    if (itemIds.length != itemAmounts.length) {
                        throw new IllegalStateException("Internal Data Mismatch");
                    }
                    for (int i = 0; i < itemIds.length; i++) {
                        int id = itemIds[i];
                        int stack = itemAmounts[i];
                        if (id > 0 && stack > 0) {
                            Item item = new Item(id - 1, stack, i);
                            if (!filter.accept(item)) {
                                continue;
                            }
                            item.setComponent(inventory);
                            items.add(item);
                        }
                    }
                }
            }
        }
        return items;
    }

    public static List<Item> items() {
        return items(item -> item != null && item.name() != null);
    }

    public static int count() {
        return items().size();
    }

    public static boolean full() {
        return count() == 28;
    }

    public static boolean empty() {
        return count() == 0;
    }

    public static Item first(Filter<Item> filter) {
        for (Item item : items()) {
            if (item != null && filter.accept(item)) {
                return item;
            }
        }
        return null;
    }

    public static boolean containsAll(Filter<Item>... filters) {
        for (Filter<Item> filter : filters) {
            if (first(filter) == null) {
                return false;
            }
        }
        return true;
    }

    public static boolean containsAll(int... ids) {
        for (int id : ids) {
            if (first(i -> i.id() == id) == null)
                return false;
        }
        return true;
    }

    public static void dropAll(Filter<Item> filter) {
        apply(filter, Item::drop);
    }

    public static void dropAllExcept(Filter<Item> filter) {
        apply(Filter.not(filter), Item::drop);
    }

    public static void apply(Filter<Item> filter, Consumer<Item> application) {
        items(filter).stream().filter(item -> item != null).forEach(application::accept);
    }

    public static void apply(Consumer<Item> application) {
        apply(i -> i != null, application);
    }

    public static void use(Item a, Item b) {
        a.use(b);
    }

    public static void use(Item a, GroundItem b) {
        a.use(b);
    }

    public static void use(Item a, GameObject b) {
        a.use(b);
    }

    public static int foodCount() {
        return items(FOOD_FILTER).size();
    }

    public static Item firstFood() {
        return first(FOOD_FILTER);
    }

    public static int stackCount() {
        int count = 0;
        for (Item item : items()) {
            int amount = item.amount();
            if (amount != -1) {
                count += amount;
            }
        }
        return count;
    }
}