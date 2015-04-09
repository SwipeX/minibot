package com.minibot.api.method.projection;

import com.minibot.Minibot;
import com.minibot.api.method.Players;
import com.minibot.api.wrapper.locatable.Locatable;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.mod.ModScript;

/**
 * @author Tyler Sedlar
 * @since 4/4/15.
 */
public class Camera {

    public static int x() {
        return Minibot.instance().client().getCameraX();
    }

    public static int y() {
        return Minibot.instance().client().getCameraY();
    }

    public static int z() {
        return Minibot.instance().client().getCameraZ();
    }

    public static int pitch() {
        return Minibot.instance().client().getCameraPitch();
    }

    public static int yaw() {
        return Minibot.instance().client().getCameraYaw();
    }

    public static int angle() {
        return (int) ((360D / 2048) * Math.min(2047 - yaw(), 2048));
    }

    public static int angleTo(Locatable locatable) {
        Tile t1 = locatable.location();
        Tile t2 = Players.local().location();
        int angle = 90 - ((int) Math.toDegrees(Math.atan2(t1.y() - t2.y(), t1.x() - t2.x())));
        if (angle < 0)
            angle = 360 + angle;
        return angle % 360;
    }
}
