package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.method.*;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by tim on 6/1/15.
 */
@Manifest(name = "Prayer9", author = "Swipe", version = "1.0.0", description = "Drinks prayer potions")
public class Prayer9 extends Macro implements Renderable {
    int lastExp = 0;
    int holder = 0;
    int startExp = -1;
    private long start_time;
    private Tile start;
    private Tile alt;
    private long lastMove;

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        if (startExp == -1) {
            start = Players.local().location();
            alt = start.derive(-4, 0);
            startExp = Game.experiences()[Skills.RANGE];
            start_time = System.currentTimeMillis();
        }
        if (Game.levels()[Skills.PRAYER] < 25) {
            Item prayer = Inventory.first(item -> item != null && item.name() != null && item.name().contains("rayer"));
            if (prayer != null) {
                prayer.processAction(ActionOpcodes.ITEM_ACTION_0, "Drink");
                Time.sleep(1800);
            }
        }
        if (Game.levels()[Skills.RANGE] <= Game.realLevels()[Skills.RANGE] + 9) {
            Item ranging = Inventory.first(item -> item != null && item.name() != null && item.name().contains("ang"));
            if (ranging != null) {
                ranging.processAction(ActionOpcodes.ITEM_ACTION_0, "Drink");
                Time.sleep(1800);
            }
        }
//        if (lastMove == -1 || System.currentTimeMillis() - lastMove > 22000) {
//            if (Players.local().location().equals(alt)) {
//                Walking.walkTo(start);
//            } else {
//                Walking.walkTo(alt);
//            }
//            lastMove = System.currentTimeMillis();
//        }
    }

    public int hourly(int val, long difference) {
        return (int) Math.ceil(val * 3600000D / difference);
    }

    public static String format(long millis) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.drawRect(0, 0, 150, 50);
        long time_diff = System.currentTimeMillis() - start_time;
        int gain = Game.experiences()[Skills.RANGE] - startExp;
        g.drawString("Time: " + format(time_diff), 10, 10);
        g.drawString("Exp: " + gain, 10, 25);
        g.drawString("Exp/H: " + hourly(gain, time_diff), 10, 40);
        g.drawString("Last Exp: " + holder, 10, 52);
        if (Game.experiences()[Skills.RANGE] != lastExp) {
            holder = (Game.experiences()[Skills.RANGE] - lastExp);
            lastExp = Game.experiences()[Skills.RANGE];
        }
    }
}
