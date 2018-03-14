package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.action.tree.DialogButtonAction;
import com.minibot.api.method.*;
import com.minibot.api.util.Random;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.util.ValueFormat;
import com.minibot.api.wrapper.WidgetComponent;
import com.minibot.api.wrapper.locatable.Area;
import com.minibot.api.wrapper.locatable.Character;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * @author Tyler Sedlar
 * @since 6/19/2015
 */
@Manifest(name = "Man Killer", author = "Tyler", version = "1.0.0", description = "Kills men and loots herbs")
public class ManKiller extends Macro implements Renderable {

    private static final Area area = new Area(new Tile(3171, 3290, 0), new Tile(3184, 3300, 0));

    private static final int COMMA_FORMAT = ValueFormat.COMMAS;
    private static final int THOUSAND_FORMAT = ValueFormat.THOUSANDS | ValueFormat.PRECISION(1);

    private static int startExp;

    @Override
    public void atStart() {
        if (!Game.playing()) {
            interrupt();
        }
        startExp = Game.experiences()[Skills.STRENGTH];
    }

    private static boolean level() {
        WidgetComponent component = Widgets.get(233, 2);
        return component != null && component.visible();
    }

    private boolean attack() {
        Character target = Players.local().target();
        if (target == null || target.health() <= 0) {
            Npc npc = Npcs.nearestByFilter(n -> {
                Character npcTarget = n.target();
                if (npcTarget != null) {
                    return npcTarget.equals(Players.local());
                }
                if (n.health() <= 0) {
                    return false;
                }
                String name = n.name();
                return name != null && name.equals("Chicken") && area.contains(n);
            });
            if (npc != null) {
                npc.processAction("Attack");
                if (Time.sleep(() -> {
                    Character playerTarget = Players.local().target();
                    return playerTarget != null || level();
                }, Random.nextInt(25000, 32500))) {
                    if (level()) {
                        RuneScape.processAction(new DialogButtonAction(15269890, -1));
                        Time.sleep(() -> !level(), Random.nextInt(4500, 6500));
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
        if (level()) {
            RuneScape.processAction(new DialogButtonAction(15269890, -1));
            Time.sleep(() -> !level(), Random.nextInt(4500, 6500));
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.drawString("Time: " + Time.format(runtime()), 13, 10);
        g.drawString("Exp: " + ValueFormat.format(Game.experiences()[Skills.STRENGTH] - startExp, COMMA_FORMAT)
                + " (" + ValueFormat.format(hourly(Game.experiences()[Skills.STRENGTH] - startExp), THOUSAND_FORMAT) +
                "/HR)", 13, 22);
    }
}