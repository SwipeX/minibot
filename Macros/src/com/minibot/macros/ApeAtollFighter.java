package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.method.*;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.concurrent.TimeUnit;

/**
 * Created by tim on 6/8/15.
 */
@Manifest(name = "Monkey Fighter", author = "Swipe", version = "1.0.0", description = "Monkey Fighter")
public class ApeAtollFighter extends Macro implements Renderable {

    private static boolean started;
    private static final int SKILL_STR = Skills.STRENGTH;
    private static int start_exp;
    private static long start_time;
    private static final String[] names = new String[]{"Monkey Guard"};


    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        if (!started) {
            started = true;
            start_exp = Game.experiences()[SKILL_STR];
            start_time = System.currentTimeMillis();
        }
        if (Minibot.instance().client().getLevels()[Skills.PRAYER] < 25) {
            GameObject alter = Objects.topAt(new Tile(2797, 2794));
            if (alter != null) {
                String name = alter.name();
                if (name != null && name.equals("Gorilla Statue")) {
                    alter.processAction(ActionOpcodes.OBJECT_ACTION_0, "Pray-at", 1, 1);
                    Time.sleep(1000);
                    return;
                }
            }
        }
        if (Minibot.instance().client().getGameSettings()[375] != 1) {
            WidgetComponent comp = Widgets.get(160, 13);
            if (comp != null) {
                comp.processAction(ActionOpcodes.WIDGET_ACTION, 1, "Activate", "");
                Time.sleep(500, 900);
            }
        }
        if (Players.local().animation() != -1) {
            return;
        }
        Npc npc = Npcs.nearestByFilter(npc1 -> {
            for (String name : names) {
                if (npc1 != null && npc1.name() != null && npc1.name().equals(name)) {
                    return true;
                }
            }
            return false;
        });
        if (npc != null && Players.local().target() == null) {
            npc.processAction("Attack");
            Time.sleep(5000);
        }
        if (Minibot.instance().client().getGameSettings()[300] / 10 >= 100) {
            WidgetComponent comp = Widgets.get(593, 30);
            if (comp != null) {
                comp.processAction(ActionOpcodes.WIDGET_ACTION, 1, "Use <col=00ff00>Special Attack</col>", "");
            }
            Time.sleep(3500);
        }
        if (Minibot.instance().client().getLevels()[Skills.STRENGTH] == Minibot.instance().client().getRealLevels()[Skills.STRENGTH]) {
            Item abs = Inventory.first(item -> item != null && item.name() != null && item.name().contains("Combat"));
            if (abs != null) {
                abs.processAction(ActionOpcodes.ITEM_ACTION_0, "Drink");
                Time.sleep(100, 500);
            }
        }
    }

    private static int hourly(int val, long difference) {
        return (int) Math.ceil(val * 3600000D / difference);
    }

    private static String format(long millis) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.GREEN);
        g.drawRect(0, 0, 150, 50);
        long time_diff = System.currentTimeMillis() - start_time;
        int gain = Game.experiences()[SKILL_STR] - start_exp;
        g.drawString("Time: " + format(time_diff), 10, 10);
        g.drawString("Exp: " + gain, 10, 25);
        g.drawString("Exp/H: " + hourly(gain, time_diff), 10, 40);
    }
}
