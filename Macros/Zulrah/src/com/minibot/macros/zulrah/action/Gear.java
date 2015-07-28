package com.minibot.macros.zulrah.action;

import com.minibot.api.method.*;
import com.minibot.api.util.Random;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.macros.zulrah.Zulrah;
import com.minibot.macros.zulrah.phase.SnakeType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tim Dekker
 * @since 7/14/15
 */
public class Gear {

    // inventory, rangedIds, and mageIds need to be populated for banking to be autonomous
    private static int[] stacks;
    private static int[] other;
    private static int[] inventory;
    private static int[] rangedIds;
    private static int[] mageIds;
    private static final String[] NAMES_RANGED;
    private static final String[] NAMES_MAGE;
    private static long lastSpec = -1;
    private static Potions.Potion potion;

    static {
        NAMES_RANGED = new String[]{"d'hide", "bow", "pipe", "ava's", "range", "blowpipe"};
        NAMES_MAGE = new String[]{"robe", "staff", "trident", "cape", "mage", "book of darkness", "infinity"};
    }

    public static void setup() {
        List<Integer> ranged = new ArrayList<>(11);
        List<Integer> magic = new ArrayList<>(11);
        List<Integer> equip = new ArrayList<>(11);
        List<Integer> pack = new ArrayList<>(28);
        List<Integer> stack = new ArrayList<>(28);
        slots:
        for (Equipment.Slot slot : Equipment.Slot.values()) {
            if (slot != null) {
                if (slot.getName() == null) {
                    continue;
                }
                String name = slot.getName().toLowerCase();
                for (String string : NAMES_MAGE) {
                    if (name.contains(string)) {
                        magic.add(slot.itemId());
                        continue slots;
                    }
                }
                for (String string : NAMES_RANGED) {
                    if (name.contains(string)) {
                        ranged.add(slot.itemId());
                        continue slots;
                    }
                }
                equip.add(slot.itemId());
            }
        }

        inventory:
        for (Item item : Inventory.items()) {
            String name = item.name().toLowerCase();
            for (String string : NAMES_MAGE) {
                if (name.contains(string)) {
                    magic.add(item.id());
                    continue inventory;
                }
            }
            for (String string : NAMES_RANGED) {
                if (name.contains(string)) {
                    ranged.add(item.id());
                    continue inventory;
                }
            }
            if (!name.contains("(")) {
                pack.add(item.id());
                stack.add(item.amount());
            }
        }
        other = equip.stream().mapToInt(i -> i).toArray();
        inventory = pack.stream().mapToInt(i -> i).toArray();
        stacks = stack.stream().mapToInt(i -> i).toArray();
        rangedIds = ranged.stream().mapToInt(i -> i).toArray();
        mageIds = magic.stream().mapToInt(i -> i).toArray();
        potion = (Potions.Potion.PRAYER.get() != null ? Potions.Potion.PRAYER : Potions.Potion.RESTORE);
        System.out.println("POTION: " + potion);
    }

    public static boolean hasInventory() {
        boolean hasVenom = Inventory.first(i -> i.name().toLowerCase().contains("venom+")) != null;
        boolean hasRanged = Inventory.first(i -> i.name().toLowerCase().contains("ranging")) != null;
        boolean hasPotion = Inventory.first(i -> {
            String itemName = i.name().toLowerCase();
            return itemName.contains("prayer potion(4)") || itemName.contains("super restore(4)");
        }) != null;
        return (hasRanged && hasVenom && hasPotion && Inventory.containsAll(inventory))
                && (Inventory.containsAll(rangedIds) || Inventory.containsAll(mageIds));
    }

    public static boolean hasEquip() {
        return Equipment.equipped(other) && (Equipment.equipped(rangedIds) || Equipment.equipped(mageIds));
    }

    public static boolean equip() {
        if (!Bank.viewing()) {
            if (!Equipment.equipped("Ring of recoil")) {
                Equipment.equip("Ring of recoil");
            }
            specialAttack();
            SnakeType type = Zulrah.phase().current().getSnakeType();
            equip(type);
            return Equipment.equipped(type.id() == SnakeType.MAGIC.id() ? rangedIds : mageIds);
        }
        return false;
    }

    private static void specialAttack() {
        if ((lastSpec == -1 || Time.millis() - lastSpec > 20000) && Equipment.equipped("Armadyl crossbow")) { //spec
            if (Game.varp(300) / 10 >= 40) {
                WidgetComponent comp = Widgets.get(593, 30);
                if (comp != null) {
                    comp.processAction("Use <col=00ff00>Special Attack</col>");
                }
                lastSpec = Time.millis();
            }
        }
    }

    public static boolean equip(SnakeType type) {
        int[] ids = (type.id() == SnakeType.MAGIC.id() ? rangedIds : mageIds);
        for (int id : ids) {
            if (!Equipment.equipped(id)) {
                Item item = Inventory.first(i -> i.id() == id);
                if (item != null) {
                    Equipment.equip(item);
                    Time.sleep(100, 200);
                    String name = item.name();
                    if (name != null) {
                        if (name.contains("rossbow")) {
                            Combat.setStyle(1);
                        }
                    }
                }
            }
        }
        return Time.sleep(() -> Equipment.equipped(ids), Random.nextInt(1500, 1800));
    }

    public static int[] rangedIds() {
        return rangedIds;
    }

    public static int[] mageIds() {
        return mageIds;
    }

    public static int[] inventoryIds() {
        return inventory;
    }

    public static int[] amounts() {
        return stacks;
    }

    public static Potions.Potion potion() {
        return potion;
    }
}