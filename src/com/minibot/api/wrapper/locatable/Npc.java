package com.minibot.api.wrapper.locatable;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.action.tree.Action;
import com.minibot.api.action.tree.NpcAction;
import com.minibot.api.method.Npcs;
import com.minibot.api.method.Players;
import com.minibot.api.method.RuneScape;
import com.minibot.api.util.Identifiable;
import com.minibot.api.util.Time;
import com.minibot.client.natives.RSNpc;
import com.minibot.client.natives.RSNpcDefinition;
import com.minibot.util.DefinitionLoader;

public class Npc extends Character<RSNpc> implements Identifiable {

    private final int index;
    private final RSNpcDefinition definition;

    public Npc(RSNpc raw, int index) {
        super(raw);
        this.index = index;
        RSNpcDefinition rawDef = raw.getDefinition();
        if (rawDef == null)
            throw new IllegalStateException("bad npc definition!");
        definition = DefinitionLoader.findNpcDefinition(rawDef.getId());
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

    @Override
    public void processAction(int opcode, String action) {
        String name = name();
        if (name == null)
            return;
        RuneScape.processAction(new NpcAction(opcode, index), action, name, 0, 0);
    }

    @Override
    public void processAction(String action) {
        if (definition == null)
            return;
        String[] actions = definition.getActions();
        if (actions == null)
            return;
        int index = Action.indexOf(actions, action);
        if (index >= 0)
            processAction(ActionOpcodes.NPC_ACTION_0 + index, action);
    }

    @Override
    public String name() {
        RSNpcDefinition def = definition();
        return def == null ? null : def.getName();
    }

    public boolean attack() {
        Player local = Players.local();
        if (local != null) {
            boolean interacting = (local.targetIndex() == arrayIndex());
            if (interacting) {
                return false;
            } else {
                processAction("Attack");
                return Time.sleep(() -> local.targetIndex() == arrayIndex(), 10000);
            }
        }
        return false;
    }
}