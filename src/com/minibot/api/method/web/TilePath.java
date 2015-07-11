package com.minibot.api.method.web;

import com.minibot.api.method.Players;
import com.minibot.api.method.Walking;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.locatable.Player;
import com.minibot.api.wrapper.locatable.Tile;

/**
 * @author Tyler Sedlar
 * @since 7/10/2015
 */
public class TilePath {

    private final Tile[] tiles;

    private int step = 0;

    public TilePath(Tile... tiles) {
        this.tiles = tiles;
    }

    public void step() {
        if (step < tiles.length) {
            Tile tile = tiles[step];
            Walking.walkTo(tile);
            if (Time.sleep(() -> {
                Player local = Players.local();
                return local != null && (tile.distance() < 3 || Players.local().animation() == -1);
            }, 15000)) {
                if (tile.distance() < 3)
                    step++;
            }
        }
    }

    public void reset() {
        step = 0;
    }
}
