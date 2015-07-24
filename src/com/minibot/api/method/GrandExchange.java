package com.minibot.api.method;

import com.minibot.api.util.Time;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.api.wrapper.locatable.Npc;

/**
 * @author Tyler Sedlar
 * @since 7/18/2015
 */
public class GrandExchange {

    public static final int COLLECT_PARENT = 402;
    public static final int COLLECT_CLOSE_PARENT = 2, COLLECT_CLOSE_GRANDCHILD = 11;
    public static final int COLLECT_BANK_CHILD = 4;

    private static long lastCollectTime = -1;

    public static boolean viewingCollect() {
        return Widgets.childrenFor(COLLECT_PARENT).length > 0;
    }

    public static boolean openCollect() {
        if (viewingCollect()) {
            return true;
        }
        Npc npc = Npcs.nearestByName("Banker");
        if (npc != null) {
            npc.processAction("Collect");
            return Time.sleep(GrandExchange::viewingCollect, 10000);
        }
        return false;
    }

    public static boolean closeCollect() {
        WidgetComponent component = Widgets.get(COLLECT_PARENT, COLLECT_CLOSE_PARENT);
        if (component != null) {
            component = component.children()[COLLECT_CLOSE_GRANDCHILD];
            if (component != null) {
                component.processAction("Close");
                return Time.sleep(() -> !viewingCollect(), 10000);
            }
        }
        return false;
    }

    public static boolean collectToBank(boolean close) {
        lastCollectTime = Time.millis();
        if (!viewingCollect()) {
            openCollect();
        }
        if (viewingCollect()) {
            WidgetComponent component = Widgets.get(COLLECT_PARENT, COLLECT_BANK_CHILD);
            if (component != null) {
                component.processAction("Collect to bank");
                Time.sleep(800, 1200);
                return !close || closeCollect();
            }
        }
        return false;
    }

    public static boolean collectToBank() {
        return collectToBank(true);
    }

    public static long lastCollectTime() {
        return lastCollectTime;
    }
}
