package com.minibot.api.method;

import com.minibot.api.util.Array;
import com.minibot.api.util.Filter;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.api.wrapper.node.RSHashTable;
import com.minibot.api.wrapper.node.RSNode;
import com.minibot.internal.mod.ModScript;

import java.awt.*;
import java.util.HashMap;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class Widgets {

    public static final int BUTTON_INPUT = 24;
    public static final int  BUTTON_SPELL = 25;
    public static final int BUTTON_CLOSE = 26;
    public static final int  BUTTON_VAR_FLIP = 28;
    public static final int  BUTTON_VAR_SET = 29;
    public static final int   BUTTON_DIALOG = 30;

    public static Object[][] raw() {
        return (Object[][]) ModScript.hook("Client#widgets").get();
    }

    public static RSHashTable table() {
        return new RSHashTable(ModScript.hook("Client#widgetNodes").get());
    }

    public static int[] positionsX() {
        return (int[]) ModScript.hook("Client#widgetPositionsX").get();
    }

    public static int[] positionsY() {
        return (int[]) ModScript.hook("Client#widgetPositionsY").get();
    }

    public static int[] widths() {
        return (int[]) ModScript.hook("Client#widgetWidths").get();
    }

    public static int[] heights() {
        return (int[]) ModScript.hook("Client#widgetHeights").get();
    }

    public static WidgetComponent[] childrenFor(int index) {
        if (!validate(index))
            return new WidgetComponent[0];
        Object[][] raw = Widgets.raw();
        Object[] children = raw[index];
        if (children == null)
            return new WidgetComponent[0];
        WidgetComponent[] array = new WidgetComponent[0];
        for (Object child : children) {
            if (child == null)
                continue;
            WidgetComponent component = new WidgetComponent(index, child);
            array = Array.add(array, component);
        }
        return array;
    }

    public static WidgetComponent get(int parent, int child) {
        WidgetComponent[] children = childrenFor(parent);
        if (children != null) {
            for (WidgetComponent wc : children) {
                if (wc.id() == child)
                    return wc;
            }
        }
        return null;
    }

    public static Rectangle boundsFor(int parent) {
        WidgetComponent[] children = childrenFor(parent);
        if (children != null) {
            int[] positionsX = Widgets.positionsX();
            int[] positionsY = Widgets.positionsY();
            int[] widths = Widgets.widths();
            int[] heights = Widgets.heights();
            for (WidgetComponent child : children) {
                if (child != null) {
                    int index = child.boundsIndex();
                    if (index > 0 && index < positionsX.length && index < positionsY.length &&
                            index < widths.length && index < heights.length)
                        return new Rectangle(positionsX[index], positionsY[index], widths[index], heights[index]);
                }
            }
        }
        return null;
    }

    public static WidgetComponent findChildByFilter(int parent, Filter<WidgetComponent> filter) {
        for (WidgetComponent child : childrenFor(parent)) {
            try {
                if (child != null && filter.accept(child))
                    return child;
                if (child != null) {
                    WidgetComponent result = child.findChildByFilter(filter);
                    if (result != null)
                        return result;
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    public static WidgetComponent findComponentByFilter(Filter<WidgetComponent> filter) {
        Object[][] raw = raw();
        if (raw == null) return null;
        for (int i = 0; i < raw.length; i++) {
            WidgetComponent child = findChildByFilter(i, filter);
            if (child != null)
                return child;
        }
        return null;
    }

    public static WidgetComponent[] findComponentsByFilter(Filter<WidgetComponent> filter) {
        WidgetComponent[] components = new WidgetComponent[0];
        Object[][] raw = raw();
        if (raw == null) return null;
        for (int i = 0; i < raw.length; i++) {
            WidgetComponent child = findChildByFilter(i, filter);
            if (child != null)
                components = Array.add(components, child);
        }
        return components.length > 0 ? components : null;
    }

    public static WidgetComponent findComponentByText(Filter<String> filter) {
        return findComponentByFilter(wc -> {
            if (wc != null) {
                String text = wc.text();
                return text != null && filter.accept(text);
            }
            return false;
        });
    }

    public static WidgetComponent findComponentByAction(Filter<String> filter) {
        return findComponentByFilter(wc -> {
            if (wc != null) {
                String[] actions = wc.actions();
                if (actions != null && actions.length > 0) {
                    for (String action : actions) {
                        if (filter.accept(action))
                            return true;
                    }
                }
            }
            return false;
        });
    }

    public static boolean validate(int parent) {
        Object[][] widgets = raw();
        return widgets != null && widgets.length >= parent && widgets[parent] != null;
    }

    public static int findParentIndex(int uid) {
        Object node = Widgets.table().iterator().findByWidgetId(uid);
        if (node != null)
            uid = (int) RSNode.uid(node);
        return uid >> 16;
    }

    public static int findChildIndex(int uid) {
        Object node = Widgets.table().iterator().findByWidgetId(uid);
        if (node != null)
            uid = (int) RSNode.uid(node);
        return uid & 0xFFFF;
    }
}
