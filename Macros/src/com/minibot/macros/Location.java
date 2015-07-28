package com.minibot.macros;

import com.minibot.api.method.Players;
import com.minibot.api.util.Renderable;
import com.minibot.api.util.Time;
import com.minibot.api.wrapper.locatable.Player;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 7/27/2015
 */
@Manifest(name = "Location", author = "Tyler", version = "1.0.0", description = "Shows location")
public class Location extends Macro implements Renderable {

    @Override
    public void run() {
        Time.sleep(200, 300);
    }

    @Override
    public void render(Graphics2D g) {
        Player player = Players.local();
        g.drawString(player != null ? player.location().toString() : "N/A", 15, 60);
    }
}
