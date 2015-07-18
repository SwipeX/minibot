package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.method.*;
import com.minibot.api.util.Random;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.util.ValueFormat;
import com.minibot.api.wrapper.locatable.Character;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;

/**
 * @author Jacob Doiron
 * @since 06/28/15
 */
@Manifest(name = "Ogre Killer", author = "Jacob", version = "1.0.0", description = "Kills ogres in the Ardougne cage")
public class OgreRanger extends Macro implements Renderable, ChatboxListener {

    private static final int TEXT_FORMAT = ValueFormat.THOUSANDS | ValueFormat.COMMAS | ValueFormat.PRECISION(1);

    private static int startExp;

    @Override
    public void atStart() {
        if (!Game.playing()) {
            interrupt();
        }
        startExp = Game.experiences()[Skills.RANGED];
    }

    private static boolean attack() {
        Character target = Players.local().target();
        if (target == null || (target.maxHealth() > 0 && target.health() <= 0)) {
            Npc npc = Npcs.nearestByFilter(n -> {
                Character npcTarget = n.target();
                if (npcTarget != null) {
                    return npcTarget.equals(Players.local());
                }
                if (n.health() <= 0 && n.maxHealth() > 0) {
                    return false;
                }
                String name = n.name();
                return name != null && name.equals("Ogre");
            });
            if (npc != null) {
                npc.processAction("Attack");
                if (Time.sleep(() -> {
                    Character playerTarget = Players.local().target();
                    return (playerTarget != null && playerTarget.maxHealth() > 0) || Widgets.viewingContinue();
                }, Random.nextInt(25000, 32500))) {
                    if (Widgets.viewingContinue()) {
                        Widgets.processContinue();
                        Time.sleep(() -> !Widgets.viewingContinue(), Random.nextInt(4500, 6500));
                    } else {
                        Time.sleep(600, 800);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        attack();
        if (Widgets.viewingContinue()) {
            Widgets.processContinue();
            Time.sleep(() -> !Widgets.viewingContinue(), Random.nextInt(4500, 6500));
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.drawString(String.format("Time: %s", Time.format(runtime())), 10, 10);
        g.drawString(String.format("Ranged Exp: %s (%s/H)", ValueFormat.format(Game.experiences()[Skills.RANGED] - startExp, TEXT_FORMAT),
                ValueFormat.format(hourly(Game.experiences()[Skills.RANGED] - startExp), TEXT_FORMAT)), 10, 22);
        g.drawString(String.format("Level: %d", Game.experiences()[Skills.RANGED]), 10, 34);
    }

    @Override
    public void messageReceived(int type, String sender, String message, String clan) {
        if (type == 2) {
            Toolkit.getDefaultToolkit().beep();
        }
    }
}