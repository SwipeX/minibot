package com.minibot.api.action.tree;

import com.minibot.api.method.Npcs;
import com.minibot.api.wrapper.locatable.Npc;

public class SpellOnNpc extends CharacterAction {

    public SpellOnNpc(int opcode, int npcIndex) {
        super(opcode, npcIndex);
    }

    public Npc npc() {
        Object[] npcs = Npcs.raw();
        int entityId = entityId();
        return npcs != null && entityId >= 0 && entityId < npcs.length ? new Npc(npcs[entityId], entityId) : null;
    }
}
