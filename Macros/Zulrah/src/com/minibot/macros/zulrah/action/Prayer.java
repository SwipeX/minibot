package com.minibot.macros.zulrah.action;

import com.minibot.api.action.tree.Action;
import com.minibot.api.method.Widgets;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.macros.zulrah.Zulrah;
import com.minibot.macros.zulrah.phase.Phase;
import com.minibot.macros.zulrah.phase.SnakeType;
import com.minibot.macros.zulrah.phase.Stage;

/**
 * @author Tim Dekker
 * @since 7/15/15
 */
public class Prayer {
    public static final int MYSTIC_MIGHT_INDEX = 27;
    public static final int EAGLE_EYE_INDEX = 26;
    public static final int PROTECT_MAGIC_INDEX = 16;
    public static final int PROTECT_RANGE_INDEX = 17;

    private static final int PRAYER_BOOK = 271;
    private static final int[] INDICES = new int[]{PROTECT_RANGE_INDEX,
            PROTECT_MAGIC_INDEX, EAGLE_EYE_INDEX, MYSTIC_MIGHT_INDEX};

    public static boolean setPrayers() {
        if (Zulrah.getMonster() == null) {
            for (int index : INDICES) {
                if (!deactivate(index)) {
                    return false;
                }
            }
            return true;
        }
        Phase phase = Zulrah.getPhase();
        if (phase != null) {
            Stage stage = phase.getCurrent();
            if (stage != null) {
                SnakeType snakeType = stage.getSnakeType();
                if (snakeType != null) {
                    int[] indexes = snakeType.getPrayerComponentIndices();
                    for (int index : indexes) {
                        if (!activate(index)) {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean activate(int index) {
        WidgetComponent component = Widgets.get(PRAYER_BOOK, index);
        if (component != null && Action.indexOf(component.actions(), "Activate") >= 0) {
            component.processAction("Activate");
            return Time.sleep(() -> Action.indexOf(component.actions(), "Deactivate") >= 0, 2000);
        }
        return false;
    }

    public static boolean deactivate(int index) {
        WidgetComponent component = Widgets.get(PRAYER_BOOK, index);
        if (component != null && Action.indexOf(component.actions(), "Deactivate") >= 0) {
            component.processAction("Deactivate");
            return Time.sleep(() -> Action.indexOf(component.actions(), "Activate") >= 0, 2000);
        }
        return false;
    }
}
