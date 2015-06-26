package com.minibot.macros;

import com.minibot.api.method.Ground;
import com.minibot.api.method.Players;
import com.minibot.api.util.Renderable;
import com.minibot.api.wrapper.locatable.GroundItem;
import com.minibot.api.wrapper.locatable.Player;
import com.minibot.api.wrapper.locatable.Tile;
import com.minibot.bot.macro.Macro;
import com.minibot.bot.macro.Manifest;

import java.awt.*;

/**
 * @author Tyler Sedlar
 * @since 6/24/2015
 */
@Manifest(name = "Test", author = "Tyler", version = "1.0.0", description = "For testing purposes")
public class Test extends Macro implements Renderable {

    private Tile tile;

    @Override
    public void run() {
        Player local = Players.local();
        if (local != null) {
            if (tile == null || !local.location().equals(tile)) {
                tile = local.location();
                System.out.println(tile);
            }
        }
        GroundItem loot = Ground.nearestByFilter(i -> {
            String name = i.name();
            return name != null && name.contains("f gra");
        });
        if (loot != null) {
            System.out.println("Mark of spaghetti!");
        }
    }

    @Override
    public void render(Graphics2D g) {
    }
}