package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.method.Bank;
import com.minibot.api.method.Game;
import com.minibot.api.method.GameTab;
import com.minibot.api.method.Inventory;
import com.minibot.api.method.Npcs;
import com.minibot.api.method.Widgets;
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
 * @author Jacob Doiron
 * @since 6/24/2015
 */
@Manifest(name = "Superglass", author = "Jacob", version = "1.1.0", description = "Makes molten glass")
public class Superglass extends Macro implements Renderable {

    private static int casts;
    private static boolean cast;
    private static boolean staff;

    private static final int MOLTEN_PRICE = 150;
    private static final int SAND_PRICE = 50;
    private static final int SEAWEED_PRICE = 50;
    private static final int FIRE_PRICE = 8;
    private static final int ASTRAL_PRICE = 175;
    private static final int PROFIT = (MOLTEN_PRICE * (int) (1.3 * 13)) - (!staff ? FIRE_PRICE * 6 : 0) -
            (ASTRAL_PRICE * 2) - (SAND_PRICE * 13) - (SEAWEED_PRICE * 13);

    private static final int TEXT_FORMAT = ValueFormat.THOUSANDS | ValueFormat.COMMAS | ValueFormat.PRECISION(1);

    private static final Filter<Item> SAND_FILTER = i -> {
        String name = i.name();
        return name != null && name.contains("et of sand");
    };

    private static final Filter<Item> SEAWEED_FILTER = i -> {
        String name = i.name();
        return name != null && name.contains("aweed");
    };

    private static final Filter<Item> MOLTEN_FILTER = i -> {
        String name = i.name();
        return name != null && name.contains("ten gl");
    };

    private static final Filter<Item> FIRE_FILTER = i -> {
        String name = i.name();
        return name != null && name.contains("re run");
    };

    private static final Filter<Item> ASTRAL_FILTER = i -> {
        String name = i.name();
        return name != null && name.contains("ral run");
    };

    private boolean openBank() {
        Npc banker = Npcs.nearestByName("Banker");
        if (banker != null) {
            banker.processAction("Bank");
            return Time.sleep(Bank::viewing, 10000);
        }
        return false;
    }

    private boolean prepareInventory() {
        Item molten = Inventory.first(MOLTEN_FILTER);
        if (molten != null) {
            molten.processAction("Deposit-All");
            //Time.sleep(300, 500);
        }
        Item fires = Inventory.first(FIRE_FILTER);
        Item astrals = Inventory.first(ASTRAL_FILTER);
        if ((!staff && ((fires != null && fires.amount() < 6) || fires == null))
                || ((astrals != null && astrals.amount() < 2) || astrals == null)) {
            interrupt();
        }
        if (Inventory.first(SAND_FILTER) == null) {
            Item sand = Bank.first(SAND_FILTER);
            if (sand != null) {
                sand.processAction("Withdraw-13");
                //Time.sleep(300, 500);
            }/* else {
                System.out.println("No sand");
                MacroSelector.halt();
            }*/
        }
        if (Inventory.first(SEAWEED_FILTER) == null) {
            Item seaweed = Bank.first(SEAWEED_FILTER);
            if (seaweed != null) {
                seaweed.processAction("Withdraw-13");
                //Time.sleep(300, 500);
            }/* else {
                System.out.println("No seaweed");
                MacroSelector.halt();
            }*/
        }
        if (Bank.close()) {
            cast = false;
            return true;
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
        if (Bank.viewing())
            prepareInventory();
        else {
            if (cast)
                openBank();
            else {
                if (GameTab.MAGIC.open()) {
                    WidgetComponent spell = Widgets.get(218, 110);
                    if (spell != null) {
                        spell.processAction("Cast");
                        casts++;
                        cast = true;
                        Time.sleep(2650, 2950);
                    }
                }
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.drawString("Runtime: " + Time.format(runtime()), 13, 15);
        g.drawString("Casts: " + ValueFormat.format(casts, TEXT_FORMAT) + " (" + hourly(casts) + "/HR)", 13, 30);
        g.drawString("Profit: " + ValueFormat.format(PROFIT * casts, TEXT_FORMAT) + " (" +
                ValueFormat.format(hourly(PROFIT * casts), TEXT_FORMAT) + "/HR)", 13, 45);
    }
}