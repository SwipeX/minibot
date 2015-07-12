/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.minibot.api.method;

/**
 * @author unsigned
 * @since 02-06-2015
 */
public class Combat {

    private static final int AUTO_RETALIATE_VARP = 172;

    public static boolean autoRetaliating() {
        return Game.varp(AUTO_RETALIATE_VARP) != 1;
    }

    public static void setAutoRetaliating(boolean on) {
        if (autoRetaliating() != on) {
            RuneScape.processAction(1, -1, 38862875, 57, "Auto retaliate", "", 50, 50);
        }
    }

    public static int style() { //0 = Punch, 1 = Kick, 3 = Block, 2 = the other one (bottom right)
        return Game.varp(43);
    }

    //0 = top left button, 1 = top right, 3 = bottom left, 2 = bottom right
    public static void setStyle(int style) {
        if (style() == style) {
            return;
        }
        int hash = 38862851;
        switch (style) {
            case 1:
                hash += 4;
                break;
            case 2:
                hash += 12;
                break;
            case 3:
                hash += 8;
                break;
        }
        RuneScape.processAction(1, -1, hash, 57, "", "", 50, 50);
    }
}