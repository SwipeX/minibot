package com.minibot.api.wrapper.locatable;

import com.minibot.internal.mod.hooks.ReflectionData;

import java.lang.*;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
@ReflectionData(className = "Player")
public class Player extends Character {

    public Player(Object raw) {
        super(raw);
    }

    public String name() {
        return hook("name").getString(get());
    }
}
