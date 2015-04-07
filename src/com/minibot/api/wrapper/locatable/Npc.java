package com.minibot.api.wrapper.locatable;

import com.minibot.api.action.tree.NpcAction;
import com.minibot.api.method.RuneScape;
import com.minibot.api.method.projection.Projection;
import com.minibot.api.util.Identifiable;
import com.minibot.api.util.Random;
import com.minibot.api.wrapper.def.NpcDefinition;
import com.minibot.internal.def.DefinitionLoader;
import com.minibot.mod.ModScript;
import com.minibot.mod.hooks.ReflectionData;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
@ReflectionData(className = "Npc")
public class Npc extends Character implements Identifiable {

    private final int index;
    private final int id;
    private final Object definition;

    public Npc(Object raw, int index) {
        super(raw);
        this.index = index;
        Object rawDef = ModScript.hook("Npc#definition").get(raw);
        this.id = NpcDefinition.id(rawDef);
        this.definition = DefinitionLoader.findNpcDefinition(id);
    }

    public int index() {
        return index;
    }

    public int id() {
        return id;
    }

    public String name() {
        return NpcDefinition.name(definition);
    }

    public String[] actions() {
        return NpcDefinition.actions(definition);
    }

    public int[] transformIds() {
        return NpcDefinition.transformIds(definition);
    }

    public int transformIndex() {
        return NpcDefinition.transformIndex(definition);
    }

    public void processAction(int opcode, String action) {
        String name = name();
        if (name == null)
            return;
        Point p = Projection.toScreen(fineX(), fineY());
        if (p == null)
            return;
        RuneScape.processAction(new NpcAction(opcode, index), action, name,
                p.x + Random.nextInt(-4, 4), p.y + Random.nextInt(-4, 4));
    }

    @Override
    public boolean valid() {
        return super.valid() && id() != -1;
    }
}
