package com.minibot.macros.zulrah;

import com.minibot.api.action.tree.Action;
import com.minibot.api.method.Widgets;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.WidgetComponent;

public enum ZulrahMode {

    RANGE(2042, 17),
    MELEE(2043, 18),
    MAGIC(2044, 16);

    private static final int PRAYER_BOOK = 271;

    public final int id, prayerComponentIndex;

    ZulrahMode(int id, int prayerComponentIndex) {
        this.id = id;
        this.prayerComponentIndex = prayerComponentIndex;
    }

    public boolean activate() {
        for (ZulrahMode zm : ZulrahMode.values()) {
            if (!zm.equals(this))
                zm.deactivate();
        }
        WidgetComponent component = Widgets.get(PRAYER_BOOK, prayerComponentIndex);
        if (component != null && Action.indexOf(component.actions(), "Activate") >= 0) {
            component.processAction("Activate");
            return Time.sleep(() -> Action.indexOf(component.actions(), "Deactivate") >= 0, 2000);
        }
        return false;
    }

    public boolean deactivate() {
        WidgetComponent component = Widgets.get(PRAYER_BOOK, prayerComponentIndex);
        if (component != null && Action.indexOf(component.actions(), "Deactivate") >= 0) {
            component.processAction("Deactivate");
            return Time.sleep(() -> Action.indexOf(component.actions(), "Activate") >= 0, 2000);
        }
        return false;
    }

    @Override
    public String toString() {
        String superString = super.toString();
        char first = Character.toUpperCase(superString.charAt(0));
        return first + superString.substring(1).toLowerCase();
    }
}