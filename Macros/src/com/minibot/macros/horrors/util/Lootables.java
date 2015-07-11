package com.minibot.macros.horrors.util;

import com.minibot.api.method.Ground;
import com.minibot.api.method.Inventory;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.Area;
import com.minibot.api.wrapper.locatable.GroundItem;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tyler Sedlar
 * @since 7/10/2015
 */
public class Lootables {

    private static final Map<String, Integer> PRICES = new HashMap<>();

    private static int radius = 20;
    private static Area area = null;

    public static void setRadius(int radius) {
        Lootables.radius = radius;
    }

    public static void setArea(Area area) {
        Lootables.area = area;
    }

    public static void clear() {
        PRICES.clear();
    }

    public static void initRareDropTable() {
        PRICES.put("Loop half of key", 7100);
        PRICES.put("Tooth half of key", 22100);
        PRICES.put("Nature rune", 250);
        PRICES.put("Runite bar", 13500);
        PRICES.put("Rune spear", 12900);
        PRICES.put("Rune battleaxe", 25200);
        PRICES.put("Rune 2h sword", 40200);
        PRICES.put("Rune sq shield", 24100);
        PRICES.put("Rune arrow", 145);
        PRICES.put("Law rune", 210);
        PRICES.put("Death rune", 220);
        PRICES.put("Dragonstone", 20500);
        PRICES.put("Rune kiteshield", 35100);
        PRICES.put("Dragon med helm", 61900);
        PRICES.put("Shield left half", 100000);
        PRICES.put("Dragon spear", 76000);
    }

    public static void initCaveHorrors() {
        PRICES.put("Teak logs", 39);
        PRICES.put("Grimy ranarr weed", 5950);
        PRICES.put("Grimy avantoe", 1900);
        PRICES.put("Grimy kwuarm", 2230);
        PRICES.put("Grimy cadantine", 1830);
        PRICES.put("Grimy dwarf weed", 1670);
        PRICES.put("Avantoe seed", 560);
        PRICES.put("Kwuarm seed", 1200);
        PRICES.put("Snapdragon seed", 22500);
        PRICES.put("Dwarf weed seed", 920);
        PRICES.put("Torstol seed", 36100);
        PRICES.put("Rune dagger", 4550);
        PRICES.put("Adamant full helm", 1910);
        PRICES.put("Black mask (10)", 630000);
    }

    private static int stackCount() {
        int count = 0;
        for (Item item : Inventory.items())
            count += item.amount();
        return count;
    }

    public static boolean valid() {
        return Ground.nearestByFilter(radius, i -> {
            String name = i.name();
            return name != null && PRICES.containsKey(name) && (area == null || area.contains(i));
        }) != null;
    }

    public static int loot() {
        GroundItem item = Ground.nearestByFilter(radius, i -> {
            String name = i.name();
            return name != null && PRICES.containsKey(name) && (area == null || area.contains(i));
        });
        if (item != null) {
            int count = stackCount();
            String name = item.name();
            item.processAction("Take");
            if (Time.sleep(() -> stackCount() != count, 10000))
                return (PRICES.get(name) * (stackCount() - count));
        }
        return -1;
    }
}
