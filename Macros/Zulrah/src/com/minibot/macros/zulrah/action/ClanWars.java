package com.minibot.macros.zulrah.action;

import com.minibot.api.method.Game;
import com.minibot.api.method.Objects;
import com.minibot.api.method.Players;
import com.minibot.api.method.Skills;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.Tile;

/**
 * @author Tim Dekker
 * @since 7/24/15
 */
public class ClanWars {

    public static void handle() {
        if (Game.levels()[Skills.PRAYER] < Game.realLevels()[Skills.PRAYER] ||
                Game.levels()[Skills.HITPOINTS] < Game.realLevels()[Skills.HITPOINTS]) {
            handlePortal();
        } else {
            //bank
        }
    }

    private static void handlePortal() {
        GameObject portal = Objects.nearestByName("Free-for-all portal");
        if (portal != null) {
            Tile location = Players.local().location();
            Tile tile = portal.location();
            tile = tile.derive(-1, -1);
            portal.processAction("Enter", tile.localX(), tile.localY());
            Time.sleep(() -> Players.local().location().x() != location.x(), 5000);
            GameObject exit = Objects.nearestByAction("Exit");
            if (exit != null) {
                Tile nextLocation = Players.local().location();
                Tile nextTile = exit.location();
                nextTile = nextTile.derive(-1, -1);
                exit.processAction("Exit", nextTile.localX(), nextTile.localY());
                Time.sleep(() -> Players.local().location().x() != nextLocation.x(), 5000);
            }
        }
    }
}
