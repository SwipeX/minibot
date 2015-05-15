package com.minibot.api.method;

import com.minibot.Minibot;
import com.minibot.api.wrapper.locatable.Tile;

public class Walking {

    public static void walkTo(Tile tile) {
        int x = tile.localX();
        int y = tile.localY();
        if (x < 0 || x > 104 || y < 0 || y > 104)
            return;
        Minibot.instance().client().setHoveredRegionTileX(x);
        Minibot.instance().client().setHoveredRegionTileY(y);
    }
}
