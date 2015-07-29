package com.minibot.macros.zulrah.util;

import com.minibot.api.method.Players;
import com.minibot.api.util.Time;
import com.minibot.api.util.ValueFormat;
import com.minibot.api.wrapper.locatable.Npc;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.macros.zulrah.Zulrah;
import com.minibot.macros.zulrah.action.Gear;
import com.minibot.macros.zulrah.action.Potions;
import com.minibot.macros.zulrah.phase.Phase;
import com.minibot.macros.zulrah.phase.Stage;

import java.awt.*;
import java.util.Arrays;

/**
 * @author Tim Dekker
 * @since 7/15/15
 */
public class Paint {

    private static final Font FONT = new Font("Arial", Font.BOLD, 9);
    private static final Rectangle PAINT_BOUNDS = new Rectangle(7, 345, 490, 18);
    private static final Rectangle PAINT_BOUNDS_2 = new Rectangle(7, 363, 490, 18);
    private static final int SETTINGS = ValueFormat.COMMAS | ValueFormat.PRECISION(1) | ValueFormat.THOUSANDS;
    private static final int COMMA = ValueFormat.COMMAS;

    private static final Color DARK = new Color(20, 20, 20, 180);

    public static void debug(Graphics g) {
        Tile local = Players.local().location();
        Tile origin = Zulrah.origin();
        Phase phase = Zulrah.phase();
        Npc zulrah = Zulrah.monster();
        Stage stage = phase.current();
        int y = 10;
        g.setColor(Color.GREEN);
        g.drawString("Zulrah Debugging By: Tim/Tyler/Jacob", 20, y += 13);
        g.drawString("Current: " + (zulrah == null ? "N/A" : zulrah.id()) + " Previous ids: " +
                Arrays.toString(Zulrah.previous().toArray()), 20, y += 13);
        if (origin != null) {
            g.drawString(String.format("Origin: %s, Offset: %s,%s", origin.toString(),
                    local.x() - origin.x(), local.y() - origin.y()), 20, y += 13);
        }
        if (stage != null) {
            g.drawString("Current: Phase - " + (phase.isConfirmed() ? " Confirmed " : " UNCONFIRMED ") + phase +
                    " Stage - " + phase.current() + " Type - " + phase.current().getSnakeType(), 20, y += 13);
        }
        Tile offset = phase.current().getTile();
        if (offset != null && offset.distance() < 15) {
            offset.draw((Graphics2D) g);
        }
        g.drawString("Potions:", 20, y += 13);
        for (Potions.Potion potion : Potions.Potion.values()) {
            if (potion.lastDrink() != -1) {
                g.drawString(potion.name() + " Remaining: " +
                        ((potion.lifetime() + potion.lastDrink()) - System.currentTimeMillis()), 20, y += 13);
            }
        }
        g.drawString("Gear: " + Gear.hasEquip() + " " + Gear.hasInventory(), 20, y += 13);
        g.drawString("Range Ids: " + Arrays.toString(Gear.rangedIds()), 20, y += 13);
        g.drawString("Magic Ids: " + Arrays.toString(Gear.mageIds()), 20, y += 13);
        g.drawString("HP: " + (zulrah != null ? zulrah.health() : "-1"), 20, y += 13);
        Zulrah.phase().draw(g, 20, y + 13);
    }

    public static void paint(Zulrah zulrah, Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fill(PAINT_BOUNDS);
        g.fill(PAINT_BOUNDS_2);
        g.setColor(Color.WHITE);
        g.setFont(FONT);
        Npc npc = Zulrah.monster();
        String label = String.format("RUNTIME: %s     PROFIT: %s (%s/HR)     HEALTH: %s",
                Time.format(zulrah.runtime()),
                ValueFormat.format(zulrah.total(), SETTINGS),
                ValueFormat.format(Time.hourly(zulrah.runtime(), zulrah.total()), SETTINGS),
                npc != null ? (npc.health() > 0 ? (npc.healthPercent() + "%") : "N/A") : "N/A");
        g.drawString(label, 242 - (g.getFontMetrics().stringWidth(label) / 2), PAINT_BOUNDS.y + 13);

        String label2 = String.format("KILLS: %s (%s/HR)    DEATHS: %s (%s/HR)     K/D: %s",
                Zulrah.kills(),
                Time.hourly(zulrah.runtime(), Zulrah.kills()),
                Zulrah.deaths(),
                Time.hourly(zulrah.runtime(), Zulrah.deaths()),
                (Zulrah.deaths() == 0 ? 100 :  String.format("%.2f", ((double) Zulrah.kills()) / ((double) Zulrah.deaths()) * 10D)) + "%");
        g.drawString(label2, 242 - (g.getFontMetrics().stringWidth(label2) / 2), PAINT_BOUNDS.y + 13 + PAINT_BOUNDS.height);

    }
}