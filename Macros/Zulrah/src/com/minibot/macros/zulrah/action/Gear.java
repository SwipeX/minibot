package com.minibot.macros.zulrah.action;

import com.minibot.api.method.*;
import com.minibot.api.util.Random;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.macros.zulrah.Zulrah;
import com.minibot.macros.zulrah.phase.SnakeType;

import java.util.ArrayList;

/**
 * @author Tim Dekker
 * @since 7/14/15
 */
public class Gear {

    private static int[] other;
    private static int[] inventory;
    private static int[] rangeIds;
    private static int[] mageIds;
    private static final String[] NAMES_RANGE;
    private static final String[] NAMES_MAGE;
    private static long lastSpec = -1;

    static {
        NAMES_RANGE = new String[]{"d'hide", "bow", "pipe", "ava's", "range", "blowpipe"};
        NAMES_MAGE = new String[]{"robe", "staff", "trident", "cape", "mage", "book of darkness", "infinity"};
        setup();
    }

    public static void setup() {
        ArrayList<Integer> range = new ArrayList<>();
        ArrayList<Integer> magic = new ArrayList<>();
        ArrayList<Integer> equip = new ArrayList<>();
        ArrayList<Integer> pack = new ArrayList<>();
        for (Equipment.Slot slot : Equipment.Slot.values()) {
            if (slot != null) {
                if (slot.getName() == null) {
                    continue;
                }
                String name = slot.getName().toLowerCase();
                for (String string : NAMES_MAGE) {
                    if (name.contains(string)) {
                        magic.add(slot.itemId());
                    }
                }
                for (String string : NAMES_RANGE) {
                    if (name.contains(string)) {
                        range.add(slot.itemId());
                    }
                }
                equip.add(slot.itemId());
            }
        }

        for (Item item : Inventory.items()) {
            String name = item.name().toLowerCase();
            for (String string : NAMES_MAGE) {
                if (name.contains(string)) {
                    magic.add(item.id());
                }
            }
            for (String string : NAMES_RANGE) {
                if (name.contains(string)) {
                    range.add(item.id());
                }
            }
            if (item.name().contains("dueling")) {
                continue;
            }
            pack.add(item.id());
        }
        other = equip.stream().mapToInt(i -> i).toArray();
        inventory = pack.stream().mapToInt(i -> i).toArray();
        rangeIds = range.stream().mapToInt(i -> i).toArray();
        mageIds = magic.stream().mapToInt(i -> i).toArray();
    }

    public static boolean hasInventory() {
        return Inventory.containsAll(inventory) && (Inventory.containsAll(rangeIds) || Inventory.containsAll(mageIds));
    }

    public static boolean hasEquip() {
        return Equipment.equipped(other) && (Equipment.equipped(rangeIds) || Equipment.equipped(mageIds));
    }

    public static boolean equip() {
        if (!Equipment.equipped("Ring of recoil")) {
            Equipment.equip("Ring of recoil");
        }
        specialAttack();
        //return equip(Zulrah.getPhase().getCurrent().getSnakeType());
        SnakeType type = Zulrah.getPhase().getCurrent().getSnakeType();
        equip(type);
        return Equipment.equipped(type.id() == SnakeType.MAGIC.id() ? rangeIds : mageIds);
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

    public static void equip(SnakeType type) {
        int[] ids = (type.id() == SnakeType.MAGIC.id() ? rangeIds : mageIds);
        for (int i = 0; i < ids.length; i++) {
            int id = ids[i];
            if (!Equipment.equipped(id)) {
                Item item = Inventory.first(j -> j.id() == id);
                if (item != null) {
                    Equipment.equip(item);
                    if (i == ids.length - 1) {
                        Time.sleep(150, 300);
                    } else {
                        Time.sleep(() -> Equipment.equipped(item.id()), Random.nextInt(500, 750));
                    }
                    String name = item.name();
                    if (name != null) {
                        if (name.contains("rossbow")) {
                            Combat.setStyle(1);
                        }
                    }
                }
            }
        }
        //return Equipment.equipped(ids);
    }

    public static int[] getRangeIds() {
        return rangeIds;
    }

    public static int[] getMageIds() {
        return mageIds;
    }
}