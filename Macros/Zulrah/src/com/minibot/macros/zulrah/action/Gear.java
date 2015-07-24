package com.minibot.macros.zulrah.action;

import com.minibot.Minibot;
import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.method.Combat;
import com.minibot.api.method.Equipment;
import com.minibot.api.method.Inventory;
import com.minibot.api.method.Widgets;
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
                final String name = slot.getName().toLowerCase();
                for (String string : NAMES_MAGE) {
                    if (name.contains(string)) {
                        magic.add(slot.itemId());
                        continue;
                    }
                }
                for (String string : NAMES_RANGE) {
                    if (name.contains(string)) {
                        range.add(slot.itemId());
                        continue;
                    }
                }
                equip.add(slot.itemId());
            }
        }

        for (Item item : Inventory.items()) {
            final String name = item.name().toLowerCase();
            for (String string : NAMES_MAGE) {
                if (name.contains(string)) {
                    magic.add(item.id());
                    continue;
                }
            }
            for (String string : NAMES_RANGE) {
                if (name.contains(string)) {
                    range.add(item.id());
                    continue;
                }
            }
            if (item.name().contains("dueling"))
                continue;
            pack.add(item.id());
        }
        other = equip.stream().mapToInt(i -> i).toArray();
        inventory = pack.stream().mapToInt(i -> i).toArray();
        rangeIds = range.stream().mapToInt(i -> i).toArray();
        mageIds = magic.stream().mapToInt(i -> i).toArray();
    }

    public static boolean hasInventory() {
        return Inventory.containsAll(inventory) &&
                (Inventory.containsAll(rangeIds) || Inventory.containsAll(mageIds));
    }

    public static boolean hasEquip(){
        return Equipment.equipped(other) &&
                (Equipment.equipped(rangeIds) || Equipment.equipped(mageIds));
    }

    public static boolean equip() {
        if (!Equipment.equipped("Ring of recoil")) {
            Equipment.equip("Ring of recoil");
        }
        specialAttack();
        return equip(Zulrah.getPhase().getCurrent().getSnakeType());
    }

    private static void specialAttack() {
        if ((lastSpec == -1 || Time.millis() - lastSpec > 1200) && Equipment.equipped("Armadyl crossbow")) {//spec
            if (Minibot.instance().client().getGameSettings()[300] / 10 >= 40) {
                WidgetComponent comp = Widgets.get(593, 30);
                if (comp != null) {
                    comp.processAction(ActionOpcodes.WIDGET_ACTION, 1, "Use <col=00ff00>Special Attack</col>", "");
                }
                lastSpec = Time.millis();
            }
        }
    }

    public static boolean equip(SnakeType type) {
        int[] ids = (type.id() == SnakeType.MAGIC.id() ? rangeIds : mageIds);
        for (int id : ids) {
            if (!Equipment.equipped(id)) {
                Item item = Inventory.first(i -> i.id() == id);
                if (item != null) {
                    Equipment.equip(item);
                    String name = item.name();
                    if (name != null) {
                        if (name.contains("rossbow")) {
                            Combat.setStyle(1);
                        }
                    }
                }
            }
        }
        return Equipment.equipped(ids);
    }

    public static int[] getRangeIds() {
        return rangeIds;
    }

    public static int[] getMageIds() {
        return mageIds;
    }
}
