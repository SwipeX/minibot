package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.action.tree.WidgetAction;
import com.minibot.api.method.*;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.util.ValueFormat;
import com.minibot.api.util.filter.Filter;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.*;

/**
 * @author Jacob Doiron
 * @since 6/24/2015
 */
@Manifest(name = "Superglass", author = "Jacob", version = "1.0.0", description = "Makes molten glass")
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

    private static final int TEXT_FORMAT = ValueFormat.THOUSANDS | ValueFormat.COMMAS | ValueFormat.PRECISION(2);

    private static final Tile BANK_CHEST = new Tile(2444, 3083, 0);

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
        GameObject chest = Objects.topAt(BANK_CHEST);
        if (chest != null) {
            chest.processAction("Use");
            return Time.sleep(Bank::viewing, 10000);
        }
        return false;
    }

    private boolean prepareInventory() {
        Item molten = Inventory.first(MOLTEN_FILTER);
        if (molten != null) {
            molten.processAction("Deposit-All");
            Time.sleep(300, 500);
        }
        if ((!staff && Inventory.first(FIRE_FILTER) == null) || Inventory.first(ASTRAL_FILTER) == null) {
            interrupt();
        }
        if (Inventory.first(SAND_FILTER) == null) {
            Item sand = Bank.first(SAND_FILTER);
            if (sand != null) {
                sand.processAction("Withdraw-13");
                Time.sleep(300, 500);
            } else {
                return false;
            }
        }
        if (Inventory.first(SEAWEED_FILTER) == null) {
            Item seaweed = Bank.first(SEAWEED_FILTER);
            if (seaweed != null) {
                seaweed.processAction("Withdraw-13");
                Time.sleep(300, 500);
            } else {
                return false;
            }
        }
        //RuneScape.processAction(new WidgetAction(ActionOpcodes.WIDGET_ACTION, 1, 11, 3), "", "");
        cast = false;
        Time.sleep(150, 375);
        return true;
    }

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        if (Bank.viewing())
            prepareInventory(); // close bank, cast = false
        else {
            if (cast)
                openBank();
            else {
                /*if (!GameTab.MAGIC.viewing()) {
                    GameTab.MAGIC.component().processAction("Magic");
                    Time.sleep(200, 450);
                }*/
                //RuneScape.processAction(new WidgetAction(ActionOpcodes.WIDGET_ACTION, 1, 218, 110), "", "");
                cast = true;
                Time.sleep(2650, 2950);
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.drawString("Runtime: " + Time.format(runtime()), 13, 15);
        g.drawString("Casts: " + ValueFormat.format(casts, TEXT_FORMAT) + " (" +
                ValueFormat.format(hourly(casts), TEXT_FORMAT) + "/HR)", 13, 30);
        g.drawString("Profit: " + ValueFormat.format(PROFIT * casts, TEXT_FORMAT) + " (" +
                ValueFormat.format(hourly(PROFIT), TEXT_FORMAT) + "/HR)", 13, 45);
    }
}