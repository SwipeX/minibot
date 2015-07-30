package com.minibot.macros.zulrah.action;

import com.minibot.api.method.*;
import com.minibot.api.method.web.TilePath;
import com.minibot.api.util.Random;
import com.minibot.api.util.Time;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.Player;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.macros.zulrah.Zulrah;

public class DeathWalk {

    private static final Tile FALADOR_BANK = new Tile(2946, 3368, 0);
    private static final Tile LUMBRIDGE_STAIRS = new Tile(3206, 3209, 0);
    private static final Tile LUMBRIDGE_BANK = new Tile(3208, 3220, 2);

    private static final TilePath FALADOR_BANK_PATH = new TilePath(new Tile(2965, 3347, 0), new Tile(2965, 3362, 0),
            new Tile(2951, 3368, 0), new Tile(2946, 3368, 0));
    private static final TilePath LUMBRIDGE_STAIR_PATH = new TilePath(new Tile(3215, 3216, 0), new Tile(3206, 3209, 0));
    private static final TilePath LUMBRIDGE_BANK_PATH = new TilePath(new Tile(3206, 3217, 2), new Tile(3208, 3220, 2));

    private static boolean atFalador() {
        Player local = Players.local();
        return local != null && local.location().x() >= 2935 && local.location().x() <= 2980;
    }

    private static boolean atLumbridge() {
        Player local = Players.local();
        return local != null && local.location().x() >= 3200 && local.location().x() <= 3230;
    }

    private static void handleBank() {
        Filter<Item> itemFilter = i -> i.name().equals("Zul-andra teleport");
        if (!Bank.viewing() && Inventory.first(itemFilter) == null) {
            Bank.openBooth();
        } else {
            if (Inventory.first(itemFilter) == null) {
                Item teleport = Bank.first(itemFilter);
                if (teleport != null) {
                    teleport.processAction("Withdraw-1");
                    Time.sleep(150, 500);
                } else {
                    System.err.println("Out of zul teleports");
                }
            } else {
                Item teleport = Inventory.first(itemFilter);
                if (teleport != null) {
                    if (Bank.close()) {
                        ClanWars.teleCamp();
                    }
                }
            }
        }
    }

    private static boolean handleCollect() {
        if (Camp.atCamp()) {
            if (Camp.collect()) {
                if (Gear.equipAll()) {
                    return Gear.hasEquip();
                }
            }
        }
        return false;
    }

    private static void handleStairs() {
        GameObject stairs = Objects.nearestByAction("Climb-up");
        if (stairs != null) {
            int plane = Game.plane();
            stairs.processAction("Climb-up", stairs.localX() - 1, stairs.localY() - 1);
            Time.sleep(() -> Game.plane() == plane + 1, Random.nextInt(5000, 7500));
            Time.sleep(500, 1500);
        }
    }

    private static void handleFalador() {
        if (FALADOR_BANK.distance() >= 10) {
            FALADOR_BANK_PATH.step();
        } else {
            handleBank();
        }
    }

    private static void handleLumbridge() {
        if (Game.plane() == 0) {
            if (LUMBRIDGE_STAIRS.distance() >= 10) {
                LUMBRIDGE_STAIR_PATH.step();
            } else {
                handleStairs();
            }
        } else if (Game.plane() == 1) {
            handleStairs();
        } else if (Game.plane() == 2) {
            if (LUMBRIDGE_BANK.distance() >= 5) {
                LUMBRIDGE_BANK_PATH.step();
            } else {
                handleBank();
            }
        }
    }

    public static void handle() {
        if (atFalador()) {
            Zulrah.fullyReset();
            handleFalador();
        } else if (atLumbridge()) {
            Zulrah.fullyReset();
            handleLumbridge();
        } else if (handleCollect()) {
            Zulrah.setDead(false);
            Teleport.act();
        }
    }
}
