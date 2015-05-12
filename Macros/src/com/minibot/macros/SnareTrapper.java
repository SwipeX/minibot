package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.Macro;
import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.method.*;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.GroundItem;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.client.natives.RSObjectDefinition;

import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/**
 * @author Tim Dekker
 * @since 5/11/15
 */
public class SnareTrapper extends Macro {
    private Tile tile;
    private int ONE_TRAP = 20;
    private int TWO_TRAP = 40;
    private int THREE_TRAP = 60;
    private int FOUR_TRAP = 80;
    private int SKILL_HUNTER = 15;

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        if (tile == null)
            tile = Players.local().location();
        Tile next = null;
        for (Tile tile : traps()) {
            GameObject obj = Objects.topAt(tile);
            if (obj == null || triggered(obj)) {
                next = tile;
                break;
            }
        }
        if (next == null) {
            System.out.println("No trap tile needs attention");
            Inventory.dropAll(item -> (item.name().equals("Raw bird meat") || item.name().equals("Bones")));
            return;
        }
        GameObject obj = Objects.topAt(next);
        if (next != null && triggered(obj)) {
            System.out.println("Triggered");
            if (Arrays.asList(obj.definition().getActions()).contains("Check"))
                obj.processAction("Check");
            else
                obj.processAction("Dismantle");
            Time.sleep(2000);
        }
        Deque<GroundItem> items = Ground.at(next.localX(), next.localY());
        if (items == null || items.size() == 0) {
            if (Objects.topAt(next) == null && next != null) {
                if (!Players.local().location().equals(next)) {
                    Walking.walkTo(next);
                    Time.sleep(500, 800);
                }
                Item snare = Inventory.first(item -> item.name().equals("Bird snare"));
                if (snare != null) {
                    snare.processAction(ActionOpcodes.ITEM_ACTION_0, "Lay");
                    Time.sleep(3500, 5000);
                }
            }
        } else {
            GroundItem item = items.getFirst();
            item.processAction(ActionOpcodes.GROUND_ITEM_ACTION_3, "Lay");
            Time.sleep(3500, 5000);
        }
    }

    boolean triggered(GameObject obj) {
        if (obj == null) return false;
        RSObjectDefinition def = obj.definition();
        if (def == null) return false;
        String[] actions = def.getActions();
        if (actions == null) return false;
        List<String> act = Arrays.asList(actions);
        return act.contains("Check") || !act.contains("Investigate");
    }

    /**
     * @return the maximum number of traps that can be used at current level.
     */
    private int trapSize() {
        int level = Game.realLevels()[SKILL_HUNTER];
        if (level < ONE_TRAP)
            return 1;
        else if (level < TWO_TRAP)
            return 2;
        else if (level < THREE_TRAP)
            return 3;
        else if (level < FOUR_TRAP)
            return 4;
        else return 5;
    }

    private Tile[] traps() {
        switch (trapSize()) {
            case 1:
                return new Tile[]{tile};
            case 2:
                return new Tile[]{tile.derive(-1, 0), tile.derive(1, 0)};
            case 3:
                return new Tile[]{tile.derive(-1, 0), tile.derive(0, -1), tile.derive(1, 0)};
            case 4:
                return new Tile[]{tile.derive(-1, 0), tile.derive(0, -1), tile.derive(1, 0), tile.derive(0, 1)};
            case 5:
                return new Tile[]{tile.derive(-1, 0), tile.derive(0, -1), tile.derive(1, 0), tile.derive(0, 1), tile};
        }
        return null;
    }

}
