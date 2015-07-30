package com.minibot.macros.zulrah.action;

import com.minibot.api.method.Bank;
import com.minibot.api.method.Equipment;
import com.minibot.api.method.GameTab;
import com.minibot.api.method.Inventory;
import com.minibot.api.util.Time;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.Item;

/**
 * @author Tyler Sedlar
 * @since 7/29/2015
 */
public class Trident {

    private static final Filter<Item> FIRE_RUNE = i -> i.name().equals("Fire rune");
    private static final Filter<Item> CHAOS_RUNE = i -> i.name().equals("Chaos rune");
    private static final Filter<Item> DEATH_RUNE = i -> i.name().equals("Death rune");
    private static final Filter<Item> SCALES = i -> i.name().equals("Zulrah's scales");

    private static boolean toggled = false;
    private static boolean charged = false;

    private static boolean hasSupplies() {
        return Inventory.containsAll(FIRE_RUNE, CHAOS_RUNE, DEATH_RUNE, SCALES);
    }

    private static boolean hasAnySupplies() {
        return Inventory.first(FIRE_RUNE) != null || Inventory.first(CHAOS_RUNE) != null ||
                Inventory.first(DEATH_RUNE) != null || Inventory.first(SCALES) != null;
    }

    public static void setToggled(boolean toggled) {
        Trident.toggled = toggled;
    }

    public static void act() {
        if (!toggled) {
            return;
        }
        if (Bank.viewing()) {
            if (charged) {
                if (hasAnySupplies()) {
                    Item fire = Inventory.first(FIRE_RUNE);
                    Item chaos = Inventory.first(CHAOS_RUNE);
                    Item death = Inventory.first(DEATH_RUNE);
                    Item scales = Inventory.first(SCALES);
                    if (fire != null) {
                        fire.processAction("Deposit-All");
                    }
                    if (chaos != null) {
                        chaos.processAction("Deposit-All");
                    }
                    if (death != null) {
                        death.processAction("Deposit-All");
                    }
                    if (scales != null) {
                        scales.processAction("Deposit-All");
                    }
                    Time.sleep(() -> !hasAnySupplies(), 2000);
                } else {
                    charged = false;
                    toggled = false;
                }
            } else {
                if (hasSupplies()) {
                    Bank.close();
                } else {
                    if (Inventory.first(FIRE_RUNE) == null) {
                        Bank.withdraw("Fire rune", Bank.WITHDRAW_ALL_BUT_ONE);
                        Time.sleep(200, 300);
                    }
                    if (Inventory.first(CHAOS_RUNE) == null) {
                        Bank.withdraw("Chaos rune", Bank.WITHDRAW_ALL_BUT_ONE);
                        Time.sleep(200, 300);
                    }
                    if (Inventory.first(DEATH_RUNE) == null) {
                        Bank.withdraw("Death rune", Bank.WITHDRAW_ALL_BUT_ONE);
                        Time.sleep(200, 300);
                    }
                    if (Inventory.first(SCALES) == null) {
                        Bank.withdraw("Zulrah's scales", Bank.WITHDRAW_ALL_BUT_ONE);
                        Time.sleep(200, 300);
                    }
                    Time.sleep(Trident::hasSupplies, 2000);
                }
            }
        } else {
            if (hasSupplies()) {
                if (!GameTab.EQUIPMENT.viewing()) {
                    GameTab.EQUIPMENT.open();
                    Time.sleep(100, 200);
                }
                if (!Equipment.Slot.WEAPON.empty()) {
                    Equipment.Slot.WEAPON.widget().processAction("Remove");
                    Time.sleep(Equipment.Slot.WEAPON::empty, 2000);
                    return;
                }
                if (!Equipment.Slot.SHIELD.empty()) {
                    Equipment.Slot.SHIELD.widget().processAction("Remove");
                    Time.sleep(Equipment.Slot.SHIELD::empty, 2000);
                    return;
                }
                Item trident = Inventory.first(i -> i.name().toLowerCase().contains("trident"));
                Item scales = Inventory.first(SCALES);
                if (trident != null && scales != null) {
                    int scaleCount = scales.amount();
                    scales.use(trident);
                    Time.sleep(() -> {
                        Item item = Inventory.first(SCALES);
                        return item == null || item.amount() != scaleCount;
                    }, 3500);
                }
            } else {
                // probably needs to handle traversal, unless this is only being called while at ClanWars
                ClanWars.openChest();
            }
        }
    }
}
