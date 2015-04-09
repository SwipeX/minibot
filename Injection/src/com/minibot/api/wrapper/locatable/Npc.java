package com.minibot.api.wrapper.locatable;

import com.minibot.api.util.Identifiable;
import com.minibot.client.natives.RSNpc;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class Npc extends Character<RSNpc> implements Identifiable {

    private final int index;

    public Npc(RSNpc raw, int index) {
        super(raw);
        this.index = index;
    }

    public int index() {
        return index;
    }

    @Override
    public boolean valid() {
        return super.valid() && id() != -1;
    }

    @Override
    public int id() {
        return -1;
    }
}
