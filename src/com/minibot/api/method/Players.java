package com.minibot.api.method;

import com.minibot.api.util.Array;
import com.minibot.api.wrapper.locatable.Player;
import com.minibot.mod.ModScript;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class Players {

    public static final int MAX_PLAYERS = 2048;

    public static Player local() {
        Object player = ModScript.hook("Client#player").get();
        return player != null ? new Player(player) : null;
    }

    public static Object[] raw() {
        return (Object[]) ModScript.hook("Client#players").get();
    }

    public static Player[] loaded() {
        Object[] raws = raw();
        if (raws == null || raws.length == 0)
            return new Player[0];
        Player[] array = new Player[0];
        for (Object player : raws) {
            if (player == null)
                continue;
            array = Array.add(array, new Player(player));
        }
        return array;
    }
}
