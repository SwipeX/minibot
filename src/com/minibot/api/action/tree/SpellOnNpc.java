package com.minibot.api.action.tree;

import com.minibot.api.method.Npcs;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.client.natives.RSNpc;

public class SpellOnNpc extends CharacterAction {

    public SpellOnNpc(int opcode, int npcIndex) {
        super(opcode, npcIndex);
    }

    public Npc npc() {
        RSNpc[] npcs = Npcs.raw();
        int entityId = entityId();
        return npcs != null && entityId >= 0 && entityId < npcs.length ? new Npc(npcs[entityId], entityId) : null;
    }
}