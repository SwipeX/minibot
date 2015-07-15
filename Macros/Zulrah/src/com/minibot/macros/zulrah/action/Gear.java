package com.minibot.macros.zulrah.action;

import com.minibot.api.method.Equipment;
import com.minibot.api.method.Inventory;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.macros.zulrah.Zulrah;
import com.minibot.macros.zulrah.boss.Phase;
import com.minibot.macros.zulrah.boss.SnakeType;
import com.minibot.macros.zulrah.boss.Stage;

import java.util.ArrayList;

/**
 * @author Tim Dekker
 * @since 7/14/15
 */
public class Gear {
    private static int[] rangeIds;
    private static int[] mageIds;
    private static final String[] NAMES_RANGE;
    private static final String[] NAMES_MAGE;

    public static final int RANGED = 0;
    public static final int MAGIC = 1;

    static {
        NAMES_RANGE = new String[]{"d'hide", "bow", "pipe", "cape"};
        NAMES_MAGE = new String[]{"robe", "staff", "trident", "ava's"};
        setup();
    }

    public static void setup() {
        ArrayList<Integer> range = new ArrayList();
        ArrayList<Integer> magic = new ArrayList();
        for (Equipment.Slot slot : Equipment.Slot.values()) {
            if (slot != null) {
                if (slot.getName() == null) {
                    continue;
                }
                final String name = slot.getName().toLowerCase();
                for (String string : NAMES_MAGE) {
                    if (name.contains(string)) {
                        magic.add(slot.id());
                    }
                }
                for (String string : NAMES_RANGE) {
                    if (name.contains(string)) {
                        range.add(slot.id());
                    }
                }
            }
        }

        for (Item item : Inventory.items()) {
            final String name = item.name().toLowerCase();
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
        }
        rangeIds = range.stream().mapToInt(i -> i).toArray();
        mageIds = magic.stream().mapToInt(i -> i).toArray();
    }

    public static boolean equip() {
        Phase phase = Zulrah.getPhase();
        if (phase != null) {
            Stage stage = phase.getCurrent();
            if (stage != null) {
                SnakeType snakeType = stage.getSnakeType();
                if (snakeType != null) {
                    if (snakeType.equals(SnakeType.MAGIC)) {
                        return equip(RANGED);
                    } else { // Range/Jad/Melee
                        return equip(MAGIC);
                    }
                }
            }
        }
        return false;
    }

    private static boolean equip(int type) {
        int[] ids = (type == MAGIC ? mageIds : rangeIds);
        for (int id : ids) {
            if (!Equipment.equipped(id)) {
                Equipment.equip(id);
                Time.sleep(50);
            }
        }
        return isTypeEquipped(type);
    }

    public static boolean isTypeEquipped(int type) {
        int[] ids = (type == MAGIC ? mageIds : rangeIds);
        return Equipment.equipped(ids);
    }
}
