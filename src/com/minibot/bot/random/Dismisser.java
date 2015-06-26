package com.minibot.bot.random;

import com.minibot.api.method.Npcs;
import com.minibot.api.method.Players;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Character;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 6/24/2015
 */
public class Dismisser extends RandomEvent {

    private Npc npc;

    @Override
    public boolean validate() {
        npc = Npcs.nearestByAction("Dismiss", 3);
        if (npc != null) {
            Character target = npc.target();
            return target != null && target.equals(Players.local());
        }
        return false;
    }

    @Override
    public void run() {
        if (npc != null) {
            npc.processAction("Dismiss");
            Time.sleep(800, 1200);
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        g.drawString("Random Dismisser", 10, 300);
    }
}