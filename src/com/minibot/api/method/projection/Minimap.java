package com.minibot.api.method.projection;

import com.minibot.Minibot;
import com.minibot.api.method.Widgets;
import com.minibot.api.wrapper.WidgetComponent;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class Minimap {

    public static int scale() {
        return Minibot.instance().client().getMapScale();
    }

    public static int rotation() {
        return Minibot.instance().client().getMapAngle();
    }

    public static int offset() {
        return Minibot.instance().client().getMapOffset();
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
