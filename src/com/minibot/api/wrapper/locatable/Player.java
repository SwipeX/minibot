package com.minibot.api.wrapper.locatable;

import com.minibot.client.natives.RSPlayer;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class Player extends Character<RSPlayer> {

    public Player(RSPlayer raw) {
        super(raw);
    }

    public String name() {
        return raw.getName();
    }
}
