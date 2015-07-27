package com.minibot.macros.zulrah.action;

import com.minibot.api.method.*;
import com.minibot.api.util.Random;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.Player;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.macros.zulrah.Zulrah;

/**
 * @author Tim Dekker, Jacob Doiron
 * @since 7/24/15
 */
public class ClanWars {

    // needs to support Gear.hasEquip and hasInventory
    // needs support for recharging tridents

    public static void handle() {
        if (Bank.viewing()) {
            if (/*Gear.hasEquip() && Gear.hasInventory()*/ Inventory.count() == 28) {
                handlePortal();
            } else {
                if (!Zulrah.lootIds.isEmpty()) {
                    for (int id : Zulrah.lootIds) {
                        Item i = Inventory.first(item -> item.id() == id);
                        if (i != null) {
                            i.processAction("Deposit-All");
                            Time.sleep(150, 400);
                        }
                    }
                }
                // withdraw needed items
            }
        } else {
            Tile cw = new Tile(3388, 3161, 0);
            GameObject chest = Objects.nearestByName("Bank chest");
            if(chest == null && cw.distance() <= 15) {
                Walking.walkTo(new Tile(3377 + Random.nextInt(-2, 2), 3168 + Random.nextInt(-2, 2), 0));
                Time.sleep(4500, 6000);
                chest = Objects.nearestByName("Bank chest");
            }
            if (chest != null && chest.distance() <= 50 && Inventory.count() < 28) {
                openChest();
            } else {
                handlePortal();
            }
        }
    }

    private static void openChest() {
        GameObject chest = Objects.nearestByName("Bank chest");
        if (chest != null) {
            chest.processAction("Use");
            Time.sleep(Bank::viewing, Random.nextInt(7500, 10000));
        }
    }

    private static void handlePortal() {
        Player local = Players.local();
        if (local != null) {
            Tile inside = new Tile(3327, 4751, 0);
            if (inside.distance() <= 5) {
                teleCamp();
            } else {
                Tile tile = new Tile(3360, 3162, 0);
                if (tile.distance() <= 30) {
                    Walking.walkTo(new Tile(tile.x() + Random.nextInt(-2, 2), tile.y() + Random.nextInt(-2, 2), 0));
                    Time.sleep(3000, 4500);
                    GameObject portal = Objects.nearestByName("Free-for-all portal");
                    if (portal != null) {
                        portal.processAction("Enter", portal.localX() - 1, portal.localY() - 2);
                        if (Time.sleep(() -> local.location().x() == 3327, Random.nextInt(5000, 7500))) {
                            Time.sleep(1200, 2000);
                            teleCamp();
                        }
                    }
                }
            }
        }
    }

    private static void teleCamp() {
        Player local = Players.local();
        if (local != null) {
            Item teleport = Inventory.first(i -> i.name().equals("Zul-andra teleport"));
            if (teleport != null) {
                int priorX = local.location().x();
                teleport.processAction("Teleport");
                Time.sleep(Camp::atCamp, Random.nextInt(5000, 7500));
            }
        }
    }
}