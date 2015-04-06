package com.minibot.api.method.projection;

import com.minibot.api.Packet;
import com.minibot.api.method.RuneScape;
import com.minibot.api.method.Widgets;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.internal.mod.ModScript;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class Minimap {

    public static int scale() {
        return ModScript.hook("Client#mapScale").getInt();
    }

    public static int angle() {
        return ModScript.hook("Client#mapAngle").getInt();
    }

    public static int offset() {
        return ModScript.hook("Client#mapOffset").getInt();
    }

    public static WidgetComponent component() {
        WidgetComponent[] children = Widgets.childrenFor(548);
        for (WidgetComponent wc : children) {
            if (wc == null || wc.width() != 172)
                continue;
            return wc;
        }
        return null;
    }
}
