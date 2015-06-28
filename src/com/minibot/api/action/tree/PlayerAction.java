package com.minibot.api.action.tree;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.method.Players;
import com.minibot.api.wrapper.locatable.Player;
import com.minibot.client.natives.RSPlayer;

public class PlayerAction extends CharacterAction {

    public PlayerAction(int opcode, int playerIndex) {
        super(opcode, playerIndex);
    }

    public int playerIndex() {
        return arg0;
    }

    @Override
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
        RSPlayer player = players[index];
        return players != null && index >= 0 && index < players.length && player !=null ? new Player(player,index) : null;
    }

    @Override
    public String toString() {
        return "PlayerAction [player-index=" + playerIndex() + " | action-index=" + actionIndex() + " | on " + player();
    }
}