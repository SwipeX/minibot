package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.method.*;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.util.ValueFormat;
import com.minibot.api.wrapper.locatable.GameObject;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 6/28/2015
 */
@Manifest(name = "Willow Chopper", author = "Tyler", version = "1.0.0", description = "Chops willows")
public class WillowChopper extends Macro implements Renderable {

    private static final int COMMA_FORMAT = ValueFormat.COMMAS;
    private static final int THOUSAND_FORMAT = ValueFormat.THOUSANDS | ValueFormat.PRECISION(2);

    private static final double EXP_EACH = 67.5D;

    private int startExp;

    @Override
    public void atStart() {
        startExp = Game.experiences()[Skills.WOODCUTTING];
    }

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        if (Inventory.full()) {
            if (!Bank.viewing()) {
                Bank.openBooth();
            } else {
                Bank.depositAll();
                Bank.close();
            }
        } else {
            if (Players.local().animation() == -1) {
                GameObject willow = Objects.nearestByName("Willow");
                if (willow != null) {
                    Tile tile = willow.location();
                    Tile derived = willow.location().derive(-1, -1);
                    willow.processAction("Chop down", derived.localX(), derived.localY());
                    if (Time.sleep(() -> Players.local().animation() != -1, 7000)) {
                        Time.sleep(() -> {
                            if (Players.local().animation() == -1 || Inventory.full())
                                return true;
                            GameObject obj = Objects.topAt(tile);
                            return obj != willow;
                        }, 30000);
                    }
                }
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.YELLOW);
        int gained = Game.experiences()[Skills.WOODCUTTING] - startExp;
        int chopped = (int) ((double) gained / EXP_EACH);
        int yOff = 11;
        g.drawString("Runtime: " + Time.format(runtime()), 13, yOff += 15);
        String fChopped = ValueFormat.format(chopped, COMMA_FORMAT);
        String fChoppedHr = ValueFormat.format(hourly(chopped), COMMA_FORMAT);
        g.drawString("Chopped: " + fChopped + " (" + fChoppedHr + "/HR)", 13, yOff += 15);
        String fExp = ValueFormat.format(gained, COMMA_FORMAT);
        String fExpHr = ValueFormat.format(hourly(gained), THOUSAND_FORMAT);
        g.drawString("Experience: " + fExp + " (" + fExpHr + "/HR)", 13, yOff + 15);
    }
}
