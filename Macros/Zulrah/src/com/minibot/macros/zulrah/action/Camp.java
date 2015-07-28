package com.minibot.macros.zulrah.action;

import com.minibot.api.method.*;
import com.minibot.api.util.Random;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Player;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.macros.zulrah.Zulrah;

/**
 * @author Tim Dekker
 * @since 7/24/15
 */
public class Camp {

    public static final Tile CAMP = new Tile(2199, 3056, 0);
    public static final String BOAT = "Sacrificial boat";

    public static void act() { // will need to check interface for npc saying if she has more items or not
        if (atCamp()) {
            if (Zulrah.isDead()) {
                if (collect()) {
                    Gear.equip();
                }
            } else {
                boardBoat();
            }
        }
    }

    public static boolean atCamp() {
        return CAMP.distance() < 20;
    }

    public static Npc findCollector() {
        return Npcs.nearestByFilter(npc -> npc.name().startsWith("Priest"));
    }

    public static boolean collect() {
        GameObject boat = Objects.nearestByName(BOAT);
        if (boat != null) {
            Tile tile = boat.location();
            if (tile.distance() > 7) {
                Walking.walkTo(tile);
                Time.sleep(2200, 3000);
            }
        }
        Npc npc = findCollector();
        if (npc != null) {
            int count = Inventory.count();
            npc.processAction("Collect");
            return Time.sleep(() -> Inventory.count() != count, 10000);
        }
        return false;
    }

    public static boolean boardBoat() {
        GameObject boat = Objects.nearestByName(BOAT);
        if (boat != null) {
            Tile tile = boat.location();
            tile = tile.derive(-1, -1);
            boat.processAction("Board", tile.localX(), tile.localY());
            if (Time.sleep(Widgets::viewingDialog, Random.nextInt(14000, 17000))) {
                Widgets.processDialogOption(0);
            }
            if (Time.sleep(Widgets::viewingContinue, Random.nextInt(2500, 5000))) {
                Widgets.processContinue();
            }
            Player local = Players.local();
            if (local != null) {
                if (Time.sleep(() -> local.location().x() != 2213 && Widgets.viewingContinue(), Random.nextInt(8000, 10000))) {
                    Time.sleep(1500, 2500);
                    Widgets.processContinue();
                }
                return Time.sleep(() -> Zulrah.monster() != null, 5000);
            }
        }
        return false;
    }
}