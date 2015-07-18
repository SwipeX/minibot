package com.minibot.macros;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.action.tree.CloseButtonAction;
import com.minibot.api.action.tree.TableAction;
import com.minibot.api.method.*;
import com.minibot.api.util.Random;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.util.ValueFormat;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * @author Tyler Sedlar
 * @since 7/11/2015
 */
@Manifest(name = "WGP", author = "Tyler", version = "1.0.0", description = "Purchases from warrior's guild")
public class WarriorGuildPurchaser extends Macro implements Renderable {

    private static final boolean POTATOES = false;

    private static final int[] MEMBERS_WORLDS = {
            302, 304, 305, 306, 309, 310, 311, 312, 313, 314, 317, 318, 319, 320, 321, 322, 325, 327,
            328, 329, 330, 333, 334, 336, 337, 338, 341, 342, 343, 344, 345, 346, 349, 350, 351, 354,
            358, 359, 360, 361, 362, 365, 366, 367, 368, 369, 370, 373, 374, 375, 376, 377, 378
    };

    private static final int STORE_WIDGET = 300, STORE_ITEMS_COMPONENT = 75;
    private static final int PROFIT_EACH = (POTATOES ? 590 : 244);
    private static final int STORE_INDEX = (POTATOES ? 3 : 4);
    private static final int ITEM_ID = (POTATOES ? 6705 : 2003);

    private static int bought;
    private static int worldIndex;
    private static long lastHop = -1;

    private static boolean buyable() {
        WidgetComponent comp = Widgets.get(STORE_WIDGET, STORE_ITEMS_COMPONENT);
        if (comp != null) {
            int[] itemStacks = comp.itemStackSizes();
            if (itemStacks != null) {
                return itemStacks[STORE_INDEX] > 0;
            }
        }
        return false;
    }

    private static boolean viewingStore() {
        WidgetComponent[] children = Widgets.childrenFor(19660875 >> 16);
        return children != null && children.length > 0 && children[0] != null && children[0].visible();
    }

    private static void close() {
        RuneScape.processAction(new CloseButtonAction(19660891));
        Time.sleep(600, 800);
    }

    private static int buy() {
        int count = Inventory.count();
        RuneScape.processAction(new TableAction(ActionOpcodes.TABLE_ACTION_3, ITEM_ID, STORE_INDEX, 19660875));
        Time.sleep(200, 400);
        close();
        return (Inventory.count() - count);
    }

    @Override
    public void run() {
        if (Inventory.full()) {
            if (!Bank.viewing()) {
                Bank.openBooth();
            } else {
                Item item = Inventory.first(i -> i.id() == ITEM_ID);
                if (item != null) {
                    item.processAction("Deposit-All");
                    Time.sleep(800, 1200);
                } else {
                    Bank.close();
                }
            }
        } else {
            if (!viewingStore()) {
                Npc npc = Npcs.nearestByName("Lidio");
                if (npc != null) {
                    npc.processAction("Trade");
                    Time.sleep(WarriorGuildPurchaser::viewingStore, 5000);
                    Time.sleep(400, 600);
                }
            } else {
                if (buyable()) {
                    bought += buy();
                }
                if (!Inventory.full()) {
                    if (lastHop != -1) {
                        while (Time.millis() - lastHop < 8500) {
                            Time.sleep(50, 100);
                        }
                    }
                    int randIdx;
                    do {
                        randIdx = Random.nextInt(MEMBERS_WORLDS.length - 1);
                    } while (randIdx == worldIndex);
                    Game.hopWorld(MEMBERS_WORLDS[(worldIndex = randIdx)]);
                    lastHop = Time.millis();
                }
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        int totalProfit = (bought * PROFIT_EACH);
        int yOff = 11;
        g.drawString("Runtime: " + Time.format(runtime()), 13, yOff += 15);
        String fBought = ValueFormat.format(bought, ValueFormat.COMMAS);
        String fBoughtHr = ValueFormat.format(hourly(bought), ValueFormat.COMMAS);
        g.drawString("Bought: " + fBought + " (" + fBoughtHr + "/HR)", 13, yOff += 15);
        String fProfit = ValueFormat.format(totalProfit, ValueFormat.COMMAS);
        String fProfitHr = ValueFormat.format(hourly(totalProfit), ValueFormat.COMMAS);
        g.drawString("Profit: " + fProfit + " (" + fProfitHr + "/HR)", 13, yOff + 15);
    }
}