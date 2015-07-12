package com.minibot.macros.clue.structure;

import com.minibot.api.method.Inventory;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.macros.clue.TeleportLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Tyler Sedlar
 * @since 7/11/2015
 */
public abstract class ClueScroll {

    public static final List<ClueScroll> CLUE_SCROLLS = new ArrayList<>();

    public final int id;

    public ClueScroll(int id) {
        this.id = id;
    }

    public static Item findInventoryItem() {
        return Inventory.first(i -> {
            String name = i.name();
            return name != null && name.contains("Clue scroll");
        });
    }

    public abstract void reset();
    public abstract void solve(AtomicReference<String> status);

    public static ClueScroll find(int id) {
        for (ClueScroll scroll : CLUE_SCROLLS) {
            if (scroll.id == id)
                return scroll;
        }
        return null;
    }

    public static void populateMedium() {
        CLUE_SCROLLS.add(new ClueScrollKeyObject(3607, TeleportLocation.FALADOR, new Tile(2909, 3539, 0), "Penda",
                "Drawers", new Tile(2921, 3576, 0), new Tile(2921, 3577, 0)));
        CLUE_SCROLLS.add(new ClueScrollObject(7304, TeleportLocation.CAMELOT, "Crate",
                new Tile(2659, 3436, 0), new Tile(2671, 3437, 0)));
    }
}
