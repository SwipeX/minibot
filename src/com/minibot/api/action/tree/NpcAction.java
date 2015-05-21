package com.minibot.api.action.tree;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.method.Npcs;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.client.natives.RSNpc;
import com.minibot.client.natives.RSNpcDefinition;
import com.minibot.util.DefinitionLoader;

import java.util.Arrays;

public class NpcAction extends CharacterAction {

    public NpcAction(int opcode, int npcIndex) {
        super(opcode, npcIndex);
    }

    public int actionIndex() {
        return opcode - ActionOpcodes.NPC_ACTION_0;
    }

    public static boolean isInstance(int opcode) {
        opcode = Action.pruneOpcode(opcode);
        return opcode >= ActionOpcodes.NPC_ACTION_0 && opcode <= ActionOpcodes.NPC_ACTION_4;
    }

    public int npcIndex() {
        return entityId();
    }

    public Npc npc() {
        int index = npcIndex();
        if (index < 0 || index > Short.MAX_VALUE)
            return null;
        RSNpc[] npcs = Npcs.raw();
        return index >= 0 && index < npcs.length ? new Npc(npcs[index], index) : null;
    }

    public RSNpcDefinition definition() {
        Npc npc = npc();
        return npc == null ? null : npc.definition();
    }

    public String name() {
        RSNpcDefinition def = definition();
        return def == null ? null : def.getName();
    }

    public String actionName() {
        RSNpcDefinition def = definition();
        if (def == null) return null;
        String[] actions = def.getActions();
        if (actions == null)
            return null;
        int actionIndex = actionIndex();
        return actionIndex >= 0 && actionIndex < actions.length ? actions[actionIndex] : null;
    }

    @Override
    public String toString() {
        return "Npc Interaction [action-name(index=" + actionIndex() + ")=" + actionName() + "] on " + npc();
    }
}
