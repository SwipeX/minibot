package com.minibot.api.action.tree;

import com.minibot.api.method.Players;
import com.minibot.api.wrapper.locatable.Player;
import com.minibot.client.natives.RSPlayer;

public class SpellOnPlayer extends CharacterAction {

    public SpellOnPlayer(int opcode, int entityId) {
        super(opcode, entityId);
    }

    public Player player() {
        RSPlayer[] players = Players.raw();
        int entityId = entityId();
        return players != null && entityId >= 0 && entityId < players.length ? new Player(players[entityId],entityId) : null;
    }
}
