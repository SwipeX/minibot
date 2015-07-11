package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.action.tree.DialogButtonAction;
import com.minibot.api.method.Bank;
import com.minibot.api.method.ChatboxListener;
import com.minibot.api.method.Game;
import com.minibot.api.method.Inventory;
import com.minibot.api.method.Mouse;
import com.minibot.api.method.Npcs;
import com.minibot.api.method.RuneScape;
import com.minibot.api.method.Skills;
import com.minibot.api.method.Widgets;
import com.minibot.api.util.Random;
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
 * @since 7/02/2015
 */
@Manifest(name = "WineMaker", author = "Jacob", version = "1.0.0", description = "99 c00k in 26 hours, s0n")
public class WineMaker extends Macro implements Renderable, ChatboxListener {

    private static int wines;

    private static final int TEXT_FORMAT = ValueFormat.THOUSANDS | ValueFormat.COMMAS | ValueFormat.PRECISION(1);

    private static final Filter<Item> JUG_FILTER = i -> {
        String name = i.name();
        return name != null && name.equals("Jug of water");
    };

    private static final Filter<Item> GRAPE_FILTER = i -> {
        String name = i.name();
        return name != null && name.equals("Grapes");
    };

    private static final Filter<Item> WINE_FILTER = i -> {
        String name = i.name();
        return name != null && name.equals("Jug of wine");
    };

    private static final Filter<Item> UNFERMENTED_FILTER = i -> {
        String name = i.name();
        return name != null && name.equals("Unfermented wine");
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
        Item wine = Inventory.first(WINE_FILTER);
        Item unf = Inventory.first(UNFERMENTED_FILTER);
        if (wine != null || unf != null) {
            Bank.depositAll();
            Time.sleep(() -> Inventory.first(WINE_FILTER) == null && Inventory.first(UNFERMENTED_FILTER) == null, Random.nextInt(4500, 5500));
        }
        if (Inventory.first(JUG_FILTER) == null) {
            Item j = Bank.first(JUG_FILTER);
            if (j != null) {
                j.processAction("Withdraw-14");
            }
        }
        if (Inventory.first(GRAPE_FILTER) == null) {
            Item g = Bank.first(GRAPE_FILTER);
            if (g != null) {
                g.processAction("Withdraw-14");
            }
        }
        return Bank.close();
    }

    private static boolean level() {
        WidgetComponent component = Widgets.get(233, 2);
        return component != null && component.visible();
    }

    private static void solve() {
        if (level()) {
            Mouse.hop(Random.nextInt(93, 166), Random.nextInt(435, 452));
            for (int i = 0; i < Random.nextInt(4, 8); i++) {
                Time.sleep(550, 875);
                Mouse.click(true);
            }
        }
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
            if (Inventory.count() == 14) {
                openBank();
            } else {
                solve();
                Time.sleep(() -> Inventory.count() == 28, Random.nextInt(4500, 5500));
                solve();
                Item jug = Inventory.first(JUG_FILTER);
                Item grape = Inventory.first(GRAPE_FILTER);
                if (jug != null && grape != null) {
                    Inventory.use(jug, grape);
                    if (Time.sleep(() -> {
                        WidgetComponent component = Widgets.get(309, 7);
                        return component != null && component.visible();
                    }, Random.nextInt(5000, 7500))) {
                        RuneScape.processAction(new DialogButtonAction(20250627, -1));
                        if (Time.sleep(() -> Inventory.count() == 14 || level(), Random.nextInt(20000, 25000))) {
                            if (level()) {
                                solve();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.drawString(String.format("Time: %s", Time.format(runtime())), 10, 10);
        g.drawString(String.format("Exp: %s (%s/H)", ValueFormat.format(200 * wines, TEXT_FORMAT),
                ValueFormat.format(hourly(200 * wines), TEXT_FORMAT)), 10, 22);
        g.drawString(String.format("Level: %d", Game.levels()[Skills.COOKING]), 10, 34);
        g.drawString(String.format("Made: %s (%s/H)", ValueFormat.format(wines, TEXT_FORMAT),
                ValueFormat.format(hourly(wines), TEXT_FORMAT)), 10, 46);
    }

    @Override
    public void messageReceived(int type, String sender, String message, String clan) {
        if (message.contains("You squeeze") && type == 105) {
            wines++;
        }
    }
}