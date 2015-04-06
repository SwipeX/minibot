package com.minibot.api.method;

import com.minibot.api.wrapper.locatable.Player;
import com.minibot.internal.mod.ModScript;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class Players {

    public static Player local() {
        Object player = ModScript.hook("Client#player").get();
        return player != null ? new Player(player) : null;
    }
}
