package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.method.Game;
import com.minibot.api.method.Ground;
import com.minibot.api.method.Inventory;
import com.minibot.api.method.Objects;
import com.minibot.api.method.Players;
import com.minibot.api.method.Skills;
import com.minibot.api.method.Walking;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.GroundItem;
import com.minibot.api.wrapper.locatable.Player;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;
import com.minibot.client.natives.RSItemDefinition;
import com.minibot.client.natives.RSObjectDefinition;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/**
 * Calculations for red chins only.
 *
 * @author Tim Dekker
 * @author Tyler Sedlar
 * @since 5/11/15
 */
@Manifest(name = "Chinchompa Hunter", author = "Tim/Tyler", version = "1.0.0", description = "Hunts Chinchompas")
public class ChinchompaHunter extends Macro implements Renderable {

    private enum ChinType {

        REGULAR(198.4D, 700), RED(265D, 1330), BLACK(315D, 1980);

        private final double exp;
        private final int price;

        ChinType(double exp, int price) {
            this.exp = exp;
            this.price = price;
        }

        public double exp() {
            return exp;
        }

        public int price() {
            return price;
        }
    }

    private static final ChinType CHIN_TYPE = ChinType.RED;

    private static final double EXP_EACH = CHIN_TYPE.exp;
    private static final int PRICE_CACHED = CHIN_TYPE.price;
    private static final int Y_POS = 12;
    private static final long FIFTEEN_MINUTES = 15 * 60 * 1000;

    public static final Filter<GameObject> TRAP_FILTER = o -> {
        String name = o.name();
        return name != null && (name.equals("Box trap") || name.equals("Shaking box"));
    };

    private static Tile tile;

    private int startExp;
    private long lastReport = System.currentTimeMillis();
    private int lastChins;

    @Override
    public void atStart() {
        Player local = Players.local();
        if (local != null) {
            tile = Players.local().location();
            startExp = Game.experiences()[Skills.HUNTER];
        }
    }

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        Tile next = getNext();
        if (next != null) {
            Deque<GroundItem> items = Ground.findByFilter(groundItem -> {
                RSItemDefinition def = groundItem.definition();
                return groundItem.location().equals(next) && def != null && def.getName().equals("Box trap");
            });
            GameObject obj = objectAt(next, TRAP_FILTER);
            if (triggered(obj)) {
                if (Arrays.asList(obj.definition().getActions()).contains("Check"))
                    obj.processAction("Check");
                else
                    obj.processAction("Dismantle");
                Time.sleep(() -> objectAt(next, TRAP_FILTER) != null && Players.local().animation() != -1, 1500L);
                Time.sleep(400, 500);
                Time.sleep(() -> objectAt(next, TRAP_FILTER) != null && Players.local().animation() == -1, 1500L);
                Time.sleep(800, 1200);
            } else if (obj == null && (items == null || items.isEmpty())) {
                if (!Players.local().location().equals(next)) {
                    Walking.walkTo(next);
                    Time.sleep(() -> {
                        return Players.local().location().equals(next);
                    }, 2000L);
                }
                Item trap = Inventory.first(item -> item.name().equals("Box trap"));
                if (trap != null) {
                    if (Players.local().location().equals(next)) {
                        trap.processAction(ActionOpcodes.ITEM_ACTION_0, "Lay");
                        Time.sleep(() -> Objects.topAt(next) != null && Players.local().animation() != -1, 1500L);
                        Time.sleep(400, 500);
                        Time.sleep(() -> Objects.topAt(next) != null && Players.local().animation() == -1, 1500L);
                    }
                }
            } else if (items != null && !items.isEmpty()) {
                GroundItem item = items.getFirst();
                if (item != null) {
                    item.processAction(ActionOpcodes.GROUND_ITEM_ACTION_3, "Lay");
                    Time.sleep(() -> Objects.topAt(next) != null && Players.local().animation() != -1, 1500L);
                    Time.sleep(400, 500);
                    Time.sleep(() -> Objects.topAt(next) != null && Players.local().animation() == -1, 1500L);
                }
            }
        }
        //reporting stats
        if (lastReport == -1 || System.currentTimeMillis() - lastReport > FIFTEEN_MINUTES) {
            int gain = Game.experiences()[Skills.HUNTER] - startExp;
            int amount = (int) ((double) gain / EXP_EACH);
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

    private boolean flowering(Tile t) {
        GameObject[] objects = Objects.allAt(t);
        if (objects != null) {
            for (GameObject object : objects) {
                if (object != null) {
                    RSObjectDefinition def = object.definition();
                    if (def != null) {
                        String name = def.getName();
                        if (name != null && name.equals("Flowers"))
                            return true;
                    }
                }
            }
        }
        return false;
    }

    private int trapCount() {
        int count = Inventory.items(item -> item.name().equals("Box trap")).size();
        Tile[] tiles = new Tile[]{tile.derive(-1, 1), tile.derive(-1, -1), tile, tile.derive(1, -1), tile.derive(1, 1)};
        for (Tile t : tiles) {
            GameObject obj = Objects.topAt(t);
            if (obj != null) {
                RSObjectDefinition def = obj.definition();
                if (def != null) {
                    String name = def.getName();
                    if (name != null && name.contains("trap"))
                        count++;
                }
            }
        }
        return count;
    }

    /**
     * @return the maximum number of traps that can be used at current level.
     */
    private int trapSize() {
        return Math.min(trapCount(), Game.realLevels()[Skills.HUNTER] / 20 + 1);
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
                return new Tile[]{tile.derive(-1, 1), tile.derive(-1, -1), tile, tile.derive(1, -1), tile.derive(1, 1)};
        }
        return new Tile[]{};
    }

    private GameObject objectAt(Tile t, Filter<GameObject> filter) {
        GameObject[] objects = Objects.allAt(t);
        if (objects == null)
            return null;
        for (GameObject obj : objects) {
            if (obj != null && filter.accept(obj))
                return obj;
        }
        return null;
    }

    public Tile getNext() {
        for (Tile tile : traps()) {
            if (flowering(tile))
                continue;
            GameObject obj = objectAt(tile, TRAP_FILTER);
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

    @Override
    public void render(Graphics2D g) {
        int gained = Game.experiences()[Skills.HUNTER] - startExp;
        int amount = (int) ((double) gained / EXP_EACH);
        int profit = amount * PRICE_CACHED;
        g.setColor(Color.YELLOW);
        g.drawString("Time: " + Time.format(runtime()), 10, Y_POS);
        g.drawString("Exp: " + gained, 10, Y_POS + 15);
        g.drawString("Exp/H: " + hourly(gained), 10, Y_POS + 30);
        g.drawString(String.format("Caught: %s (%s/H)", amount, hourly(amount)), 10, Y_POS + 45);
        g.drawString("Profit: " + profit, 10, Y_POS + 60);
        g.drawString("Profit/H: " + hourly(profit), 10, Y_POS + 75);
    }
}