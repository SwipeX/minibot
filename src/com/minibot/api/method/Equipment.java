package com.minibot.api.method;

import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.client.natives.RSItemDefinition;

/**
 * @author Tim Dekker
 * @since 7/12/15
 */
public class Equipment {
    private static final int PARENT = 347;

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
        if (slot != null && !slot.isEmpty()) {
            WidgetComponent widget = slot.getWidget();
            if (widget != null) {
                String[] actions = widget.actions();
                if (actions != null && actions.length > 0) {
                    widget.processAction(actions[0]);
                }
            }
        }
    }

    public static void unequip(final int id) {
        for (Slot slot : Slot.values()) {
            if (slot.getItemId() == id) {
                unequip(slot);
            }
        }
    }

    public static void unequip(final String name) {
        if (!isEquipped(name)) return;
        for (Slot slot : Slot.values()) {
            final WidgetComponent wc = slot.getWidget();
            if (wc != null) {
                if (contains(wc.actions(), name)) {
                    unequip(slot);
                }
            }
        }
    }

    public static WidgetComponent[] getWidgets() {
        WidgetComponent[] widgets = new WidgetComponent[Slot.values().length];
        for (int i = 0; i < Slot.values().length; i++) {
            widgets[i] = Slot.values()[i].getWidget();
        }
        return widgets;
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
        for (String string : array) {
            if (string.equals(element)) {
                return true;
            }
        }
        return false;
    }


    public static enum Slot {
        HEAD(6, 0), CAPE(7, 1), NECK(8, 2), WEAPON(9, 3), CHEST(10, 4), SHIELD(11, 5), LEGS(
                12, 6), HANDS(13, 7), FEET(14, 8), RING(15, 9), AMMO(16, 10);

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
            ItemTables.Entry entry = ItemTables.getEquipment()[index];
            if (entry != null) {
                return entry.id();
            }
            return -1;
        }

        public int getAmount() {
            ItemTables.Entry entry = ItemTables.getEquipment()[index];
            if (entry != null) {
                return entry.getQuantity();
            }
            return -1;
        }

        public String getName() {
            ItemTables.Entry entry = ItemTables.getEquipment()[index];
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

    }
}
