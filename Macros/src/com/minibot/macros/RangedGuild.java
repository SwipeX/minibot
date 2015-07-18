package com.minibot.macros;

import com.minibot.api.method.*;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.util.ValueFormat;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * @author Tyler Sedlar
 * @since 7/11/2015
 */
@Manifest(name = "Ranged Guild", author = "Tyler", version = "1.0.0", description = "Does range guild")
public class RangedGuild extends Macro implements Renderable {

    private static final int COMMA_FORMAT = ValueFormat.COMMAS;
    private static final int THOUSAND_FORMAT = ValueFormat.THOUSANDS | ValueFormat.PRECISION(2);

    private static int score;
    private static int startExp;

    private static int fireIndex() {
        return Game.varp(156);
    }

    private static boolean playing() {
        int varp = fireIndex();
        return varp >= 1 && varp <= 10;
    }

    private static int score() {
        return Game.varp(157);
    }

    private static void fire() {
        GameObject object = Objects.topAt(new Tile(2679, 3426, 0));
        if (object != null) {
            object.processAction("Fire-at");
            Time.sleep(200, 300);
        }
    }

    @Override
    public void atStart() {
        startExp = Game.experiences()[Skills.RANGED];
    }

    @Override
    public void run() {
        if (playing()) {
            Item arrow = Inventory.first(i -> {
                String name = i.name();
                return name != null && name.equals("Bronze arrow");
            });
            if (arrow != null) {
                arrow.processAction("Wield");
                Time.sleep(300, 400);
            }
            fire();
            if (fireIndex() > 10) {
                score += score();
            }
        } else {
            if (Widgets.viewingDialog()) {
                Widgets.processDialogOption(0);
                Time.sleep(400, 600);
            } else if (Widgets.viewingContinue()) {
                Widgets.processContinue();
                Time.sleep(400, 600);
            } else {
                Npc npc = Npcs.nearestByName("Competition Judge");
                if (npc != null) {
                    npc.processAction("Talk-to");
                    Time.sleep(Widgets::viewingContinue, 2000);
                }
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        int gained = Game.experiences()[Skills.RANGED] - startExp;
        int tickets = (score / 10);
        int yOff = 11;
        g.drawString("Runtime: " + Time.format(runtime()), 13, yOff += 15);
        String fTickets = ValueFormat.format(tickets, COMMA_FORMAT);
        String fTicketsHr = ValueFormat.format(hourly(tickets), COMMA_FORMAT);
        g.drawString("Tickets: " + fTickets + " (" + fTicketsHr + "/HR)", 13, yOff += 15);
        String fExp = ValueFormat.format(gained, COMMA_FORMAT);
        String fExpHr = ValueFormat.format(hourly(gained), THOUSAND_FORMAT);
        g.drawString("Experience: " + fExp + " (" + fExpHr + "/HR)", 13, yOff + 15);
    }
}