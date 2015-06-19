package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.method.*;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Tim Dekker
 * @since 5/12/15
 */
@Manifest(name = "Falconry", author = "Swipe", version = "1.0.0", description = "Hunts Kebbits")
public class Falconry extends Macro implements Renderable {
    boolean started;
    private int SKILL_HUNTER = 21;
    private int start_exp = 0;
    private long start_time;
    private String NPC = "Spotted kebbit";//Dark kebbits = bad exp
    private String FALCON = "Gyr Falcon";


    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        if (!started) {
            started = true;
            start_exp = Game.experiences()[SKILL_HUNTER];
            start_time = System.currentTimeMillis();
        }
        if (Players.local().animation() != -1)
            return;
        Inventory.dropAllExcept(item -> item.name().equals("Coins"));
        Npc falcon = (Npc) Game.getHinted();//Npcs.nearest(FALCON); TODO TEST
        if (falcon != null) {
            Point screen = falcon.screen();
            if (screen.x < 0 || screen.x > 600) {
                Walking.walkTo(falcon.location());
                Time.sleep(1000, 2000);
            } else {
                falcon.processAction(ActionOpcodes.NPC_ACTION_0, "Retrieve");
                Time.sleep(500);
            }
        } else {
            Npc kebbit = Npcs.nearest(NPC);
            if (kebbit != null) {
                Point screen = kebbit.screen();
                if (screen.x < 0 || screen.x > 600) {
                    Walking.walkTo(kebbit.location());
                    Time.sleep(1000, 2000);
                } else {
                    kebbit.processAction(ActionOpcodes.NPC_ACTION_0, "Catch");
                    Time.sleep(500);
                }
            }
        }
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
        g.setColor(Color.GREEN);
        g.drawRect(0, 0, 150, 50);
        long time_diff = System.currentTimeMillis() - start_time;
        int gain = Game.experiences()[21] - start_exp;
        g.drawString("Time: " + format(time_diff), 10, 10);
        g.drawString("Exp: " + gain, 10, 25);
        g.drawString("Exp/H: " + hourly(gain, time_diff), 10, 40);
    }
}
