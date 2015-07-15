package com.minibot.api.wrapper.locatable;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.action.tree.PlayerAction;
import com.minibot.api.method.Players;
import com.minibot.api.method.RuneScape;
import com.minibot.client.natives.RSPlayer;

import java.awt.Point;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class Player extends Character<RSPlayer> {

    private final int index;

    public Player(RSPlayer raw, int index) {
        super(raw);
        this.index = index;
    }

    @Override
    public void processAction(String action) {
        processAction(ActionOpcodes.PLAYER_ACTION_0, action);
    }

    @Override
    public void processAction(int opcode, String action) {
        Point p = screen();
        RuneScape.processAction(new PlayerAction(opcode, index()), action, "", p.x, p.y);
    }

    public int index() {
        if (index < 0) {
            for (Player player : Players.loaded()) {
                if (player.name().equals(name())) {
                    return player.index();
                }
            }
        }
        return index;
    }

    @Override
    public int id() {
        throw new UnsupportedOperationException();
    }

    public String name() {
        return raw.getName();
    }
}