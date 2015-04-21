package com.minibot.api.method;

import com.minibot.Minibot;
import com.minibot.api.wrapper.locatable.Player;
import com.minibot.client.natives.RSPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class Players {

    public static final int MAX_PLAYERS = 2048;

    public static Player local() {
        RSPlayer player = Minibot.instance().client().getPlayer();
        return player != null ? new Player(player) : null;
    }

    public static RSPlayer[] raw() {
        return Minibot.instance().client().getPlayers();
    }

    public static Player[] loaded() {
        List<Player> players = new ArrayList<>();
        RSPlayer[] raws = raw();
        if (raws == null || raws.length == 0)
            return new Player[0];
        for (RSPlayer player : raws) {
            if (player == null)
                continue;
            players.add(new Player(player));
        }
        return players.toArray(new Player[players.size()]);
    }
}
