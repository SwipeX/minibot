package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.Macro;
import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.method.*;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.GroundItem;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.client.natives.RSItemDefinition;
import com.minibot.client.natives.RSObjectDefinition;
import com.minibot.ui.Manifest;

import java.awt.*;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * TODO rewrite this script to use dynamic sleeping
 * @author Tim Dekker
 * @since 5/11/15
 */
@Manifest(name = "SnareTrapper", author = "Swipe", version = "1.0.0", description = "Traps Birds")
public class SnareTrapper extends Macro implements Renderable {
    private static Tile tile;
    private int ONE_TRAP = 20;
    private int TWO_TRAP = 40;
    private int THREE_TRAP = 60;
    private int FOUR_TRAP = 80;
    private int SKILL_HUNTER = 21;
    private int start_exp = 0;
    private long start_time;

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        if (tile == null) {
            tile = Players.local().location();
            start_exp = Game.experiences()[SKILL_HUNTER];
            start_time = System.currentTimeMillis();
        }
        if (Players.local().animation() != -1)
            return;
        Tile next = getNext();
        if (next == null) {
            Inventory.dropAll(item -> (item.name().equals("Raw bird meat") || item.name().equals("Bones")));
            Time.sleep(50);
            return;
        } else {
            GameObject obj = Objects.topAt(next);
            if (next != null && triggered(obj)) {
                System.out.println("Triggered");
                if (Arrays.asList(obj.definition().getActions()).contains("Check"))
                    obj.processAction("Check");
                else
                    obj.processAction("Dismantle");
                Time.sleep(1500, 2000);
            } else if (next != null && obj == null) {
                final Tile finalNext = next;
                Deque<GroundItem> items = Ground.findByFilter(groundItem -> {
                    RSItemDefinition def = groundItem.definition();
                    return groundItem.location().equals(finalNext) && def != null && def.getName().equals("Bird snare");
                });
                if (items == null || items.size() == 0) {
                    if (!Players.local().location().equals(next)) {
                        Walking.walkTo(next);
                        Time.sleep(500, 800);
                    }
                    Item snare = Inventory.first(item -> item.name().equals("Bird snare"));
                    if (snare != null) {
                        if (Players.local().location().equals(next))
                            snare.processAction(ActionOpcodes.ITEM_ACTION_0, "Lay");
                        Time.sleep(1500, 2000);
                    }
                } else if (items != null && items.size() > 0) {
                    GroundItem item = items.getFirst();
                    item.processAction(ActionOpcodes.GROUND_ITEM_ACTION_3, "Lay");
                    Time.sleep(1500, 2000);
                }
            }
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
        return new Tile[]{};
    }

    public Tile getNext() {
        //No trap
        for (Tile tile : traps()) {
            GameObject obj = Objects.topAt(tile);
            if (obj == null) {
                return tile;
            }
        }
        //Triggered
        for (Tile tile : traps()) {
            GameObject obj = Objects.topAt(tile);
            if (obj != null && triggered(obj)) {
                return tile;
            }
        }
        return null;
    }

    public int hourly(int val, long difference) {
        return (int) Math.ceil(val * 3600000D / difference);
    }

    public static String format(long millis) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.GREEN);
        g.drawRect(0, 0, 150, 50);
        long time_diff = System.currentTimeMillis() - start_time;
        int gain = Game.experiences()[21] - start_exp;
        g.drawString("Time: " + format(time_diff), 10, 10);
        g.drawString("Exp: " + gain, 10, 25);
        g.drawString("Exp/H: " + hourly(gain, time_diff), 10, 40);
    }
}
