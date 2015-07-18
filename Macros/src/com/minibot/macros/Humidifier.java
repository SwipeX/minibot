package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.method.*;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.util.ValueFormat;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * @author Tyler Sedlar
 * @since 6/24/2015
 */
@Manifest(name = "Humidifier", author = "Tyler", version = "1.0.0", description = "Humidifies jugs")
public class Humidifier extends Macro implements Renderable {

    private static final int TEXT_FORMAT = ValueFormat.THOUSANDS | ValueFormat.COMMAS | ValueFormat.PRECISION(1);

    private static final Filter<Item> JUG_FILTER = i -> {
        String name = i.name();
        return name != null && name.equals("Jug");
    };

    private static final Filter<Item> WATER_JUG_FILTER = i -> {
        String name = i.name();
        return name != null && name.equals("Jug of water");
    };

    private static final int ASTRAL_PRICE = 142, JUG_PRICE = 120, WATER_JUG_PRICE = 150;
    private static final int PROFIT_PER_JUG = (WATER_JUG_PRICE - JUG_PRICE);
    private static final int EXP_PER_CAST = 65;

    private static int casts, profit;

    private static boolean openBank() {
        Npc banker = Npcs.nearestByName("Banker");
        if (banker != null) {
            banker.processAction("Bank");
            return Time.sleep(Bank::viewing, 10000);
        }
        return false;
    }

    @Override
    public void atStart() {
        if (!Game.playing()) {
            interrupt();
        }
    }

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        if (Bank.viewing()) {
            Item water = Inventory.first(WATER_JUG_FILTER);
            if (water != null) {
                water.processAction("Deposit-All");
            }
            Item invJug = Inventory.first(JUG_FILTER);
            if (invJug == null) {
                Item bankJug = Bank.first(JUG_FILTER);
                if (bankJug != null) {
                    bankJug.processAction("Withdraw-All");
                    Bank.close();
                    Time.sleep(400, 600);
                } else {
                    System.out.println("Out of jugs");
                    interrupt();
                }
            } else {
                Bank.close();
                Time.sleep(400, 600);
            }
        } else {
            if (Inventory.first(JUG_FILTER) != null) {
                WidgetComponent humidify = Widgets.get(218, 99);
                if (humidify != null) {
                    humidify.processAction("Cast");
                    if (Time.sleep(() -> Inventory.first(WATER_JUG_FILTER) != null, 7000)) {
                        int count = Inventory.items(WATER_JUG_FILTER).size();
                        casts++;
                        profit += ((count * PROFIT_PER_JUG) - ASTRAL_PRICE);
                    }
                }
            } else {
                openBank();
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.drawString("Runtime: " + Time.format(runtime()), 13, 15);
        g.drawString("Casts: " + ValueFormat.format(casts, TEXT_FORMAT) + " (" + hourly(casts) + "/HR)", 13, 30);
        g.drawString("Experience: " + ValueFormat.format(casts * EXP_PER_CAST, TEXT_FORMAT) + " (" +
                hourly(casts * EXP_PER_CAST) + "/HR)", 13, 45);
        g.drawString("Profit: " + ValueFormat.format(profit, TEXT_FORMAT) + " (" +
                ValueFormat.format(hourly(profit), TEXT_FORMAT) + "/HR)", 13, 60);
    }
}