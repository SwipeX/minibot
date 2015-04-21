package com.minibot.api.action.tree;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.method.Players;
import com.minibot.api.wrapper.locatable.Player;
import com.minibot.client.natives.RSPlayer;

public class PlayerAction extends CharacterAction {

    public PlayerAction(int opcode, int player_index) {
        super(opcode, player_index);
    }

    public int playerIndex() {
        return arg0;
    }

    public int actionIndex() {
        return opcode - ActionOpcodes.PLAYER_ACTION_0;
    }

    public static boolean isInstance(int opcode) {
        opcode = Action.pruneOpcode(opcode);
        return opcode >= ActionOpcodes.PLAYER_ACTION_0
                && opcode <= ActionOpcodes.PLAYER_ACTION_7;
    }

    public Player player() {
        int index = playerIndex();
        if (index < 0 || index > Players.MAX_PLAYERS)
            return null;
        RSPlayer[] players = Players.raw();
        return players != null && index >= 0 && index < players.length ? new Player(players[index]) : null;
    }

    @Override
    public String toString() {
        return "Player Action[" + playerIndex() + "](" + "@" + actionIndex() + ") on " + player();
    }
}
