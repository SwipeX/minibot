package com.minibot.api.method;

import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.WidgetComponent;

/**
 * @author Tim Dekker
 * @since 7/12/15
 */
public class Equipment {
    private static final int PARENT = 347;

    public static void equip(final Item item) {
        if (item != null) {
            item.processAction("Wear");
            item.processAction("Wield");
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
                if (contains(wc.actions(), name))
                    unequip(slot);
            }
        }
    }

    public static WidgetComponent[] getWidgets() {
        WidgetComponent[] widgets = new WidgetComponent[Slot.values().length];
        for (int i = 0; i < Slot.values().length; i++)
            widgets[i] = Slot.values()[i].getWidget();

        return widgets;
    }


    public static boolean isEquipped(String... itemNames) {
        for (WidgetComponent slot : getWidgets()) {
            String[] actions = slot.actions();
            if (slot == null)
                continue;
            for (String s : itemNames)
                if (contains(actions, s)) {
                    return true;
                }
        }
        return false;
    }


    public static boolean isEquipped(int... ids) {
        for (Slot slot : Slot.values()) {
            for (int id : ids)
                if (id == slot.getItemId())
                    return true;
        }
        return false;
    }

    private static boolean contains(String[] array, String element) {
        for (String string : array)
            if (string.equals(element))
                return true;
        return false;
    }

    public enum Slot {
        HEAD(6), CAPE(7), NECK(8), WEAPON(9), CHEST(10), SHIELD(11),
        LEGS(12), HANDS(13), FEET(14), RING(15), AMMO(16);

        private int id;

        Slot(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }

        public int getItemId() {
            WidgetComponent component = getWidget();
            if (component != null) {
                System.out.println(component.itemId());
                return component.itemId();
            }
            return -1;
        }

        public int getAmount() {
            WidgetComponent component = getWidget();
            if (component != null) {
                System.out.println(component.itemAmount());
                return component.itemAmount();
            }
            return -1;
        }

        public WidgetComponent getWidget() {
            return Widgets.get(PARENT, id());
        }

        public boolean isEmpty() {
            return getItemId() <= 0;
        }

    }
}
