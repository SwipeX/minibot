package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.method.Npcs;
import com.minibot.api.method.Players;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.locatable.Character;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

/**
 * @author Tyler Sedlar
 * @since 7/6/2015
 */
@Manifest(name = "Seagull", author = "Tyler", version = "1.0.0", description = "Kills seagulls")
public class Seagull extends Macro {

    private static long lastAttack = -1;

    private static boolean attack() {
        boolean force = (lastAttack != -1 && (Time.millis() - lastAttack) > 8000);
        Character target = Players.local().target();
        if (force || target == null || (target.health() <= 0)) {
            Npc npc = Npcs.nearestByFilter(n -> {
                Character npcTarget = n.target();
                if (npcTarget != null) {
                    return npcTarget == Players.local();
                }
                if (n.health() <= 0) {
                    return false;
                }
                String name = n.name();
                return name != null && name.equals("Seagull");
            });
            if (npc != null) {
                npc.processAction("Attack");
                lastAttack = Time.millis();
                if (Time.sleep(() -> {
                    Character playerTarget = Players.local().target();
                    return playerTarget != null;
                }, 5000)) {
                    Time.sleep(600, 800);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        attack();
        Time.sleep(800, 1200);
    }
}