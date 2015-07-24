package com.minibot.macros;

import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.action.tree.TableAction;
import com.minibot.api.method.*;
import com.minibot.api.util.Random;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.util.List;

@Manifest(name = "AmylaseBuyer", author = "Jacob", version = "1.0.0", description = "Buys and opens amylase packs")
public class AmylaseBuyer extends Macro {

    private static boolean bought;
    private static boolean opened;

    private static boolean shopOpen() {
        WidgetComponent component = Widgets.get(300, 76);
        return component != null && component.visible();
    }

    @Override
    public void atStart() {
        if (!Game.playing()) {
            interrupt();
        }
    }

    @Override
    public void run() {
        if (!opened) {
            Item amylase = Inventory.first(i -> i.name().equals("Amylase pack"));
            if (amylase != null) {
                List<Item> items = Inventory.items();
                for (int i = 0; i < items.size(); i++) {
                    if (items != null && !items.isEmpty()) {
                        Item item = items.get(i);
                        if (item != null && item.name().equals("Amylase pack")) {
                            item.processAction("Open");
                        }
                    }
                }
                bought = false;
                opened = true;
            }
        }
        Item marks = Inventory.first(i -> i.name().equals("Mark of grace"));
        if (marks != null && marks.amount() >= 10) {
            if (!shopOpen()) { // interface not up
                Npc grace = Npcs.nearestByName("Grace");
                if (grace != null) { // npc is around
                    grace.processAction("Trade");
                    Time.sleep(AmylaseBuyer::shopOpen, Random.nextInt(5000, 7500));
                }
            } else { // buy right amount and close interface
                if (!bought) {
                    int clicks = Math.min(3, marks.amount() / 10);
                    for (int i = 0; i < clicks; i++) {
                        RuneScape.processAction(new TableAction(ActionOpcodes.TABLE_ACTION_3, 12641, 6, 19660875));
                        Time.sleep(120, 300);
                    }
                    opened = false;
                    bought = true;
                    WidgetComponent close = Widgets.get(300, 91);
                    if (close != null && close.visible()) {
                        close.processAction("Close");
                        Time.sleep(() -> {
                            GameTab current = GameTab.current();
                            return current != null && current == GameTab.INVENTORY;
                        }, Random.nextInt(5000, 7500));
                    }
                }
            }
        } else {
            interrupt();
        }
    }
}