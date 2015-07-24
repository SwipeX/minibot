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

    public static void equip(Item item) {
        if (item != null) {
            RSItemDefinition def = item.definition();
            if (def != null) {
                String[] actions = def.getActions();
                if (actions != null) {
                    if (contains(actions, "Wear")) {
                        item.processAction("Wear");
                    } else {
                        item.processAction("Wield");
                    }
                    /*Time.sleep(() -> {
                        return equipped(item.id());
                    }, Random.nextInt(160, 300));*/
                }
            }
        }
    }

    public static void equip(int id) {
        equip(Inventory.first(i -> i.id() == id));
    }

    public static void equip(String name) {
        equip(Inventory.first(i -> i.name().equals(name)));
    }

    public static void unequip(final Slot slot) {
        GameTab current = GameTab.current();
        if (slot != null && !slot.empty()) {
            WidgetComponent widget = slot.widget();
            if (widget != null) {
                if (GameTab.EQUIPMENT.open()) {
                    widget.processAction(ActionOpcodes.WIDGET_ACTION, "Remove", slot.targetText());
                    current.open();
                }
            }
        }
    }

    public static void unequip(Filter<Slot> filter) {
        for (Slot slot : Slot.values()) {
            if (filter.accept(slot)) {
                unequip(slot);
            }
        }
    }

    public static void unequip(int id) {
        unequip(slot -> slot.id() == id);
    }

    public static void unequip(String name) {
        unequip(slot -> slot.getName().equals(name));
    }

    private static boolean equipped(String name) {
        for (Slot slot : Slot.values()) {
            if (name.equals(slot.getName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean equipped(String... names) {
        for (String name : names) {
            if (!equipped(name)) {
                return false;
            }
        }
        return true;
    }

    private static boolean equipped(int id) {
        for (Slot slot : Slot.values()) {
            if (id == slot.itemId()) {
                return true;
            }
        }
        return false;
    }

    public static boolean equipped(int... ids) {
        for (int id : ids) {
            if (!equipped(id)) {
                return false;
            }
        }
        return true;
    }

    private static boolean contains(String[] array, String element) {
        if (element == null) {
            return false;
        }
        for (String string : array) {
            if (string != null && string.equals(element)) {
                return true;
            }
        }
        return false;
    }

    private static ItemContainers.Entry atIndex(int index, ItemContainers.Entry[] entries) {
        for (ItemContainers.Entry entry : entries) {
            if (entry.index() == index) {
                return entry;
            }
        }
        return null;
    }

    public static enum Slot {

        HEAD(6, 0),
        CAPE(7, 1),
        NECK(8, 2),
        WEAPON(9, 3),
        CHEST(10, 4),
        SHIELD(11, 5),
        LEGS(12, 7),
        HANDS(13, 9),
        FEET(14, 10),
        RING(15, 12),
        AMMO(16, 13);

        private final int id;
        private final int index;

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

        public int itemId() {
            ItemContainers.Entry entry = atIndex(index, ItemContainers.equipment());
            if (entry != null) {
                return entry.id();
            }
            return -1;
        }

        public int amount() {
            ItemContainers.Entry entry = atIndex(index, ItemContainers.equipment());
            if (entry != null) {
                return entry.amount();
            }
            return -1;
        }

        public String getName() {
            ItemContainers.Entry entry = atIndex(index, ItemContainers.equipment());
            if (entry != null) {
                return entry.name();
            }
            return null;
        }

        public WidgetComponent widget() {
            return Widgets.get(PARENT, id);
        }

        public boolean empty() {
            return itemId() <= 0;
        }

        public String targetText() {
            String name = getName();
            if (name != null) {
                return String.format("<col=ff9040>%s</col>", getName());
            }
            return null;
        }
    }
}
