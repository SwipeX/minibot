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
        return player != null ? new Player(player, -1) : null;
    }

    public static RSPlayer[] raw() {
        return Minibot.instance().client().getPlayers();
    }

    public static Player[] loaded() {
        RSPlayer[] raws = raw();
        if (raws == null || raws.length == 0)
            return new Player[0];
        int[] indices = Minibot.instance().client().getPlayerIndices();
        if (indices == null || indices.length == 0)
            return new Player[0];
        List<Player> Players = new ArrayList<>(indices.length);
        for (int index : indices) {
            RSPlayer raw = raws[index];
            if (raw == null)
                continue;
            Player player = new Player(raw, index);
            Players.add(player);
        }
        return Players.toArray(new Player[Players.size()]);
    }

    public static Player atIndex(int idx) {
        RSPlayer[] raws = raw();
        if (raws == null || raws.length == 0)
            return null;
        return new Player(raws[idx], idx);
    }
}
