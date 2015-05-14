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

import java.awt.*;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Tim Dekker
 * @since 5/11/15
 */
public class Chins extends Macro implements Renderable {
    private static Tile tile;
    private int ONE_TRAP = 20;
    private int TWO_TRAP = 40;
    private int THREE_TRAP = 60;
    private int FOUR_TRAP = 80;
    private int SKILL_HUNTER = 21;
    private int start_exp = 0;
    private long start_time;
    private static final int EXP_EACH = 265;
    private static final int PRICE_CACHED = 1290;

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
            return;
        } else {
            final Tile finalNext = next;
            Deque<GroundItem> items = Ground.findByFilter(groundItem -> {
                RSItemDefinition def = groundItem.definition();
                return groundItem.location().equals(finalNext) && def != null && def.getName().equals("Box trap");
            });
            GameObject obj = Objects.topAt(next);
            if (triggered(obj)) {
                if (Arrays.asList(obj.definition().getActions()).contains("Check"))
                    obj.processAction("Check");
                else
                    obj.processAction("Dismantle");
                Time.sleep(100, 400);
            } else if (obj == null && (items == null || items.size() == 0)) {
                if (!Players.local().location().equals(next)) {
                    Walking.walkTo(next);
                    Time.sleep(500, 800);
                }
                Item snare = Inventory.first(item -> item.name().equals("Box trap"));
                if (snare != null) {
                    if (Players.local().location().equals(next))
                        snare.processAction(ActionOpcodes.ITEM_ACTION_0, "Lay");
                    Time.sleep(500, 800);
                }
            } else if (items != null && items.size() > 0) {
                GroundItem item = items.getFirst();
                if (item != null) {
                    item.processAction(ActionOpcodes.GROUND_ITEM_ACTION_3, "Lay");
                    Time.sleep(200, 400);
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
        return Game.realLevels()[SKILL_HUNTER] / 20 + 1;
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
        //Triggered
        for (Tile tile : traps()) {
            GameObject obj = Objects.topAt(tile);
            if (obj != null && triggered(obj)) {
                return tile;
            }
        }
        //No trap
        for (Tile tile : traps()) {
            GameObject obj = Objects.topAt(tile);
            if (obj == null) {
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
        g.setColor(Color.YELLOW);
        g.drawRect(0, 0, 150, 85);
        long time_diff = System.currentTimeMillis() - start_time;
        int gain = Game.experiences()[21] - start_exp;
        int amount = gain / EXP_EACH;
        int profit = amount * PRICE_CACHED;
        g.drawString("Time: " + format(time_diff), 10, 10);
        g.drawString("Exp: " + gain, 10, 25);
        g.drawString("Exp/H: " + hourly(gain, time_diff), 10, 40);
        g.drawString("Profit: " + profit, 10, 55);
        g.drawString("Profit/H: " + hourly(profit, time_diff), 10, 70);
    }
}
