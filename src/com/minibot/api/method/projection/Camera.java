package com.minibot.api.method.projection;

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
        return ModScript.hook("Client#cameraX").getInt();
    }

    public static int y() {
        return ModScript.hook("Client#cameraY").getInt();
    }

    public static int z() {
        return ModScript.hook("Client#cameraZ").getInt();
    }

    public static int pitch() {
        return ModScript.hook("Client#cameraPitch").getInt();
    }

    public static int yaw() {
        return ModScript.hook("Client#cameraYaw").getInt();
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
