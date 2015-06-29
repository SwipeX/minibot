package com.minibot.macros;

import com.minibot.Minibot;
import com.minibot.api.method.Game;
import com.minibot.api.method.Inventory;
import com.minibot.api.method.Players;
import com.minibot.api.method.Skills;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.Item;
import com.minibot.api.wrapper.locatable.Player;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * @author Tim Dekker
 * @since 6/1/15.
 */
@Manifest(name = "Prayer", author = "Swipe", version = "1.0.0", description = "Drinks prayer potions")
public class Prayer extends Macro implements Renderable {

    private Tile alt;

    private int lastExp;
    private int holder;
    private int startExp;
    private long lastMove;

    @Override
    public void atStart() {
        if (!Game.playing()) {
            interrupt();
        }
        Player local = Players.local();
        if (local != null) {
            Tile start = Players.local().location();
            alt = start.derive(-4, 0);
            startExp = Game.experiences()[Skills.RANGED];
        }
    }

    @Override
    public void run() {
        Minibot.instance().client().resetMouseIdleTime();
        if (Game.levels()[Skills.PRAYER] < 25) {
            Item prayer = Inventory.first(item -> item != null && item.name() != null && item.name().contains("rayer"));
            if (prayer != null) {
                prayer.processAction("Drink");
                Time.sleep(1800);
            }
        }
        if (Game.levels()[Skills.RANGED] <= Game.realLevels()[Skills.RANGED] + 9) {
            Item ranging = Inventory.first(item -> item != null && item.name() != null && item.name().contains("ang"));
            if (ranging != null) {
                ranging.processAction("Drink");
                Time.sleep(1800);
            }
        }
    }


    @Override
    public void render(Graphics2D g) {
        g.setColor(Color.CYAN);
        int gained = Game.experiences()[Skills.RANGED] - startExp;
        g.drawString("Time: " + Time.format(runtime()), 10, 10);
        g.drawString("Exp: " + gained, 10, 25);
        g.drawString("Exp/H: " + hourly(gained), 10, 40);
        g.drawString("Last Exp: " + holder, 10, 52);
        if (Game.experiences()[Skills.RANGED] != lastExp) {
            holder = (Game.experiences()[Skills.RANGED] - lastExp);
            lastExp = Game.experiences()[Skills.RANGED];
        }
    }
}