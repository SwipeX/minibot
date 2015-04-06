package com.minibot.api.method;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.action.tree.WalkHereAction;
import com.minibot.api.method.projection.Projection;
import com.minibot.api.wrapper.locatable.Tile;

import java.awt.*;

/**
 * Project: minibot
 * Date: 06-04-2015
 * Time: 22:00
 * Created by Dogerina.
 * Copyright under GPL license by Dogerina.
 */
public class Movement {

    /**
     * TODO minimap navigation
     */
    public static void walk(Tile tile) {
        Point t2s = Projection.toScreen(tile);
        if (t2s == null)
            return;
        RuneScape.processAction(new WalkHereAction(), "Walk here", "", t2s.x, t2s.y);
    }
}
