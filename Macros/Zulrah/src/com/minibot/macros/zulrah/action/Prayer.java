package com.minibot.macros.zulrah.action;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.method.Game;
import com.minibot.api.method.Skills;
import com.minibot.api.method.Widgets;
import com.minibot.api.util.Random;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.macros.zulrah.Zulrah;
import com.minibot.macros.zulrah.phase.SnakeType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tim Dekker
 * @since 7/15/15
 */
public enum Prayer {

    THICK_SKIN(-1),
    BURST_OF_STRENGTH(-1),
    CLARITY_OF_THOUGHT(-1),
    ROCK_SKIN(-1),
    SUPER_HUMAN_STRENGTH(-1),
    IMPROVED_REFLEXES(-1),
    RAPID_RESTORE(-1),
    RAPID_HEAL(-1),
    PROTECT_ITEM(-1),
    STEEL_SKIN(-1),
    ULTIMATE_STRENGTH(-1),
    INCREDIBLE_REFLEXES(-1),
    PROTECT_FROM_MAGIC(16),
    PROTECT_FROM_MISSILES(17),
    PROTECT_FROM_MELEE(-1),
    RETRIBUTION(-1),
    REDEMPTION(-1),
    SMITE(-1),
    SHARP_EYE(-1),
    MYSTIC_WILL(-1),
    HAWK_EYE(-1),
    MYSTIC_LORE(25),
    EAGLE_EYE(26),
    MYSTIC_MIGHT(27),
    CHIVALRY(-1),
    PIETY(-1);

    public static final int PRAYER_BOOK = 271;
    public static final int VARP_TOGGLED = 83;

    public final int componentIndex;

    Prayer(int componentIndex) {
        this.componentIndex = componentIndex;
    }

    public boolean toggled() {
        int mask = (int) Math.pow(2, ordinal());
        return (Game.varp(VARP_TOGGLED) & mask) == mask;
    }

    public boolean setActive(boolean active) {
        if (points() == 0) {
            return false;
        }
        boolean toggled = toggled();
        if ((active && toggled) || (!active && !toggled)) {
            return true;
        } else {
            WidgetComponent component = Widgets.get(PRAYER_BOOK, componentIndex);
            if (component != null) {
                String action = (active ? "Activate" : "Deactivate");
                component.processAction(ActionOpcodes.WIDGET_ACTION, 1, action, "");
                Time.sleep(150, 250);
                return true;
            } else {
                return false;
            }
        }
    }

    public static boolean setZulrahPrayers() {
        Npc zulrah = Zulrah.monster();
        if (zulrah == null) {
            deactivateAll();
            return true;
        }
        SnakeType type = Zulrah.phase().current().getSnakeType();
        Prayer[] prayers = type.getPrayers();
        if (prayers == null) {
            deactivateAll();
        } else {
            for (Prayer prayer : prayers) {
                if (!prayer.toggled()) {
                    prayer.setActive(true);
                }
            }
            return Time.sleep(() -> {
                for (Prayer prayer : prayers) {
                    if (!prayer.toggled()) {
                        return false;
                    }
                }
                return true;
            }, Random.nextInt(1500));
        }
        return true;
    }

    public static boolean deactivateAll() {
        List<Prayer> untoggled = new ArrayList<>();
        for (Prayer prayer : values()) {
            if (prayer.toggled()) {;
                prayer.setActive(false);
                untoggled.add(prayer);
            }
        }
        return Time.sleep(() -> {
            for (Prayer prayer : untoggled) {
                if (prayer.toggled()) {
                    return false;
                }
            }
            return true;
        }, Random.nextInt(1500));
    }

    public static int points() {
        return Game.levels()[Skills.PRAYER];
    }
}