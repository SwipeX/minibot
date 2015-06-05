package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.method.*;
import com.minibot.api.util.Condition;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.GroundItem;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;
import com.minibot.client.natives.RSItemDefinition;
import com.minibot.client.natives.RSObjectDefinition;

import java.awt.*;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Calculations for red chins only.
 *
 * @author Tim Dekker
 * @author Tyler Sedlar
 * @since 5/11/15
 */
@Manifest(name = "Chinchompa Hunter", author = "Tim/Tyler", version = "1.0.0", description = "Hunts Chinchompas")
public class ChinchompaHunter extends Macro implements Renderable {

    private static final int EXP_EACH = 265;
    private static final int PRICE_CACHED = 1290;
    private static final int Y_POS = 12;
    private static final long FIFTEEN_MINUTES = 15 * 60 * 1000;

    private static Tile tile;

    private static final int SKILL_HUNTER = 21;
    private int startExp;
    private long startTime;
    private long lastReport = System.currentTimeMillis();
    private int lastChins = 0;

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        if (tile == null) {
            tile = Players.local().location();
            startExp = Game.experiences()[SKILL_HUNTER];
            startTime = System.currentTimeMillis();
        }
        Tile next = getNext();
        if (next != null) {
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
                    public boolean validate() {
                        return Objects.topAt(next) != null && Players.local().animation() != -1;
                    }
                }, 1500L);
                Time.sleep(400, 500);
                Time.sleep(new Condition() {
                    public boolean validate() {
                        return Objects.topAt(next) != null && Players.local().animation() == -1;
                    }
                }, 1500L);
                Time.sleep(800, 1200);
            } else if (obj == null && (items == null || items.isEmpty())) {
                if (!Players.local().location().equals(next)) {
                    Walking.walkTo(next);
                    Time.sleep(new Condition() {
                        public boolean validate() {
                            return Players.local().location().equals(next);
                        }
                    }, 2000L);
                }
                Item trap = Inventory.first(item -> item.name().equals("Box trap"));
                if (trap != null) {
                    if (Players.local().location().equals(next)) {
                        trap.processAction(ActionOpcodes.ITEM_ACTION_0, "Lay");
                        Time.sleep(new Condition() {
                            public boolean validate() {
                                return Objects.topAt(next) != null && Players.local().animation() != -1;
                            }
                        }, 1500L);
                        Time.sleep(400, 500);
                        Time.sleep(new Condition() {
                            public boolean validate() {
                                return Objects.topAt(next) != null && Players.local().animation() == -1;
                            }
                        }, 1500L);
                    }
                }
            } else if (items != null && !items.isEmpty()) {
                GroundItem item = items.getFirst();
                if (item != null) {
                    item.processAction(ActionOpcodes.GROUND_ITEM_ACTION_3, "Lay");
                    Time.sleep(new Condition() {
                        public boolean validate() {
                            return Objects.topAt(next) != null && Players.local().animation() != -1;
                        }
                    }, 1500L);
                    Time.sleep(400, 500);
                    Time.sleep(new Condition() {
                        public boolean validate() {
                            return Objects.topAt(next) != null && Players.local().animation() == -1;
                        }
                    }, 1500L);
                }
            }
        }
        //reporting stats
        if (lastReport == -1 || System.currentTimeMillis() - lastReport > FIFTEEN_MINUTES) {
            int gain = Game.experiences()[SKILL_HUNTER] - startExp;
            int amount = gain / EXP_EACH;
            Minibot.connection().chin(Players.local().name(), amount - lastChins, (int) ((System.currentTimeMillis() - lastReport) / 1000));
            lastReport = System.currentTimeMillis();
            lastChins = amount;
        }
    }

    private boolean triggered(GameObject obj) {
        if (obj == null)
            return false;
        RSObjectDefinition def = obj.definition();
        if (def == null)
            return false;
        String[] actions = def.getActions();
        if (actions == null)
            return false;
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
        for (Tile tile : traps()) {
            GameObject obj = Objects.topAt(tile);
            if (obj == null)
                return tile;
        }
        for (Tile tile : traps()) {
            GameObject obj = Objects.topAt(tile);
            if (obj != null && triggered(obj))
                return tile;
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
        long timeDiff = System.currentTimeMillis() - startTime;
        int gain = Game.experiences()[SKILL_HUNTER] - startExp;
        int amount = gain / EXP_EACH;
        int profit = amount * PRICE_CACHED;
        g.setColor(Color.YELLOW);
        g.drawString("Time: " + format(timeDiff), 10, Y_POS);
        g.drawString("Exp: " + gain, 10, Y_POS + 15);
        g.drawString("Exp/H: " + hourly(gain, timeDiff), 10, Y_POS + 30);
        g.drawString(String.format("Caught: %s (%s/H)", amount, hourly(amount, timeDiff)), 10, Y_POS + 45);
        g.drawString("Profit: " + profit, 10, Y_POS + 60);
        g.drawString("Profit/H: " + hourly(profit, timeDiff), 10, Y_POS + 75);
    }
}