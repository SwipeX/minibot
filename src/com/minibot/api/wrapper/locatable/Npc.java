package com.minibot.api.wrapper.locatable;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.action.tree.NpcAction;
import com.minibot.api.method.RuneScape;
import com.minibot.api.util.Identifiable;
import com.minibot.client.natives.RSNpc;
import com.minibot.client.natives.RSNpcDefinition;
import com.minibot.util.DefinitionLoader;

import java.util.Arrays;

public class Npc extends Character<RSNpc> implements Identifiable {

    private final int index;
    private final RSNpcDefinition definition;

    public Npc(RSNpc raw, int index) {
        super(raw);
        this.index = index;
        RSNpcDefinition rawDef = raw.getDefinition();
        if (rawDef == null)
            throw new IllegalStateException("bad npc definition!");
        this.definition = DefinitionLoader.findNpcDefinition(rawDef.getId());
    }

    public int arrayIndex() {
        return index;
    }

    @Override
    public boolean validate() {
        return super.validate() && id() != -1;
    }

    @Override
    public int id() {
        return definition == null ? -1 : definition.getId();
    }

    public RSNpcDefinition definition() {
        return definition;
    }

    public void processAction(int opcode, String action) {
        String name = name();
        if (name == null)
            return;
        RuneScape.processAction(new NpcAction(opcode, index), action, name, 0, 0);
    }

    public void processAction(String action) {
        if (definition == null) return;
        String[] actions = definition.getActions();
        if (actions == null)
            return;
        int index = Arrays.asList(actions).indexOf(action);
        if (index >= 0)
            processAction(ActionOpcodes.NPC_ACTION_0 + index, action);
    }

    public String name() {
        RSNpcDefinition def = definition();
        return def == null ? null : def.getName();
    }
}
