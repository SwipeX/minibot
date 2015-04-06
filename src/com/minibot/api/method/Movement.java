package com.minibot.api.method;

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

    public static void walk(Tile tile) {
        final Point t2s = Projection.toScreen(tile);
    }
}
