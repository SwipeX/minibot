package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.bot.macro.Macro;
import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.method.*;
import com.minibot.api.util.Condition;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.GroundItem;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.client.natives.RSItemDefinition;
import com.minibot.client.natives.RSObjectDefinition;
import com.minibot.bot.macro.Manifest;

import java.awt.*;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Calculations for red chins only.
 *
 * @author Tim Dekker
 * @since 5/11/15
 */
@Manifest(name = "ChinHunter", author = "Swipe", version = "1.0.0", description = "Hunts Red Chinchompas")
public class ChinHunter extends Macro implements Renderable {
    private static Tile tile;
    private int SKILL_HUNTER = 21;
    private int start_exp = 0;
    private long start_time;
    private static final int EXP_EACH = 265;
    private static final int PRICE_CACHED = 1290;
    private static final int Y_POS = 12;

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        if (tile == null) {
            tile = Players.local().location();
            start_exp = Game.experiences()[SKILL_HUNTER];
            start_time = System.currentTimeMillis();
        }
        final Tile next = getNext();
        if (next == null) {
            return;
        } else {
            Deque<GroundItem> items = Ground.findByFilter(groundItem -> {
                RSItemDefinition def = groundItem.definition();
                return groundItem.location().equals(next) && def != null && def.getName().equals("Box trap");
            });
            GameObject obj = Objects.topAt(next);
            if (triggered(obj)) {
                if (Arrays.asList(obj.definition().getActions()).contains("Check"))
                    obj.processAction("Check");
                else
                    obj.processAction("Dismantle");
                Time.sleep(new Condition() {
                    @Override
                    public boolean validate() {
                        return Objects.topAt(next) == null && Players.local().animation() == -1;
                    }
                }, 1500L);
            } else if (obj == null && (items == null || items.size() == 0)) {
                if (!Players.local().location().equals(next)) {
                    Walking.walkTo(next);
                    Time.sleep(new Condition() {
                        @Override
                        public boolean validate() {
                            return Players.local().location().equals(next);
                        }
                    }, 1000L);
                }
                Item snare = Inventory.first(item -> item.name().equals("Box trap"));
                if (snare != null) {
                    if (Players.local().location().equals(next)) {
                        snare.processAction(ActionOpcodes.ITEM_ACTION_0, "Lay");
                        Time.sleep(300, 400);
                        Time.sleep(new Condition() {
                            @Override
                            public boolean validate() {
                                return Players.local().animation() == -1;
                            }
                        }, 1500L);
                    }
                }
            } else if (items != null && items.size() > 0) {
                GroundItem item = items.getFirst();
                if (item != null) {
                    item.processAction(ActionOpcodes.GROUND_ITEM_ACTION_3, "Lay");
                    Time.sleep(new Condition() {
                        @Override
                        public boolean validate() {
                            return Objects.topAt(next) != null && Players.local().animation() == -1;
                        }
                    }, 1500L);
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
                return new Tile[]{tile.derive(-1, 1), tile.derive(-1, -1), tile, tile.derive(1, -1), tile.derive(1, 1),};
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
        g.setColor(Color.YELLOW);
        g.drawRect(0, 0, 150, 90);
        long time_diff = System.currentTimeMillis() - start_time;
        int gain = Game.experiences()[21] - start_exp;
        int amount = gain / EXP_EACH;
        int profit = amount * PRICE_CACHED;
        g.drawString("Time: " + format(time_diff), 10, Y_POS);
        g.drawString("Exp: " + gain, 10, Y_POS + 15);
        g.drawString("Exp/H: " + hourly(gain, time_diff), 10, Y_POS + 30);
        g.drawString(String.format("Caught: %s (%s/H)", amount, hourly(amount, time_diff)), 10, Y_POS + 45);
        g.drawString("Profit: " + profit, 10, Y_POS + 60);
        g.drawString("Profit/H: " + hourly(profit, time_diff), 10, Y_POS + 75);
    }
}
