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

    public TilePath(Tile... tiles) {
        this.tiles = tiles;
    }

    public void step() {
        Tile farthestTile = null;
        for (int i = tiles.length - 1; i > 0; i--) {
            if (tiles[i].distance() < 30) {
                farthestTile = tiles[i];
                break;
            }
        }
        if (farthestTile == null) {
            return;
        }
        final Tile tile = farthestTile;
        Walking.walkTo(tile);
        Time.sleep(800, 1000);
        Time.sleep(() -> {
            Player local = Players.local();
            return local != null && tile.distance() < 3;
        }, 8000);
    }
}
