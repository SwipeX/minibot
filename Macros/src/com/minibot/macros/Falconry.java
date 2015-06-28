package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.action.ActionOpcodes;
import com.minibot.api.method.Game;
import com.minibot.api.method.Inventory;
import com.minibot.api.method.Npcs;
import com.minibot.api.method.Players;
import com.minibot.api.method.Skills;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Player;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.concurrent.TimeUnit;

/**
 * @author Tim Dekker
 * @since 5/12/15
 */
@Manifest(name = "Falconry", author = "Swipe", version = "1.0.0", description = "Hunts Kebbits")
public class Falconry extends Macro implements Renderable {

    private int startExp;

    @Override
    public void atStart() {
        Player local = Players.local();
        if (local != null) {
            startExp = Game.experiences()[Skills.HUNTER];
        } else {
            interrupt();
        }
    }

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        Player local = Players.local();
        if (local != null) {
            if (local.animation() != -1)
                return;
            Inventory.dropAllExcept(item -> item.name().equals("Coins"));
            Npc falcon = (Npc) Game.getHinted();
            if (falcon != null) {
                //Point screen = falcon.screen();
                //if (screen.x < 0 || screen.x > 600) {
                //    Walking.walkTo(falcon.location());
                //    Time.sleep(1000, 2000);
                //} else {
                    falcon.processAction(ActionOpcodes.NPC_ACTION_0, "Retrieve");
                    Time.sleep(400, 750);
                //}
            } else {
                String NPC = "Spotted kebbit";
                Npc kebbit = Npcs.nearestByName(NPC);
                if (kebbit != null) {
                    //Point screen = kebbit.screen();
                    //if (screen.x < 0 || screen.x > 600) {
                    //    Walking.walkTo(kebbit.location());
                    //    Time.sleep(1000, 2000);
                    //} else {
                        kebbit.processAction(ActionOpcodes.NPC_ACTION_0, "Catch");
                        Time.sleep(600, 1100);
                    //}
                }
            }
        }
    }

    public static String format(long millis) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.GREEN);
        int gained = Game.experiences()[Skills.HUNTER] - startExp;
        g.drawString("Time: " + format(runtime()), 10, 10);
        g.drawString("Exp: " + gained, 10, 25);
        g.drawString("Exp/H: " + hourly(gained), 10, 40);
    }
}