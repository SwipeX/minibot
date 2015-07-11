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
import java.util.List;

/**
 * @author Jacob Doiron
 * @since 7/07/2015
 */
@Manifest(name = "PoisonMaker", author = "Jacob", version = "1.0.0", description = "Make weapon poison(++)")
public class PoisonMaker extends Macro implements Renderable, ChatboxListener {

    private static final int selected = 3; // default (0): crack coconuts, 1 = make milk, 2 = add nightshade, 3 = add berries
    private static int made;

    private static Filter<Item> zero;
    private static Filter<Item> one;
    private static Filter<Item> two;

    private static final int TEXT_FORMAT = ValueFormat.THOUSANDS | ValueFormat.COMMAS | ValueFormat.PRECISION(1);

    private static final Filter<Item> HAMMER_FILTER = i -> {
        String name = i.name();
        return name != null && name.equals("Hammer");
    };

    private static final Filter<Item> VIAL_FILTER = i -> {
        String name = i.name();
        return name != null && name.equals("Vial");
    };

    private static final Filter<Item> COCONUT_FILTER = i -> {
        String name = i.name();
        return name != null && name.equals("Coconut");
    };

    private static final Filter<Item> HALF_FILTER = i -> {
        String name = i.name();
        return name != null && name.equals("Half coconut");
    };

    private static final Filter<Item> SHELL_FILTER = i -> {
        String name = i.name();
        return name != null && name.equals("Coconut shell");
    };

    private static final Filter<Item> NIGHTSHADE_FILTER = i -> {
        String name = i.name();
        return name != null && name.equals("Cave nightshade");
    };

    private static final Filter<Item> IVY_FILTER = i -> {
        String name = i.name();
        return name != null && name.equals("Poison ivy berries");
    };

    private static final Filter<Item> MILK_FILTER = i -> {
        String name = i.name();
        return name != null && name.equals("Coconut milk");
    };

    private static final Filter<Item> UNF_FILTER = i -> {
        String name = i.name();
        return name != null && name.equals("Weapon poison++ (unf)");
    };

    private static final Filter<Item> POISON_FILTER = i -> {
        String name = i.name();
        return name != null && name.equals("Weapon poison(++)");
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
        if (selected == 0) {
            zero = HALF_FILTER;
            one = HAMMER_FILTER;
            two = COCONUT_FILTER;
        } else if (selected == 1) {
            zero = MILK_FILTER;
            one = HALF_FILTER;
            two = VIAL_FILTER;
        } else if (selected == 2) {
            zero = UNF_FILTER;
            one = MILK_FILTER;
            two = NIGHTSHADE_FILTER;
        } else if (selected == 3) {
            zero = POISON_FILTER;
            one = UNF_FILTER;
            two = IVY_FILTER;
        } else {
            zero = null;
            one = null;
            two = null;
        }
        Item deposit = Inventory.first(zero);
        if (deposit != null) {
            deposit.processAction("Deposit-All");
            Item shell = Inventory.first(SHELL_FILTER);
            if (shell != null) {
                shell.processAction("Deposit-All");
            }
            Time.sleep(() -> shell != null ? Inventory.first(zero) == null && Inventory.first(SHELL_FILTER) == null
                    : Inventory.first(zero) == null, Random.nextInt(4500, 5500));
        }
        if (Inventory.first(one) == null) {
            Item first = Bank.first(one);
            if (first != null) {
                first.processAction(selected == 0 ? "Withdraw-1" : "Withdraw-14");
            }
        }
        if (Inventory.first(two) == null) {
            Item second = Bank.first(two);
            if (second != null) {
                second.processAction(selected == 0 ? "Withdraw-All" : "Withdraw-14");
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
            Time.sleep(() -> Inventory.count() == 28, Random.nextInt(4500, 5500));
            Item first = Inventory.first(one);
            Item second = Inventory.first(two);
            if (first != null && second != null) {
                List<Item> items = Inventory.items();
                if (selected == 0) {
                    for (int i = 1; i < 28; i++) {
                        Inventory.use(first, items.get(i));
                    }
                } else if (selected == 1) {
                    for (int i = 0; i < 14; i++) {
                        Inventory.use(items.get(i), items.get(i + 14));
                    }
                } else if (selected == 2 || selected == 3) {
                    Inventory.use(first, second);
                    if (Time.sleep(() -> {
                        WidgetComponent component = Widgets.get(309, 6);
                        return component != null && component.visible();
                    }, Random.nextInt(5000, 6500))) {
                        RuneScape.processAction(new DialogButtonAction(20250627, -1));
                        if (Time.sleep(() -> Inventory.count() == 14 || level(), Random.nextInt(20000, 25000))) {
                            if (level()) {
                                solve();
                            }
                        }
                    }
                } else {
                    interrupt();
                }
                openBank();
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.drawString(String.format("Time: %s", Time.format(runtime())), 10, 10);
        g.drawString(String.format("Made: %s (%s/H)", ValueFormat.format(made, TEXT_FORMAT),
                ValueFormat.format(hourly(made), TEXT_FORMAT)), 10, 22);
    }

    @Override
    public void messageReceived(int type, String sender, String message, String clan) {
        if ((message.contains("break") || message.contains("fill") || message.contains("mix")) && type == 105) {
            made++;
        }
    }
}