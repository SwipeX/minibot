package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.action.tree.DialogButtonAction;
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

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 7/11/2015
 */
@Manifest(name = "BowStringer", author = "Tyler", version = "1.0.0", description = "Strings bows")
public class BowStringer extends Macro implements ChatboxListener, Renderable {

    private static final int TEXT_FORMAT = ValueFormat.THOUSANDS | ValueFormat.COMMAS | ValueFormat.PRECISION(1);

    private static final int PROFIT = 105;

    private static final String BOWS = "Yew longbow (u)";
    private static final Filter<Item> BOW_FILTER = i -> {
        String name = i.name();
        return name != null && name.equals(BOWS);
    };
    private static final Filter<Item> STRING_FILTER = i -> {
        String name = i.name();
        return name != null && name.equals("Bow string");
    };

    private static final int MAKE_UID = 20250627;

    private int fletched = 0;
    private int cut = 0;
    private int startExp;

    private boolean openBank() {
        Npc banker = Npcs.nearestByName("Banker");
        if (banker != null) {
            banker.processAction("Bank");
            return Time.sleep(Bank::viewing, 10000);
        }
        return false;
    }

    private boolean validateMake() {
        WidgetComponent[] components = Widgets.childrenFor(MAKE_UID >> 16);
        if (components.length > 0) {
            WidgetComponent component = components[0];
            return component != null && component.visible();
        }
        return false;
    }

    @Override
    public void atStart() {
        startExp = Game.experiences()[Skills.FLETCHING];
    }

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        Item logs = Inventory.first(BOW_FILTER);
        if (logs != null) {
            if (Bank.viewing()) {
                Bank.close();
                Time.sleep(400, 600);
            } else {
                Item string = Inventory.first(STRING_FILTER);
                if (string != null) {
                    int count = Inventory.items(BOW_FILTER).size();
                    string.use(logs);
                    if (Time.sleep(this::validateMake, 5000)) {
                        Time.sleep(200, 300);
                        RuneScape.processAction(new DialogButtonAction(MAKE_UID, -1));
                        if (Time.sleep(() -> Inventory.items(BOW_FILTER).size() != count, 5000)) {
                            Time.sleep(() -> {
                                if (cut >= 28 || Inventory.items(BOW_FILTER).isEmpty() || Widgets.viewingContinue()) {
                                    cut = 0;
                                    return true;
                                }
                                return false;
                            }, 20000);
                            cut = 0;
                        }
                    }
                }
            }
        } else {
            if (!Bank.viewing()) {
                openBank();
            } else {
                Item bows = Inventory.first(i -> {
                    String name = i.name();
                    return name != null && name.toLowerCase().contains("bow") && !name.contains("(u)");
                });
                if (bows != null) {
                    bows.processAction("Deposit-All");
                    Time.sleep(200, 300);
                }
                Item bankBows = Bank.first(BOW_FILTER);
                Item bankStrings = Bank.first(STRING_FILTER);
                if (bankBows != null && bankStrings != null) {
                    Bank.withdraw(bankBows, 14);
                    Time.sleep(200, 300);
                    Bank.withdraw(bankStrings, 14);
                    Time.sleep(200, 300);
                    Bank.close();
                    Time.sleep(800, 1000);
                }
            }
        }
    }

    @Override
    public void messageReceived(int type, String sender, String message, String clan) {
        if (message.startsWith("You add a string")) {
            cut++;
            fletched++;
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.drawString(String.format("Time: %s", Time.format(runtime())), 13, 10);
        int gained = (Game.experiences()[Skills.FLETCHING] - startExp);
        g.drawString(String.format("Experience: %s (%s/H)", ValueFormat.format((gained), TEXT_FORMAT),
                ValueFormat.format(hourly(gained), TEXT_FORMAT)), 13, 22);
        g.drawString(String.format("Fletched: %s (%d/H)", ValueFormat.format(fletched, TEXT_FORMAT),
                hourly(fletched)), 13, 34);
        g.drawString(String.format("Profit: %s (%s/H)", ValueFormat.format(PROFIT * fletched, TEXT_FORMAT),
                ValueFormat.format(hourly(PROFIT * fletched), TEXT_FORMAT)), 13, 46);
    }
}
