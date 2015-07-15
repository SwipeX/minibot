package com.minibot.macros.zulrah;

import com.minibot.api.method.Equipment;
import com.minibot.api.method.Inventory;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Tim Dekker
 * @since 7/14/15
 */
public class Gear {
    private int[] rangeIds;
    private int[] mageIds;
    private static final String[] NAMES_RANGE;
    private static final String[] NAMES_MAGE;

    public static final int RANGED = 0;
    public static final int MAGIC = 1;

    static {
        NAMES_RANGE = new String[]{"d'hide", "bow", "pipe", "cape"};
        NAMES_MAGE = new String[]{"robe", "staff", "trident", "ava's"};
    }

    public Gear(int[] rangeIds, int[] mageIds) {
        this.rangeIds = rangeIds;
        this.mageIds = mageIds;
    }

    public Gear() {
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
                        magic.add(slot.getItemId());
                    }
                }
                for (String string : NAMES_RANGE) {
                    if (name.contains(string)) {
                        range.add(slot.getItemId());
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
        this.rangeIds = range.stream().mapToInt(i -> i).toArray();
        this.mageIds = magic.stream().mapToInt(i -> i).toArray();
    }

    public boolean equipType(int type) {
        int[] ids = (type == MAGIC ? mageIds : rangeIds);
        for (int id : ids) {
            if (!Equipment.isEquipped(id)) {
                Equipment.equip(id);
                Time.sleep(50);
            }
        }
        return isTypeEquipped(type);
    }

    public boolean isTypeEquipped(int type) {
        int[] ids = (type == MAGIC ? mageIds : rangeIds);
        return Equipment.isEquipped(ids);
    }
}
