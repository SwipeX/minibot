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
 * @author Jacob Doiron
 * @since 6/24/2015
 */
@Manifest(name = "Superglass", author = "Jacob", version = "1.1.1", description = "Makes molten glass")
public class Superglass extends Macro implements Renderable, ChatboxListener {

    private static int casts;
    private static int sandFails;
    private static int seaweedFails;
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

    private static final Filter<Item> SAND_FILTER = i -> i.name().equals("Bucket of sand");
    private static final Filter<Item> SEAWEED_FILTER = i -> i.name().equals("Seaweed");
    private static final Filter<Item> MOLTEN_FILTER = i -> i.name().equals("Molten glass");
    private static final Filter<Item> FIRE_FILTER = i -> i.name().equals("Fire rune");
    private static final Filter<Item> ASTRAL_FILTER = i -> i.name().equals("Astral rune");

    private static boolean openBank() {
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
        }
        Item fires = Inventory.first(FIRE_FILTER);
        Item astrals = Inventory.first(ASTRAL_FILTER);
        if ((!staff && ((fires != null && fires.amount() < 6) || fires == null))
                || ((astrals != null && astrals.amount() < 2) || astrals == null)) {
            Game.logout();
            interrupt();
        }
        if (Inventory.first(SAND_FILTER) == null) {
            Item sand = Bank.first(SAND_FILTER);
            if (sand != null) {
                sand.processAction("Withdraw-13");
            }
        }
        if (Inventory.first(SEAWEED_FILTER) == null) {
            Item seaweed = Bank.first(SEAWEED_FILTER);
            if (seaweed != null) {
                seaweed.processAction("Withdraw-13");
            }
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
        if (Bank.viewing()) {
            prepareInventory();
        } else {
            if (cast) {
                openBank();
            } else {
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
        g.drawString(String.format("Time: %s", Time.format(runtime())), 13, 10);
        g.drawString(String.format("Casts: %s (%d/H)", ValueFormat.format(casts, TEXT_FORMAT), hourly(casts)), 13, 22);
        g.drawString(String.format("Profit: %s (%s/H)", ValueFormat.format(PROFIT * casts, TEXT_FORMAT),
                ValueFormat.format(hourly(PROFIT * casts), TEXT_FORMAT)), 13, 34);
    }

    @Override
    public void messageReceived(int type, String sender, String message, String clan) {
        if (message != null && message.contains("You don't have any")) {
            if (message.contains("sand") || message.contains("seaweed")) {
                casts--;
            }
        }
    }
}