package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.method.*;
import com.minibot.api.util.*;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.api.wrapper.locatable.Character;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * @author Jacob Doiron
 * @since 06/28/15
 */
@Manifest(name = "Ogre Killer", author = "Jacob", version = "1.0.0", description = "Kills ogres in the Ardougne cage")
public class OgreRanger extends Macro implements Renderable, ChatboxListener {

    private static final int TEXT_FORMAT = ValueFormat.THOUSANDS | ValueFormat.COMMAS | ValueFormat.PRECISION(1);

    private static int startExp;
    private static long lastSpec = -1;
    private static boolean force;
    private static boolean recharge;

    @Override
    public void atStart() {
        if (!Game.playing()) {
            interrupt();
        }
        startExp = Game.experiences()[Skills.RANGED];
    }

    private static void specialAttack() {
        if ((lastSpec == -1 || Time.millis() - lastSpec > 5000) && Equipment.equipped("Toxic blowpipe") && !recharge) {
            if (Game.varp(300) / 10 >= 50) {
                WidgetComponent comp = Widgets.get(593, 30);
                if (comp != null) {
                    comp.processAction("Use <col=00ff00>Special Attack</col>");
                }
                lastSpec = Time.millis();
            }
        }
    }

    private static boolean attack() {
        Character target = Players.local().target();
        if (target == null || (target.maxHealth() > 0 && target.health() <= 0)) {
            Npc npc = Npcs.nearestByFilter(n -> {
                Character npcTarget = n.target();
                if (npcTarget != null) {
                    return npcTarget.equals(Players.local());
                }
                return !(n.health() <= 0 && n.maxHealth() > 0) && n.name().equals("Ogre");
            });
            if (npc != null) {
                specialAttack();
                npc.processAction("Attack");
                Time.sleep(2250, 3000);
                if (Time.sleep(() -> {
                    Character playerTarget = Players.local().target();
                    return !Game.playing() || force || (playerTarget != null && playerTarget.maxHealth() > 0) || Widgets.viewingContinue();
                }, Random.nextInt(25000, 32500))) {
                    force = false;
                    if (Widgets.viewingContinue()) {
                        Widgets.processContinue();
                        Time.sleep(() -> !Widgets.viewingContinue(), Random.nextInt(4500, 6500));
                    } else {
                        Time.sleep(150, 450);
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
        if (recharge) {
            Item scales = Inventory.first(i -> i.name().equals("Zulrah's scales"));
            Item darts = Inventory.first(i -> i.name().contains("dart"));
            if (scales != null && darts != null) {
                if (Equipment.equipped("Toxic blowpipe")) {
                    Equipment.unequip("Toxic blowpipe");
                    Time.sleep(1500, 2000);
                } else {
                    Item blowpipe = Inventory.first(i -> i.name().equals("Toxic blowpipe"));
                    if (blowpipe != null) {
                        darts.use(blowpipe);
                        Time.sleep(900, 1800);
                        scales.use(blowpipe);
                        Time.sleep(1500, 2500);
                        Equipment.equip("Toxic blowpipe");
                        recharge = !Time.sleep(() -> Equipment.equipped("Toxic blowpipe"), Random.nextInt(5000, 7500));
                    }
                }
            } else {
                Game.logout();
                interrupt();
            }
        } else {
            attack();
            if (Widgets.viewingContinue()) {
                Widgets.processContinue();
                Time.sleep(() -> !Widgets.viewingContinue(), Random.nextInt(4500, 6500));
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.drawString(String.format("Time: %s", Time.format(runtime())), 10, 10);
        g.drawString(String.format("Ranged Exp: %s (%s/H)", ValueFormat.format(Game.experiences()[Skills.RANGED] - startExp, TEXT_FORMAT),
                ValueFormat.format(hourly(Game.experiences()[Skills.RANGED] - startExp), TEXT_FORMAT)), 10, 22);
        g.drawString(String.format("Level: %d", Game.levels()[Skills.RANGED]), 10, 34);
    }

    @Override
    public void messageReceived(int type, String sender, String message, String clan) {
        if (message.contains("Your blowpipe needs to")) {
            recharge = true;
        } else if (message.contains("Someone else is fighting")) {
            force = true;
        }
    }
}