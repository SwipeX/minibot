package com.minibot.api.method;

import com.minibot.api.action.tree.Action;
import com.minibot.api.util.Time;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.Item.Source;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.client.natives.RSObjectDefinition;
import com.minibot.util.Array;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class Bank {

    public static final int BANK_PARENT = 12;
    public static final int DEPOSIT_ALL = 27;
    public static final int DEPOSIT_EQUIPMENT = 29;
    public static final int SLOT_CONTAINER = 6;

    private static Filter<Item> itemFilter(String itemName) {
        return item -> item.name().equals(itemName);
    }

    public static boolean viewing() {
        WidgetComponent component = Widgets.get(BANK_PARENT, 0);
        return component != null && component.visible();
    }

    public static void depositAll() {
        WidgetComponent depositButton = Widgets.get(BANK_PARENT, DEPOSIT_ALL);
        if (depositButton != null) {
            depositButton.processAction("Deposit inventory");
        }
    }

    public static void depositEquipment() {
        WidgetComponent depositButton = Widgets.get(BANK_PARENT, DEPOSIT_EQUIPMENT);
        if (depositButton != null) {
            depositButton.processAction("Deposit worn items");
        }
    }

    public static GameObject nearestBooth() {
        return Objects.nearestByAction("Bank", 10);
    }

    public static boolean openBooth() {
        GameObject booth = nearestBooth();
        if (booth != null) {
            booth.processAction("Bank");
            return Time.sleep(Bank::viewing, 10000);
        }
        return false;
    }

    /**
     * This function will be able to withdraw- any amount that is on the menu, but will default to Withdraw-All
     *
     * @param item   - the item wishing to be withdrawn
     * @param amount - the amount wishing to be taken
     *               TODO - withdraw-x without presetting it before starting script
     */
    public static void withdraw(Item item, int amount) {
        if (item != null && amount > 0) {
            String[] actions = item.owner().actions();
            String targetAction = "Withdraw-" + amount;
            for (String string : actions)
                if (string.contains(targetAction)) {
                    item.processAction(targetAction);
                    return;
                }
            item.processAction("Withdraw-All");
        }
    }
    /**
     * see @withdraw above
     */
    public static void withdraw(String item, int amount) {
        withdraw(first(itemFilter(item)), amount);
    }

    public static boolean close() {
        WidgetComponent component = Widgets.get(12, 3);
        if (component != null) {
            component = component.children()[11];
            if (component != null) {
                component.processAction("Close");
                return Time.sleep(() -> !Bank.viewing(), 3000);
            }
        }
        return false;
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
                    array = Array.add(array, (new Item(slot, Source.BANK, slot.rawIndex())));
            }
            return array;
        }
        return new Item[0];
    }

    public static Item first(Filter<Item> filter) {
        for (Item item : items()) {
            if (item != null && filter.accept(item))
                return item;
        }
        return null;
    }
}
