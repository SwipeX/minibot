package com.minibot.api.method;

import com.minibot.Minibot;
import com.minibot.api.action.tree.DialogButtonAction;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.client.natives.RSWidget;
import com.minibot.util.Array;

import java.awt.Rectangle;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class Widgets {

    /** Values of the Widget#buttonType field (also referred to as actionType) **/
    public static final int BUTTON_INPUT = 1;
    public static final int BUTTON_SPELL = 2;
    public static final int BUTTON_CLOSE = 3;
    public static final int BUTTON_VAR_FLIP = 4;
    public static final int BUTTON_VAR_SET = 5;
    public static final int BUTTON_DIALOG = 6;

    public static RSWidget[][] raw() {
        return Minibot.instance().client().getWidgets();
    }

    public static int[] positionsX() {
        return Minibot.instance().client().getWidgetPositionsX();
    }

    public static int[] positionsY() {
        return Minibot.instance().client().getWidgetPositionsY();
    }

    public static int[] widths() {
        return Minibot.instance().client().getWidgetWidths();
    }

    public static int[] heights() {
        return Minibot.instance().client().getWidgetHeights();
    }

    public static WidgetComponent[] childrenFor(int index) {
        if (!validate(index)) {
            return new WidgetComponent[0];
        }
        RSWidget[][] raw = Widgets.raw();
        RSWidget[] children = raw[index];
        if (children == null) {
            return new WidgetComponent[0];
        }
        WidgetComponent[] array = new WidgetComponent[children.length];
        for (int i = 0; i < array.length; i++) {
            RSWidget raw_ = children[i];
            if (raw_ == null) {
                continue;
            }
            array[i] = new WidgetComponent(index, raw_);
        }
        return array;
    }

    public static WidgetComponent get(int parent, int child) {
        WidgetComponent[] children = childrenFor(parent);
        if (children != null) {
            for (WidgetComponent wc : children) {
                if (wc != null && wc.index() == child) {
                    return wc;
                }
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
                            index < widths.length && index < heights.length) {
                        return new Rectangle(positionsX[index], positionsY[index], widths[index], heights[index]);
                    }
                }
            }
        }
        return null;
    }

    public static WidgetComponent get(int parent, Filter<WidgetComponent> filter) {
        for (WidgetComponent child : childrenFor(parent)) {
            try {
                if (child != null && filter.accept(child)) {
                    return child;
                }
                if (child != null) {
                    WidgetComponent result = child.child(filter);
                    if (result != null) {
                        return result;
                    }
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public static WidgetComponent get(Filter<WidgetComponent> filter) {
        RSWidget[][] raw = raw();
        if (raw == null) {
            return null;
        }
        for (int i = 0; i < raw.length; i++) {
            WidgetComponent child = get(i, filter);
            if (child != null) {
                return child;
            }
        }
        return null;
    }

    public static WidgetComponent[] getAll(Filter<WidgetComponent> filter) {
        WidgetComponent[] components = new WidgetComponent[0];
        RSWidget[][] raw = raw();
        if (raw == null) {
            return null;
        }
        for (int i = 0; i < raw.length; i++) {
            WidgetComponent child = get(i, filter);
            if (child != null) {
                components = Array.add(components, child);
            }
        }
        return components.length > 0 ? components : null;
    }

    public static WidgetComponent byText(Filter<String> filter) {
        return get(wc -> {
            if (wc != null) {
                String text = wc.text();
                return text != null && filter.accept(text);
            }
            return false;
        });
    }

    public static WidgetComponent byAction(Filter<String> filter) {
        return get(wc -> {
            if (wc != null) {
                String[] actions = wc.actions();
                if (actions != null && actions.length > 0) {
                    for (String action : actions) {
                        if (filter.accept(action)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        });
    }

    public static boolean validate(int parent) {
        RSWidget[][] widgets = raw();
        return widgets != null && widgets.length >= parent && widgets[parent] != null;
    }

    private static int continueDialogIndex = -1;

    public static boolean viewingContinue() {
        int[] uids = {15007745, 15138818, 14221314};
        for (int uid : uids) {
            if (Widgets.childrenFor(uid >> 16).length > 0) {
                continueDialogIndex = uid;
                return true;
            }
        }
        return false;
    }

    public static void processContinue() {
        if (viewingContinue()) {
            RuneScape.processAction(new DialogButtonAction(continueDialogIndex, -1));
        }
    }

    public static boolean viewingDialog() {
        int uid = 14352384;
        return Widgets.childrenFor(uid >> 16).length > 0;
    }

    public static void processDialogOption(int optionIndex) {
        if (viewingDialog()) {
            RuneScape.processAction(new DialogButtonAction(14352384, optionIndex + 1));
        }
    }
}