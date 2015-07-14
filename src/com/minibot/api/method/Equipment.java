package com.minibot.api.method;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.client.natives.RSItemDefinition;

/**
 * @author Tim Dekker
 * @since 7/12/15
 */
public class Equipment {
    private static final int PARENT = 387;

    public static void equip(final Item item) {
        if (item != null) {
            RSItemDefinition def = item.definition();
            if (def != null) {
                String[] actions = def.getActions();
                if (actions != null) {
                    if (contains(actions, "Wear"))
                        item.processAction("Wear");
                    else
                        item.processAction("Wield");
                }
            }
        }
    }

    public static void equip(final int id) {
        equip(Inventory.first(i -> i.id() == id));
    }

    public static void equip(final String name) {
        equip(Inventory.first(i -> i.name().equals(name)));
    }


    public static void unequip(final Slot slot) {
        GameTab current = GameTab.current();
        if (slot != null && !slot.isEmpty()) {
            WidgetComponent widget = slot.getWidget();
            if (widget != null) {
                if (GameTab.EQUIPMENT.open()) {
                    widget.processAction(ActionOpcodes.WIDGET_ACTION, "Remove", slot.getTarget());
                    current.open();
                }
            }
        }
    }

    public static void unequip(Filter<Slot> filter) {
        for (Slot slot : Slot.values()) {
            if (filter.accept(slot))
                unequip(slot);
        }
    }

    public static void unequip(final int id) {
        unequip(slot -> slot.id() == id);
    }

    public static void unequip(final String name) {
        unequip(slot -> slot.getName().equals(name));
    }

    public static boolean isEquipped(String... itemNames) {
        for (Slot slot : Slot.values()) {
            for (String str : itemNames) {
                if (str.equals(slot.getName())) {
                    return true;
                }
            }
        }
        return false;
    }


    public static boolean isEquipped(int... ids) {
        for (Slot slot : Slot.values()) {
            for (int id : ids) {
                if (id == slot.getItemId()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean contains(String[] array, String element) {
        if (element == null) return false;
        for (String string : array) {
            if (string == null) {
                continue;
            } else if (string.equals(element)) {
                return true;
            }
        }
        return false;
    }

    private static ItemTables.Entry atIndex(int index, ItemTables.Entry[] entries) {
        for (ItemTables.Entry entry : entries) {
            if (entry.getIndex() == index)
                return entry;
        }
        return null;
    }

    public static enum Slot {
        HEAD(6, 0), CAPE(7, 1), NECK(8, 2), WEAPON(9, 3), CHEST(10, 4), SHIELD(11, 5), LEGS(
                12, 7), HANDS(13, 9), FEET(14, 10), RING(15, 12), AMMO(16, 13);

        private int id;
        private int index;

        Slot(int id, int index) {
            this.id = id;
            this.index = index;
        }

        public int id() {
            return id;
        }

        public int index() {
            return index;
        }

        public int getItemId() {
            ItemTables.Entry entry = atIndex(index, ItemTables.getEquipment());
            if (entry != null) {
                return entry.id();
            }
            return -1;
        }

        public int getAmount() {
            ItemTables.Entry entry = atIndex(index, ItemTables.getEquipment());
            if (entry != null) {
                return entry.getQuantity();
            }
            return -1;
        }

        public String getName() {
            ItemTables.Entry entry = atIndex(index, ItemTables.getEquipment());
            if (entry != null) {
                return entry.name();
            }
            return null;
        }

        public WidgetComponent getWidget() {
            return Widgets.get(PARENT, id);
        }

        public boolean isEmpty() {
            return getItemId() <= 0;
        }

        public String getTarget() {
            String name = getName();
            if (name != null)
                return String.format("<col=ff9040>%s</col>", getName());
            return null;
        }
    }
}
