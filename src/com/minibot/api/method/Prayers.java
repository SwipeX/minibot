/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.minibot.api.method;

import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Prayer;
import com.minibot.api.wrapper.WidgetComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dogerina
 * @since 15-07-2015
 */
public class Prayers {

    public static boolean active(Prayer prayer) {
        return (activeVarp() & prayer.bits()) != 0;
    }

    public static int activeVarp() {
        return Game.varp(83);
    }

    public static int activeCount() {
        return Integer.bitCount(activeVarp());
    }

    public static boolean anyActive() {
        return activeVarp() != 0;
    }

    public static boolean allInactive() {
        return !anyActive();
    }

    public static Prayer[] active() {
        int value = activeVarp();
        List<Prayer> active = new ArrayList<>();
        for (Prayer prayer : Prayer.values()) {
            if ((value & prayer.bits()) != 0) {
                active.add(prayer);
            }
        }
        return active.toArray(new Prayer[active.size()]);
    }

    public static void toggle(boolean endState, Prayer prayer) {
        WidgetComponent widget = Widgets.get(271, prayer.componentIndex());
        if (widget != null) {
            boolean currState;
            if ((currState = active(prayer)) != endState) {
                widget.processAction(!currState ? "Activate" : "Deactivate");
            }
        }
    }

    public static void toggle(Prayer prayer) {
        toggle(!active(prayer), prayer);
    }

    public static void flick(Prayer prayer, int delay) {
        WidgetComponent widget = Widgets.get(271, prayer.componentIndex());
        if (widget != null) {
            widget.processAction(!active(prayer) ? "Activate" : "Deactivate");
            Time.sleep(delay);
        }
    }

    public static void flick(Prayer prayer, int delay, int times) {
        for (int i = 0; i < times; i++) {
            flick(prayer, delay);
        }
    }
}
