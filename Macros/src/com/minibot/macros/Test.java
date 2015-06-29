package com.minibot.macros;

import com.minibot.api.method.Game;
import com.minibot.api.method.Players;
import com.minibot.api.util.Renderable;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 6/24/2015
 */
@Manifest(name = "Test", author = "Tyler", version = "1.0.0", description = "For testing purposes")
public class Test extends Macro implements Renderable {

    @Override
    public void atStart() {
        if (Players.local() == null) {
            interrupt();
        }
    }

    @Override
    public void run() {
    }

    @Override
    public void render(Graphics2D g) {
        g.drawString(Players.local().location().toString(), 50, 50);
        g.drawString(Game.runEnabled() + " - " + Game.energy(), 50, 65);
    }
}