package com.minibot.macros.zulrah.util;

import com.minibot.api.method.Players;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.macros.zulrah.Zulrah;
import com.minibot.macros.zulrah.boss.Phase;

import java.awt.*;
import java.util.Arrays;

/**
 * @author Tim Dekker
 * @since 7/15/15
 */
public class Debug {

    public static final Color DARK = new Color(100, 100, 100, 180);

    public static void paint(Graphics g) {
        Tile local = Players.local().location();
        Tile origin = Zulrah.getOrigin();
        Phase phase = Zulrah.getPhase();
        int y = 10;
        g.setColor(DARK);
        g.drawRect(0, 0, 200, 350);
        g.setColor(Color.GREEN);
        g.drawString("Zulrah Debugging", 20, y += 13);
        g.drawString("Previous ids: " + Arrays.toString(Zulrah.getPrevious().toArray(new Integer[0])),
                20, y += 13);
        g.drawString(String.format("Origin: %s, Offset: %s,%s", origin.toString(),
                origin.x() - local.x(), origin.y() - local.y()), 20, y += 13);
        if (phase != null)
            g.drawString("Current: Phase - " + phase + " Stage - " + phase.getCurrent() +
                    " Type - " + phase.getCurrent().getSnakeType(), 20, y += 13);
    }
}
