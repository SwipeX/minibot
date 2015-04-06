package com.minibot.api.action.tree;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.method.Npcs;
import com.minibot.api.wrapper.def.NpcDefinition;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.internal.def.DefinitionLoader;

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
        Object[] npcs = Npcs.raw();
        return index >= 0 && index < npcs.length ? new Npc(npcs[index], index) : null;
    }

    public Object definition() {
        return DefinitionLoader.findNpcDefinition(entityId());
    }

    public String name() {
        return NpcDefinition.name(definition());
    }

    public String actionName() {
        String[] actions = NpcDefinition.actions(definition());
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
