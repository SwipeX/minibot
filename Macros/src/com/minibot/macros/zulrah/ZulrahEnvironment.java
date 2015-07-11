package com.minibot.macros.zulrah;

import com.minibot.api.method.Inventory;
import com.minibot.api.method.Npcs;
import com.minibot.api.method.Objects;
import com.minibot.api.method.Widgets;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Tile;

/**
 * @author Tyler Sedlar
 * @since 7/11/2015
 */
public class ZulrahEnvironment {

    public static final Tile CAMP = new Tile(2199, 3056, 0);

    private static Tile source, left, right, bottom, topLeft, topRight, centralBottom;

    public static Tile source() {
        return source;
    }

    public static void setSource(Tile t) {
        source = t;
        left = t.derive(-10, 0);
        right = t.derive(10, 0);
        bottom = t.derive(0, -10);
        topLeft = t.derive(-5, 2);
        topRight = t.derive(6, 2);
        centralBottom = t.derive(4, -3);
    }

    public static Tile topLeft() {
        return topLeft;
    }

    public static Tile topRight() {
        return topRight;
    }

    public static Tile bottom() {
        return centralBottom;
    }

    public static boolean atCamp() {
        return CAMP.distance() < 20;
    }

    public static Npc findCollector() {
        return Npcs.nearestByName("Priestest Zul-Gwenwynig");
    }

    public static Npc findZulrah() {
        return Npcs.nearestByName("Zulrah");
    }

    public static ZulrahDirection findZulrahDirection() {
        Npc zulrah = findZulrah();
        if (zulrah != null && source != null) {
            Tile zulrahTile = zulrah.location();
            Tile[] tiles = {left, right, bottom};
            Tile nearest = null;
            for (Tile t : tiles) {
                if (nearest == null || t.distance(zulrahTile) < nearest.distance(zulrahTile))
                    nearest = t;
            }
            assert nearest != null;
            if (nearest.equals(left)) {
                return ZulrahDirection.LEFT;
            } else if (nearest.equals(right)) {
                return ZulrahDirection.RIGHT;
            } else if (nearest.equals(bottom)) {
                return ZulrahDirection.BOTTOM;
            }
        }
        return null;
    }

    public static boolean collect() {
        Npc npc = findCollector();
        if (npc != null) {
            int count = Inventory.count();
            npc.processAction("Collect");
            return Time.sleep(() -> Inventory.count() != count, 10000);
        }
        return false;
    }

    public static boolean boardBoat() {
        GameObject boat = Objects.nearestByName("Sacrificial boat");
        if (boat != null) {
            Tile tile = boat.location();
            tile = tile.derive(-1, -1);
            boat.processAction("Board", tile.localX(), tile.localY());
            if (Time.sleep(Widgets::viewingDialog, 15000)) {
                Widgets.processDialogOption(0);
                if (Time.sleep(Widgets::viewingContinue, 5000)) {
                    Widgets.processContinue();
                    return Time.sleep(() -> findZulrah() != null, 5000);
                }
            }
        }
        return false;
    }
}