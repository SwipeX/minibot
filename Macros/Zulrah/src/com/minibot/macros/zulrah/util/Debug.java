package com.minibot.macros.zulrah.util;

import com.minibot.api.method.Players;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.macros.zulrah.Zulrah;
import com.minibot.macros.zulrah.action.Gear;
import com.minibot.macros.zulrah.action.Potions;
import com.minibot.macros.zulrah.phase.Phase;

import java.awt.*;
import java.util.Arrays;

/**
 * @author Tim Dekker
 * @since 7/15/15
 */
public class Debug {

    private static final Color DARK = new Color(20, 20, 20, 180);
    public static void paint(Graphics g) {
        Tile local = Players.local().location();
        Tile origin = Zulrah.getOrigin();
        Phase phase = Zulrah.getPhase();
        Npc zulrah = Zulrah.getMonster();
        int y = 10;
        g.setColor(Color.GREEN);
        g.drawString("Zulrah Debugging By: Swipe", 20, y += 13);
        g.drawString("Current: " + (zulrah == null ? "N/A" : zulrah.id()) + " Previous ids: " + Arrays.toString(Zulrah.getPrevious().toArray(new Integer[0])),
                20, y += 13);
        if (origin != null)
            g.drawString(String.format("Origin: %s, Offset: %s,%s", origin.toString(),
                    local.x() - origin.x(), local.y() - origin.y()), 20, y += 13);
        if (phase != null)
            g.drawString("Current: Phase - " + (phase.isConfirmed() ? " Confirmed " : " UNCOMFIRMED ") + phase + " Stage - " + phase.getCurrent() +
                    " Type - " + phase.getCurrent().getSnakeType(), 20, y += 13);
        Tile offset = phase.getCurrent().getTile();
        if (offset != null && offset.distance() < 15)
            offset.draw((Graphics2D) g);
        g.drawString("Potions:", 20, y += 13);
        for (Potions.Potion potion : Potions.Potion.values()) {
            if (potion.getLastDrink() != -1) {
                g.drawString(potion.name() + " Remaining: " +
                        ((potion.getLifetime() + potion.getLastDrink()) - System.currentTimeMillis()), 20, y += 13);
            }
        }
        g.drawString("Gear: ", 20, y += 13);
        g.drawString("Range Ids: " + Arrays.toString(Gear.getRangeIds()), 20, y += 13);
        g.drawString("Magic Ids: " + Arrays.toString(Gear.getMageIds()), 20, y += 13);
    }
}
