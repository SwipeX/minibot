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
            if (Inventory.count() == 28) {
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
                String[] force = {"Super restore(1)", "Super restore(2)", "Super restore(3)", "Prayer potion(1)",
                        "Prayer potion(2)", "Prayer potion(3)", "Anti-venom+(1)", "Vial"};
                for (String name : force) {
                    Item i = Inventory.first(item -> item.name().equals(name));
                    if (i != null) {
                        i.processAction("Deposit-All");
                        Time.sleep(150, 400);
                    }
                }
                if (!Equipment.equipped("Ring of recoil")) {
                    Bank.withdraw("Ring of recoil", 1);
                    Time.sleep(150, 400);
                    Bank.close();
                    return;
                }
                if (Inventory.first(i -> i.name().equals("Ring of recoil")) == null) {
                    Bank.withdraw("Ring of recoil", 1);
                }
                if (!Equipment.equipped(slot -> {
                    return slot.getName().toLowerCase().contains("ava's");
                })) {
                    Item ava = Bank.first(i -> i.name().toLowerCase().contains("ava's"));
                    Bank.withdraw(ava, 1);
                }
                String[] withdraw = {"super restore(4)", "prayer potion(4)", "venom", "ranging", "dueling", "zul-andra"};
                for (String str : withdraw) {
                    if (Gear.potion() == Potions.Potion.PRAYER && str.equals("super restore(4)")) {
                        continue;
                    } else if (Gear.potion() == Potions.Potion.RESTORE && str.equals("prayer potion(4)")) {
                        continue;
                    }
                    if (Inventory.first(i -> i.name().toLowerCase().contains(str)) == null) {
                        Bank.withdraw(Bank.first(i -> {
                            String itemName = i.name();
                            return !itemName.contains("1") && !(str.equals("venom") && itemName.length() != 14) &&
                                    itemName.toLowerCase().contains(str);
                        }), 1);
                        Time.sleep(150, 400);
                    }
                }
                if (Time.sleep(() -> {
                    for (String str : withdraw) {
                        if (Gear.potion() == Potions.Potion.PRAYER && str.equals("super restore(4)")) {
                            continue;
                        } else if (Gear.potion() == Potions.Potion.RESTORE && str.equals("prayer potion(4)")) {
                            continue;
                        }
                        if (Inventory.first(i -> i.name().toLowerCase().contains(str)) == null) {
                            return false;
                        }
                    }
                    return true;
                }, 2000)) {
                    Bank.withdraw("Shark", Bank.WITHDRAW_ALL);
                    Time.sleep(() -> Inventory.count() == 28, 2000);
                }
            }
        } else {
            Item ring = Inventory.first(i -> i.name().contains("recoil"));
            if (ring != null && !Equipment.equipped("Ring of recoil")) {
                Equipment.equip("Ring of recoil");
                Time.sleep(1000);
            }
            Item accumulator = Inventory.first(i -> i.name().toLowerCase().contains("ava's"));
            if (accumulator != null && !Equipment.equipped(accumulator.name())) {
                Equipment.equip(accumulator);
            }
            Tile cw = new Tile(3388, 3161, 0);
            GameObject chest = Objects.nearestByName("Bank chest");
            if (chest == null && cw.distance() <= 15) {
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

    public static void teleCamp() {
        Item teleport = Inventory.first(i -> i.name().equals("Zul-andra teleport"));
        if (teleport != null) {
            teleport.processAction("Teleport");
            Time.sleep(Camp::atCamp, Random.nextInt(5000, 7500));
        }
    }
}