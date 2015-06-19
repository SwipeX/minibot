package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.action.tree.DialogButtonAction;
import com.minibot.api.method.*;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.util.ValueFormat;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.GroundItem;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 6/19/2015
 */
@Manifest(name = "Nightshade", author = "Tyler", version = "1.0.0", description = "Collects nightshade")
public class Nightshade extends Macro implements Renderable {

    private static final int COCONUT_PRICE = 910, VIAL_PRICE = 2, WEAPON_POISON_PRICE = 6160;
    private static final int SAPPHIRE_LANTERN = 4702, SKAVID_MAP = 2376;
    private static final int COMMA_FORMAT = ValueFormat.COMMAS;
    private static final int THOUSAND_FORMAT = ValueFormat.THOUSANDS | ValueFormat.PRECISION(2);

    private static final Tile CAVE_FRONT = new Tile(2523, 3070, 0);
    private static final Tile CAVE = new Tile(2522, 3068, 0);
    private static final Tile BANK_CHEST = new Tile(2444, 3083, 0);

    private static final Filter<Item> RING_FILTER = i -> {
        String name = i.name();
        return name != null && name.contains("Ring of duel");
    };

    private static final Filter<Item> SHADE_FILTER = i -> {
        String name = i.name();
        return name != null && name.equals("Cave nightshade");
    };

    private int looted = 0;

    private boolean teleport() {
        Item ring = Inventory.first(RING_FILTER);
        if (ring != null) {
            ring.processAction("Rub");
            Time.sleep(1500, 1700);
            RuneScape.processAction(new DialogButtonAction(14352384, 2), "", "");
            return Time.sleep(() -> BANK_CHEST.distance() < 10, 10000);
        }
        return false;
    }

    private boolean inCave() {
        return Npcs.nearest(n -> {
            String name = n.name();
            return name != null && name.equals("Skavid");
        }) != null;
    }

    private boolean enterCave() {
        GameObject cave = Objects.topAt(CAVE);
        if (cave != null) {
            cave.processAction("Enter", CAVE.localX(), CAVE.localY());
            Time.sleep(1500, 1700);
            RuneScape.processAction(new DialogButtonAction(15007745, -1), "", "");
            Time.sleep(1500, 1700);
            RuneScape.processAction(new DialogButtonAction(14352384, 1), "", "");
            return true;
        }
        return false;
    }

    private boolean loot() {
        GroundItem item = Ground.nearestByFilter(i -> {
            String name = i.name();
            return name != null && name.contains("nightshade");
        });
        if (item != null) {
            item.processAction("Take");
            int count = Inventory.count();
            Time.sleep(() -> Inventory.count() != count, 5000);
            return true;
        }
        return false;
    }

    private boolean openBank() {
        GameObject chest = Objects.topAt(BANK_CHEST);
        if (chest != null) {
            chest.processAction("Use");
            return Time.sleep(Bank::viewing, 10000);
        }
        return false;
    }

    private boolean prepareInventory() {
        Item shade = Inventory.first(SHADE_FILTER);
        if (shade != null) {
            shade.processAction("Deposit-All");
            Time.sleep(300, 500);
        }
        if (Inventory.first(RING_FILTER) == null) {
            Item ring = Bank.first(RING_FILTER);
            if (ring != null) {
                ring.processAction("Withdraw-1");
                Time.sleep(300, 500);
            } else {
                return false;
            }
        }
        return true;
    }

    private boolean hop() {
        return false; // TODO: add world hopping
    }

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        if (Inventory.first(SHADE_FILTER) != null && BANK_CHEST.distance() < 10) {
            if (!Bank.viewing()) {
                openBank();
            } else {
                prepareInventory();
            }
        } else {
            if (!inCave()) {
                if (CAVE_FRONT.distance() > 3) {
                    Walking.walkTo(CAVE_FRONT);
                    Time.sleep(600, 900);
                } else {
                    enterCave();
                }
            } else {
                if (Inventory.full()) {
                    teleport();
                } else {
                    if (loot()) {
                        looted++;
                    } else {
                        hop();
                    }
                }
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        int yOff = 15;
        g.drawString("Runtime: " + Time.format(runtime()), 13, yOff += 15);
        String fLooted = ValueFormat.format(looted, COMMA_FORMAT);
        g.drawString("Looted: " + fLooted + " (" + hourly(looted) + "/HR)", 13, yOff += 15);
        int per = (WEAPON_POISON_PRICE - (COCONUT_PRICE + VIAL_PRICE));
        int profit = looted * per;
        String fProfit = ValueFormat.format(profit, COMMA_FORMAT);
        String fProfitHr = ValueFormat.format(hourly(profit), THOUSAND_FORMAT);
        g.drawString("Profit: " + fProfit + " (" + fProfitHr + "/HR)", 13, yOff + 15);
    }
}
