package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.action.tree.DialogButtonAction;
import com.minibot.api.method.*;
import com.minibot.api.util.Random;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.concurrent.TimeUnit;

/**
 * @author Tim Dekker
 * @since 6/8/15
 */
@Manifest(name = "ZooFighter2", author = "Swipe", version = "1.0.0", description = "Zoo Fighter")
public class ZooFighter2 extends Macro implements Renderable {

    private static boolean started;
    private static final int SKILL_STR = Skills.RANGED;
    private static int start_exp;
    private static long start_time;
    private static final String[] names = new String[]{"Cyclops", "Jogre", "Wolf"};

    private static boolean level() {
        WidgetComponent component = Widgets.get(233, 2);
        return component != null && component.visible();
    }

    @Override
    public void run() {
        if (level()) {
            RuneScape.processAction(new DialogButtonAction(15269890, -1));
            Time.sleep(() -> !level(), Random.nextInt(4500, 6500));
        }
        if (Random.nextInt(0, 1000) == 2) {
            Walking.walkTo(Players.local().location());
        }
        Minibot.instance().client().resetMouseIdleTime();
        if (!started) {
            started = true;
            start_exp = Game.experiences()[SKILL_STR];
            start_time = System.currentTimeMillis();
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