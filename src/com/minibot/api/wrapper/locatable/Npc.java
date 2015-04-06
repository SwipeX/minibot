package com.minibot.api.wrapper.locatable;

import com.minibot.api.method.RuneScape;
import com.minibot.api.method.projection.Projection;
import com.minibot.api.util.Random;
import com.minibot.api.wrapper.def.NpcDefinition;
import com.minibot.internal.mod.hooks.ReflectionData;

import java.awt.*;
import java.lang.*;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
@ReflectionData(className = "Npc")
public class Npc extends Character {

    private final int index;
    private final NpcDefinition definition;

    public Npc(Object raw, int index) {
        super(raw);
        this.index = index;
        Object def = hook("definition").get(get());
        NpcDefinition definition = null;
        if (def != null) {
            definition = new NpcDefinition(def);
            if (definition.id() == -1) {
                definition = null;
            } else {
                if (definition.transformed())
                    definition.fix();
            }
        }
        this.definition = definition;
    }

    public int index() {
        return index;
    }

    public NpcDefinition definition() {
        return definition;
    }

    public void doAction(int packetId, String action) {
        if (definition == null)
            return;
        String name = definition.name();
        if (name == null)
            return;
        Point p = Projection.toScreen(fineX(), fineY());
        if (p == null)
            return;
        RuneScape.doAction(0, 0, packetId, index(), action, "<col=ffff00>" + name,
                p.x + Random.nextInt(-4, 4), p.y + Random.nextInt(-4, 4));
    }

    @Override
    public boolean valid() {
        if (!super.valid())
            return false;
        NpcDefinition def = definition();
        return def != null && def.id() != -1;
    }
}
