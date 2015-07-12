package com.minibot.api.method;

import com.minibot.Minibot;
import com.minibot.api.method.web.Web;
import com.minibot.api.wrapper.locatable.Tile;

public class Walking {

    private static final int MAX_DIST = 98;

    public static void walkTo(Tile tile) {
        int x = tile.localX();
        int y = tile.localY();
        if (x < 0) {
            x = 0;
        }
        if (x > MAX_DIST) {
            x = MAX_DIST;
        }
        if (y < 0) {
            y = 0;
        }
        if (y > MAX_DIST) {
            y = MAX_DIST;
        }
        Minibot.instance().client().setHoveredRegionTileX(x);
        Minibot.instance().client().setHoveredRegionTileY(y);
    }

    private static Web web = new Web();

    public static void setWeb(Web web) {
        Walking.web = web;
    }

    public static Web web() {
        return web;
    }
}